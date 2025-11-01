package com.ptit.library.repository;

import com.ptit.library.model.BookRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRecordRepository extends JpaRepository<BookRecord, Integer> {
    
    List<BookRecord> findByStudentId(String studentId);
    
    List<BookRecord> findByStudentIdAndStatus(String studentId, String status);
    
    @Query(value = "SELECT r.student_id, r.id as record_id, b.id as book_id, b.title, " +
                   "b.author, b.published_year, r.borrow_date, r.status " +
                   "FROM books b JOIN BorrowRecords r ON b.id = r.book_id " +
                   "WHERE r.student_id = :studentId", nativeQuery = true)
    List<Object[]> findRecordsWithBookInfo(@Param("studentId") String studentId);
}
