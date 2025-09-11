package edu.cit.lada.nathanxander.campusequipmentloan.service;

import edu.cit.lada.nathanxander.campusequipmentloan.model.Loan;
import edu.cit.lada.nathanxander.campusequipmentloan.model.Student;
import edu.cit.lada.nathanxander.campusequipmentloan.repository.LoanRepository;
import edu.cit.lada.nathanxander.campusequipmentloan.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final StudentRepository studentRepository;

    public LoanService(LoanRepository loanRepository,
                       StudentRepository studentRepository) {
        this.loanRepository = loanRepository;
        this.studentRepository = studentRepository;
    }

    public interface LateFeeStrategy {
        double calculateLateFee(LocalDate dueDate, LocalDate returnDate);
    }

    public static class DefaultLateFeeStrategy implements LateFeeStrategy {
        private static final double DAILY_PENALTY = 50.0;

        @Override
        public double calculateLateFee(LocalDate dueDate, LocalDate returnDate) {
            if (returnDate == null || !returnDate.isAfter(dueDate)) {
                return 0.0;
            }
            long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
            return daysLate * DAILY_PENALTY;
        }
    }

    // Create loan with rules
    public Loan createLoan(Long studentId, Loan loan) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Rule 1: Max 2 active loans
        List<Loan> activeLoans = loanRepository.findByStudentIdAndReturnDateIsNull(studentId);
        if (activeLoans.size() >= 2) {
            throw new RuntimeException("Student already has 2 active loans");
        }

        // Rule 2: Loan length = 7 days
        loan.setStudent(student);
        loan.setStartDate(LocalDate.now());
        loan.setDueDate(loan.getStartDate().plusDays(7));
        loan.setStatus("ONGOING");

        return loanRepository.save(loan);
    }

    // Return loan with late fee calculation
    public Loan returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        loan.setReturnDate(LocalDate.now());
        loan.setStatus("RETURNED");

        // Rule 3 & 4: Overdue + ₱50/day penalty
        LateFeeStrategy strategy = new DefaultLateFeeStrategy();
        double lateFee = strategy.calculateLateFee(loan.getDueDate(), loan.getReturnDate());
        loan.setPenalty(lateFee);

        return loanRepository.save(loan);
    }
}
