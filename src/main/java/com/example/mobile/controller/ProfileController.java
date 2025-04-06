package com.example.mobile.controller;

import com.example.mobile.dto.request.LocationUpdateDto;
import com.example.mobile.dto.request.ProfileUpdateDTO;
import com.example.mobile.dto.response.ApiResponse;
import com.example.mobile.dto.response.ProfileResponse;
import com.example.mobile.model.Album;
import com.example.mobile.model.Profile;
import com.example.mobile.model.User;
import com.example.mobile.model.enums.Gender;
import com.example.mobile.service.IImageUploadService;
import com.example.mobile.service.IProfileService;
import com.example.mobile.service.IRelationshipService;
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
import java.util.stream.Collectors;

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

    @Autowired
    private IRelationshipService relationshipService;

    @PostMapping(value = "/images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload single image", description = "Allows user to upload one profile image to a specific position")
    public ResponseEntity<?> uploadImage(
            @RequestPart("file") MultipartFile file,
            @RequestParam("position") String picPosition) {
        try {
            Album userImages = imageUploadService.uploadImage(file, picPosition);
            return ResponseEntity.ok(userImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Image upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm hồ sơ", description = "Lọc hồ sơ người dùng theo các tiêu chí như tên, giới tính, tuổi, chiều cao, và khoảng cách")
    public ResponseEntity<ApiResponse<List<String>>> searchProfiles(
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) Double maxDistance
    ) {
        try {
            // Tìm kiếm hồ sơ theo các tiêu chí
            List<Profile> profiles = profileService.searchProfiles(gender, minAge, maxAge, maxDistance);

            // Lấy danh sách userId tương ứng với các profile
            List<ObjectId> userIds = profiles.stream()
                    .map(p -> {
                        // Lấy userId từ profileId
                        User user = userService.getUserByProfileId(p.getId());
                        return user.getId(); // Lấy ObjectId userId
                    })
                    .collect(Collectors.toList());

            // Lọc ra các userIds mà bạn đã có mối quan hệ với họ
            List<ObjectId> filteredUserIds = relationshipService.filterUserIdsWithRelationship(userIds);

            // Loại bỏ các userIds đã có mối quan hệ
            userIds.removeAll(filteredUserIds);

            // Chuyển ObjectId thành String
            List<String> remainingUserIds = userIds.stream()
                    .map(ObjectId::toHexString)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Tìm kiếm thành công", remainingUserIds));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi hệ thống: " + e.getMessage(), null));
        }
    }



    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin hồ sơ", description = "Trả về thông tin chi tiết của hồ sơ người dùng dựa trên ID")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfileByUserId(@PathVariable String id) {
        ObjectId objectId = new ObjectId(id);
        ProfileResponse profile = profileService.findByUserId(objectId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "", profile));
    }

    @GetMapping("/recent")
    @Operation(summary = "Lấy danh sách user mới", description = "Trả về danh sách người dùng mới đăng ký trong 7 ngày gần nhất, không bao gồm những người đã có quan hệ")
    public ResponseEntity<ApiResponse<List<User>>> getUsersCreatedWithinLast7Days() {
        try {
            // Sử dụng phương thức mới để lọc người dùng chưa có quan hệ
            List<User> users = userService.getUsersCreatedWithinLast7DaysExcludingRelationships();
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



    @PostMapping("/update-location")
    @Operation(summary = "Cập nhật vị trí", description = "Cập nhật vĩ độ và kinh độ của người dùng hiện tại")
    public ResponseEntity<ApiResponse<Void>> updateLocation(@Valid @RequestBody LocationUpdateDto request) {
        try {
            profileService.updateLocation(request);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Cập nhật vị trí thành công", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi hệ thống: " + e.getMessage(), null));
        }
    }
}