package com.infoworks.lab.domain.entities;

import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.entity.Ignore;
import com.it.soul.lab.sql.entity.PrimaryKey;
import com.it.soul.lab.sql.entity.TableName;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "GridFileDocument")
@TableName(value = "GridFileDocument")
public class GridFileDocument extends Entity {

    @Id
    @PrimaryKey(name = "uuid")
    private String uuid;
    private String name;
    private Long timestamp = System.currentTimeMillis();
    private Map<String, Object> fileMeta = new HashMap<>();

    @Ignore @Transient
    private InputStream content;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Map<String, Object> getFileMeta() {
        return fileMeta;
    }

    public void setFileMeta(Map<String, Object> fileMeta) {
        this.fileMeta = fileMeta;
    }

    public InputStream getContent() {
        return content;
    }

    public void setContent(InputStream content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return fileMeta.get("description").toString();
    }

    public void setDescription(String description) {
        fileMeta.put("description", description);
    }

    public String getContentType(){
        return fileMeta.get("contentType").toString();
    }

    public void setContentType(String contentType) {
        fileMeta.put("contentType", contentType);
    }

    public long getContentLength(){
        return Long.valueOf(fileMeta.get("contentLength").toString());
    }

    public void setContentLength(long contentLength) {
        fileMeta.put("contentLength", contentLength);
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
