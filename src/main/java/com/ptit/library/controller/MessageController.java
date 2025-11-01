package com.ptit.library.controller;

import com.google.gson.Gson;
import com.ptit.library.model.Message;
import com.ptit.library.service.MessageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/message")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private Gson gson;
    
    @GetMapping
    public String messagePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/auth";
        }
        
        return "message";
    }
    
    @PostMapping("/send")
    @ResponseBody
    public ResponseEntity<String> sendMessage(@RequestParam String receiverId,
                                             @RequestParam String content,
                                             HttpSession session) {
        
        String senderId = (String) session.getAttribute("username");
        if (senderId == null) {
            return ResponseEntity.status(401)
                    .body("Vui lòng đăng nhập");
        }
        
        Message message = messageService.sendMessage(senderId, receiverId, content);
        String json = gson.toJson(message);
        
        return ResponseEntity.ok()
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(json);
    }
    
    @GetMapping("/conversation")
    @ResponseBody
    public ResponseEntity<String> getConversation(@RequestParam String userId,
                                                 HttpSession session) {
        
        String currentUser = (String) session.getAttribute("username");
        if (currentUser == null) {
            return ResponseEntity.status(401)
                    .body("Vui lòng đăng nhập");
        }
        
        List<Message> messages = messageService.getConversation(currentUser, userId);
        String json = gson.toJson(messages);
        
        return ResponseEntity.ok()
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(json);
    }
    
    @GetMapping("/unread")
    @ResponseBody
    public ResponseEntity<String> getUnreadMessages(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(401)
                    .body("Vui lòng đăng nhập");
        }
        
        List<Message> messages = messageService.getUnreadMessages(username);
        String json = gson.toJson(messages);
        
        return ResponseEntity.ok()
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(json);
    }
}
