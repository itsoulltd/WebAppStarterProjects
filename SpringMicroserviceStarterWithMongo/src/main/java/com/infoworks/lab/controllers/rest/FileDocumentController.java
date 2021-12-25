package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.domain.entities.FileDocument;
import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.services.iDocumentService;
import com.infoworks.lab.util.services.iResourceService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/documents")
public class FileDocumentController {

    private iDocumentService docService;
    private iResourceService resService;

    public FileDocumentController(iDocumentService docService, iResourceService resService) {
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
    public ResponseEntity<List<Map>> query(@RequestParam("limit") Integer limit
            , @RequestParam("offset") Integer offset){
        List<FileDocument> documents = Arrays.asList(docService.readSync(offset, limit));
        List<Map> metas = documents
                .stream()
                .map(doc -> {
                    Map mp = new HashMap(doc.getFileMeta());
                    mp.put("uuid", doc.getUuid());
                    return mp;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(metas);
    }

    @PostMapping("/upload/image")
    public ResponseEntity<Map> uploadContent(
            @RequestParam("content") MultipartFile content,
            RedirectAttributes redirectAttributes) throws IOException {
        //
        if (content.getContentType().equalsIgnoreCase("image/jpeg")
                || content.getContentType().equalsIgnoreCase("image/png")) {
            //Lets do it for image only document.getContentType() == "image/jpeg"
            FileDocument document = new FileDocument();
            document.setName(content.getOriginalFilename());
            document.setContentType(content.getContentType());
            document.setContentLength(content.getResource().contentLength());
            document.setDescription(content.getResource().getDescription());
            //...
            BufferedImage bufferedImage = resService.readAsImage(content.getInputStream(), BufferedImage.TYPE_INT_RGB);
            iResourceService.Format format = content.getContentType().equalsIgnoreCase("image/jpeg")
                    ? iResourceService.Format.JPEG
                    : iResourceService.Format.PNG;
            String base64Image = resService.readImageAsBase64(bufferedImage, format);
            document.setContent(base64Image);
            //...
            docService.add(document);
            Map mp = new HashMap(document.getFileMeta());
            mp.put("uuid", document.getUuid());
            return ResponseEntity.ok(mp);
        }
        Map<String, String > data = new HashMap<>();
        data.put("error", "contentType not an image.");
        return ResponseEntity.badRequest().body(data);
    }

    @GetMapping("/download/image")
    public ResponseEntity<Resource> downloadContent(@RequestParam("uuid") String uuid) throws IOException {
        //
        FileDocument document = docService.read(uuid);
        //
        if (document == null) return ResponseEntity.notFound().build();
        if (document.getContentType().equalsIgnoreCase("image/jpeg")
                || document.getContentType().equalsIgnoreCase("image/png")) {
            //Lets do it for image only document.getContentType() == "image/jpeg"
            String base64Str = document.getContent();
            BufferedImage decryptedImg = resService.readImageFromBase64(base64Str);
            iResourceService.Format format = document.getContentType().equalsIgnoreCase("image/jpeg")
                    ? iResourceService.Format.JPEG
                    : iResourceService.Format.PNG;
            InputStream ios = new ByteArrayInputStream(resService.readImageAsBytes(decryptedImg, format));
            int contentLength = ios.available();
            byte[] bytes = new byte[contentLength];
            ios.read(bytes);
            ByteArrayResource resource = new ByteArrayResource(bytes);
            ios.close();
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
        Map<String, String > data = new HashMap<>();
        data.put("error", "contentType not an image.");
        return ResponseEntity.badRequest().build();
    }

}