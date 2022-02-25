package com.infoworks.lab.services.impl;

import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.services.iFileStorageService;
import com.it.soul.lab.data.simple.SimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    public void put(String filename, InputStream multipartFile) {
        super.put(filename, multipartFile);
        try {
            String fileLocation = getTargetLocation(filename);
            fileSavedStatusMap.put(filename, save(fileLocation, multipartFile));
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            fileSavedStatusMap.put(filename, false);
        }
    }

    @Override
    public InputStream remove(String filename) {
        try {
            String fullPath = getTargetLocation(filename);
            deleteFile(fullPath);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        fileSavedStatusMap.remove(filename);
        return super.remove(filename);
    }

    private void deleteFile(String fullPath) throws SecurityException {
        File file = new File(fullPath);
        if (file.delete()){
            LOG.info("Deleted: " + fullPath);
        }
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
