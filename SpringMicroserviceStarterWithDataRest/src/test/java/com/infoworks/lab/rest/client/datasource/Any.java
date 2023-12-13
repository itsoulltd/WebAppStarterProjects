package com.infoworks.lab.rest.client.datasource;

import com.it.soul.lab.sql.entity.Entity;

public class Any<ID> extends Entity {
    private ID id;

    public Any() {}

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
}
