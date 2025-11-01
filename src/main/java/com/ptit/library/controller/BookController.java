package com.ptit.library.controller;

import com.google.gson.Gson;
import com.ptit.library.model.Book;
import com.ptit.library.model.User;
import com.ptit.library.service.BookService;
import com.ptit.library.service.UserService;
import com.ptit.library.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/book")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private Gson gson;
    
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<String> searchBooks(@RequestParam String keyword,
                                             @RequestParam(defaultValue = "") String filter1,
                                             @RequestParam(defaultValue = "") String filter2,
                                             @RequestParam(defaultValue = "") String filter3) {
        
        List<Book> books = bookService.searchBooks(keyword, filter1, filter2, filter3);
        
        String json = gson.toJson(books);
        return ResponseEntity.ok()
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(json);
    }
    
    @GetMapping("/home")
    public String home() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/auth";
        }
        
        return "home";
    }
    
    @GetMapping("/borrow")
    public String borrowPage(Model model) {
        String username = SecurityUtil.getAuthenticatedUsername();
        Optional<User> userOpt = userService.findUserWithStudentInfo(username);
        if(!userOpt.isPresent()) {
            userOpt = userService.findByUsername(username);
        }
        
        long registerNo = System.currentTimeMillis() % 999999;
        LocalDate today = java.time.LocalDate.now();
        LocalDate dueDate = today.plusDays(30);
        DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        model.addAttribute("registerNo", registerNo);
        model.addAttribute("registerDate", today.format(formatter));
        model.addAttribute("dueDate", dueDate.format(formatter));
        
        User user = userOpt.get();
        model.addAttribute("user", user);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("firstName", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("avatar", user.getAvatar());
        
        return "borrow";
    }
}
