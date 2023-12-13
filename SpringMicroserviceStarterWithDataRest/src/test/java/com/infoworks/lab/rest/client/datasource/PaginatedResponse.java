package com.infoworks.lab.rest.client.datasource;

import com.infoworks.lab.rest.models.Response;

import java.util.List;
import java.util.Map;

public class PaginatedResponse extends Response {

    private Map<String, List<Map<String, Object>>> embedded;
    private Links links;
    private Page page;

    public PaginatedResponse() {}

    public PaginatedResponse(Map<String, Object> dataMap) {
        this.embedded = (Map) dataMap.get("_embedded");
        this.links = new Links((Map) dataMap.get("_links"));
        this.page = new Page((Map) dataMap.get("page"));
    }

    public Map<String, List<Map<String, Object>>> getEmbedded() {
        return embedded;
    }

    public void setEmbedded(Map<String, List<Map<String, Object>>> embedded) {
        this.embedded = embedded;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}