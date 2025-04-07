package com.example.mobile.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class MessageDTO {
    @SerializedName("id")
    private String id;

    @SerializedName("content")
    private String content;

    @SerializedName("senderId")
    private String senderId;

    @SerializedName("receiverId")
    private String receiverId;

    @SerializedName("sendTime")
    private String sendTime;

    @SerializedName("read")
    private boolean read;
}