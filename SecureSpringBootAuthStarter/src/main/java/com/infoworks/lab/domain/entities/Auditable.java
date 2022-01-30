package com.infoworks.lab.domain.entities;

import com.it.soul.lab.sql.entity.Entity;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Auditable extends Entity {

    @CreatedDate
    LocalDate createdDate;

    @LastModifiedDate
    LocalDate lastModifiedDate;

    @AttributeOverride(name = "username", column = @Column(name = "created_by"))
    @Embedded @CreatedBy
    Username createdBy;

    @AttributeOverride(name = "username", column = @Column(name = "last_modified_by"))
    @Embedded @LastModifiedBy
    Username lastModifiedBy;

}
