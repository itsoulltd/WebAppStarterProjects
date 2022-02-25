package com.infoworks.lab.services;

import com.infoworks.lab.rest.models.SearchQuery;
import com.it.soul.lab.data.simple.SimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service("localStorageService")
public class LocalStorageService extends SimpleDataSource<String, InputStream> implements iFileStorageService<InputStream> {

    private static Logger LOG = LoggerFactory.getLogger(LocalStorageService.class);
    private Map<String, Boolean> fileSavedStatusMap = new ConcurrentHashMap<>();
    private Executor executor = Executors.newSingleThreadExecutor();

    @Value("${app.upload.dir}")
    private String uploadPath;

    public String[] fileNames(){
        return getInMemoryStorage().keySet().toArray(new String[0]);
    }

    @Override
    public InputStream read(String filename) {
        try {
            String fileLocation = getTargetLocation(filename);
            File file = new File(fileLocation);
            FileInputStream ios = new FileInputStream(file);
            return ios;
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void put(String filename, InputStream multipartFile) {
        try {
            String fileLocation = getTargetLocation(filename);
            fileSavedStatusMap.put(filename, save(fileLocation, multipartFile));
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            fileSavedStatusMap.put(filename, false);
        }
    }

    @Override
    public boolean containsKey(String filename) {
        return fileSavedStatusMap.containsKey(filename);
    }

    @Override
    public InputStream remove(String filename) {
        if (containsKey(filename)){
            try {
                String fullPath = getTargetLocation(filename);
                if (deleteFile(fullPath)) {
                    fileSavedStatusMap.remove(filename);
                    return new FileInputStream("");
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return null;
    }

    private boolean deleteFile(String fullPath) throws SecurityException {
        File file = new File(fullPath);
        boolean deleted = file.delete();
        if (deleted){
            LOG.info("Deleted: " + fullPath);
        }
        return deleted;
    }

    protected synchronized List<String> getUnsavedFiles(){
        final List<String> notSavedYet = fileSavedStatusMap.entrySet()
                .stream()
                .filter(enty -> !enty.getValue())
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
        return notSavedYet;
    }

    private String getTargetLocation(String fileName) {
        File currDir = new File(uploadPath);
        String path = currDir.getAbsolutePath();
        String fileLocation = path + "/" + fileName;
        return fileLocation;
    }

    @Override
    public boolean save(String fileLocation, InputStream in) throws IOException {
        try(FileOutputStream f = new FileOutputStream(fileLocation)){
            int data;
            while ((data = in.read()) != -1) {
                f.write(data);
            }
        }
        return true;
    }

    private void retrySave(String fileName, Map fileSavedStatusMap) throws IOException {
        InputStream file = read(fileName);
        String fileLocation = getTargetLocation(fileName);
        fileSavedStatusMap.put(fileName, save(fileLocation, file));
    }

    public void retry(boolean async) {
        List<String> notSavedYet = getUnsavedFiles();
        notSavedYet.forEach(fileName -> {
            if (async){
                executor.execute(() -> {
                    try {
                        retrySave(fileName, fileSavedStatusMap);
                    } catch (IOException e) {
                        LOG.error(e.getMessage(), e);
                    }
                });
            }else {
                try {
                    retrySave(fileName, fileSavedStatusMap);
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public InputStream findByName(String name) {
        return read(name);
    }

    @Override
    public List<InputStream> search(SearchQuery query) {
        //TODO:
        return null;
    }
}
