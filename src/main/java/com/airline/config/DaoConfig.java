package com.airline.config;

import dao.AdminDAO;
import dao.BookingDAO;
import dao.CustomerDAO;
import dao.FeedbackDAO;
import dao.FlightDAO;
import dao.HotelDAO;
import dao.TaxiDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoConfig {

    @Bean
    public AdminDAO adminDAO() {
        return new AdminDAO();
    }

    @Bean
    public CustomerDAO customerDAO() {
        return new CustomerDAO();
    }

    @Bean
    public FlightDAO flightDAO() {
        return new FlightDAO();
    }

    @Bean
    public BookingDAO bookingDAO() {
        return new BookingDAO();
    }

    @Bean
    public HotelDAO hotelDAO() {
        return new HotelDAO();
    }

    @Bean
    public TaxiDAO taxiDAO() {
        return new TaxiDAO();
    }

    @Bean
    public FeedbackDAO feedbackDAO() {
        return new FeedbackDAO();
    }
}
