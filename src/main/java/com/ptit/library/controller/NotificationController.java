package com.ptit.library.controller;

import com.google.gson.Gson;
import com.ptit.library.model.Notification;
import com.ptit.library.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/notification")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private Gson gson;
    
    @GetMapping
    @ResponseBody
    public ResponseEntity<String> getNotifications(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(401)
                    .body("Vui lòng đăng nhập");
        }
        
        List<Notification> notifications = notificationService.getNotificationsByUserId(username);
        String json = gson.toJson(notifications);
        
        return ResponseEntity.ok()
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(json);
    }
    
    @GetMapping("/unread")
    @ResponseBody
    public ResponseEntity<String> getUnreadNotifications(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(401)
                    .body("Vui lòng đăng nhập");
        }
        
        List<Notification> notifications = notificationService.getUnreadNotifications(username);
        String json = gson.toJson(notifications);
        
        return ResponseEntity.ok()
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(json);
    }
    
    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<Long> getUnreadCount(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(401).build();
        }
        
        long count = notificationService.countUnreadNotifications(username);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping("/read/{id}")
    @ResponseBody
    public ResponseEntity<String> markAsRead(@PathVariable Integer id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok("Đã đánh dấu đã đọc");
    }
    
    @PostMapping("/read-all")
    @ResponseBody
    public ResponseEntity<String> markAllAsRead(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(401)
                    .body("Vui lòng đăng nhập");
        }
        
        notificationService.markAllAsRead(username);
        return ResponseEntity.ok("Đã đánh dấu tất cả là đã đọc");
    }
}
