package edu.cit.lada.nathanxander.campusequipmentloan.controller;

import edu.cit.lada.nathanxander.campusequipmentloan.model.Student;
import edu.cit.lada.nathanxander.campusequipmentloan.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Create new student
    @PostMapping
    public ResponseEntity<?> addStudent(@RequestBody Student student) {
        // Check if studentNo already exists
        if (studentRepository.existsByStudentNo(student.getStudentNo())) {
            return ResponseEntity.ok().body("Student already exists");
        }

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
