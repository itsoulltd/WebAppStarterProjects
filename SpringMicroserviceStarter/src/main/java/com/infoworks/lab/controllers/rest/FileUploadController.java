package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.services.iFileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileUploadController {

    private static Logger LOG = LoggerFactory.getLogger(FileUploadController.class);
    private iFileStorageService<InputStream> storageService;

    @Autowired
    public FileUploadController(iFileStorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/rowCount")
    public ItemCount getRowCount(){
        ItemCount count = new ItemCount();
        count.setCount(Integer.valueOf(storageService.size()).longValue());
        return count;
    }

    @GetMapping
    public ResponseEntity<List<String>> query(@RequestParam("limit") Integer limit
            , @RequestParam("offset") Integer offset){
        List<String> names = Arrays.asList(storageService.fileNames());
        return ResponseEntity.ok(names);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadContent(
            @RequestParam("content") MultipartFile content,
            RedirectAttributes redirectAttributes) throws IOException {
        //Store-InMemory First:
        storageService.put(content.getOriginalFilename(), content.getInputStream());
        return ResponseEntity.ok("Content Received: " + content.getOriginalFilename());
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadContent(@RequestParam("fileName") String fileName) throws IOException {
        //
        InputStream ios = storageService.read(fileName);
        if (ios == null) return ResponseEntity.notFound().build();
        //
        int contentLength = ios.available();
        byte[] bytes = new byte[contentLength];
        ios.read(bytes);
        ByteArrayResource resource = new ByteArrayResource(bytes);
        ios.close();
        //
        return createResponseEntity(fileName, contentLength, resource);
    }

    private ResponseEntity<Resource> createResponseEntity(String fileName, int contentLength, Resource resource) {
        if (resource == null) return ResponseEntity.notFound().build();
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", fileName));
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        //
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(contentLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @DeleteMapping
    public Boolean delete(@RequestParam("filename") String name){
        InputStream stream = storageService.remove(name);
        return stream != null;
    }

    private static SimpleDateFormat fileNameDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    @Value("${app.upload.dir}")
    private String uploadPath;

    @GetMapping("/search/{query}")
    public ResponseEntity<Resource> searchContent(@PathVariable("query") String query) throws IOException {
        SearchQuery search = new SearchQuery();
        search.add("query").isEqualTo(query);
        List<File> files = storageService.searchFiles(Paths.get(uploadPath).toFile(), search);
        if (files.isEmpty()) return ResponseEntity.notFound().build();
        //Searching By File-Names:-
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        storageService.prepareZipEntryFrom(files, baos);
        int contentLength = baos.size();
        ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
        baos.close();
        //
        String fileName = String.format("%s_%s.zip", UUID.randomUUID().toString().substring(0, 8)
                , fileNameDateFormatter.format(new Date()));
        return createResponseEntity(fileName, contentLength, resource);
    }

}
