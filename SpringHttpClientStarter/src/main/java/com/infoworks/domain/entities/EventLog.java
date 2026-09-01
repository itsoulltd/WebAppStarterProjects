package com.infoworks.domain.entities;

import com.infoworks.entity.PrimaryKey;
import com.infoworks.entity.TableName;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

@TableName(value = "event_log")
@Entity(name = "event_log")
@Table(name="event_log", indexes = {@Index(name = "idx_event", columnList = "event")})
public class EventLog extends Auditable<Long, Long> {

    @PrimaryKey(name="id", auto=true) @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String event;
    private String status;
    @Lob
    private String description;
    private long timestamp = Instant.now().toEpochMilli();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventLog that = (EventLog) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
