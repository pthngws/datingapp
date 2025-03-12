package com.example.mobile.model.enums;

public enum SmokingHabit {
    HUT_VOI_BAN_BE("Hút thuốc với bạn bè"),
    HUT_KHI_NHAU("Hút thuốc khi nhậu"),
    KHONG_HUT_THUOC("Không hút thuốc"),
    HUT_THUONG_XUYEN("Hút thuốc thường xuyên"),
    CO_GANG_BO("Đang cố gắng bỏ");

    private final String displayName;

    SmokingHabit(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
