package edu.cit.lada.nathanxander.campusequipmentloan.repository;

import edu.cit.lada.nathanxander.campusequipmentloan.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    java.util.List<Loan> findByStatus(String status);
    List<Loan> findByStudentIdAndReturnDateIsNull(Long studentId);
}
