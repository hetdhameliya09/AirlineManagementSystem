package com.airline.controller;

import dao.FlightDAO;
import model.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    private final FlightDAO flightDAO;

    public HomeController(FlightDAO flightDAO) {
        this.flightDAO = flightDAO;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("flights", flightDAO.getAllFlights());
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        if ("admin".equals(username) && "admin123".equals(password)) {
            session.setAttribute("userRole", "ADMIN");
            session.setAttribute("username", username);
            return "redirect:/admin/dashboard";
        }

        var customer = new dao.CustomerDAO().login(username, password);
        if (customer != null) {
            session.setAttribute("userRole", "CUSTOMER");
            session.setAttribute("customer", customer);
            session.setAttribute("username", customer.getName());
            return "redirect:/customer/dashboard";
        }

        model.addAttribute("error", "Invalid username or password");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("customer", new Customer());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Customer customer, Model model) {
        var customerDAO = new dao.CustomerDAO();
        if (customerDAO.addCustomer(customer)) {
            model.addAttribute("success", "Registration successful. Please login.");
            return "login";
        }
        model.addAttribute("error", "Registration failed. Username may already exist.");
        return "register";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
