package com.infoworks.lab.rest.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.infoworks.lab.client.data.rest.Any;
import com.infoworks.lab.client.data.rest.PaginatedResponse;
import com.infoworks.lab.client.spring.DataRestClient;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.QueryParam;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * How to Extend and Implement Search Functions.
 * @param <A>
 */
public class SearchClient<A extends Any> extends DataRestClient<A> {

    public SearchClient(Class<? extends Any> type, URL baseUrl) {
        super(type, baseUrl);
    }

    public Optional<List<A>> search(String function, QueryParam... params) {
        if (Objects.isNull(function)) return Optional.ofNullable(null);
        PaginatedResponse response = load();
        Object href = response.getLinks().getSearch().get("href");
        if (href != null) {
            String searchAction = function + encodedQueryParams(params);
            HttpEntity<Map> entity = new HttpEntity(null, getHttpHeaders());
            String searchUrl = href + "/" + searchAction;
            String result = exchange(HttpMethod.GET, entity, searchUrl);
            try {
                Map<String, Object> dataMap = Message.unmarshal(new TypeReference<Map<String, Object>>() {
                }, result);
                return Optional.ofNullable(parsePageItems(dataMap));
            } catch (IOException var12) {
            }
        }
        return Optional.ofNullable(null);
    }

    private String encodedQueryParams(QueryParam... params) {
        StringBuilder buffer = new StringBuilder("?");
        for (QueryParam query : params) {
            if (query.getValue() == null || query.getValue().isEmpty()) continue;
            try {
                buffer.append(query.getKey()
                        + "="
                        + URLEncoder.encode(query.getValue(), "UTF-8")
                        + "&");
            } catch (UnsupportedEncodingException e) {
            }
        }
        String value = buffer.toString();
        value = value.substring(0, value.length() - 1);
        return value;
    }
}
