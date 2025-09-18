package edu.cit.lada.nathanxander.campusequipmentloan.controller;

import edu.cit.lada.nathanxander.campusequipmentloan.model.Equipment;
import edu.cit.lada.nathanxander.campusequipmentloan.model.Loan;
import edu.cit.lada.nathanxander.campusequipmentloan.model.Student;
import edu.cit.lada.nathanxander.campusequipmentloan.repository.EquipmentRepository;
import edu.cit.lada.nathanxander.campusequipmentloan.repository.LoanRepository;
import edu.cit.lada.nathanxander.campusequipmentloan.repository.StudentRepository;
import edu.cit.lada.nathanxander.campusequipmentloan.service.LoanService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LoanController {

    private final LoanRepository loanRepository;
    private final EquipmentRepository equipmentRepository;
    private final StudentRepository studentRepository;
    private final LoanService loanService;

    public LoanController(LoanRepository loanRepository,
                          EquipmentRepository equipmentRepository,
                          StudentRepository studentRepository,
                          LoanService loanService) {
        this.loanRepository = loanRepository;
        this.equipmentRepository = equipmentRepository;
        this.studentRepository = studentRepository;
        this.loanService = loanService;
    }

    //loan creation request
    public static class LoanRequest {
        public Long equipmentId;
        public Long studentId;
        public String startDate;
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

        try {
            // ✅ check rule from service
            loanService.validateMaxActiveLoans(request.studentId);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        Loan loan = new Loan();
        loan.setEquipment(equipment);
        loan.setStudent(studentOpt.get());
        loan.setStartDate(LocalDate.parse(request.startDate));
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
    public ResponseEntity<?> returnLoan(@PathVariable Long id, @RequestBody Map<String,String> body) {
        Optional<Loan> loanOpt = loanRepository.findById(id);

        if (loanOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Loan not found.");
        }

        Loan loan = loanOpt.get();
        if ("RETURNED".equalsIgnoreCase(loan.getStatus())) {
            return ResponseEntity.badRequest().body("Loan already returned.");
        }

        // postman return date if set / default is current date
        LocalDate returnDate = body.containsKey("returnDate") 
            ? LocalDate.parse(body.get("returnDate")) 
            : LocalDate.now();

        loan.setReturnDate(returnDate);
        loan.setStatus("RETURNED");

        // calculate penalty
        LoanService.LateFeeStrategy strategy = new LoanService.DefaultLateFeeStrategy();
        double lateFee = strategy.calculateLateFee(loan.getDueDate(), loan.getReturnDate());
        loan.setPenalty(lateFee);

        // update overdue
        loan.setOverdue(loan.getReturnDate().isAfter(loan.getDueDate()));

        // make equipment available again
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
