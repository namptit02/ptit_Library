package com.ptit.library.service;

import com.ptit.library.dto.FriendEvent;
import com.ptit.library.model.Friendship;
import com.ptit.library.model.User;
import com.ptit.library.repository.FriendshipRepository;
import com.ptit.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {


	private final FriendshipRepository friendshipRepository;
	private final UserRepository userRepository;
	private final SimpMessagingTemplate messagingTemplate;

	private void sendFriendEvent(String username, FriendEvent evt) {
		messagingTemplate.convertAndSendToUser(username, "/queue/friends", evt);
	}

	public boolean isFriends(String a, String b) {
		return friendshipRepository.isFriends(a, b);
	}

	public List<String> listFriends(String me) {
		List<Friendship> list = friendshipRepository.findAcceptedByUser(me);
		return list.stream()
				.map(f -> f.getUserId().equals(me) ? f.getFriendId() : f.getUserId())
				.sorted(String.CASE_INSENSITIVE_ORDER)
				.collect(Collectors.toList());
	}

	public List<String> listPendingReceived(String me) {
		return friendshipRepository.findPendingReceived(me).stream()
				.map(Friendship::getUserId) // người gửi yêu cầu
				.sorted(String.CASE_INSENSITIVE_ORDER)
				.collect(Collectors.toList());
	}

	public List<String> listPendingSent(String me) {
		return friendshipRepository.findPendingSent(me).stream()
				.map(Friendship::getFriendId) // người mình đã gửi tới
				.sorted(String.CASE_INSENSITIVE_ORDER)
				.collect(Collectors.toList());
	}

	@Transactional
	public void requestFriend(String me, String other) {
		if (me.equalsIgnoreCase(other)) {
			throw new IllegalArgumentException("Không thể kết bạn với chính mình");
		}
		userRepository.findByUsername(me).orElseThrow(() -> new IllegalArgumentException("User không tồn tại: " + me));
		userRepository.findByUsername(other).orElseThrow(() -> new IllegalArgumentException("User không tồn tại: " + other));

		Optional<Friendship> pairOpt = friendshipRepository.findPair(me, other);
		if (pairOpt.isPresent()) {
			Friendship f = pairOpt.get();
			switch (f.getStatus()) {
				case "accepted" -> { throw new IllegalStateException("Hai bên đã là bạn"); }
				case "pending" -> {
					if (other.equalsIgnoreCase(f.getUserId())) {
						// auto-accept nếu chiều ngược lại đã gửi
						acceptFriend(me, other);
						return;
					} else {
						throw new IllegalStateException("Bạn đã gửi lời mời, chờ đối phương chấp nhận");
					}
				}
				case "blocked" -> { throw new IllegalStateException("Quan hệ đang bị block"); }
				default -> {}
			}
		}

		Friendship created = Friendship.builder()
				.userId(me)
				.friendId(other)
				.status("pending")
				.requestedBy(me)
				.createdAt(Timestamp.from(Instant.now()))
				.build();
		friendshipRepository.save(created);

		sendFriendEvent(other, FriendEvent.builder()
				.type("REQUEST_RECEIVED")
				.from(me).to(other)
				.at(LocalDateTime.now())
				.build());
	}

	@Transactional
	public void acceptFriend(String me, String other) {
		Friendship f = friendshipRepository.findPair(me, other)
				.orElseThrow(() -> new IllegalStateException("Không có lời mời kết bạn"));
		if (!"pending".equalsIgnoreCase(f.getStatus())) {
			throw new IllegalStateException("Trạng thái không hợp lệ để chấp nhận");
		}
		boolean iAmReceiver = me.equalsIgnoreCase(f.getFriendId());
		if (!iAmReceiver) {
			throw new IllegalStateException("Chỉ người nhận mới có thể chấp nhận");
		}

		f.setStatus("accepted");
		Timestamp now = Timestamp.from(Instant.now());
		f.setRespondedAt(now);
		f.setLastInteractionAt(now);
		friendshipRepository.save(f);

		sendFriendEvent(other, FriendEvent.builder()
				.type("REQUEST_ACCEPTED")
				.from(me).to(other)
				.at(LocalDateTime.now())
				.build());
		sendFriendEvent(me, FriendEvent.builder()
				.type("REQUEST_ACCEPTED")
				.from(other).to(me)
				.at(LocalDateTime.now())
				.build());
	}

	@Transactional
	public void declineFriend(String me, String other) {
		Friendship f = friendshipRepository.findPair(me, other)
				.orElseThrow(() -> new IllegalStateException("Không có lời mời kết bạn"));
		if (!"pending".equalsIgnoreCase(f.getStatus())) {
			throw new IllegalStateException("Trạng thái không hợp lệ để từ chối");
		}
		boolean iAmReceiver = me.equalsIgnoreCase(f.getFriendId());
		if (!iAmReceiver) {
			throw new IllegalStateException("Chỉ người nhận mới có thể từ chối");
		}
		friendshipRepository.delete(f);

		sendFriendEvent(other, FriendEvent.builder()
				.type("REQUEST_DECLINED")
				.from(me).to(other)
				.at(LocalDateTime.now())
				.build());
	}

	@Transactional
	public void removeFriend(String me, String other) {
		Friendship f = friendshipRepository.findPair(me, other)
				.orElseThrow(() -> new IllegalStateException("Hai bên chưa có quan hệ"));
		if (!"accepted".equalsIgnoreCase(f.getStatus())) {
			throw new IllegalStateException("Chỉ có thể hủy bạn khi đang ở trạng thái accepted");
		}
		friendshipRepository.delete(f);

		sendFriendEvent(other, FriendEvent.builder()
				.type("UNFRIENDED")
				.from(me).to(other)
				.at(LocalDateTime.now())
				.build());
	}

	@Transactional
	public void blockUser(String me, String other) {
		Optional<Friendship> pairOpt = friendshipRepository.findPair(me, other);
		Friendship f = pairOpt.orElseGet(() -> Friendship.builder().userId(me).friendId(other).build());
		f.setStatus("blocked");
		Timestamp now = Timestamp.from(Instant.now());
		if (f.getCreatedAt() == null) f.setCreatedAt(now);
		f.setRespondedAt(now);
		f.setLastInteractionAt(now);
		friendshipRepository.save(f);

		sendFriendEvent(other, FriendEvent.builder()
				.type("BLOCKED")
				.from(me).to(other)
				.at(LocalDateTime.now())
				.build());
	}

	@Transactional
	public void unblockUser(String me, String other) {
		Friendship f = friendshipRepository.findPair(me, other)
				.orElseThrow(() -> new IllegalStateException("Không có bản ghi block"));
		if (!"blocked".equalsIgnoreCase(f.getStatus())) {
			throw new IllegalStateException("Quan hệ hiện không ở trạng thái blocked");
		}
		friendshipRepository.delete(f);

		sendFriendEvent(other, FriendEvent.builder()
				.type("UNBLOCKED")
				.from(me).to(other)
				.at(LocalDateTime.now())
				.build());
	}
}
