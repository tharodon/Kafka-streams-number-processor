package ru.home.numberprocessor.document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Builder
@Document(collection = "processRules")
public class NumberProcessRule {

    @Id
    private String id;

    @Field(name = "lengthOfGroup")
    private Long lengthOfGroup;

    @Field(name = "timeOFWindowMinutes")
    private Long timeOFWindowMinutes;

    @Field(name = "message")
    private String message;
}
