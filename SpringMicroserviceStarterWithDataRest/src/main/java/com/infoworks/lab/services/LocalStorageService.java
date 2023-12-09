package com.infoworks.lab.services;

import com.infoworks.lab.rest.models.SearchQuery;
import com.it.soul.lab.data.simple.SimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service("localStorageService")
public class LocalStorageService extends SimpleDataSource<String, InputStream> implements iFileStorageService<InputStream> {

    private static Logger LOG = LoggerFactory.getLogger(LocalStorageService.class);
    private Map<String, Boolean> fileSavedStatusMap = new ConcurrentHashMap<>();
    private Executor executor = Executors.newSingleThreadExecutor();
    private final String uploadPath;

    public LocalStorageService(@Value("${app.upload.dir}") String uploadPath) {
        this.uploadPath = uploadPath;
    }

    protected Map<String, Boolean> getFileSavedStatusMap() {
        return fileSavedStatusMap;
    }

    public String[] fileNames(){
        return getFileSavedStatusMap().keySet().toArray(new String[0]);
    }

    @Override
    public int size() {
        return getFileSavedStatusMap().size();
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
            getFileSavedStatusMap().put(filename, save(fileLocation, multipartFile));
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            getFileSavedStatusMap().put(filename, false);
        }
    }

    @Override
    public boolean containsKey(String filename) {
        return getFileSavedStatusMap().containsKey(filename);
    }

    @Override
    public InputStream remove(String filename) {
        if (containsKey(filename)){
            try {
                String fullPath = getTargetLocation(filename);
                if (deleteFile(fullPath)) {
                    getFileSavedStatusMap().remove(filename);
                    return new FileInputStream("");
                }
            } catch (Exception e) {
                LOG.error(e.getMessage());
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

    @Override
    public InputStream replace(String filename, InputStream inputStream) {
        if (!containsKey(filename)) return null;
        InputStream old = read(filename);
        put(filename, inputStream);
        return old;
    }

    protected List<String> getUnsavedFiles() {
        final List<String> notSavedYet = getFileSavedStatusMap().entrySet()
                .stream()
                .filter(entry -> !entry.getValue())
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
                        retrySave(fileName, getFileSavedStatusMap());
                    } catch (IOException e) {
                        LOG.error(e.getMessage(), e);
                    }
                });
            }else {
                try {
                    retrySave(fileName, getFileSavedStatusMap());
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
        List<File> subFiles = searchFiles(Paths.get(uploadPath).toFile(), query);
        List<InputStream> finalRes = new ArrayList<>();
        subFiles.stream()
                .filter(File::isFile)
                .forEach(inFile -> {
                    try {
                        finalRes.add(new FileInputStream(inFile));
                    } catch (FileNotFoundException e) {
                        LOG.error(e.getMessage());
                    }
                });
        return finalRes;
    }

    @Override
    public List<File> searchFiles(File searchDir, SearchQuery query) {
        List<File> subFiles = new ArrayList<>();
        if (query != null) {
            query.getProperties()
                    .stream()
                    .filter(qp -> qp.getValue() != null && !qp.getValue().isEmpty())
                    .forEach(qp -> {
                        final String lookingFor = qp.getValue();
                        File[] results = searchDir.listFiles((dirPath, name) -> {
                            boolean found = dirPath.isDirectory() && dirPath.getName().contains(lookingFor);
                            if(!found) found = name.contains(lookingFor);
                            return found;
                        });
                        if (results == null) return; //Means Continue:
                        List<File> allFiles = new ArrayList<>();
                        allFiles.addAll(Arrays.stream(results)
                                .filter(File::isFile)
                                .collect(Collectors.toList()));
                        allFiles.addAll(Arrays.stream(results)
                                .filter(File::isDirectory)
                                .flatMap(inDir -> {
                                    File[] files = inDir.listFiles();
                                    return (files != null) ? Arrays.stream(files) : null;
                                })
                                .collect(Collectors.toList()));
                        subFiles.addAll(allFiles);
                    });
        }
        return subFiles;
    }

    public void prepareZipEntryFrom(List<File> files, OutputStream oStream) throws IOException {
        ZipOutputStream zips = new ZipOutputStream(oStream);
        files.forEach(fileToZip -> {
            //Making Zip-entry:-
            try (FileInputStream fis = new FileInputStream(fileToZip)) {
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zips.putNextEntry(zipEntry);
                //
                byte[] bytes = new byte[1024];
                int length;
                while((length = fis.read(bytes)) >= 0) {
                    zips.write(bytes, 0, length);
                }
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
            //Zipping Done
        });
        zips.finish();
        zips.close();
    }

}
