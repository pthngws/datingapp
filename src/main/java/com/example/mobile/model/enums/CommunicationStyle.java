package com.example.mobile.model.enums;

public enum CommunicationStyle {
    NGHIEN_NHAN_TIN("Nghiện nhắn tin"),
    THICH_GOI_DIEN("Thích gọi điện"),
    THICH_GOI_VIDEO("Thích gọi video"),
    IT_NHAN_TIN("Ít nhắn tin"),
    THICH_GAP_MAT_TRUC_TIEP("Thích gặp mặt trực tiếp");

    private final String tenTiengViet;

    CommunicationStyle(String tenTiengViet) {
        this.tenTiengViet = tenTiengViet;
    }

    public String getDisplayName() {
        return tenTiengViet;
    }
}
