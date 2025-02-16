package com.example.mobile.repository;
import com.example.mobile.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
    List<User> findByCreateDateAfter(LocalDate date);
    Optional<User> findByEmail(String email);

}