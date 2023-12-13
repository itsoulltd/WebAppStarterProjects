package com.infoworks.lab.rest.client;

import com.infoworks.lab.domain.entities.Passenger;
import com.infoworks.lab.rest.client.datasource.Links;
import com.infoworks.lab.rest.client.datasource.Page;
import com.infoworks.lab.rest.client.datasource.PaginatedResponse;
import com.infoworks.lab.rest.client.datasource.RestDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;

public class DatasourceClientTest {

    @Test
    public void doLoadTest() throws IOException {
        //
        RestTemplate template = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/api/data/passengers")
                .setConnectTimeout(Duration.ofMillis(5000))
                .setReadTimeout(Duration.ofMillis(7000))
                .build();
        //
        URL url = new URL("http://localhost:8080/api/data/passengers");
        RestDataSource<String, Passenger> dataSource = new RestDataSource(url, template);

        PaginatedResponse response = dataSource.load();
        Assert.assertTrue(response != null);

        Page page = response.getPage();
        Assert.assertTrue(page != null);

        Links links = response.getLinks();
        Assert.assertTrue(links != null);
    }

    /////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////
}
