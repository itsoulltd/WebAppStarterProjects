package com.infoworks.lab.services;

import com.infoworks.lab.rest.models.SearchQuery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

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

}