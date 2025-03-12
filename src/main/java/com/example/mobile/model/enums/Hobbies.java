package com.example.mobile.model.enums;

public enum Hobbies {
    BONG_DA("Bóng đá"),
    LAN("Lặn"),
    BONG_RO("Bóng rổ"),
    TOUR_DI_BO("Tour đi bộ"),
    CAU_LONG("Cầu lông"),
    SUOI_NUOC_NONG("Suối nước nóng"),
    DAT_CHO_DI_DAO("Dắt chó đi dạo"),
    PHUOT("Phượt"),
    DU_LICH("Du lịch"),
    LUOT_SONG("Lướt sóng"),
    DU_LUON("Dù lượn"),
    CAU_CA("Câu cá"),
    CAM_TRAI("Cắm trại"),
    HOAT_DONG_NGOAI_TROI("Hoạt động ngoài trời"),
    PICNIC("Picnic"),
    MANGA("Manga"),
    MARVEL("Marvel"),
    DISNEY("Disney"),
    THE_THAO_DIEN_TU("Thể thao điện tử"),
    LIEN_MINH_HUYEN_THOAI("Liên Minh Huyền Thoại"),
    DI_TINH_NGUYEN("Đi tình nguyện"),
    DOC_SACH("Đọc sách"),
    GIAI_DO("Giải đố"),
    XEM_PHIM("Xem phim"),
    TAP_GYM("Tập gym"),
    NAU_AN("Nấu ăn"),
    GAME_ONLINE("Game online"),
    MUA_SAM("Mua sắm"),
    LAM_VUON("Làm vườn"),
    TIKTOK("Tiktok"),
    INSTAGRAM("Instagram"),
    NETFLIX("Netflix"),
    COSPLAY("Cosplay"),
    HAT("Hát"),
    NHAC_CU("Nhạc cụ"),
    NHAY("Nhảy"),
    TAROT("Tarot"),
    VE("Vẽ"),
    TRANG_DIEM("Trang điểm"),
    ANIME("Anime"),
    PHIM_KINH_DI("Phim kinh dị"),
    DAP_XE("Đạp xe"),
    BOI_LOI("Bơi lội"),
    TRA_SUA("Trà sữa"),
    CAFE("Café"),
    RUOU("Rượu"),
    LE_HOI("Lễ hội"),
    XE_HOI("Xe hơi"),
    DI_CHOI_DEM("Đi chơi đêm"),
    BOWLING("Bowling");

    private final String displayName;

    Hobbies(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
