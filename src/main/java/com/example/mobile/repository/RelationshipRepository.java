package com.example.mobile.repository;

import com.example.mobile.model.Relationship;
import com.example.mobile.model.enums.RelationshipStatus;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RelationshipRepository extends MongoRepository<Relationship, ObjectId> {
    Optional<Relationship> findByUserId1AndUserId2(ObjectId userId1, ObjectId UserId2);
    List<Relationship> findByUserId2AndStatus(ObjectId userId2, RelationshipStatus status);
    List<Relationship> findByUserId1(ObjectId userId1);

    @Query("{$or: [{'userId1': ?0, 'status': ?1}, {'userId2': ?0, 'status': ?1}]}")
    List<Relationship> findByUserId1AndStatusOrUserId2AndStatus(ObjectId userId, RelationshipStatus status, ObjectId currentUserId, RelationshipStatus matched);
}
