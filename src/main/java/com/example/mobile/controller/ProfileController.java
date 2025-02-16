package com.example.mobile.controller;

import com.example.mobile.dto.response.ApiResponse;
import com.example.mobile.model.Profile;
import com.example.mobile.model.User;
import com.example.mobile.service.IProfileService;
import com.example.mobile.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Profile>>> searchProfiles(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Boolean gender,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) Integer minHeight,
            @RequestParam(required = false) Integer maxHeight
    ) {
        try
        {
            List<Profile> profiles = profileService.searchProfiles(firstName, lastName, gender, age, minAge, maxAge, minHeight, maxHeight);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Search completed", profiles));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<User>>> getUsersCreatedWithinLast7Days() {
        try {
            List<User> users = userService.getUsersCreatedWithinLast7Days();
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Users created within the last 7 days", users));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }
}
