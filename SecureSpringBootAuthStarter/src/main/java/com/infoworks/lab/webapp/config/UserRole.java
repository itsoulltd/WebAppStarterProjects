package com.infoworks.lab.webapp.config;

public enum UserRole {
    ADMIN("Admin","admin","ADMIN","ADMIN_ROLE","ROLE_ADMIN")
    , TENANT("Tenant","TENANT","tenant")
    , USER("User","USER","user");

    private String[] tags;

    UserRole(String...tags) {
        this.tags = tags;
    }

    public String[] getTags() {
        return tags;
    }

    public String[] roleTags() {
        return getTags();
    }

    public String[] roles() {
        return getTags();
    }
}
