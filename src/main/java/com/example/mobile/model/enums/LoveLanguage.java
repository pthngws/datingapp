package com.example.mobile.model.enums;

public enum LoveLanguage {
    HANH_DONG_TINH_TE("Những hành động tinh tế"),
    MON_QUA("Những món quà"),
    CU_CHI_AU_YEM("Những cử chỉ âu yếm"),
    LOI_KHEN("Những lời khen"),
    THOI_GIAN_BEN_NHAU("Thời gian bên nhau");

    private final String displayName;

    LoveLanguage(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
