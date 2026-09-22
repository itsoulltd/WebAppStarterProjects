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
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    @PostConstruct
    public void postInit() {
        Path reportingDirPath = Path.of(uploadPath);
        if (!Files.exists(reportingDirPath)) {
            try {
                Path path = Files.createDirectory(reportingDirPath);
                LOG.info("Directory created at path: " + path.toAbsolutePath());
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        } else {
            LOG.info("Directory found at path: " + reportingDirPath.toAbsolutePath());
        }
    }

    private String getBaseUrl(HttpServletRequest request) {
        return request.getRequestURL().substring(0, request.getRequestURL().indexOf(request.getRequestURI()));
    }

    public enum TaskType {
        ReportWriter
    }

    @GetMapping("/prepare/{task_type}")
    public ResponseEntity<Map> prepareReport(@PathVariable(name = "task_type") String taskType
            , @RequestParam(name = "marker", required = false, defaultValue = "") String marker
            , @Parameter(hidden = true) HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();

        //Validate taskType:
        try {
            var _type = TaskType.valueOf(taskType);
        } catch (Exception e) {
            result.put("status", "FAILED");
            String[] alTasks = Arrays.stream(TaskType.values()).map(Enum::name).toArray(String[]::new);
            result.put("reason", taskType + " is not appropriate. e.g. " + String.join(", ", alTasks));
            return ResponseEntity.badRequest().body(result);
        }

        //Validate marker:
        boolean wasMarkerBlank = (marker == null || marker.isBlank());
        if (wasMarkerBlank) marker = taskType + "_" + UUID.randomUUID();

        //Check already started:
        EventLog event = logRepository.findByEvent(marker).orElse(null);
        if (event != null) {
            result.put("status", event.getStatus());
            try { result.putAll(MessageParser.unmarshal(Map.class, event.getDescription(), mapper)); }
            catch (Exception ignore) {}
            return ResponseEntity.ok(result);
        } else {
            //Re-enforce properties:
            Map<String, Object> data = new HashMap<>();
            data.put("task_type", taskType);
            data.put("marker", marker);
            data.put("base_url", getBaseUrl(request) + "/reports/v1/download");
            //
            event = new EventLog();
            event.setEvent(marker);
            event.setDescription(MessageParser.printJson(data, mapper));
            //Start a task as an async flow:
            switch (TaskType.valueOf(taskType)) {
                case ReportWriter -> {
                    event.setStatus("STARTED");
                    this.taskQueue.add(new ReportWriter(data, uploadPath, mapper));
                }
                default -> {
                    event.setStatus("CANCELED");
                    LOG.info("");
                }
            }
            //
            logRepository.save(event);
            result.put("status", event.getStatus());
            result.putAll(data);
        }
        return ResponseEntity.ok(result);
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
