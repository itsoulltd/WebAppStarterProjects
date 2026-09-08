package com.infoworks.services;

import com.infoworks.domain.models.ZipFile;
import com.infoworks.utils.services.iResources;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

public class ZipBrowserTest {

    private static Logger LOG = LoggerFactory.getLogger(ZipBrowserTest.class);

    @BeforeEach
    public void before() {}

    private InputStream createInputStream(String fileName, iResources resources) throws FileNotFoundException {
        if (resources == null) {
            Path path = Paths.get("src","test", "resources", fileName);
            File imfFile = new File(path.toFile().getAbsolutePath());
            InputStream ios = new FileInputStream(imfFile);
            return ios;
        } else {
            File imfFile = new File(fileName);
            InputStream ios = resources.createStream(imfFile);
            return ios;
        }
    }

    @Test
    public void readZipDir_All() throws IOException {
        FileStoreService uploadFile = new FileStoreService("target/", iResources.create());

        iResources resources = iResources.create();
        try (InputStream ios = createInputStream("data/ZipTest.zip", resources)) {
            Assertions.assertNotNull(ios);
            List<ZipFile> files = uploadFile.unzipContents(ios);
            //Assertions.assertEquals(2, files.size());
            files.forEach(file -> LOG.info(file.filename()));
        }
        LOG.info("====================================");
    }

    @Test
    public void readZipDir() throws IOException {
        FileStoreService uploadFile = new FileStoreService("target/", iResources.create());

        iResources resources = iResources.create();
        try (InputStream ios = createInputStream("data/ZipTest.zip", resources)) {
            Assertions.assertNotNull(ios);
            List<ZipFile> files = uploadFile.unzipContents(ios, ".pdf", ".png");
            //Assertions.assertEquals(2, files.size());
            files.forEach(file -> LOG.info(file.filename()));
        }
        LOG.info("====================================");
    }

    //@Test
    public void unZipAndSave() throws IOException {
        FileStoreService uploadFile = new FileStoreService("target/", iResources.create());

        iResources resources = iResources.create();
        try (InputStream ios = createInputStream("data/ZipTest.zip", resources)) {
            Assertions.assertNotNull(ios);
            //read:
            List<ZipFile> files = uploadFile.unzipContents(ios, ".pdf");
            //Assertions.assertEquals(2, files.size());
            //save:
            files.forEach(file -> {
                try (InputStream contentSteam = new ByteArrayInputStream(file.content())) {
                    String filename = String.format("%s_%s", Instant.now().toEpochMilli(), file.filename());
                    uploadFile.put(filename, contentSteam);
                    LOG.info(file.filename() + " Saved.");
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            });
        }
        LOG.info("====================================");
    }


}
