package com.infoworks.services.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.objects.Message;
import com.infoworks.objects.MessageParser;
import com.infoworks.objects.Response;
import com.infoworks.tasks.ExecutableTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ReportWriter extends ExecutableTask<Message, Response> {

    private static Logger LOG = LoggerFactory.getLogger(ReportWriter.class);
    private Map<String, Object> data;
    private String saveDir;
    private ObjectMapper mapper;

    public ReportWriter(Map<String, Object> data, String saveDir, ObjectMapper mapper) {
        this.data = data;
        this.saveDir = saveDir;
        this.mapper = mapper;
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        Map<String, Object> data = new HashMap<>();
        data.putAll(this.data);
        data.put("status", "COMPLETE");
        //TODO:
        try { Thread.sleep(5000); } catch (Exception ignore) {}
        //...
        return new Response().setStatus(200).setMessage(MessageParser.printJson(data, mapper));
    }
}
