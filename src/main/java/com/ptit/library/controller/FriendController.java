package com.ptit.library.controller;

import com.ptit.library.service.FriendService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

	private final FriendService friendService;

	private String requireLogin(HttpSession session) {
		Object u = session.getAttribute("username");
		return u == null ? null : u.toString();
	}

	@GetMapping("/list")
	public ResponseEntity<?> listFriends(HttpSession session) {
		String me = requireLogin(session);
		if (me == null) return ResponseEntity.status(401).body("Vui lòng đăng nhập");
		return ResponseEntity.ok(friendService.listFriends(me));
	}

	@GetMapping("/pending/received")
	public ResponseEntity<?> listPendingReceived(HttpSession session) {
		String me = requireLogin(session);
		if (me == null) return ResponseEntity.status(401).body("Vui lòng đăng nhập");
		return ResponseEntity.ok(friendService.listPendingReceived(me));
	}

	@GetMapping("/pending/sent")
	public ResponseEntity<?> listPendingSent(HttpSession session) {
		String me = requireLogin(session);
		if (me == null) return ResponseEntity.status(401).body("Vui lòng đăng nhập");
		return ResponseEntity.ok(friendService.listPendingSent(me));
	}

	@PostMapping("/request")
	public ResponseEntity<?> requestFriend(@RequestParam("to") String to, HttpSession session) {
		String me = requireLogin(session);
		if (me == null) return ResponseEntity.status(401).body("Vui lòng đăng nhập");
		try {
			friendService.requestFriend(me, to);
			return ResponseEntity.ok("OK");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/accept")
	public ResponseEntity<?> acceptFriend(@RequestParam("of") String of, HttpSession session) {
		String me = requireLogin(session);
		if (me == null) return ResponseEntity.status(401).body("Vui lòng đăng nhập");
		try {
			friendService.acceptFriend(me, of);
			return ResponseEntity.ok("OK");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/decline")
	public ResponseEntity<?> declineFriend(@RequestParam("of") String of, HttpSession session) {
		String me = requireLogin(session);
		if (me == null) return ResponseEntity.status(401).body("Vui lòng đăng nhập");
		try {
			friendService.declineFriend(me, of);
			return ResponseEntity.ok("OK");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/remove")
	public ResponseEntity<?> removeFriend(@RequestParam("user") String user, HttpSession session) {
		String me = requireLogin(session);
		if (me == null) return ResponseEntity.status(401).body("Vui lòng đăng nhập");
		try {
			friendService.removeFriend(me, user);
			return ResponseEntity.ok("OK");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/block")
	public ResponseEntity<?> blockUser(@RequestParam("user") String user, HttpSession session) {
		String me = requireLogin(session);
		if (me == null) return ResponseEntity.status(401).body("Vui lòng đăng nhập");
		try {
			friendService.blockUser(me, user);
			return ResponseEntity.ok("OK");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/unblock")
	public ResponseEntity<?> unblockUser(@RequestParam("user") String user, HttpSession session) {
		String me = requireLogin(session);
		if (me == null) return ResponseEntity.status(401).body("Vui lòng đăng nhập");
		try {
			friendService.unblockUser(me, user);
			return ResponseEntity.ok("OK");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@GetMapping("/is-friends")
	public ResponseEntity<?> isFriends(@RequestParam("user") String user, HttpSession session) {
		String me = requireLogin(session);
		if (me == null) return ResponseEntity.status(401).body("Vui lòng đăng nhập");
		return ResponseEntity.ok(Map.of("friends", friendService.isFriends(me, user)));
	}
}
