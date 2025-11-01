package com.ptit.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    
    @Id
    @Column(name = "student_code", length = 50)
    private String studentCode;
    
    @Column(name = "full_name", length = 255, nullable = false)
    private String fullName;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Column(name = "gender", length = 10)
    private String gender;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Column(name = "address", length = 255)
    private String address;
    
    // Constructor for basic info
    public Student(String studentCode, String fullName) {
        this.studentCode = studentCode;
        this.fullName = fullName;
    }
}
