package com.example.mobile.controller;

import com.example.mobile.dto.request.ProfileUpdateDTO;
import com.example.mobile.dto.response.ApiResponse;
import com.example.mobile.model.Album;
import com.example.mobile.model.Profile;
import com.example.mobile.model.User;
import com.example.mobile.model.enums.Gender;
import com.example.mobile.service.IImageUploadService;
import com.example.mobile.service.IProfileService;
import com.example.mobile.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {
    @Autowired
    private IProfileService profileService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IImageUploadService imageUploadService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload multiple images")
    public ResponseEntity<?> uploadImages(
            @RequestPart("files") MultipartFile[] files) {
        try {
            Album userImages = imageUploadService.uploadImages(files);
            return ResponseEntity.ok(userImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }





    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Profile>>> searchProfiles(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Gender gender,
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

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Profile>> updateProfile(@RequestBody ProfileUpdateDTO updatedProfile) {
        try {
            // Lấy ID từ token
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            ObjectId userId = new ObjectId(authentication.getName());  // userId lưu trong "sub" của token

            // Lấy profile từ user
            User user = userService.findUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Không tìm thấy User với ID: " + userId, null));
            }

            // Lấy profile từ user
            if (user.getProfileId() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Không tìm thấy Profile của user", null));
            }

            Profile profile = profileService.updateProfile(user.getProfileId(), updatedProfile); // Cập nhật profile

            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Cập nhật profile thành công", profile));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi hệ thống", null));
        }
    }

}
