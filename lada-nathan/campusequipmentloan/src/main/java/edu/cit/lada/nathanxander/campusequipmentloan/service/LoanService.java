package edu.cit.lada.nathanxander.campusequipmentloan.service;

import edu.cit.lada.nathanxander.campusequipmentloan.model.Loan;
import edu.cit.lada.nathanxander.campusequipmentloan.repository.LoanRepository;
import edu.cit.lada.nathanxander.campusequipmentloan.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

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
    //Max active loans =< 2
    public List<Loan> getActiveLoans(Long studentId) {
        return loanRepository.findByStudentIdAndStatus(studentId, "ONGOING");
    }
}
