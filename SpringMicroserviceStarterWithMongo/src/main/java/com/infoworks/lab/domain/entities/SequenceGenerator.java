package com.infoworks.lab.domain.entities;

import com.it.soul.lab.sql.entity.TableName;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "SequenceGenerator")
@TableName(value = "SequenceGenerator")
public class SequenceGenerator {
    @Id
    private String id;
    private long seq;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }
}
