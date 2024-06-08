package com.infoworks.lab.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.infoworks.lab.client.data.rest.Any;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;
import java.util.Map;

public class Auditable<ID, VERSION> extends Any<ID> implements Externalizable {

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
        out.writeObject(marshallingToMap(true));
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        Map<String, Object> data = (Map<String, Object>) in.readObject();
        unmarshallingFromMap(data, true);
    }

    @Override
    @JsonIgnore
    public Map<String, Object> get_links() {
        return super.get_links();
    }
}
