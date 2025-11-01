# PTIT Library System - Spring Boot

Hệ thống quản lý thư viện PTIT được xây dựng bằng Spring Boot MVC, chuyển đổi từ dự án Servlet ban đầu.

## 🚀 Công nghệ sử dụng

- **Spring Boot 3.2.0**
- **Spring MVC** - Web framework
- **Spring Data JPA** - ORM và database access
- **Spring Security** - Authentication & Authorization
- **Thymeleaf** - Template engine (thay thế JSP)
- **Spring WebSocket** - Real-time messaging
- **SQL Server** - Database
- **Maven** - Build tool
- **Lombok** - Giảm boilerplate code

## 📁 Cấu trúc dự án

```
PTITLibrary-SpringBoot/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ptit/library/
│   │   │       ├── PTITLibraryApplication.java    # Main class
│   │   │       ├── config/                        # Configuration classes
│   │   │       │   ├── SecurityConfig.java        # Spring Security
│   │   │       │   ├── WebConfig.java             # Web configuration
│   │   │       │   └── WebSocketConfig.java       # WebSocket config
│   │   │       ├── controller/                    # Spring MVC Controllers
│   │   │       │   ├── HomeController.java
│   │   │       │   ├── UserController.java
│   │   │       │   ├── BookController.java
│   │   │       │   ├── BorrowController.java
│   │   │       │   ├── MessageController.java
│   │   │       │   └── NotificationController.java
│   │   │       ├── service/                       # Business logic layer
│   │   │       │   ├── UserService.java
│   │   │       │   ├── BookService.java
│   │   │       │   ├── BorrowService.java
│   │   │       │   ├── MessageService.java
│   │   │       │   └── NotificationService.java
│   │   │       ├── repository/                    # Data access layer
│   │   │       │   ├── UserRepository.java
│   │   │       │   ├── BookRepository.java
│   │   │       │   ├── BookRecordRepository.java
│   │   │       │   ├── MessageRepository.java
│   │   │       │   └── NotificationRepository.java
│   │   │       └── model/                         # Entity classes
│   │   │           ├── User.java
│   │   │           ├── Book.java
│   │   │           ├── BookRecord.java
│   │   │           ├── Message.java
│   │   │           ├── Notification.java
│   │   │           └── ValidationResponse.java
│   │   └── resources/
│   │       ├── application.properties             # Application config
│   │       ├── static/                            # Static resources
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   ├── images/
│   │       │   └── data/
│   │       └── templates/                         # Thymeleaf templates
│   │           ├── auth.html
│   │           ├── home.html
│   │           ├── borrow.html
│   │           ├── message.html
│   │           └── ...
└── pom.xml                                        # Maven configuration
```

## ⚙️ Cài đặt và Chạy

### Yêu cầu hệ thống

- JDK 17 hoặc cao hơn
- Maven 3.6+
- SQL Server (đã cài đặt và có database PtitLibrary)
- IDE: IntelliJ IDEA / Eclipse / VS Code

### Các bước cài đặt

#### 1. Clone hoặc copy dự án

```bash
cd PTITLibrary-SpringBoot
```

#### 2. Cấu hình database

Mở file `src/main/resources/application.properties` và chỉnh sửa thông tin kết nối database:

```properties
spring.datasource.url=jdbc:sqlserver://YOUR_SERVER_NAME:1433;databaseName=PtitLibrary;encrypt=false;trustServerCertificate=true
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

#### 3. Build và chạy project

**Sử dụng Maven:**

```bash
# Build project
mvn clean install

# Chạy ứng dụng
mvn spring-boot:run
```

**Hoặc từ IDE:**

- Run `PTITLibraryApplication.java` trực tiếp

#### 4. Truy cập ứng dụng

Mở trình duyệt và truy cập:
```
http://localhost:8080
```

## 🔑 Tính năng chính

### 1. Xác thực & Phân quyền
- Đăng ký tài khoản mới
- Đăng nhập/Đăng xuất
- Session management
- Password encryption (BCrypt)

### 2. Quản lý Sách
- Tìm kiếm sách theo từ khóa
- Lọc và sắp xếp sách
- Xem thông tin chi tiết sách

### 3. Mượn Sách
- Đăng ký mượn sách
- Xem lịch sử mượn sách
- Theo dõi trạng thái mượn

### 4. Tin nhắn Real-time
- Gửi/nhận tin nhắn
- WebSocket support
- Xem danh sách hội thoại

### 5. Thông báo
- Nhận thông báo hệ thống
- Đánh dấu đã đọc
- Đếm số thông báo chưa đọc

## 📡 API Endpoints

### User Management
- `POST /user/login` - Đăng nhập
- `POST /user/register` - Đăng ký
- `POST /user/logout` - Đăng xuất
- `GET /user/profile` - Xem profile

### Book Management
- `GET /book/search?keyword={keyword}` - Tìm kiếm sách
- `GET /book/home` - Trang chủ sách
- `GET /book/borrow` - Trang mượn sách

### Borrow Management
- `POST /borrow` - Mượn sách (body: int[] bookIds)
- `GET /borrow/records` - Xem lịch sử mượn

### Message Management
- `GET /message` - Trang tin nhắn
- `POST /message/send` - Gửi tin nhắn
- `GET /message/conversation?userId={userId}` - Lấy hội thoại
- `GET /message/unread` - Tin nhắn chưa đọc

### Notification Management
- `GET /notification` - Lấy tất cả thông báo
- `GET /notification/unread` - Thông báo chưa đọc
- `GET /notification/count` - Đếm thông báo chưa đọc
- `POST /notification/read/{id}` - Đánh dấu đã đọc
- `POST /notification/read-all` - Đánh dấu tất cả đã đọc

## 🔐 Spring Security Configuration

Spring Security đã được cấu hình với:
- Form-based authentication
- BCrypt password encoding
- Session management
- CSRF protection (disabled cho API)
- Public endpoints: `/`, `/auth`, `/user/login`, `/user/register`, `/css/**`, `/js/**`

## 🗃️ Database Schema

Dự án sử dụng các bảng chính:
- **Users** - Thông tin người dùng
- **Students** - Thông tin sinh viên
- **Books** - Thông tin sách
- **BorrowRecords** - Lịch sử mượn sách
- **Messages** - Tin nhắn
- **Notifications** - Thông báo

## 🐛 Troubleshooting

### Lỗi kết nối database
- Kiểm tra SQL Server đang chạy
- Kiểm tra thông tin kết nối trong `application.properties`
- Kiểm tra firewall cho port 1433

### Lỗi 404 Not Found
- Kiểm tra static resources đã được copy đúng vị trí
- Kiểm tra URL mapping trong Controller

### Lỗi template không tìm thấy
- Kiểm tra file .html nằm trong `src/main/resources/templates/`
- Kiểm tra tên file khớp với return value trong Controller

### Lỗi CSS/JS không load
- Kiểm tra file nằm trong `src/main/resources/static/`
- Kiểm tra syntax Thymeleaf: `th:src="@{/css/style.css}"`

## 📚 Tài liệu tham khảo

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- [Thymeleaf](https://www.thymeleaf.org/documentation.html)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Security](https://spring.io/projects/spring-security)

## 📝 TODO

- [ ] Test đầy đủ các chức năng
- [ ] Thêm unit tests
- [ ] Thêm API documentation (Swagger)

