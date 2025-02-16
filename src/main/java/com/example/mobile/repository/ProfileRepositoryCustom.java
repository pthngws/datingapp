package com.example.mobile.repository;

import com.example.mobile.model.Profile;

import java.util.List;

public interface ProfileRepositoryCustom {
    List<Profile> searchProfiles(String firstName, String lastName, Boolean gender, Integer minAge, Integer maxAge, Integer age, Integer minHeight, Integer maxHeight);
}
