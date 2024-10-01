package org.wallhack.logprocessor.realtimelogprocessor.service.dto;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@Document(indexName = "realtime-logs")
@NoArgsConstructor
public class LogDocument {
    @Id
    private String id;
    @Field(type = FieldType.Text, name = "message")
    private String message;
    @Field(type = FieldType.Text, name = "level")
    private String level;
    @Field(type = FieldType.Date, name = "timestamp")
    private Date timestamp;
}

