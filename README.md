# Ứng Dụng Hẹn Hò - Spring Boot & MongoDB

## Mục Lục
- [Giới Thiệu](#giới-thiệu)
- [Cài Đặt](#cài-đặt)
  - [Cài Đặt Spring Boot](#cài-đặt-spring-boot)
  - [Cài Đặt Cơ Sở Dữ Liệu MongoDB](#cài-đặt-cơ-sở-dữ-liệu-mongodb)
  - [Cấu Hình Redis](#cấu-hình-redis)
- [Các Chức Năng](#các-chức-năng)
- [Cấu Trúc Dự Án](#cấu-trúc-dự-án)
- [Yêu Cầu Hệ Thống](#yêu-cầu-hệ-thống)
- [Cảm Ơn](#cảm-ơn)

## Giới Thiệu

Đây là một ứng dụng hẹn hò được phát triển bằng **Spring Boot** và **MongoDB**. Ứng dụng cho phép người dùng tạo hồ sơ cá nhân, tìm kiếm và kết nối với những người dùng khác thông qua các tính năng như nhắn tin, gợi ý kết bạn và quản lý mối quan hệ.

## Cài Đặt

### Cài Đặt Spring Boot

Clone repository về máy:

```bash
git clone https://github.com/pthngws/datingapp.git
cd datingapp
```

Cài đặt các phụ thuộc bằng Maven:

```bash
mvn install
```

### Cài Đặt Cơ Sở Dữ Liệu MongoDB

1. Cài đặt MongoDB và khởi chạy dịch vụ.
2. Cấu hình kết nối MongoDB trong file `src/main/resources/application.properties`:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/datingapp
```

### Cấu Hình Redis

Nếu sử dụng Redis để cache dữ liệu, đảm bảo Redis đã được cài đặt và chạy.

Cấu hình trong `application.properties`:

```properties
spring.redis.host=localhost
spring.redis.port=6379
```

## Các Chức Năng

### Đăng Ký & Đăng Nhập
- Xác thực bằng JWT.
- Người dùng có thể đăng ký, đăng nhập và quản lý tài khoản.

### Quản Lý Hồ Sơ Cá Nhân
- Cập nhật ảnh đại diện, thông tin cá nhân.

### Gợi Ý Kết Bạn
- Hệ thống đề xuất dựa trên sở thích và vị trí.

### Nhắn Tin
- Hỗ trợ chat thời gian thực giữa các người dùng.

### Like & Match
- Người dùng có thể thích hoặc bỏ qua hồ sơ gợi ý.

## Yêu Cầu Hệ Thống

- **Java**: Phiên bản 17 trở lên.
- **MongoDB**: Phiên bản 4.4 trở lên.
- **Maven**: Phiên bản 3.6 trở lên.
- **Redis**: (Tùy chọn) để tối ưu hiệu suất.

## Cảm Ơn

- Hy vọng bạn sẽ thích dự án này! 🚀

