package dashboard;

import dao.AirportDAO;
import model.Airport;
import java.util.List;
import java.util.Scanner;

public class AirportDashboard {

    private final AirportDAO dao = new AirportDAO();
    private final Scanner sc = new Scanner(System.in);

    public void menu() {
        while (true) {
            System.out.println("\n======================================");
            System.out.println("         AIRPORT MANAGEMENT");
            System.out.println("======================================");
            System.out.println("1. Add Airport");
            System.out.println("2. View All Airports");
            System.out.println("3. Search Airport");
            System.out.println("4. Update Airport");
            System.out.println("5. Delete Airport");
            System.out.println("6. Back");
            System.out.print("Enter Choice: ");

            int choice = readInt();
            switch (choice) {
                case 1:
                    addAirport();
                    break;
                case 2:
                    viewAllAirports();
                    break;
                case 3:
                    searchAirport();
                    break;
                case 4:
                    updateAirport();
                    break;
                case 5:
                    deleteAirport();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid Choice. Please try again.");
            }
        }
    }

    private void addAirport() {
        System.out.println("\n--- Add New Airport ---");
        String code = readNonEmptyString("Enter Airport Code (e.g. JFK): ");
        String name = readNonEmptyString("Enter Airport Name: ");
        String city = readNonEmptyString("Enter City: ");
        String country = readNonEmptyString("Enter Country: ");

        Airport airport = new Airport(code, name, city, country);
        if (dao.addAirport(airport)) {
            System.out.println("SUCCESS: Airport added successfully!");
        } else {
            System.out.println("ERROR: Failed to add airport.");
        }
    }

    private void viewAllAirports() {
        System.out.println("\n--- All Airports ---");
        List<Airport> airports = dao.getAllAirports();
        if (airports.isEmpty()) {
            System.out.println("No airports found.");
        } else {
            for (Airport a : airports) {
                System.out.println(a);
            }
        }
    }

    private void searchAirport() {
        System.out.println("\n--- Search Airport ---");
        String query = readNonEmptyString("Enter Airport Code/City/Name/Country keyword: ");
        List<Airport> airports = dao.searchAirportByQuery(query);
        if (airports.isEmpty()) {
            System.out.println("No matching airports found.");
        } else {
            for (Airport a : airports) {
                System.out.println(a);
            }
        }
    }

    private void updateAirport() {
        System.out.println("\n--- Update Airport ---");
        System.out.print("Enter Airport ID to Update: ");
        int id = readInt();
        Airport existing = dao.searchAirportById(id);
        if (existing == null) {
            System.out.println("ERROR: Airport not found with ID: " + id);
            return;
        }
        System.out.println("Current: " + existing);
        String code = readNonEmptyString("Enter New Airport Code: ");
        String name = readNonEmptyString("Enter New Airport Name: ");
        String city = readNonEmptyString("Enter New City: ");
        String country = readNonEmptyString("Enter New Country: ");

        existing.setAirportCode(code);
        existing.setAirportName(name);
        existing.setCity(city);
        existing.setCountry(country);

        if (dao.updateAirport(existing)) {
            System.out.println("SUCCESS: Airport updated successfully!");
        } else {
            System.out.println("ERROR: Failed to update airport.");
        }
    }

    private void deleteAirport() {
        System.out.println("\n--- Delete Airport ---");
        System.out.print("Enter Airport ID to Delete: ");
        int id = readInt();
        if (dao.deleteAirport(id)) {
            System.out.println("SUCCESS: Airport deleted successfully!");
        } else {
            System.out.println("ERROR: Airport not found or deletion failed.");
        }
    }

    private int readInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Invalid input. Please enter a valid number: ");
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
