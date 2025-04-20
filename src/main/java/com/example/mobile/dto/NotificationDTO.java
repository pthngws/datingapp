package com.example.mobile.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
@Data
public class NotificationDTO {
    @SerializedName("id")
    private String id;
    @SerializedName("userId")
    private String userId;
    @SerializedName("type")
    private String type;
    @SerializedName("content")
    private String content;
    @SerializedName("isRead")
    private boolean isRead;
    @SerializedName("createdAt")
    private String createdAt;
}
