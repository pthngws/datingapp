package com.example.mobile.service;

import com.example.mobile.model.Profile;

import java.util.List;

public interface IProfileService {


    List<Profile> searchProfiles(String firstName, String lastName, Boolean gender, Integer age, Integer minAge, Integer maxAge, Integer minHeight, Integer maxHeight);
}
