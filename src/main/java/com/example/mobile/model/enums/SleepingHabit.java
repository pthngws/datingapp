package com.example.mobile.model.enums;

public enum SleepingHabit {
    DAY_SOM("Dậy sớm"),
    CU_DEM("Cú đêm"),
    GIO_LINH_HOAT("Giờ giấc linh hoạt");

    private final String displayName;

    SleepingHabit(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
