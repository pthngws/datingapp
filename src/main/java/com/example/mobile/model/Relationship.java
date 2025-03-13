package com.example.mobile.model;

import com.example.mobile.model.enums.RelationshipStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "relationships")
public class Relationship {
    @Id
    private String id;

    private String userId1;

    private String userId2;

    private RelationshipStatus status;

}
