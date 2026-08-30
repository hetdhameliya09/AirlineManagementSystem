package com.airline.config;

import dao.AdminDAO;
import dao.BookingDAO;
import dao.CustomerDAO;
import dao.FeedbackDAO;
import dao.FlightDAO;
import dao.HotelDAO;
import dao.TaxiDAO;
import dao.AirportDAO;
import dao.PassengerDAO;
import dao.TicketDAO;
import dao.PaymentDAO;
import dao.HotelBookingDAO;
import dao.NotificationDAO;
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

    @Bean
    public AirportDAO airportDAO() {
        return new AirportDAO();
    }

    @Bean
    public PassengerDAO passengerDAO() {
        return new PassengerDAO();
    }

    @Bean
    public TicketDAO ticketDAO() {
        return new TicketDAO();
    }

    @Bean
    public PaymentDAO paymentDAO() {
        return new PaymentDAO();
    }

    @Bean
    public HotelBookingDAO hotelBookingDAO() {
        return new HotelBookingDAO();
    }

    @Bean
    public NotificationDAO notificationDAO() {
        return new NotificationDAO();
    }
}
