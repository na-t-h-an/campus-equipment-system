package edu.cit.lada.nathanxander.campusequipmentloan.controller;

import edu.cit.lada.nathanxander.campusequipmentloan.model.Equipment;
import edu.cit.lada.nathanxander.campusequipmentloan.model.Loan;
import edu.cit.lada.nathanxander.campusequipmentloan.model.Student;
import edu.cit.lada.nathanxander.campusequipmentloan.repository.EquipmentRepository;
import edu.cit.lada.nathanxander.campusequipmentloan.repository.LoanRepository;
import edu.cit.lada.nathanxander.campusequipmentloan.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LoanController {

    private final LoanRepository loanRepository;
    private final EquipmentRepository equipmentRepository;
    private final StudentRepository studentRepository;

    public LoanController(LoanRepository loanRepository,
                          EquipmentRepository equipmentRepository,
                          StudentRepository studentRepository) {
        this.loanRepository = loanRepository;
        this.equipmentRepository = equipmentRepository;
        this.studentRepository = studentRepository;
    }

    //loan creation request
    public static class LoanRequest {
        public Long equipmentId;
        public Long studentId;
        public String dueDate;
    }

    // POST /api/loans
    @PostMapping("/loans")
    public ResponseEntity<?> createLoan(@RequestBody LoanRequest request) {
        Optional<Equipment> equipmentOpt = equipmentRepository.findById(request.equipmentId);
        Optional<Student> studentOpt = studentRepository.findById(request.studentId);

        if (equipmentOpt.isEmpty() || studentOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid equipment or student ID.");
        }

        Equipment equipment = equipmentOpt.get();
        if (!equipment.isAvailability()) {
            return ResponseEntity.badRequest().body("Equipment is not available.");
        }

        Loan loan = new Loan();
        loan.setEquipment(equipment);
        loan.setStudent(studentOpt.get());
        loan.setStartDate(LocalDate.now());
        loan.setDueDate(LocalDate.parse(request.dueDate));
        loan.setStatus("ONGOING");

        // Mark equipment unavailable
        equipment.setAvailability(false);
        equipmentRepository.save(equipment);

        loanRepository.save(loan);

        return ResponseEntity.ok(loan);
    }

    // POST /api/loans/{id}/return
    @PostMapping("/loans/{id}/return")
    public ResponseEntity<?> returnLoan(@PathVariable Long id) {
        Optional<Loan> loanOpt = loanRepository.findById(id);

        if (loanOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Loan not found.");
        }

        Loan loan = loanOpt.get();
        if ("RETURNED".equalsIgnoreCase(loan.getStatus())) {
            return ResponseEntity.badRequest().body("Loan already returned.");
        }

        loan.setReturnDate(LocalDate.now());
        loan.setStatus("RETURNED");

        Equipment equipment = loan.getEquipment();
        equipment.setAvailability(true);
        equipmentRepository.save(equipment);

        loanRepository.save(loan);

        return ResponseEntity.ok(loan);
    }

    @GetMapping("/loans/status/{status}")
    public List<Loan> getLoansByStatus(@PathVariable String status) {
        return loanRepository.findByStatus(status);
    }
}
