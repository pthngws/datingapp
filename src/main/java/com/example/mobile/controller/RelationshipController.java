package com.example.mobile.controller;

import com.example.mobile.dto.response.ApiResponse;
import com.example.mobile.dto.response.ProfileResponse;
import com.example.mobile.service.IRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relationships")
public class RelationshipController {

    @Autowired
    private IRelationshipService relationshipService;

    @PostMapping("/{userId}/like")
    public ResponseEntity<ApiResponse<String>> likeUser(@PathVariable String userId) {
        String result = relationshipService.likeUser(userId);
        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "Thành công", result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{userId}/skip")
    public ResponseEntity<ApiResponse<String>> skipUser(@PathVariable String userId) {
        String result = relationshipService.skipUser(userId);
        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "Thành công", result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{userId}/block")
    public ResponseEntity<ApiResponse<String>> blockUser(@PathVariable String userId) {
        String result = relationshipService.blockUser(userId);
        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "Thành công", result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/block")
    public ResponseEntity<ApiResponse<String>> unBlockUser(@PathVariable String userId) {
        String result = relationshipService.unblockUser(userId);
        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "Thành công", result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/match")
    public ResponseEntity<ApiResponse<String>> unmatchUser(@PathVariable String userId) {
        String result = relationshipService.unmatchUser(userId);
        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "Thành công", result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/liked-users")
    public ResponseEntity<ApiResponse<List<ProfileResponse>>> getLikedUsers() {
        List<ProfileResponse> likedUsers = relationshipService.getUsersWhoLikedMe();
        ApiResponse<List<ProfileResponse>> response = new ApiResponse<>(HttpStatus.OK.value(), "Thành công", likedUsers);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/matched-users")
    public ResponseEntity<ApiResponse<List<ProfileResponse>>> getMatchedUsers() {
        List<ProfileResponse> matchedUsers = relationshipService.getUsersWhoMatchedMe();
        ApiResponse<List<ProfileResponse>> response = new ApiResponse<>(HttpStatus.OK.value(), "Thành công", matchedUsers);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}