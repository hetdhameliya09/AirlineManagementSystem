package dashboard;

import dao.PassengerDAO;
import model.Passenger;
import java.util.List;
import java.util.Scanner;

public class PassengerDashboard {

    private final PassengerDAO dao = new PassengerDAO();
    private final Scanner sc = new Scanner(System.in);

    public void menu() {
        while (true) {
            System.out.println("\n======================================");
            System.out.println("         PASSENGER MANAGEMENT");
            System.out.println("======================================");
            System.out.println("1. Add Passenger");
            System.out.println("2. View All Passengers");
            System.out.println("3. Search Passenger");
            System.out.println("4. Update Passenger");
            System.out.println("5. Delete Passenger");
            System.out.println("6. Back");
            System.out.print("Enter Choice: ");

            int choice = readInt();
            switch (choice) {
                case 1:
                    addPassenger();
                    break;
                case 2:
                    viewAllPassengers();
                    break;
                case 3:
                    searchPassenger();
                    break;
                case 4:
                    updatePassenger();
                    break;
                case 5:
                    deletePassenger();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid Choice. Please try again.");
            }
        }
    }

    private void addPassenger() {
        System.out.println("\n--- Add New Passenger ---");
        System.out.print("Enter Booking ID: ");
        int bookingId = readInt();
        String name = readNonEmptyString("Enter Passenger Name: ");
        System.out.print("Enter Age: ");
        int age = readInt();
        String gender = readNonEmptyString("Enter Gender (Male/Female/Other): ");
        String seat = readNonEmptyString("Enter Seat Number (e.g. 12A): ");

        Passenger passenger = new Passenger(bookingId, name, age, gender, seat);
        int newId = dao.addPassenger(passenger);
        if (newId != -1) {
            System.out.println("SUCCESS: Passenger added! Passenger ID: " + newId);
        } else {
            System.out.println("ERROR: Failed to add passenger.");
        }
    }

    private void viewAllPassengers() {
        System.out.println("\n--- All Passengers ---");
        List<Passenger> list = dao.getAllPassengers();
        if (list.isEmpty()) {
            System.out.println("No passengers found.");
        } else {
            for (Passenger p : list) {
                System.out.println(p);
            }
        }
    }

    private void searchPassenger() {
        System.out.println("\n--- Search Passenger ---");
        System.out.print("Enter Passenger ID: ");
        int id = readInt();
        Passenger p = dao.searchPassengerById(id);
        if (p == null) {
            System.out.println("No passenger found with ID: " + id);
        } else {
            System.out.println(p);
        }
    }

    private void updatePassenger() {
        System.out.println("\n--- Update Passenger ---");
        System.out.print("Enter Passenger ID to Update: ");
        int id = readInt();
        Passenger existing = dao.searchPassengerById(id);
        if (existing == null) {
            System.out.println("ERROR: Passenger not found with ID: " + id);
            return;
        }
        System.out.println("Current: " + existing);
        System.out.print("Enter New Booking ID: ");
        int bookingId = readInt();
        String name = readNonEmptyString("Enter New Name: ");
        System.out.print("Enter New Age: ");
        int age = readInt();
        String gender = readNonEmptyString("Enter New Gender: ");
        String seat = readNonEmptyString("Enter New Seat Number: ");

        existing.setBookingId(bookingId);
        existing.setName(name);
        existing.setAge(age);
        existing.setGender(gender);
        existing.setSeatNumber(seat);

        if (dao.updatePassenger(existing)) {
            System.out.println("SUCCESS: Passenger updated successfully!");
        } else {
            System.out.println("ERROR: Failed to update passenger.");
        }
    }

    private void deletePassenger() {
        System.out.println("\n--- Delete Passenger ---");
        System.out.print("Enter Passenger ID to Delete: ");
        int id = readInt();
        if (dao.deletePassenger(id)) {
            System.out.println("SUCCESS: Passenger deleted successfully!");
        } else {
            System.out.println("ERROR: Passenger not found or deletion failed.");
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

    private String readNonEmptyString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = sc.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input cannot be blank. Please try again.");
            }
        } while (input.isEmpty());
        return input;
    }
}
