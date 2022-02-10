package com.infoworks.lab.domain.entities;

import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.entity.PrimaryKey;
import com.it.soul.lab.sql.entity.TableName;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Document(collection = "FileDocument")
@TableName(value = "FileDocument")
public class FileDocument extends Entity {

    @Id
    @PrimaryKey(name = "uuid")
    private String uuid = UUID.randomUUID().toString();
    private String content;
    private Map<String, Object> fileMeta = new HashMap<>();

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Map getFileMeta() {
        return fileMeta;
    }

    public void setFileMeta(Map<String, Object> fileMeta) {
        this.fileMeta = fileMeta;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return fileMeta.get("name").toString();
    }

    public void setName(String name) {
        fileMeta.put("name", name);
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

}
