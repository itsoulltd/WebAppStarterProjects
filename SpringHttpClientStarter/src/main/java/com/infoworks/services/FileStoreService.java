package com.infoworks.services;

import com.infoworks.utils.services.impl.FileStore;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

@Service
public class FileStoreService extends FileStore {

    private static Logger LOG = LoggerFactory.getLogger(FileStoreService.class);
    private final String uploadPath;

    public FileStoreService(@Value("${app.upload.dir}") String uploadPath) {
        super(uploadPath);
        this.uploadPath = uploadPath;
    }

    @PostConstruct
    public void postInit() {
        loadFileSavedStatusMap();
    }

    protected void loadFileSavedStatusMap() {
        //Load from uploadPath:
        File uploadDir = new File(uploadPath);
        if (getFileSavedStatusMap().isEmpty() && uploadDir.isDirectory()) {
            Set<String> excluded = Set.of(".ini", ".tmp", ".bak");
            File[] files = Optional.ofNullable(uploadDir.listFiles(File::isFile)).orElse(new File[0]);
            Arrays.stream(files)
                    .map(File::getName)
                    .filter(name -> name.contains(".") && !excluded.contains(name.substring(name.indexOf("."))))
                    .forEach(name -> getFileSavedStatusMap().put(name, true));
            LOG.info(String.format("FileSavedStatusMap get loaded from %s, count: %s", uploadPath, size()));
        }
    }

    public String getUploadPath() {
        return uploadPath;
    }
}
