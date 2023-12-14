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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class RestDataSource<Key, Value extends Any<Key>> extends SimpleDataSource<Key, Value> implements AutoCloseable{

    private final URL baseUrl;
    private ExecutorService service;
    private RestTemplate template;
    private PaginatedResponse baseResponse;
    private Class<? extends Any<Key>> anyClassType;

    public RestDataSource(Class<? extends Any<Key>> type, URL baseUrl) {
        this(type, baseUrl, new RestTemplate());
    }

    public RestDataSource(Class<? extends Any<Key>> type,URL baseUrl, RestTemplate template) {
        this(type, baseUrl, template, Executors.newSingleThreadExecutor());
    }

    public RestDataSource(Class<? extends Any<Key>> type,URL baseUrl, RestTemplate template, ExecutorService service) {
        this.anyClassType = type;
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
        //Do all memory clean-up and terminate running process:
        clear();
        //immediate shutdown all enqueued tasks and return
        service.shutdown();
        service = null;
    }

    public PaginatedResponse load() throws RuntimeException {
        if (baseResponse != null) return baseResponse;
        //Load the base URL:
        HttpHeaders headers = new HttpHeaders();
        Map body = new HashMap();
        HttpEntity<Map> entity = new HttpEntity<>(body, headers);
        String rootURL = baseUrl.toString();
        try {
            ResponseEntity<String> rs = template.exchange(rootURL
                    , HttpMethod.GET
                    , entity
                    , String.class);
            //Checking Network Error:
            if (rs.getStatusCodeValue() >= 400)
                throw new RuntimeException( rs.getBody() + ". Status Code: " + rs.getStatusCodeValue());
            String result = rs.getBody();
            Map<String, Object> dataMap = Message.unmarshal(new TypeReference<Map<String, Object>>() {}, result);
            baseResponse = new PaginatedResponse(dataMap);
            return baseResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void load(Consumer<PaginatedResponse> consumer) {
        if (consumer == null) return;
        if (baseResponse != null)
            consumer.accept(baseResponse);
        //Load the base URL:
        getService().submit(() -> {
            try {
                baseResponse = load();
                consumer.accept(baseResponse);
            } catch (RuntimeException e) {
                PaginatedResponse response = new PaginatedResponse();
                response.setError(e.getMessage());
                response.setStatus(400);
                consumer.accept(response);
            }
        });
    }

    @Override
    public void put(Key key, Value value) {
        //TODO: Put will do PUT
    }

    @Override
    public Key add(Value value) throws RuntimeException {
        //TODO: Add will do POST
        Key key = value.getId();
        return key;
    }

    @Override
    public Value remove(Key key) {
        //TODO: Remove will do DELETE
        return null;
    }

    @Override
    public Value read(Key key) throws RuntimeException{
        //First check in Cache:
        Value any = super.read(key);
        if (any != null) return any;
        //Read will do GET
        try {
            HttpHeaders headers = new HttpHeaders();
            Map<String, Object> body = new HashMap();
            HttpEntity<Map> get = new HttpEntity<>(body, headers);
            String getPath = baseUrl.toString() + "/" + key.toString();
            ResponseEntity<String> getResponse = template.exchange(getPath
                    , HttpMethod.GET
                    , get
                    , String.class);
            String getResult = getResponse.getBody();
            Value value = (Value) Message.unmarshal(anyClassType, getResult);
            return value;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int size() {
        //If baseResponse has been loaded:
        if (baseResponse != null) {
            return baseResponse.getPage().getTotalElements();
        }
        //
        return super.size();
    }

    /**
     * Following are new-funcs:
     */

    /**
     * Fetch Next page Until the End of Line.
     * Also add paged items into local cache.
     * @return
     */
    public Optional<List<Value>> next() {
        if (isLastPage()) return Optional.ofNullable(null);
        if (baseResponse != null){
            Page page = baseResponse.getPage();
            Map<String, Object> dataMap = fetchNext(page);
            //Update Next page info:
            baseResponse.updatePage(dataMap);
            baseResponse.updateLinks(dataMap);
            //Parse next items:
            List<Value> items = parsePageItems(dataMap);
            //Add into in-memory store:
            if (items != null || !items.isEmpty()) {
                items.forEach(item -> {
                    Key key = item.getId();
                    super.put(key, item);
                });
            }
            return Optional.ofNullable(items);
        }
        return Optional.ofNullable(null);
    }

    /**
     * "page" : {
     *     "size" : 5,
     *     "totalElements" : 50,
     *     "totalPages" : 10,
     *     "number" : 0
     *   }
     * At the bottom is extra data about the page settings,
     * including the size of a page, total elements, total pages, and the page number you are currently viewing.
     * @return
     */
    public boolean isLastPage() {
        if (baseResponse == null) return true;
        Page current = baseResponse.getPage();
        int currentPage = current.getNumber();
        return (currentPage >= current.getTotalPages()) ? true : false;
    }

    /**
     * May return Null
     * @param current
     * @return
     */
    protected Map<String, Object> fetchNext(Page current) {
        int currentPage = current.getNumber();
        int pageSize = current.getSize();
        int nextPage = currentPage + 1;
        //
        HttpHeaders headers = new HttpHeaders();
        Map body = new HashMap();
        HttpEntity<Map> entity = new HttpEntity<>(body, headers);
        //
        String nextPagePath = baseUrl.toString() + "?page={page}&size={size}";
        ResponseEntity<String> rs = template.exchange(nextPagePath
                , HttpMethod.GET
                , entity
                , String.class, nextPage, pageSize);
        String result = rs.getBody();
        Map<String, Object> dataMap = null;
        try {
            dataMap = Message.unmarshal(
                    new TypeReference<Map<String, Object>>() {}, result);
        } catch (IOException e) {}
        return dataMap;
    }

    protected List<Value> parsePageItems(Map<String, Object> dataMap) {
        List<Value> typedObjects = new ArrayList<>();
        if (dataMap == null) return typedObjects;
        //Parse DataMap to get-objects:
        Map<String, List<Map<String, Object>>> embedded =
                (Map) dataMap.get("_embedded");
        List<Map<String, Object>> objects = getCollectionResourceRel(embedded);
        if (objects == null) return typedObjects;
        //Try to re-recreate objects:
        for (Map<String, Object> entry : objects) {
            try {
                Value parsed = (Value) anyClassType.newInstance();
                parsed.unmarshallingFromMap(entry, false);
                typedObjects.add(parsed);
            } catch (InstantiationException
                     | IllegalAccessException e) {}
        }
        return typedObjects;
    }

    /**
     * Declared in Spring-Data-Rest repository annotation:
     * e.g. @RepositoryRestResource(collectionResourceRel = "passengers")
     * @return
     */
    protected List<Map<String, Object>> getCollectionResourceRel(Map<String, List<Map<String, Object>>> embedded) {
        if (embedded == null) return null;
        //String apiPathName = getApiPathName();
        //List<Map<String, Object>> objects = embedded.get(apiPathName);
        Optional<String> possibleKey = embedded.keySet().stream().findFirst();
        return possibleKey.isPresent() ? embedded.get(possibleKey.get()) : null;
    }

    /**
     * Declared in Spring-Data-Rest repository annotation:
     * e.g. @RepositoryRestResource(path = "passengers")
     * @return
     */
    protected String getApiPathName() {
        String path = baseUrl.getPath();
        String[] paths = path.split("/");
        String pathName = paths[paths.length - 1];
        return pathName;
    }

    public void next(Consumer<Optional<List<Value>>> consumer) {
        if (consumer != null) {
            getService().submit(() -> consumer.accept(next()));
        }
    }

}
