package dashboard;

import dao.BookingDAO;
import model.Booking;
import validation.DateValidator;
import validation.ValidationUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class BookingDashboard {

    private final BookingDAO dao = new BookingDAO();
    private final Scanner sc = new Scanner(System.in);

    public void menu() {
        while (true) {
            System.out.println("\n======================================");
            System.out.println("          BOOKING MANAGEMENT");
            System.out.println("======================================");
            System.out.println("1. Add Booking");
            System.out.println("2. View All Bookings");
            System.out.println("3. Search Booking");
            System.out.println("4. Update Booking");
            System.out.println("5. Delete Booking");
            System.out.println("6. Back");
            System.out.print("Enter Choice: ");

            int choice = readInt();
            switch (choice) {
                case 1:
                    addBooking();
                    break;
                case 2:
                    viewAllBookings();
                    break;
                case 3:
                    searchBooking();
                    break;
                case 4:
                    updateBooking();
                    break;
                case 5:
                    deleteBooking();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid Choice. Please try again.");
            }
        }
    }

    private void addBooking() {
        System.out.println("\n--- Add New Booking ---");
        System.out.print("Enter Customer ID: ");
        int customerId = readInt();
        System.out.print("Enter Flight ID: ");
        int flightId = readInt();
        
        // Auto insert current date
        String date = DateValidator.today().toString();
        System.out.println("Booking Date (Auto): " + date);

        String status = readNonEmptyString("Enter Status (CONFIRMED/CANCELLED/PENDING): ");
        System.out.print("Enter Total Amount: ");
        double amount = readDouble();

        Booking booking = new Booking(customerId, flightId, date, status, amount);
        int newId = dao.addBooking(booking);
        if (newId != -1) {
            System.out.println("SUCCESS: Booking created successfully! Booking ID: " + newId);
        } else {
            System.out.println("ERROR: Failed to create booking.");
        }
    }

    private void viewAllBookings() {
        System.out.println("\n--- All Bookings ---");
        List<Booking> list = dao.getAllBookings();
        if (list.isEmpty()) {
            System.out.println("No bookings found.");
        } else {
            for (Booking b : list) {
                System.out.println(b);
            }
        }
    }

    private void searchBooking() {
        System.out.println("\n--- Search Booking ---");
        System.out.print("Enter Booking ID: ");
        int id = readInt();
        Booking b = dao.searchBookingById(id);
        if (b == null) {
            System.out.println("No booking found with ID: " + id);
        } else {
            System.out.println(b);
        }
    }

    private void updateBooking() {
        System.out.println("\n--- Update Booking ---");
        System.out.print("Enter Booking ID to Update: ");
        int id = readInt();
        Booking existing = dao.searchBookingById(id);
        if (existing == null) {
            System.out.println("ERROR: Booking not found with ID: " + id);
            return;
        }
        System.out.println("Current: " + existing);
        System.out.print("Enter New Customer ID: ");
        int custId = readInt();
        System.out.print("Enter New Flight ID: ");
        int fliId = readInt();
        
        LocalDate date = DateValidator.inputDate(sc, "Enter New Booking Date (yyyy-MM-dd): ");
        String status = readNonEmptyString("Enter New Status (CONFIRMED/CANCELLED): ");
        System.out.print("Enter New Amount: ");
        double amt = readDouble();

        existing.setCustomerId(custId);
        existing.setFlightId(fliId);
        existing.setBookingDate(date.toString());
        existing.setStatus(status);
        existing.setTotalAmount(amt);

        if (dao.updateBooking(existing)) {
            System.out.println("SUCCESS: Booking updated successfully!");
        } else {
            System.out.println("ERROR: Failed to update booking.");
        }
    }

    private void deleteBooking() {
        System.out.println("\n--- Delete Booking ---");
        System.out.print("Enter Booking ID to Delete: ");
        int id = readInt();
        if (dao.deleteBooking(id)) {
            System.out.println("SUCCESS: Booking deleted successfully!");
        } else {
            System.out.println("ERROR: Booking not found or deletion failed.");
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
