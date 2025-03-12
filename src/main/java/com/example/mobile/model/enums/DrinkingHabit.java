package com.example.mobile.model.enums;

public enum DrinkingHabit {
    KHONG_DANH_CHO_MINH("Không dành cho mình"),
    LUON_TINH_TAO("Luôn tỉnh táo"),
    UONG_CO_TRACH_NHIEM("Uống có trách nhiệm"),
    CHI_DIP_DAC_BIET("Chỉ những dịp đặc biệt"),
    UONG_CUOI_TUAN("Uống giao lưu vào cuối tuần"),
    HAU_NHU_MOI_TOI("Hầu như mỗi tối");

    private final String displayName;

    DrinkingHabit(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
