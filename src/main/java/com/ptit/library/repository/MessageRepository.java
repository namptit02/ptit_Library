package com.ptit.library.repository;

import com.ptit.library.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    
    @Query("SELECT m FROM Message m WHERE " +
           "(m.senderId = :userId OR m.receiverId = :userId) " +
           "ORDER BY m.timestamp DESC")
    List<Message> findMessagesByUserId(@Param("userId") String userId);
    
    @Query("SELECT m FROM Message m WHERE " +
           "(m.senderId = :user1 AND m.receiverId = :user2) OR " +
           "(m.senderId = :user2 AND m.receiverId = :user1) " +
           "ORDER BY m.timestamp ASC")
    List<Message> findConversation(@Param("user1") String user1, @Param("user2") String user2);
    
    List<Message> findByReceiverIdAndIsReadFalse(String receiverId);
}
