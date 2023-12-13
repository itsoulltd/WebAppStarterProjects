package com.infoworks.lab.rest.client.datasource;

import com.it.soul.lab.sql.entity.Entity;

import java.util.Map;

public class Links extends Entity {

    private Map<String, Object> self;
    private Map<String, Object> profile;
    private Map<String, Object> search;
    public Links() {}

    public Links(Map<String, Map<String, Object>> dataMap) {
        this.profile = dataMap.get("profile");
        this.search = dataMap.get("search");
        this.self = dataMap.get("self");
    }

    public Map<String, Object> getSelf() {
        return self;
    }

    public void setSelf(Map<String, Object> self) {
        this.self = self;
    }

    public Map<String, Object> getProfile() {
        return profile;
    }

    public void setProfile(Map<String, Object> profile) {
        this.profile = profile;
    }

    public Map<String, Object> getSearch() {
        return search;
    }

    public void setSearch(Map<String, Object> search) {
        this.search = search;
    }
}
