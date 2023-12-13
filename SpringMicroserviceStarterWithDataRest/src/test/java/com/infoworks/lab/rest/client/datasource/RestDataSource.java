package com.infoworks.lab.rest.client.datasource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.infoworks.lab.rest.models.Message;
import com.it.soul.lab.data.simple.SimpleDataSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class RestDataSource<Key, Value> extends SimpleDataSource<Key, Value> implements AutoCloseable{

    private int pageCursor = 0;
    private final URL baseUrl;
    private ExecutorService service;
    private RestTemplate template;
    private PaginatedResponse baseResponse;

    public RestDataSource(URL baseUrl, RestTemplate template) {
        this(baseUrl, template, Executors.newSingleThreadExecutor());
    }

    public RestDataSource(URL baseUrl, RestTemplate template, ExecutorService service) {
        this.baseUrl = baseUrl;
        this.service = service;
        this.template = template;
    }

    protected ExecutorService getService() {
        if (service == null){
            service = Executors.newSingleThreadExecutor();
        }
        return service;
    }

    @Override
    public void close() throws Exception {
        //TODO: Do all memory clean-up and terminate running process:
        pageCursor = 0;
        clear();
        //immediate shutdown all enqueued tasks and return
        service.shutdown();
        service = null;
    }

    public PaginatedResponse load() throws IOException {
        if (baseResponse != null) return baseResponse;
        //Load the base URL:
        HttpHeaders headers = new HttpHeaders();
        Map body = new HashMap();
        HttpEntity<Map> entity = new HttpEntity<>(body, headers);
        String rootURL = baseUrl.toString();
        ResponseEntity<String> rs = template.exchange(rootURL
                , HttpMethod.GET
                , entity
                , String.class);
        String result = rs.getBody();
        Map<String, Object> dataMap = Message.unmarshal(new TypeReference<Map<String, Object>>() {}, result);
        baseResponse = new PaginatedResponse(dataMap);
        return baseResponse;
    }

    @Override
    public void put(Key key, Value value) {
        //TODO:
    }

    @Override
    public Key add(Value value) throws RuntimeException {
        Object key = value.hashCode();
        put((Key) key, value);
        return (Key) key;
    }

    @Override
    public Value remove(Key key) {
        //TODO:
        return null;
    }

    @Override
    public Value read(Key key) {
        //TODO:
        return null;
    }

    /**
     * Always read from Local-Cache.
     * @param offset
     * @param pageSize
     * @return
     */
    @Override
    public Value[] readSync(int offset, int pageSize) {
        return super.readSync(offset, pageSize);
    }

    @Override
    public void readAsync(int offset, int pageSize, Consumer<Value[]> consumer) {
        //TODO:
        Value[] res = (Value[]) new Object[0];
        //...
        if (consumer != null) {
            getService().submit(() -> consumer.accept(res));
        }
    }

    @Override
    public int size() {
        //TODO:
        return 0;
    }

}
