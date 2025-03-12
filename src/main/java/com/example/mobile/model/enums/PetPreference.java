package com.example.mobile.model.enums;

public enum PetPreference {
    CHO("Chó"),
    MEO("Mèo"),
    BO_SAT("Bò sát"),
    CHIM("Chim"),
    HAMSTER("Hamster"),
    THO("Thỏ"),
    KHAC("Khác"),
    KHONG_NUOI_THU_CUNG("Không nuôi thú cưng"),
    TAT_CA("Tất cả các loại thú cưng"),
    YEU_THICH_KHONG_NUOI("Yêu thích nhưng không nuôi");

    private final String displayName;

    PetPreference(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
