package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.domain.entities.GridFileDocument;
import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.services.iDocumentService;
import com.infoworks.lab.util.services.iResourceService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/gridfs/v1")
public class GridFDocumentController {

    private iDocumentService<GridFileDocument> docService;
    private iResourceService resService;

    public GridFDocumentController(@Qualifier("gridFDocumentService") iDocumentService docService, iResourceService resService) {
        this.docService = docService;
        this.resService = resService;
    }

    @GetMapping("/count")
    public ItemCount getRowCount(){
        ItemCount count = new ItemCount();
        count.setCount(Integer.valueOf(docService.size()).longValue());
        return count;
    }

    @GetMapping
    public ResponseEntity<List<Map>> query(
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit
            , @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset){
        List<GridFileDocument> documents = Arrays.asList(docService.readSync(offset, limit));
        List<Map> metas = documents
                .stream()
                .map(doc -> {
                    Map mp = new HashMap(doc.getFileMeta());
                    mp.put("uuid", doc.getUuid());
                    mp.put("name", doc.getName());
                    mp.put("timestamp", doc.getTimestamp());
                    return mp;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(metas);
    }

    @GetMapping("/findByName/{name}")
    public ResponseEntity<Map> findByName(@PathVariable("name") String name) {
        GridFileDocument document = docService.findByName(name);
        Map mp = new HashMap(document.getFileMeta());
        mp.put("uuid", document.getUuid());
        mp.put("name", document.getName());
        mp.put("timestamp", document.getTimestamp());
        return ResponseEntity.ok(mp);
    }

    @DeleteMapping
    public Boolean delete(@RequestParam("uuid") String uuid) {
        GridFileDocument document = docService.remove(uuid);
        return document != null;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map> uploadContent(
            @RequestParam("content") MultipartFile content,
            RedirectAttributes redirectAttributes) throws IOException {
        //
        GridFileDocument document = new GridFileDocument();
        document.setName(content.getOriginalFilename());
        document.setContentType(content.getContentType());
        document.setContentLength(content.getResource().contentLength());
        document.setDescription(content.getResource().getDescription());
        document.setContent(content.getInputStream());
        //
        docService.add(document);
        Map mp = new HashMap(document.getFileMeta());
        mp.put("uuid", document.getUuid());
        mp.put("name", document.getName());
        mp.put("timestamp", document.getTimestamp());
        return ResponseEntity.ok(mp);
    }

    @PostMapping("/upload/base64")
    public ResponseEntity<Map> uploadStringContent(@RequestBody SearchQuery content) throws IOException {
        //
        GridFileDocument document = new GridFileDocument();
        document.setName(content.get("name", String.class));
        document.setContentType(content.get("contentType", String.class));
        document.setDescription(content.get("description", String.class));
        String base64Str = content.get("content", String.class);
        if (base64Str != null && !base64Str.isEmpty() && iDocumentService.isValidBase64String(base64Str)){
            InputStream contentStream;
            if (document.getContentType().toLowerCase().startsWith("image")){
                iResourceService.Format format = document.getContentType().equalsIgnoreCase("image/jpeg")
                        ? iResourceService.Format.JPEG
                        : iResourceService.Format.PNG;
                contentStream = new ByteArrayInputStream(
                        resService.readImageAsBytes(resService.readImageFromBase64(base64Str)
                                , format)
                );
            }else {
                contentStream = new ByteArrayInputStream(base64Str.getBytes());
            }
            if (contentStream != null){
                document.setContentLength(contentStream.available());
            }
            document.setContent(contentStream);
            docService.add(document);
            //
            Map mp = new HashMap(document.getFileMeta());
            mp.put("uuid", document.getUuid());
            mp.put("name", document.getName());
            mp.put("timestamp", document.getTimestamp());
            return ResponseEntity.ok(mp);
        }
        Map<String, String> data = new HashMap<>();
        data.put("error", "content is empty or null.");
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/download") @SuppressWarnings("Duplicates")
    public ResponseEntity<Resource> downloadContent(@RequestParam("uuid") String uuid) throws IOException {
        //
        GridFileDocument document = docService.read(uuid);
        if (document == null) return ResponseEntity.notFound().build();
        //
        InputStream ios = document.getContent();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int data = ios.read();
        while (data >= 0){
            out.write(data);
            data = ios.read();
        }
        byte[] bytes = out.toByteArray();
        int contentLength = bytes.length;
        ByteArrayResource resource = new ByteArrayResource(bytes);
        ios.close();
        out.close();
        //
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", document.getName()));
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

}
