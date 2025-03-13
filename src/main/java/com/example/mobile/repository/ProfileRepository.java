package com.example.mobile.repository;

import com.example.mobile.model.Profile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends MongoRepository<Profile, ObjectId>, ProfileRepositoryCustom {
}
