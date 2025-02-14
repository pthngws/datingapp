package com.example.mobile.service;

import com.example.mobile.model.Profile;

import java.util.List;

public interface IProfileService {
    List<Profile> getProfilesByFirstName(String firstName);

    List<Profile> getProfilesByLastName(String lastName);

    List<Profile> getProfilesByGender(boolean gender);

    List<Profile> getProfilesByAge(int age);

    List<Profile> getProfilesByAgeRange(int minAge, int maxAge);

    List<Profile> getProfilesByHeightRange(int minHeight, int maxHeight);

}
