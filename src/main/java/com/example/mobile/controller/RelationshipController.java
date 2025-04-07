package com.example.mobile.controller;

import com.example.mobile.dto.response.ApiResponse;
import com.example.mobile.dto.response.UserInfoResponse;
import com.example.mobile.service.IRelationshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relationships")
@Tag(name = "3. Relationship", description = "Các API liên quan đến tương tác giữa người dùng")
public class RelationshipController {

    @Autowired
    private IRelationshipService relationshipService;

    @Operation(summary = "Thích một người dùng", description = "Gửi yêu cầu thích một người dùng dựa trên userId")
    @PostMapping("/{userId}/like")
    public ResponseEntity<ApiResponse<String>> likeUser(@PathVariable String userId) {
        String result = relationshipService.likeUser(userId);
        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "Thành công", result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Bỏ qua một người dùng", description = "Bỏ qua một người dùng dựa trên userId")
    @PostMapping("/{userId}/skip")
    public ResponseEntity<ApiResponse<String>> skipUser(@PathVariable String userId) {
        String result = relationshipService.skipUser(userId);
        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "Thành công", result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Chặn một người dùng", description = "Chặn một người dùng dựa trên userId")
    @PostMapping("/{userId}/block")
    public ResponseEntity<ApiResponse<String>> blockUser(@PathVariable String userId) {
        String result = relationshipService.blockUser(userId);
        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "Thành công", result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Bỏ chặn một người dùng", description = "Bỏ chặn một người dùng dựa trên userId")
    @DeleteMapping("/{userId}/block")
    public ResponseEntity<ApiResponse<String>> unBlockUser(@PathVariable String userId) {
        String result = relationshipService.unblockUser(userId);
        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "Thành công", result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Hủy ghép đôi với một người dùng", description = "Hủy ghép đôi với một người dùng dựa trên userId")
    @DeleteMapping("/{userId}/match")
    public ResponseEntity<ApiResponse<String>> unmatchUser(@PathVariable String userId) {
        String result = relationshipService.unmatchUser(userId);
        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "Thành công", result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Lấy danh sách người dùng đã thích tôi", description = "Trả về danh sách các profile của những người dùng đã thích người dùng hiện tại")
    @GetMapping("/liked-users")
    public ResponseEntity<ApiResponse<List<UserInfoResponse>>> getLikedUsers() {
        List<UserInfoResponse> likedUsers = relationshipService.getUsersWhoLikedMe();
        ApiResponse<List<UserInfoResponse>> response = new ApiResponse<>(HttpStatus.OK.value(), "Thành công", likedUsers);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Lấy danh sách người dùng đã ghép đôi với tôi", description = "Trả về danh sách các profile của những người dùng đã ghép đôi với người dùng hiện tại")
    @GetMapping("/matched-users")
    public ResponseEntity<ApiResponse<List<UserInfoResponse>>> getMatchedUsers() {
        List<UserInfoResponse> matchedUsers = relationshipService.getUsersWhoMatchedMe();
        ApiResponse<List<UserInfoResponse>> response = new ApiResponse<>(HttpStatus.OK.value(), "Thành công", matchedUsers);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}