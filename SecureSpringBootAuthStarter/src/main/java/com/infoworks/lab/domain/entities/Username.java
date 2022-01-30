package com.infoworks.lab.domain.entities;

import javax.persistence.Embeddable;

@Embeddable
public class Username {

    private String username;

    public Username() {}
    public Username(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
