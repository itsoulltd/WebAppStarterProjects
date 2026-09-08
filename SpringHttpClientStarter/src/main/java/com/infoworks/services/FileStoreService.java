package com.infoworks.services;

import com.infoworks.domain.models.ZipFile;
import com.infoworks.utils.services.iResources;
import com.infoworks.utils.services.impl.FileStore;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class FileStoreService extends FileStore {

    private static Logger LOG = LoggerFactory.getLogger(FileStoreService.class);
    private final String uploadPath;
    private final iResources resources;

    public FileStoreService(@Value("${app.upload.dir}") String uploadPath
            , iResources resources) {
        super(uploadPath);
        this.uploadPath = uploadPath;
        this.resources = resources;
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

    @Override
    public boolean containsKey(String filename) {
        return getFileSavedStatusMap().get(filename);
    }

    /**
     * Make sure returned file should be recycled by marking with File::deleteOnExit() method. So that OS can recycle/reclaim the storage.
     * e.g.
     * var file = createLocalCopyFromResources(my_filename);
     * ...use the file...
     * file.deleteOnExit();
     * @param filename
     * @return
     * @throws IOException
     */
    public File createLocalCopyFromResources(String filename) throws IOException {
        Path tempDir = Files.createTempDirectory("temp-");
        Path target = tempDir.resolve(Path.of(filename).getFileName().toString());
        try (InputStream inputStream = resources.createStream(new File(filename))) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return target.toFile();
    }

    /**
     *
     * @param inputStream
     * @param searchWithExtensions e.g. ".pdf", ".xml", ".png" ... etc
     * @return
     * @throws IOException
     */
    public List<ZipFile> unzipContents(InputStream inputStream, String...searchWithExtensions) throws IOException {
        List<ZipFile> files = new ArrayList<>();
        List<String> searchWith = Arrays.asList(searchWithExtensions);
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            //unZip and browse files:
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                // Directory -> skip
                if (entry.isDirectory()) continue;
                String name = entry.getName().toLowerCase();
                // Only leaf searchWithExtensions files:
                boolean matchFound = searchWith.isEmpty() || searchWith.stream().anyMatch(ext -> name.endsWith(ext));
                if (matchFound) {
                    byte[] content = zipInputStream.readAllBytes();
                    files.add(new ZipFile(entry.getName(), Path.of(entry.getName()).getFileName().toString(), content));
                }
            }
        }
        return files;
    }
}
