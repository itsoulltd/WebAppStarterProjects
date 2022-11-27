package com.infoworks.lab.services;

import com.infoworks.lab.rest.models.SearchQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class LocalStorageServiceTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void notFound() {
        iFileStorageService<InputStream> iFile = new LocalStorageService("/Users/Public");
        SearchQuery query = new SearchQuery();
        query.add("filename").isEqualTo("qwtep");
        List<InputStream> stream = iFile.search(query);
        //Assert.assertTrue(stream.isEmpty());
        System.out.println("Count: " + stream.size());
    }

    @Test
    public void notFoundEmpty() {
        iFileStorageService<InputStream> iFile = new LocalStorageService("/Users/Public");
        SearchQuery query = new SearchQuery();
        query.add("filename").isEqualTo(" ");
        List<InputStream> stream = iFile.search(query);
        //Assert.assertTrue(stream.isEmpty());
        System.out.println("Count: " + stream.size());
    }

    @Test
    public void fileExistAll() {
        iFileStorageService<InputStream> iFile = new LocalStorageService("/Users/Public");
        SearchQuery query = new SearchQuery();
        query.add("filename").isEqualTo("emn");
        List<InputStream> stream = iFile.search(query);
        //Assert.assertTrue(!stream.isEmpty());
        System.out.println("Count: " + stream.size());
    }

    @Test
    public void contentLengthCheck() {
        iFileStorageService<InputStream> iFile = new LocalStorageService("/Users/Public");
        SearchQuery query = new SearchQuery();
        query.add("filename").isEqualTo("emn");
        List<InputStream> stream = iFile.search(query);
        //Assert.assertTrue(!stream.isEmpty());
        int cLength = stream.stream()
                .flatMapToInt(ios -> {
                    try {
                        int length = ios.available();
                        return IntStream.of(length);
                    } catch (IOException e) {}
                    return IntStream.of(0);
                }).sum();
        System.out.println("Total Content Length: " + cLength);
        System.out.println("Count: " + stream.size());
    }

    @Test
    public void searchByFileNameInDir() {
        iFileStorageService<InputStream> iFile = new LocalStorageService("/Users/Public");
        SearchQuery query = new SearchQuery();
        query.add("filename").isEqualTo("emn");
        List<File> stream = iFile.searchFiles(Paths.get("/Users/Public").toFile(), query);
        //Assert.assertTrue(!stream.isEmpty());
        System.out.println("Count: " + stream.size());
    }

    @Test
    public void searchByDirNameInDir() {
        iFileStorageService<InputStream> iFile = new LocalStorageService("/Users/Public");
        SearchQuery query = new SearchQuery();
        query.add("dirname").isEqualTo("Reports");
        List<File> stream = iFile.searchFiles(Paths.get("/Users/Public").toFile(), query);
        //Assert.assertTrue(stream.isEmpty());
        System.out.println("Count: " + stream.size());
    }

    @Test
    public void fileExistSub() {
        iFileStorageService<InputStream> iFile = new LocalStorageService("/Users/Public/Downloads");
        SearchQuery query = new SearchQuery();
        query.add("filename").isEqualTo("emn");
        List<InputStream> stream = iFile.search(query);
        //Assert.assertTrue(!stream.isEmpty());
        System.out.println("Count: " + stream.size());
    }

    @Test
    public void dirExist() {
        iFileStorageService<InputStream> iFile = new LocalStorageService("/Users/Public");
        SearchQuery query = new SearchQuery();
        query.add("dirname").isEqualTo("Downloads");
        List<InputStream> stream = iFile.search(query);
        //Assert.assertTrue(!stream.isEmpty());
        System.out.println("Count: " + stream.size());
    }

    @Test
    public void emptyDirTest() {
        iFileStorageService<InputStream> iFile = new LocalStorageService("/Users/Public");
        SearchQuery query = new SearchQuery();
        query.add("dirname").isEqualTo("Reports");
        List<InputStream> stream = iFile.search(query);
        //Assert.assertTrue(!stream.isEmpty());
        System.out.println("Count: " + stream.size());
    }

    @Test
    public void makingZip() throws IOException {
        iFileStorageService<InputStream> iFile = new LocalStorageService("/Users/Public/Reports");
        SearchQuery query = new SearchQuery();
        query.add("filename").isEqualTo("emn");
        List<File> files = iFile.searchFiles(Paths.get("/Users/Public/Reports").toFile(), query);
        SimpleDateFormat fileNameDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = String.format("%s_%s.zip", UUID.randomUUID().toString().substring(0, 8)
                , fileNameDateFormatter.format(new Date()));
        File toWrite = Paths.get("/Users/Public/Reports", fileName).toFile();
        //
        if (!files.isEmpty()) {
            //Searching By File-Names:-
            OutputStream fos = new FileOutputStream(toWrite);
            iFile.prepareZipEntryFrom(files, fos);
            fos.flush();
            fos.close();
        }
        System.out.println("Count: " + files.size());
    }

}