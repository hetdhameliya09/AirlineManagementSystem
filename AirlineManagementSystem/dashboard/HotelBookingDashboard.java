package dashboard;

import dao.HotelBookingDAO;
import model.HotelBooking;
import validation.DateValidator;
import validation.ValidationUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class HotelBookingDashboard {

    private final HotelBookingDAO dao = new HotelBookingDAO();
    private final Scanner sc = new Scanner(System.in);

    public void menu() {
        while (true) {
            System.out.println("\n======================================");
            System.out.println("       HOTEL BOOKING MANAGEMENT");
            System.out.println("======================================");
            System.out.println("1. Add Hotel Booking");
            System.out.println("2. View All Hotel Bookings");
            System.out.println("3. Search Hotel Booking");
            System.out.println("4. Update Hotel Booking");
            System.out.println("5. Delete Hotel Booking");
            System.out.println("6. Back");
            System.out.print("Enter Choice: ");

            int choice = readInt();
            switch (choice) {
                case 1:
                    addHotelBooking();
                    break;
                case 2:
                    viewAllHotelBookings();
                    break;
                case 3:
                    searchHotelBooking();
                    break;
                case 4:
                    updateHotelBooking();
                    break;
                case 5:
                    deleteHotelBooking();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid Choice. Please try again.");
            }
        }
    }

    private void addHotelBooking() {
        System.out.println("\n--- Add New Hotel Booking ---");
        System.out.print("Enter Customer ID: ");
        int customerId = readInt();
        System.out.print("Enter Hotel ID: ");
        int hotelId = readInt();
        
        LocalDate checkIn = DateValidator.inputFutureDate(sc, "Enter Check-in Date (yyyy-MM-dd): ");
        LocalDate checkOut = DateValidator.inputCheckOutDate(sc, "Enter Check-out Date (yyyy-MM-dd): ", checkIn);

        System.out.print("Enter Number of Rooms: ");
        int rooms = readInt();
        System.out.print("Enter Total Price: ");
        double total = readDouble();
        String status = readNonEmptyString("Enter Status (CONFIRMED/CANCELLED): ");

        HotelBooking booking = new HotelBooking(customerId, hotelId, checkIn.toString(), checkOut.toString(), rooms, total, status);
        if (dao.addHotelBooking(booking)) {
            System.out.println("SUCCESS: Hotel booking added successfully!");
        } else {
            System.out.println("ERROR: Failed to add hotel booking.");
        }
    }

    private void viewAllHotelBookings() {
        System.out.println("\n--- All Hotel Bookings ---");
        List<HotelBooking> list = dao.getAllHotelBookings();
        if (list.isEmpty()) {
            System.out.println("No hotel bookings found.");
        } else {
            for (HotelBooking hb : list) {
                System.out.println(hb);
            }
        }
    }

    private void searchHotelBooking() {
        System.out.println("\n--- Search Hotel Booking ---");
        System.out.print("Enter Hotel Booking ID: ");
        int id = readInt();
        HotelBooking hb = dao.searchHotelBookingById(id);
        if (hb == null) {
            System.out.println("No hotel booking found with ID: " + id);
        } else {
            System.out.println(hb);
        }
    }

    private void updateHotelBooking() {
        System.out.println("\n--- Update Hotel Booking ---");
        System.out.print("Enter Hotel Booking ID to Update: ");
        int id = readInt();
        HotelBooking existing = dao.searchHotelBookingById(id);
        if (existing == null) {
            System.out.println("ERROR: Hotel booking not found with ID: " + id);
            return;
        }
        System.out.println("Current: " + existing);
        System.out.print("Enter New Customer ID: ");
        int custId = readInt();
        System.out.print("Enter New Hotel ID: ");
        int hotelId = readInt();
        
        LocalDate checkIn = DateValidator.inputFutureDate(sc, "Enter New Check-in Date (yyyy-MM-dd): ");
        LocalDate checkOut = DateValidator.inputCheckOutDate(sc, "Enter New Check-out Date (yyyy-MM-dd): ", checkIn);

        System.out.print("Enter New Number of Rooms: ");
        int rooms = readInt();
        System.out.print("Enter New Total Price: ");
        double total = readDouble();
        String status = readNonEmptyString("Enter New Status: ");

        existing.setCustomerId(custId);
        existing.setHotelId(hotelId);
        existing.setCheckInDate(checkIn.toString());
        existing.setCheckOutDate(checkOut.toString());
        existing.setNumRooms(rooms);
        existing.setTotalPrice(total);
        existing.setStatus(status);

        if (dao.updateHotelBooking(existing)) {
            System.out.println("SUCCESS: Hotel booking updated successfully!");
        } else {
            System.out.println("ERROR: Failed to update hotel booking.");
        }
    }

    private void deleteHotelBooking() {
        System.out.println("\n--- Delete Hotel Booking ---");
        System.out.print("Enter Hotel Booking ID to Delete: ");
        int id = readInt();
        if (dao.deleteHotelBooking(id)) {
            System.out.println("SUCCESS: Hotel booking deleted successfully!");
        } else {
            System.out.println("ERROR: Hotel booking not found or deletion failed.");
        }
    }

    private int readInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Invalid input. Enter a valid integer: ");
            sc.next();
        }
        int val = sc.nextInt();
        sc.nextLine();
        return val;
    }

    private double readDouble() {
        while (!sc.hasNextDouble()) {
            System.out.print("Invalid input. Enter a valid number: ");
            sc.next();
        }
        double val = sc.nextDouble();
        sc.nextLine();
        return val;
    }

    private String readNonEmptyString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = sc.nextLine().trim();
            if (!ValidationUtils.isNotEmpty(input)) {
                System.out.println("Input cannot be blank. Please try again.");
            }
        } while (!ValidationUtils.isNotEmpty(input));
        return input;
    }
}
