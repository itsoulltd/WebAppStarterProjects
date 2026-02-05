package com.infoworks.lab.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.infoworks.entity.Entity;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;
import java.util.Map;

public class Auditable<ID, VERSION> extends Entity implements Externalizable {

    @CreatedDate @Field("created_date")
    LocalDateTime createdDate;

    @LastModifiedDate @Field("last_modified_date")
    LocalDateTime lastModifiedDate;

    @CreatedBy @Field("created_by")
    Username createdBy;

    @LastModifiedBy @Field("last_modified_by")
    Username lastModifiedBy;

    @Version @JsonIgnore
    private VERSION version;

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(marshalling(true));
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        Map<String, Object> data = (Map<String, Object>) in.readObject();
        unmarshalling(data, true);
    }
}
