package com.ptit.library.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(
		name = "Friendships",
		indexes = {
				@Index(name = "idx_friendships_status_user", columnList = "user_id,status"),
				@Index(name = "idx_friendships_status_friend", columnList = "friend_id,status")
		},
		uniqueConstraints = {
				@UniqueConstraint(name = "uq_friendships_unordered", columnNames = {"u_min","u_max"})
		}
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
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

	@Column(name = "u_min", insertable = false, updatable = false)
	private String uMin;

	@Column(name = "u_max", insertable = false, updatable = false)
	private String uMax;
}
