package com.example.mobile.controller;


import com.example.mobile.model.Profile;
import com.example.mobile.model.User;
import com.example.mobile.service.IProfileService;
import com.example.mobile.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {
    @Autowired
    private IProfileService profileService;

    @Autowired
    private IUserService userService;

    @GetMapping("/firstName")
    public List<Profile> getProfilesByFirstName(@RequestParam String firstName) {
        return profileService.getProfilesByFirstName(firstName);
    }

    @GetMapping("/lastName")
    public List<Profile> getProfilesByLastName(@RequestParam String lastName) {
        return profileService.getProfilesByLastName(lastName);
    }

    @GetMapping("/gender")
    public List<Profile> getProfilesByGender(@RequestParam boolean gender) {
        return profileService.getProfilesByGender(gender);
    }

    @GetMapping("/age")
    public List<Profile> getProfilesByAge(@RequestParam int age) {
        return profileService.getProfilesByAge(age);
    }

    @GetMapping("/ageRange")
    public List<Profile> getProfilesByAgeRange(@RequestParam int minAge, @RequestParam int maxAge) {
        return profileService.getProfilesByAgeRange(minAge, maxAge);
    }

    @GetMapping("/heightRange")
    public List<Profile> getProfilesByHeightRange(@RequestParam int minHeight, @RequestParam int maxHeight) {
        return profileService.getProfilesByHeightRange(minHeight, maxHeight);
    }

    @GetMapping("/recent")
    public List<User> getUsersCreatedWithinLast7Days() {
        return userService.getUsersCreatedWithinLast7Days();
    }

}
