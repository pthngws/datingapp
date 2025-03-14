package com.example.mobile.model.enums;

public enum Gender {
    MALE("Nam"),
    FEMALE("Nữ");

    private final String vietnamese;

    Gender(String vietnamese) {
        this.vietnamese = vietnamese;
    }

    public String getDisplayName() {
        return vietnamese;
    }
}

