package edu.cit.lada.nathanxander.campusequipmentloan.config;

import edu.cit.lada.nathanxander.campusequipmentloan.repository.StudentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class StudentUserDetailsConfig {

    private final StudentRepository studentRepository;

    public StudentUserDetailsConfig(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = org.springframework.security.core.userdetails.User
                .withUsername("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        InMemoryUserDetailsManager inMemory = new InMemoryUserDetailsManager(admin);

        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                // Try DB first
                return studentRepository.findByUsername(username)
                        .map(student -> org.springframework.security.core.userdetails.User
                                .withUsername(student.getUsername())
                                .password(student.getPassword())
                                .roles("STUDENT")
                                .build()
                        )
                        .orElseGet(() -> inMemory.loadUserByUsername(username)); // fallback to admin
            }
        };
    }
}
