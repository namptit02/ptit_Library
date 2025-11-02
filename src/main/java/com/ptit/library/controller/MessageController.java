package com.ptit.library.controller;

import com.ptit.library.dto.ChatPayload;
import com.ptit.library.model.Message;
import com.ptit.library.service.MessageService;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public String messagePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/auth";
        return "message";
    }

    // Trả Message (Jackson serialize)
    @PostMapping("/send")
    @ResponseBody
    public ResponseEntity<Message> sendMessage(@RequestParam String receiverId,
                                               @RequestParam String content,
                                               HttpSession session) {
        String senderId = (String) session.getAttribute("username");
        if (senderId == null) return ResponseEntity.status(401).build();

        Message saved = messageService.sendMessage(senderId, receiverId, content);

        // bắn realtime cho 2 bên
        ChatPayload payload = ChatPayload.from(saved, null);
        messagingTemplate.convertAndSendToUser(receiverId, "/queue/messages", payload);
        messagingTemplate.convertAndSendToUser(senderId,   "/queue/messages", payload);

        return ResponseEntity.ok(saved);
    }

    // Trả List<Message>
    @GetMapping("/conversation")
    @ResponseBody
    public ResponseEntity<List<Message>> getConversation(@RequestParam String userId,
                                                         HttpSession session) {
        String currentUser = (String) session.getAttribute("username");
        if (currentUser == null) return ResponseEntity.status(401).build();

        List<Message> messages = messageService.getConversation(currentUser, userId);
        return ResponseEntity.ok(messages);
    }

    // Trả List<Message> chưa đọc
    @GetMapping("/unread")
    @ResponseBody
    public ResponseEntity<List<Message>> getUnreadMessages(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(messageService.getUnreadMessages(username));
    }

    // Mark read
    @PostMapping("/read")
    @ResponseBody
    public ResponseEntity<Void> markRead(@RequestParam Integer id, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return ResponseEntity.status(401).build();
        messageService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    // ====== STOMP: nhận từ FE, lưu DB, đẩy realtime ======
    @MessageMapping("/chat.private")
    public void handlePrivate(@Payload ChatPayload incoming, Principal principal) {
        String sender = (principal != null ? principal.getName() : incoming.getSender());
        String receiver = incoming.getReceiver();

        Message saved = messageService.sendMessage(sender, receiver, incoming.getContent());

        ChatPayload out = ChatPayload.from(saved, incoming.getClientMsgId());
        messagingTemplate.convertAndSendToUser(receiver, "/queue/messages", out);
        messagingTemplate.convertAndSendToUser(sender,   "/queue/messages", out);
    }


}
