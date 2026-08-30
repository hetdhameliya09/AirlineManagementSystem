package com.airline.controller;

import dao.AirportDAO;
import dao.CustomerDAO;
import dao.FlightDAO;
import dao.NotificationDAO;
import model.Customer;
import model.Notification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class HomeController {

    private final FlightDAO flightDAO;
    private final AirportDAO airportDAO;
    private final CustomerDAO customerDAO;
    private final NotificationDAO notificationDAO;

    public HomeController(FlightDAO flightDAO, AirportDAO airportDAO, CustomerDAO customerDAO, NotificationDAO notificationDAO) {
        this.flightDAO = flightDAO;
        this.airportDAO = airportDAO;
        this.customerDAO = customerDAO;
        this.notificationDAO = notificationDAO;
    }

    @GetMapping("/")
    public String home(@RequestParam(required = false) String departure,
                       @RequestParam(required = false) String arrival,
                       @RequestParam(required = false) String query,
                       Model model) {
        if (departure != null && !departure.trim().isEmpty() && arrival != null && !arrival.trim().isEmpty()) {
            model.addAttribute("flights", flightDAO.searchFlightsByRoute(departure, arrival));
        } else if (query != null && !query.trim().isEmpty()) {
            model.addAttribute("flights", flightDAO.searchFlightsByQuery(query));
        } else {
            model.addAttribute("flights", flightDAO.getAllFlights());
        }

        model.addAttribute("airports", airportDAO.getAllAirports());
        model.addAttribute("selectedDeparture", departure);
        model.addAttribute("selectedArrival", arrival);
        model.addAttribute("searchQuery", query);
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String success,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        if (success != null) {
            model.addAttribute("success", success);
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        if ("admin".equals(username) && "admin123".equals(password)) {
            session.setAttribute("userRole", "ADMIN");
            session.setAttribute("username", "System Admin");
            return "redirect:/admin/dashboard";
        }

        Customer customer = customerDAO.login(username, password);
        if (customer != null) {
            session.setAttribute("userRole", "CUSTOMER");
            session.setAttribute("customer", customer);
            session.setAttribute("username", customer.getName());
            return "redirect:/customer/dashboard";
        }

        model.addAttribute("error", "Invalid username or password. Please try again.");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("customer", new Customer());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Customer customer, Model model, HttpSession session) {
        if (customer.getUsername() == null || customer.getUsername().trim().isEmpty() ||
            customer.getPassword() == null || customer.getPassword().trim().isEmpty()) {
            model.addAttribute("error", "Username and password are required.");
            return "register";
        }

        if (customerDAO.addCustomer(customer)) {
            // Auto login after registration
            Customer loggedIn = customerDAO.login(customer.getUsername(), customer.getPassword());
            if (loggedIn != null) {
                // Send welcome notification
                String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                Notification n = new Notification(loggedIn.getCustomerId(), "Welcome to SkyWays Airline! Account created successfully.", now, "UNREAD");
                notificationDAO.addNotification(n);

                session.setAttribute("userRole", "CUSTOMER");
                session.setAttribute("customer", loggedIn);
                session.setAttribute("username", loggedIn.getName());
                return "redirect:/customer/dashboard?success=Welcome!+Registration+successful.";
            }
            return "redirect:/login?success=Account+created+successfully.+Please+login.";
        }

        model.addAttribute("error", "Registration failed. Username may already be in use.");
        return "register";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/?message=Logged+out+successfully";
    }
}
