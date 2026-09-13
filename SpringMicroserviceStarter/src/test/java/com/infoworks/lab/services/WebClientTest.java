package com.infoworks.lab.services;

import com.infoworks.lab.webapp.config.BeanConfig;
import com.infoworks.lab.webapp.config.TestJPAH2Config;
import com.infoworks.utils.rest.client.DownloadTask;
import com.infoworks.utils.services.iResources;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

@SpringBootTest(classes = {BeanConfig.class, TestJPAH2Config.class})
public class WebClientTest {

    private static Logger LOG = LoggerFactory.getLogger(WebClientTest.class);

    @Test
    public void downloadTaskTest_HttpClient() {
        //CAUTION: CHECK DOWNLOAD URL and CONTENT BEFORE RUN THE TEST
        //Test Url-1: https://farm7.staticflickr.com/6089/6115759179_86316c08ff_z_d.jpg
        //
        DownloadTask task = new DownloadTask("https://farm7.staticflickr.com/6089/6115759179_86316c08ff_z_d.jpg"
                , null);
        task.setToken("my-token");
        DownloadTask.ResourceResponse response = task.execute(null);
        LOG.info("HttpClient Status: " + response.getStatus());
        //
        if (response.getResource() != null) {
            try (InputStream iso = response.getResource()) {
                iResources service = iResources.create();
                BufferedImage img = service.readAsImage(iso, TYPE_INT_RGB);
                Assertions.assertNotNull(img);
                LOG.info("HttpClient Image Downloaded: " + response.filename());
                LOG.info("HttpClient Image Size: " + response.contentLength());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void downloadTaskTest_Spring() {
        //CAUTION: CHECK DOWNLOAD URL and CONTENT BEFORE RUN THE TEST
        //Test Url-1: https://farm7.staticflickr.com/6089/6115759179_86316c08ff_z_d.jpg
        //
        com.infoworks.utils.rest.spring.DownloadTask task
                = new com.infoworks.utils.rest.spring.DownloadTask("https://farm7.staticflickr.com/6089/6115759179_86316c08ff_z_d.jpg"
                , null);
        task.setToken("my-token");
        com.infoworks.utils.rest.spring.DownloadTask.ResourceResponse response = task.execute(null);
        LOG.info("Spring Status: " + response.getStatus());
        //
        if (response.getResource() != null) {
            try (InputStream iso = response.getResource().getInputStream()) {
                iResources service = iResources.create();
                BufferedImage img = service.readAsImage(iso, TYPE_INT_RGB);
                Assertions.assertNotNull(img);
                LOG.info("Spring Image Downloaded: " + response.getResource().getFilename());
                LOG.info("Spring Image Size: " + response.getResource().contentLength());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
