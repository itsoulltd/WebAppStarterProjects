package com.infoworks.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.domain.entities.EventLog;
import com.infoworks.domain.repositories.EventLogRepository;
import com.infoworks.objects.MessageParser;
import com.infoworks.objects.Response;
import com.infoworks.services.FileStoreService;
import com.infoworks.services.tasks.ReportWriter;
import com.infoworks.tasks.queue.TaskQueue;
import com.infoworks.utils.eventq.EventQueue;
import com.infoworks.utils.services.iResources;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/reports/v1")
public class ReportingController {

    private static Logger LOG = LoggerFactory.getLogger(ReportingController.class);
    private final FileStoreService fileService;
    private final ObjectMapper mapper;
    private final EventLogRepository logRepository;
    private final String uploadPath;
    private final TaskQueue taskQueue;
    private final iResources resources;

    public ReportingController(
            ObjectMapper mapper
            , iResources resources
            , EventLogRepository logRepository
            , @Value("${app.upload.dir.report}") String uploadPath
            , @Value("${concurrent.thread-pool.reporting-controller}") String concurrent) {
        //
        this.uploadPath = uploadPath;
        this.mapper = mapper;
        this.resources = resources;
        this.fileService = new FileStoreService(uploadPath, resources);
        this.logRepository = logRepository;
        //
        int poolSize = Integer.parseInt(Optional.ofNullable(concurrent).orElse("1").toString());
        this.taskQueue = new EventQueue(Executors.newFixedThreadPool(poolSize), true);
        this.taskQueue.onTaskComplete((message, state) -> {
            if (message instanceof Response) {
                String msg = ((Response) message).getMessage();
                try {
                    Map<String, Object> data = MessageParser.unmarshal(Map.class, msg, mapper);
                    String marker = Optional.ofNullable(data.get("marker")).orElse("").toString();
                    String status = Optional.ofNullable(data.get("status")).orElse("FAILED").toString();
                    EventLog event = logRepository.findByEvent(marker).orElse(null);
                    if (event != null) {
                        event.setStatus(status);
                        logRepository.save(event);
                    }
                } catch (IOException e) {
                    EventLog log = new EventLog();
                    log.setEvent("report_write_exception");
                    log.setStatus("FAILED");
                    log.setDescription(e.getMessage());
                    this.logRepository.save(log);
                }
            }
        });
    }

    @GetMapping("/prepare")
    public ResponseEntity<Map> prepareReport(
            @RequestParam(name = "reportMarker", required = false, defaultValue = "") String marker
            , @Parameter(hidden = true) HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();
        boolean wasMarkerBlank = (marker == null || marker.isBlank());
        if (wasMarkerBlank) marker = UUID.randomUUID().toString();

        //Check already started:
        EventLog event = logRepository.findByEvent(marker).orElse(null);
        if (event != null) {
            result.put("status", event.getStatus());
            try { result.putAll(MessageParser.unmarshal(Map.class, event.getDescription(), mapper)); }
            catch (Exception ignore) {}
            return ResponseEntity.ok(result);
        } else {
            //
            Map<String, Object> data = new HashMap<>();
            data.put("marker", marker);
            data.put("filename", createReportFilename(marker, "xlsx"));
            data.put("download_url", String.format("%s%s/download?filename=%s"
                    , getBaseUrl(request)
                    , "/reports/v1"
                    , data.get("filename")));
            //
            event = new EventLog();
            event.setEvent(marker);
            event.setStatus("STARTED");
            event.setDescription(MessageParser.printJson(data, mapper));
            logRepository.save(event);
            //Start a task as an async flow:
            taskQueue.add(new ReportWriter(data, uploadPath, mapper));
            //
            result.put("status", event.getStatus());
            result.putAll(data);
        }
        return ResponseEntity.ok(result);
    }

    private String createReportFilename(String marker, String format) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("%s_%s.%s", marker, timestamp, format);
    }

    private String getBaseUrl(HttpServletRequest request) {
        return request.getRequestURL().substring(0, request.getRequestURL().indexOf(request.getRequestURI()));
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadContent(
            @RequestParam("filename") String filename) throws IOException {
        //Find and download:
        InputStream ios = fileService.read(filename);
        if (ios == null) return ResponseEntity.notFound().build();
        //
        int contentLength = ios.available();
        byte[] bytes = new byte[contentLength];
        ios.read(bytes);
        ByteArrayResource resource = new ByteArrayResource(bytes);
        ios.close();
        //
        return createResponseEntity(filename, contentLength, resource);
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
}
