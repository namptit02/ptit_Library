package com.ptit.library.controller;

import com.google.gson.Gson;
import com.ptit.library.model.User;
import com.ptit.library.model.ValidationResponse;
import com.ptit.library.service.UserService;
import com.ptit.library.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private Gson gson;

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<String> register(@RequestParam String username,
                                          @RequestParam String password,
                                          @RequestParam String email,
                                          @RequestParam String retypePassword,
                                          @RequestParam(required = false) String done) {
        
        String role = "USER";

        ValidationResponse result = userService.validateUser(username, email, password, retypePassword);
        
        // Check if account already exists
        if (userService.checkAccountExists(username)) {
            result.setValid(false);
            result.setGlobalMessage("Tài khoản đã tồn tại");
        }
        
        if (result.isValid() && "register".equals(done)) {
            User user = new User(username, password, email, role);
            
            if (userService.registerUser(user)) {
                result.setGlobalMessage("Đăng ký thành công");
            } else {
                result.setGlobalMessage("Mã Sinh Viên và Email không khớp");
                result.setValid(false);
            }
        }
        
        String json = gson.toJson(result);
        return ResponseEntity.ok()
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(json);
    }
    
    @GetMapping("/profile")
    public String profile(Model model) {
        String username = SecurityUtil.getAuthenticatedUsername();
        Optional<User> userOpt = userService.findUserWithStudentInfo(username);
        if(userOpt.isEmpty()) {
            userOpt = userService.findByUsername(username);
        }
        userOpt.ifPresent(user -> model.addAttribute("user", user));

        return "user";
    }
    
    @PostMapping("/update-personal-info")
    @ResponseBody
    public ResponseEntity<String> updatePersonalInfo(@RequestParam(required = false) String fullName,
                                                    @RequestParam(required = false) String dateOfBirth,
                                                    @RequestParam(required = false) String gender,
                                                    @RequestParam(required = false) String phone,
                                                    @RequestParam(required = false) String address) {
        ValidationResponse response = new ValidationResponse();
        String username = SecurityUtil.getAuthenticatedUsername();
        
        try {
            LocalDate dob = null;
            if (dateOfBirth != null && !dateOfBirth.trim().isEmpty()) {
                dob = LocalDate.parse(dateOfBirth);
            }
            
            boolean updated = userService.updatePersonalInfo(username, fullName, dob, gender, phone, address);
            if (updated) {
                response.setValid(true);
                response.setGlobalMessage("Cập nhật thông tin cá nhân thành công");
            } else {
                response.setValid(false);
                response.setGlobalMessage("Cập nhật thông tin cá nhân thất bại");
            }
        } catch (Exception e) {
            response.setValid(false);
            response.setGlobalMessage("Định dạng dữ liệu không hợp lệ: " + e.getMessage());
        }
        
        String json = gson.toJson(response);
        return ResponseEntity.ok()
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(json);
    }

    @PostMapping("/change-password")
    @ResponseBody
    public ResponseEntity<String> changePassword(@RequestParam String oldPassword,
                                               @RequestParam String newPassword,
                                               @RequestParam String retypePassword) {
        ValidationResponse response = new ValidationResponse();
        String username = SecurityUtil.getAuthenticatedUsername();
        
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            response.setValid(false);
            response.setGlobalMessage("Mật khẩu cũ không được để trống");
        } else if (newPassword == null || newPassword.trim().isEmpty()) {
            response.setValid(false);
            response.setGlobalMessage("Mật khẩu mới không được để trống");
        } else if (newPassword.length() < 6) {
            response.setValid(false);
            response.setGlobalMessage("Mật khẩu mới phải có ít nhất 6 ký tự");
        } else if (!newPassword.equals(retypePassword)) {
            response.setValid(false);
            response.setGlobalMessage("Mật khẩu nhập lại không khớp");
        } else {
            boolean changed = userService.changePassword(username, oldPassword, newPassword);
            if (changed) {
                response.setValid(true);
                response.setGlobalMessage("Đổi mật khẩu thành công");
            } else {
                response.setValid(false);
                response.setGlobalMessage("Mật khẩu cũ không chính xác");
            }
        }
        
        String json = gson.toJson(response);
        return ResponseEntity.ok()
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(json);
    }

    @PostMapping("/avatar")
    public String uploadAvatar(@RequestParam("avatar") MultipartFile file, HttpServletRequest request) {
        String username = SecurityUtil.getAuthenticatedUsername();
        
        if (!file.isEmpty()) {
            try {
                String uploadDir = "src/main/resources/static/images/avatars/";
                Path uploadPath = Paths.get(uploadDir);
                
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                String fileName = username + "_" + System.currentTimeMillis() + ".jpg";
                Path filePath = uploadPath.resolve(fileName);
                
                Files.copy(file.getInputStream(), filePath);
                
                String avatarPath = "/images/avatars/" + fileName;
                userService.updateAvatar(username, avatarPath);
                
                HttpSession session = request.getSession();
                session.setAttribute("avatar", avatarPath);
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return "redirect:/user/profile";
    }
}
