package dashboard;

import dao.FlightDAO;
import model.Flight;
import validation.DateValidator;
import validation.ValidationUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class FlightDashboard {

    private final FlightDAO dao = new FlightDAO();
    private final Scanner sc = new Scanner(System.in);

    public void menu() {
        while (true) {
            System.out.println("\n======================================");
            System.out.println("          FLIGHT MANAGEMENT");
            System.out.println("======================================");
            System.out.println("1. Add Flight");
            System.out.println("2. View All Flights");
            System.out.println("3. Search Flight");
            System.out.println("4. Update Flight");
            System.out.println("5. Delete Flight");
            System.out.println("6. Back");
            System.out.print("Enter Choice: ");

            int choice = readInt();
            switch (choice) {
                case 1:
                    addFlight();
                    break;
                case 2:
                    viewAllFlights();
                    break;
                case 3:
                    searchFlight();
                    break;
                case 4:
                    updateFlight();
                    break;
                case 5:
                    deleteFlight();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid Choice. Please try again.");
            }
        }
    }

    private void addFlight() {
        System.out.println("\n--- Add New Flight ---");
        String number = readNonEmptyString("Enter Flight Number (e.g. AI-101): ");
        String airline = readNonEmptyString("Enter Airline Name: ");
        String dep = readNonEmptyString("Enter Departure Airport/City: ");
        String arr = readNonEmptyString("Enter Arrival Airport/City: ");
        
        LocalDate depDate = DateValidator.inputFutureDate(sc, "Enter Departure Date (yyyy-MM-dd): ");
        String depTimeStr = readNonEmptyString("Enter Departure Time (HH:MM): ");
        String depTime = depDate.toString() + " " + depTimeStr;

        LocalDate arrDate = DateValidator.inputFutureDate(sc, "Enter Arrival Date (yyyy-MM-dd): ");
        String arrTimeStr = readNonEmptyString("Enter Arrival Time (HH:MM): ");
        String arrTime = arrDate.toString() + " " + arrTimeStr;

        System.out.print("Enter Ticket Price: ");
        double price = readDouble();
        System.out.print("Enter Available Seats: ");
        int seats = readInt();

        Flight flight = new Flight(number, airline, dep, arr, depTime, arrTime, price, seats);
        if (dao.addFlight(flight)) {
            System.out.println("SUCCESS: Flight added successfully!");
        } else {
            System.out.println("ERROR: Failed to add flight.");
        }
    }

    private void viewAllFlights() {
        System.out.println("\n--- All Flights ---");
        List<Flight> flights = dao.getAllFlights();
        if (flights.isEmpty()) {
            System.out.println("No flights found.");
        } else {
            for (Flight f : flights) {
                System.out.println(f);
            }
        }
    }

    private void searchFlight() {
        System.out.println("\n--- Search Flight ---");
        String query = readNonEmptyString("Enter Flight Number/Airline/Departure/Arrival keyword: ");
        List<Flight> flights = dao.searchFlightsByQuery(query);
        if (flights.isEmpty()) {
            System.out.println("No matching flights found.");
        } else {
            for (Flight f : flights) {
                System.out.println(f);
            }
        }
    }

    private void updateFlight() {
        System.out.println("\n--- Update Flight ---");
        System.out.print("Enter Flight ID to Update: ");
        int id = readInt();
        Flight existing = dao.searchFlightById(id);
        if (existing == null) {
            System.out.println("ERROR: Flight not found with ID: " + id);
            return;
        }
        System.out.println("Current: " + existing);
        String number = readNonEmptyString("Enter New Flight Number: ");
        String airline = readNonEmptyString("Enter New Airline Name: ");
        String dep = readNonEmptyString("Enter New Departure Airport/City: ");
        String arr = readNonEmptyString("Enter New Arrival Airport/City: ");
        
        LocalDate depDate = DateValidator.inputFutureDate(sc, "Enter New Departure Date (yyyy-MM-dd): ");
        String depTimeStr = readNonEmptyString("Enter New Departure Time (HH:MM): ");
        String depTime = depDate.toString() + " " + depTimeStr;

        LocalDate arrDate = DateValidator.inputFutureDate(sc, "Enter New Arrival Date (yyyy-MM-dd): ");
        String arrTimeStr = readNonEmptyString("Enter New Arrival Time (HH:MM): ");
        String arrTime = arrDate.toString() + " " + arrTimeStr;

        System.out.print("Enter New Price: ");
        double price = readDouble();
        System.out.print("Enter New Available Seats: ");
        int seats = readInt();

        existing.setFlightNumber(number);
        existing.setAirlineName(airline);
        existing.setDepartureAirport(dep);
        existing.setArrivalAirport(arr);
        existing.setDepartureTime(depTime);
        existing.setArrivalTime(arrTime);
        existing.setPrice(price);
        existing.setAvailableSeats(seats);

        if (dao.updateFlight(existing)) {
            System.out.println("SUCCESS: Flight updated successfully!");
        } else {
            System.out.println("ERROR: Failed to update flight.");
        }
    }

    private void deleteFlight() {
        System.out.println("\n--- Delete Flight ---");
        System.out.print("Enter Flight ID to Delete: ");
        int id = readInt();
        if (dao.deleteFlight(id)) {
            System.out.println("SUCCESS: Flight deleted successfully!");
        } else {
            System.out.println("ERROR: Flight not found or deletion failed.");
        }
    }

    private int readInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Invalid input. Enter a valid number: ");
            sc.next();
        }
        int val = sc.nextInt();
        sc.nextLine();
        return val;
    }

    private double readDouble() {
        while (!sc.hasNextDouble()) {
            System.out.print("Invalid input. Enter a valid price/number: ");
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
