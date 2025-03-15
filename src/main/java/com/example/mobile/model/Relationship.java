package com.example.mobile.model;

import com.example.mobile.model.enums.RelationshipStatus;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "relationships")
public class Relationship {
    @Id
    private ObjectId id;

    private ObjectId userId1;

    private ObjectId userId2;

    private RelationshipStatus status;

}
