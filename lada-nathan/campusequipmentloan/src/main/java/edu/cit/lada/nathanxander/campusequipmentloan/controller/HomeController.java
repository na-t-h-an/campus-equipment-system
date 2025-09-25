package edu.cit.lada.nathanxander.campusequipmentloan.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Welcome to the Campus Equipment Loan System!";
    }

    @GetMapping("/home")
    public String dashboard() {
        return "You are logged in. This is the home page.";
    }
}
