package com.infoworks.lab.services.impl;

import com.infoworks.lab.domain.entities.FileDocument;
import com.infoworks.lab.domain.repositories.FileDocumentRepository;
import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.rest.models.pagination.Pagination;
import com.infoworks.lab.rest.models.pagination.SortOrder;
import com.infoworks.lab.webapp.config.MongoConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {MongoConfig.class})
public class FileDocumentServiceIntegrationTest {

    @Before
    public void setUp() throws Exception {
        /**/
    }

    @Autowired
    private MongoTemplate template;

    @Autowired
    private FileDocumentRepository repository;

    @Test
    public void testSearchInBatch(){
        FileDocumentService service = new FileDocumentService(repository, template);
        //
        int batchSize = 2;
        SearchQuery searchQuery = Pagination.createQuery(SearchQuery.class, batchSize, SortOrder.ASC);
        //
        Map<Long, List<FileDocument>> data = service.searchInBatchGroup(searchQuery);
        Assert.assertTrue("", data.size() >= 0);
        Assert.assertTrue("", data.get(0l).size() <= batchSize);
    }

    @Test
    public void testSearchInBatch_2(){
        FileDocumentService service = new FileDocumentService(repository, template);
        //
        int batchSize = 3;
        SearchQuery searchQuery = Pagination.createQuery(SearchQuery.class, batchSize, SortOrder.ASC);
        //
        Map<Long, List<FileDocument>> data = service.searchInBatchGroup(searchQuery);
        Assert.assertTrue("", data.size() >= 0);
        Assert.assertTrue("", data.get(0l).size() <= batchSize);
    }

    @Test
    public void testSearchInBatch_3(){
        FileDocumentService service = new FileDocumentService(repository, template);
        //
        int batchSize = 4;
        SearchQuery searchQuery = Pagination.createQuery(SearchQuery.class, batchSize, SortOrder.ASC);
        //
        Map<Long, List<FileDocument>> data = service.searchInBatchGroup(searchQuery);
        Assert.assertTrue("", data.size() >= 0);
        Assert.assertTrue("", data.get(0l).size() <= batchSize);
    }

    @Test
    public void testSearchInBatch_4(){
        FileDocumentService service = new FileDocumentService(repository, template);
        //
        int batchSize = 5;
        SearchQuery searchQuery = Pagination.createQuery(SearchQuery.class, batchSize, SortOrder.ASC);
        //searchQuery.add("name").isEqualTo("dso");
        searchQuery.add("contentType").isEqualTo("image/jpeg");
        //
        Map<Long, List<FileDocument>> data = service.searchInBatchGroup(searchQuery);
        Assert.assertTrue("", data.size() >= 0);
    }

    @Test
    public void testSearchInBatch_5(){
        FileDocumentService service = new FileDocumentService(repository, template);
        //
        int batchSize = 5;
        SearchQuery searchQuery = Pagination.createQuery(SearchQuery.class, batchSize, SortOrder.ASC);
        searchQuery.add("timestamp").isEqualTo("1649778638175");
        //
        List<FileDocument> data = service.search(searchQuery);
        System.out.println("Item Found: " + data.size());
        Assert.assertTrue("", data.size() >= 0);
        Assert.assertTrue(data.get(0).getTimestamp().equals(Long.valueOf("1649778638175")));
    }

    @Test
    public void testSearchInBatch_6(){
        FileDocumentService service = new FileDocumentService(repository, template);
        //
        int batchSize = 5;
        SearchQuery searchQuery = Pagination.createQuery(SearchQuery.class, batchSize, SortOrder.ASC);
        searchQuery.add("timestamp").isGreaterThenOrEqual("1649778638175");
        //
        List<FileDocument> data = service.search(searchQuery);
        System.out.println("Item Found: " + data.size());
        Assert.assertTrue("", data.size() >= 0);
        Assert.assertTrue(data.get(0).getTimestamp().equals(Long.valueOf("1649778638175")));
    }

    @Test
    public void testSearchInBatch_7(){
        FileDocumentService service = new FileDocumentService(repository, template);
        //
        int batchSize = 5;
        SearchQuery searchQuery = Pagination.createQuery(SearchQuery.class, batchSize, SortOrder.ASC);
        searchQuery.add("timestamp").isLessThenOrEqual("q1649778638175w");
        //
        List<FileDocument> data = service.search(searchQuery);
        System.out.println("Item Found: " + data.size());
        Assert.assertTrue("", data.size() >= 0);
    }

    @Test
    public void testRemoveInBatch(){
        FileDocumentService service = new FileDocumentService(repository, template);
        //
        int batchSize = 3;
        SearchQuery searchQuery = Pagination.createQuery(SearchQuery.class, batchSize, SortOrder.ASC);
        //searchQuery.add("name").isEqualTo("dso");
        searchQuery.add("contentType").isEqualTo("image/png");
        //
        long data = service.remove(searchQuery);
        Assert.assertTrue("", data >= 0);
        System.out.println("deleted: " + data);
    }
}