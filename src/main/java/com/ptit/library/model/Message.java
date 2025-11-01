package com.ptit.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "Messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "sender_id", length = 50)
    private String senderId;
    
    @Column(name = "receiver_id", length = 50)
    private String receiverId;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    
    @Column(name = "is_read")
    private Boolean isRead;
}
