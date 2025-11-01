package com.ptit.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "Notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "user_id", length = 50)
    private String userId;
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    
    @Column(name = "is_read")
    private Boolean isRead;
    
    @Column(name = "type", length = 50)
    private String type;
}
