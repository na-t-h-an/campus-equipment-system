package edu.cit.lada.nathanxander.campusequipmentloan.controller;

import edu.cit.lada.nathanxander.campusequipmentloan.model.Student;
import edu.cit.lada.nathanxander.campusequipmentloan.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder; // injected encoder

    // Constructor injection
    public StudentController(StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Create new student
    @PostMapping
    public ResponseEntity<?> addStudent(@RequestBody Student student) {
        // Check if studentNo already exists
        if (studentRepository.existsByStudentNo(student.getStudentNo())) {
            return ResponseEntity.ok().body("Student already exists");
        }

        // Encode password before saving
        student.setPassword(passwordEncoder.encode(student.getPassword()));

        // Check if name already exists
        if (studentRepository.existsByName(student.getName())) {
            return ResponseEntity.ok().body("Student already exists");
        }

        Student saved = studentRepository.save(student);
        return ResponseEntity.ok(saved);
    }

    // Get all students
    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}
