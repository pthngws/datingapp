package com.example.mobile.controller;

import com.example.mobile.dto.request.ProfileUpdateDTO;
import com.example.mobile.dto.response.ApiResponse;
import com.example.mobile.dto.response.ProfileResponse;
import com.example.mobile.model.Album;
import com.example.mobile.model.Profile;
import com.example.mobile.model.User;
import com.example.mobile.model.enums.Gender;
import com.example.mobile.service.IImageUploadService;
import com.example.mobile.service.IProfileService;
import com.example.mobile.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@Tag(name = "2. Profile", description = "Các API liên quan đến hồ sơ người dùng")
public class ProfileController {
    @Autowired
    private IProfileService profileService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IImageUploadService imageUploadService;

    @PostMapping(value = "/images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Tải ảnh", description = "Cho phép người dùng tải lên nhiều ảnh hồ sơ cùng lúc")
    public ResponseEntity<?> uploadImages(
            @RequestPart("files") MultipartFile[] files) {
        try {
            Album userImages = imageUploadService.uploadImages(files);
            return ResponseEntity.ok(userImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Tải ảnh thất bại: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm hồ sơ", description = "Lọc hồ sơ người dùng theo các tiêu chí như tên, giới tính, tuổi, chiều cao")
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
        try {
            List<Profile> profiles = profileService.searchProfiles(firstName, lastName, gender, age, minAge, maxAge, minHeight, maxHeight);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Tìm kiếm thành công", profiles));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/recent")
    @Operation(summary = "Lấy danh sách user mới", description = "Trả về danh sách người dùng mới đăng ký trong 7 ngày gần nhất")
    public ResponseEntity<ApiResponse<List<User>>> getUsersCreatedWithinLast7Days() {
        try {
            List<User> users = userService.getUsersCreatedWithinLast7Days();
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Danh sách user mới trong vòng 7 ngày", users));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/update")
    @Operation(summary = "Cập nhật hồ sơ", description = "Cho phép người dùng cập nhật thông tin hồ sơ cá nhân")
    public ResponseEntity<ApiResponse<Profile>> updateProfile(@Valid @RequestBody ProfileUpdateDTO updatedProfile) {
        try {
            Profile profile = profileService.updateProfile(updatedProfile);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Cập nhật profile thành công", profile));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi hệ thống", null));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin hồ sơ", description = "Trả về thông tin chi tiết của hồ sơ người dùng dựa trên ID")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfileByUserId(@PathVariable String id) {
        ObjectId objectId = new ObjectId(id);
        ProfileResponse profile = profileService.findByUserId(objectId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "", profile));
    }
}