package com.infoworks.services;

import com.infoworks.utils.rest.client.DownloadTask;
import com.infoworks.utils.services.iResources;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.reactive.function.client.WebClient;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WebClientTest {

    private static Logger LOG = LoggerFactory.getLogger(WebClientTest.class);

    //@Test
    public void downloadTaskTest() {
        //CAUTION: CHECK DOWNLOAD URL and CONTENT BEFORE RUN THE TEST
        //Test Url-1: https://farm7.staticflickr.com/6089/6115759179_86316c08ff_z_d.jpg
        //
        DownloadTask task = new DownloadTask("https://farm7.staticflickr.com/6089/6115759179_86316c08ff_z_d.jpg"
                , null);
        task.setToken("my-token");
        DownloadTask.ResourceResponse response = task.execute(null);
        LOG.info("Status: " + response.getStatus());
        //
        if (response.getResource() != null) {
            try (InputStream iso = response.getResource()) {
                iResources service = iResources.create();
                BufferedImage img = service.readAsImage(iso, TYPE_INT_RGB);
                Assertions.assertNotNull(img);
                LOG.info("Image Downloaded: " + response.filename());
                LOG.info("Image Size: " + response.contentLength());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //@Test
    public void downloadPdfAndCountWordsTest() throws IOException {
        //CAUTION: CHECK DOWNLOAD URL and CONTENT BEFORE RUN THE TEST
        //Test Url-2: https://file-examples.com/storage/fe2b56191b6a91eed93e57a/2017/10/file-sample_150kB.pdf

        WebClient webClient =
                WebClient.builder().baseUrl("https://file-examples.com")
                        .defaultHeaders(headers -> headers.setBasicAuth("username", "password"))
                        .build();
        //
        Resource response = webClient.get()
                .uri(String.format("/storage/fe2b56191b6a91eed93e57a/2017/10/%s"
                        , URLEncoder.encode("file-sample_150kB.pdf", Charset.defaultCharset())))
                .retrieve()
                .bodyToMono(Resource.class)
                .block();
        //
        if (response.getInputStream() != null) {
            try (InputStream iso = response.getInputStream()) {
                PDDocument document = Loader.loadPDF(iso.readAllBytes());
                assertNotNull(document);
                //document.close();
                LOG.info("PDF Downloaded: " + response.getDescription());
                LOG.info("PDF Size: " + response.contentLength());
                //
                WordCounter counter = new WordCounter();
                long wordCount = counter.pdfWordCount(document);
                LOG.info("PDF Word Count: " + wordCount);
                //
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
