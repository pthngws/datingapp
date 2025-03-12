package com.example.mobile.repository;

import com.example.mobile.model.enums.Gender;
import com.example.mobile.model.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class   ProfileRepositoryImpl implements ProfileRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Profile> searchProfiles(String firstName, String lastName, Gender gender, Integer minAge, Integer maxAge, Integer age, Integer minHeight, Integer maxHeight) {
        Query query = new Query();

        if (firstName != null) {
            query.addCriteria(Criteria.where("firstName").regex(firstName, "i"));
        }
        if (lastName != null) {
            query.addCriteria(Criteria.where("lastName").regex(lastName, "i"));
        }
        if (gender != null) {
            query.addCriteria(Criteria.where("gender").is(gender.name()));
        }
        if (minAge != null && maxAge != null) {
            query.addCriteria(Criteria.where("age").gte(minAge).lte(maxAge));
        }

        return mongoTemplate.find(query, Profile.class);
    }
}
