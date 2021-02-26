package com.lab.infoworks.domain.entities;

import javax.persistence.Embeddable;

@Embeddable
public class Username {
    private String username;

    public Username(String username) {
        this.username = username;
    }

    public Username() {}
}
