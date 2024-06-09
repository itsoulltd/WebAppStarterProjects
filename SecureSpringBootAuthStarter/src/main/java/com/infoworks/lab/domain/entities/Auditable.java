package com.infoworks.lab.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.infoworks.lab.client.data.rest.Any;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;
import java.util.Map;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Auditable<ID, VERSION> extends Any<ID> implements Externalizable {

    @CreatedDate @Column(name = "created_date")
    LocalDateTime createdDate;

    @LastModifiedDate  @Column(name = "last_modified_date")
    LocalDateTime lastModifiedDate;

    @AttributeOverride(name = "username", column = @Column(name = "created_by"))
    @Embedded @CreatedBy
    Username createdBy;

    @AttributeOverride(name = "username", column = @Column(name = "last_modified_by"))
    @Embedded @LastModifiedBy
    Username lastModifiedBy;

    @Version @JsonIgnore
    private VERSION version;

    public VERSION getVersion() {
        return version;
    }

    public void setVersion(VERSION version) {
        this.version = version;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(marshallingToMap(true));
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        Map<String, Object> data = (Map<String, Object>) in.readObject();
        unmarshallingFromMap(data, true);
    }

    @Override @JsonIgnore
    public Map<String, Object> get_links() {
        return super.get_links();
    }
}
