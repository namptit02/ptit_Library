package com.ptit.library.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(
		name = "Friendships"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friendship {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	// hai đầu quan hệ
	@Column(name = "user_id", length = 20, nullable = false)
	private String userId;

	@Column(name = "friend_id", length = 20, nullable = false)
	private String friendId;

	// pending | accepted | blocked
	@Column(name = "status", length = 20, nullable = false)
	private String status;

	// ai gửi lời mời
	@Column(name = "requested_by", length = 20)
	private String requestedBy;

	@Column(name = "created_at")
	private Timestamp createdAt;

	@Column(name = "responded_at")
	private Timestamp respondedAt;

	@Column(name = "last_interaction_at")
	private Timestamp lastInteractionAt;
}
