package edu.cit.lada.nathanxander.campusequipmentloan.repository;

import java.util.Optional;
import edu.cit.lada.nathanxander.campusequipmentloan.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByStudentNo(String studentNo);
    boolean existsByStudentNo(String studentNo);
    boolean existsByName(String name);
    Optional<Student> findByUsername(String username);
}
