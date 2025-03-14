package com.example.mobile.model.enums;

public enum ZodiacSign {
    BACH_DUONG("Bạch Dương"),
    KIM_NGUU("Kim Ngưu"),
    SONG_TU("Song Tử"),
    CU_GIAI("Cự Giải"),
    SU_TU("Sư Tử"),
    XU_NU("Xử Nữ"),
    THIEN_BINH("Thiên Bình"),
    BO_CAP("Bọ Cạp"),
    NHAN_MA("Nhân Mã"),
    MA_KET("Ma Kết"),
    BAO_BINH("Bảo Bình"),
    SONG_NGU("Song Ngư");

    private final String tenTiengViet;

    ZodiacSign(String tenTiengViet) {
        this.tenTiengViet = tenTiengViet;
    }

    public String getDisplayName() {
        return tenTiengViet;
    }

    @Override
    public String toString() {
        return tenTiengViet;
    }
}
