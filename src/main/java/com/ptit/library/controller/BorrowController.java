package com.ptit.library.controller;

import com.google.gson.Gson;
import com.ptit.library.service.BorrowService;
import com.ptit.library.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/borrow")
public class BorrowController {
    
    @Autowired
    private BorrowService borrowService;
    
    @Autowired
    private Gson gson;
    
    @PostMapping
    @ResponseBody
    public ResponseEntity<String> borrowBooks(@RequestBody int[] bookIds) {

        String username = SecurityUtil.getAuthenticatedUsername();
        if (username == null) {
            return ResponseEntity.status(401)
                    .body("Vui lòng đăng nhập");
        }
        
        List<Integer> failedBooks = borrowService.borrowBooks(username, bookIds);
        
        if (failedBooks.isEmpty()) {
            return ResponseEntity.ok()
                    .header("Content-Type", "text/plain; charset=UTF-8")
                    .body("Đăng ký mượn sách thành công " + username);
        } else {
            return ResponseEntity.ok()
                    .header("Content-Type", "text/plain; charset=UTF-8")
                    .body("Một số sách không thể mượn: " + failedBooks);
        }
    }
    
    @GetMapping("/records")
    @ResponseBody
    public ResponseEntity<String> getRecords() {
        String username = SecurityUtil.getAuthenticatedUsername();
        if (username == null) {
            return ResponseEntity.status(401)
                    .body("Vui lòng đăng nhập");
        }
        
        List<Object[]> records = borrowService.getRecordsWithBookInfo(username);
        String json = gson.toJson(records);
        
        return ResponseEntity.ok()
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(json);
    }
}
