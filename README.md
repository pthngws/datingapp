# Ứng Dụng Hẹn Hò - Spring Boot & MongoDB

## Mục Lục
- [Giới Thiệu](#giới-thiệu)
- [Công Nghệ Sử Dụng](#công-nghệ-sử-dụng)
- [Cài Đặt](#cài-đặt)
  - [Cấu Hình Ứng Dụng](#cấu-hình-ứng-dụng)
  - [Chạy Ứng Dụng](#chạy-ứng-dụng)
- [Các Chức Năng](#các-chức-năng)
- [Cấu Trúc Dự Án](#cấu-trúc-dự-án)
- [Yêu Cầu Hệ Thống](#yêu-cầu-hệ-thống)
- [Liên Hệ & Đóng Góp](#liên-hệ--đóng-góp)

## Giới Thiệu
Ứng dụng hẹn hò này được phát triển nhằm tạo ra một nền tảng giúp người dùng có thể kết nối và tìm kiếm đối tượng phù hợp. Với hệ thống gợi ý thông minh, nhắn tin trực tiếp và bảo mật cao, ứng dụng đảm bảo trải nghiệm mượt mà và an toàn cho người dùng.

## Công Nghệ Sử Dụng
- **Backend**: Spring Boot
- **Database**: MongoDB
- **Cache**: Redis
- **Bảo mật**: Spring Security & JWT
- **Tài liệu API**: Swagger

## Cài Đặt

### Cấu Hình Ứng Dụng
1. Clone repository về máy:
   ```bash
   git clone https://github.com/pthngws/datingapp.git
   cd datingapp
   ```
2. Cấu hình MongoDB trong `application.properties`:
   ```properties
   spring.data.mongodb.uri=mongodb://localhost:27017/datingapp
   ```
3. Cấu hình Redis (nếu sử dụng):
   ```properties
   spring.redis.host=localhost
   spring.redis.port=6379
   ```

### Chạy Ứng Dụng
Chạy ứng dụng bằng Maven:
```bash
mvn spring-boot:run
```

Truy cập Swagger UI tại: `http://localhost:8080/swagger-ui.html`

## Các Chức Năng

### 1. Xác Thực & Quản Lý Người Dùng
- Đăng ký, đăng nhập bằng JWT.
- Cập nhật thông tin cá nhân.

### 2. Gợi Ý Kết Nối
- Gợi ý đối tượng dựa trên bộ lọc.

### 3. Hệ Thống Nhắn Tin
- Nhắn tin thời gian thực giữa các người dùng.
- Lưu trữ lịch sử trò chuyện.

### 4. Like & Match
- Người dùng có thể thích hoặc bỏ qua hồ sơ khác.
- Khi cả hai cùng thích, hệ thống tạo kết nối "match".

## Cấu Trúc Dự Án
```
src/
├── main/
│   ├── java/com/example/mobile/       # Backend logic (Controllers, Services, Repositories)
│   ├── resources/
│   │   ├── templates/                    # Giao diện người dùng (HTML)
│   │   ├── static/                       # File tĩnh (CSS, JS, hình ảnh)
│   │   ├── application.properties        # File cấu hình ứng dụng                 
```

## Yêu Cầu Hệ Thống
- **Java**: 17+
- **MongoDB**: 4.4+
- **Maven**: 4+
- **Redis**: (khuyến khích, để tối ưu hiệu suất)
- **Docker**: (tùy chọn, giúp triển khai dễ dàng hơn)

## Liên Hệ & Đóng Góp
Mọi đóng góp đều được chào đón! Nếu bạn muốn cải thiện dự án này, hãy fork repository và gửi pull request.

📩 Liên hệ: pthngws@gmail.com

Hy vọng bạn sẽ thích dự án này! 🚀

