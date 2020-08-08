package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.services.LocalStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/files")
public class FileUploadController {

    private LocalStorageService storageService;

    @Autowired
    public FileUploadController(@Qualifier("local") LocalStorageService storageService) {
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
        List<MultipartFile> files = Arrays.asList(storageService.readSynch(offset, limit));
        List<String> names = files.stream().map(file -> file.getOriginalFilename()).collect(Collectors.toList());
        return ResponseEntity.ok(names);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadContent(
            @RequestParam("content") MultipartFile content,
            RedirectAttributes redirectAttributes){
        //Store-InMemory First:
        storageService.put(content.getOriginalFilename(), content);
        //storageService.save(false);
        return ResponseEntity.ok("Content Received: " + content.getOriginalFilename());
    }

}
