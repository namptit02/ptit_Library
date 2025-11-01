package com.ptit.library.service;

import com.ptit.library.model.Book;
import com.ptit.library.model.BookRecord;
import com.ptit.library.repository.BookRecordRepository;
import com.ptit.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private BookRecordRepository bookRecordRepository;
    
    @Transactional
    public List<Integer> borrowBooks(String username, int[] bookIds) {
        LocalDate today = LocalDate.now();
        List<Integer> failedBooks = new ArrayList<>();
        
        for (int bookId : bookIds) {
            Optional<Book> bookOpt = bookRepository.findById(bookId);
            
            if (bookOpt.isPresent()) {
                Book book = bookOpt.get();
                
                // Check if book is available
                if (book.getCopiesAvailable() > 0) {
                    // Update book availability
                    book.setCopiesAvailable(book.getCopiesAvailable() - 1);
                    bookRepository.save(book);
                    
                    // Create borrow record
                    BookRecord record = new BookRecord();
                    record.setStudentId(username);
                    record.setBookId(bookId);
                    record.setBorrowDate(today);
                    record.setStatus("Đang chờ");
                    bookRecordRepository.save(record);
                } else {
                    failedBooks.add(bookId);
                }
            } else {
                failedBooks.add(bookId);
            }
        }
        
        return failedBooks;
    }
    
    public List<BookRecord> getRecordsByStudentId(String studentId) {
        return bookRecordRepository.findByStudentId(studentId);
    }
    
    public List<Object[]> getRecordsWithBookInfo(String studentId) {
        return bookRecordRepository.findRecordsWithBookInfo(studentId);
    }
}
