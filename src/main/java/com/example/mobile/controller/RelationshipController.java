package com.example.mobile.controller;

import com.example.mobile.dto.response.ProfileResponse;
import com.example.mobile.service.IRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relationships")
public class RelationshipController {
    @Autowired
    private IRelationshipService relationshipService;

    @PostMapping("/{userId}/like")
    public ResponseEntity<?> likeUser(@PathVariable String userId) {
        relationshipService.likeUser(userId);
        return ResponseEntity.ok("You liked user " + userId);
    }

    @PostMapping("/{userId}/skip")
    public ResponseEntity<?> skipUser(@PathVariable String userId) {
        relationshipService.skipUser(userId);
        return ResponseEntity.ok("You skipped user " + userId);
    }

    @PostMapping("/{userId}/block")
    public ResponseEntity<?> blockUser(@PathVariable String userId) {
        relationshipService.blockUser(userId);
        return ResponseEntity.ok("You blocked user " + userId);
    }

    @GetMapping("/likes")
    public ResponseEntity<List<ProfileResponse>> getLikes() {
        List<ProfileResponse> likedUsers = relationshipService.getUsersWhoLikedMe();
        return ResponseEntity.ok(likedUsers);
    }

    // Lấy danh sách những người đã match với mình
    @GetMapping("/matches")
    public ResponseEntity<List<ProfileResponse>> getMatches() {
        List<ProfileResponse> matchedUsers = relationshipService.getUsersWhoMatchedMe();
        return ResponseEntity.ok(matchedUsers);
    }
}
