package com.airline.controller;

import dao.AdminDAO;
import dao.CustomerDAO;
import dao.FlightDAO;
import dao.HotelDAO;
import dao.TaxiDAO;
import model.Customer;
import model.Flight;
import model.Hotel;
import model.Taxi;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminDAO adminDAO;
    private final CustomerDAO customerDAO;
    private final FlightDAO flightDAO;
    private final HotelDAO hotelDAO;
    private final TaxiDAO taxiDAO;

    public AdminController(AdminDAO adminDAO, CustomerDAO customerDAO, FlightDAO flightDAO, HotelDAO hotelDAO, TaxiDAO taxiDAO) {
        this.adminDAO = adminDAO;
        this.customerDAO = customerDAO;
        this.flightDAO = flightDAO;
        this.hotelDAO = hotelDAO;
        this.taxiDAO = taxiDAO;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("customers", customerDAO.getAllCustomers());
        model.addAttribute("flights", flightDAO.getAllFlights());
        model.addAttribute("hotels", hotelDAO.getAllHotels());
        model.addAttribute("taxis", taxiDAO.getAllTaxis());
        return "admin-dashboard";
    }

    @GetMapping("/customers")
    public String customers(Model model) {
        model.addAttribute("customers", customerDAO.getAllCustomers());
        return "admin-customers";
    }

    @GetMapping("/flights")
    public String flights(Model model) {
        model.addAttribute("flights", flightDAO.getAllFlights());
        model.addAttribute("flight", new Flight());
        return "admin-flights";
    }

    @PostMapping("/flights")
    public String saveFlight(@ModelAttribute Flight flight) {
        flightDAO.addFlight(flight);
        return "redirect:/admin/flights";
    }

    @PostMapping("/flights/delete/{id}")
    public String deleteFlight(@PathVariable int id) {
        flightDAO.deleteFlight(id);
        return "redirect:/admin/flights";
    }

    @GetMapping("/hotels")
    public String hotels(Model model) {
        model.addAttribute("hotels", hotelDAO.getAllHotels());
        model.addAttribute("hotel", new Hotel());
        return "admin-hotels";
    }

    @PostMapping("/hotels")
    public String saveHotel(@ModelAttribute Hotel hotel) {
        hotelDAO.addHotel(hotel);
        return "redirect:/admin/hotels";
    }

    @GetMapping("/taxis")
    public String taxis(Model model) {
        model.addAttribute("taxis", taxiDAO.getAllTaxis());
        model.addAttribute("taxi", new Taxi());
        return "admin-taxis";
    }

    @PostMapping("/taxis")
    public String saveTaxi(@ModelAttribute Taxi taxi) {
        taxiDAO.addTaxi(taxi);
        return "redirect:/admin/taxis";
    }
}
