package com.example.mobile.repository;

import com.example.mobile.model.enums.Gender;
import com.example.mobile.model.Profile;

import java.util.List;

public interface ProfileRepositoryCustom {
    List<Profile> searchProfiles( Gender gender, Integer minAge, Integer maxAge);
}
