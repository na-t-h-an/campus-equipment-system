package edu.cit.lada.nathanxander.campusequipmentloan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()  // allow H2 console
                        .requestMatchers("/api/users/**").permitAll()   // allow registration
                        .requestMatchers("/api/students/**").permitAll()
                        .anyRequest().authenticated()                            // secure everything else
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/home", true)  // redirect after login
                        .permitAll()
                )
//                .logout(logout -> logout
//                        .logoutSuccessUrl("/login?logout")
//                        .permitAll()
//                )
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.disable());

        return http.build();
    }
}
