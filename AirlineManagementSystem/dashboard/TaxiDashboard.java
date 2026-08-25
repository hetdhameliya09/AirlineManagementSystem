package dashboard;

import dao.TaxiDAO;
import model.Taxi;
import java.util.List;
import java.util.Scanner;

public class TaxiDashboard {

    private final TaxiDAO dao = new TaxiDAO();
    private final Scanner sc = new Scanner(System.in);

    public void menu() {
        while (true) {
            System.out.println("\n======================================");
            System.out.println("           TAXI MANAGEMENT");
            System.out.println("======================================");
            System.out.println("1. Add Taxi");
            System.out.println("2. View All Taxis");
            System.out.println("3. Search Taxi");
            System.out.println("4. Update Taxi");
            System.out.println("5. Delete Taxi");
            System.out.println("6. Back");
            System.out.print("Enter Choice: ");

            int choice = readInt();
            switch (choice) {
                case 1:
                    addTaxi();
                    break;
                case 2:
                    viewAllTaxis();
                    break;
                case 3:
                    searchTaxi();
                    break;
                case 4:
                    updateTaxi();
                    break;
                case 5:
                    deleteTaxi();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid Choice. Please try again.");
            }
        }
    }

    private void addTaxi() {
        System.out.println("\n--- Add New Taxi ---");
        String driver = readNonEmptyString("Enter Driver Name: ");
        String phone = readNonEmptyString("Enter Phone Number: ");
        String vehicleNo = readNonEmptyString("Enter Vehicle Number (e.g. NY-1234): ");
        String type = readNonEmptyString("Enter Vehicle Type (Sedan/SUV/Hatchback): ");
        System.out.print("Enter Price Per KM: ");
        double price = readDouble();
        String status = readNonEmptyString("Enter Status (AVAILABLE/BOOKED): ");

        Taxi taxi = new Taxi(driver, phone, vehicleNo, type, price, status);
        if (dao.addTaxi(taxi)) {
            System.out.println("SUCCESS: Taxi added successfully!");
        } else {
            System.out.println("ERROR: Failed to add taxi.");
        }
    }

    private void viewAllTaxis() {
        System.out.println("\n--- All Taxis ---");
        List<Taxi> list = dao.getAllTaxis();
        if (list.isEmpty()) {
            System.out.println("No taxis found.");
        } else {
            for (Taxi t : list) {
                System.out.println(t);
            }
        }
    }

    private void searchTaxi() {
        System.out.println("\n--- Search Taxi ---");
        String query = readNonEmptyString("Enter Driver/Vehicle No/Type keyword: ");
        List<Taxi> list = dao.searchTaxisByQuery(query);
        if (list.isEmpty()) {
            System.out.println("No matching taxis found.");
        } else {
            for (Taxi t : list) {
                System.out.println(t);
            }
        }
    }

    private void updateTaxi() {
        System.out.println("\n--- Update Taxi ---");
        System.out.print("Enter Taxi ID to Update: ");
        int id = readInt();
        Taxi existing = dao.searchTaxiById(id);
        if (existing == null) {
            System.out.println("ERROR: Taxi not found with ID: " + id);
            return;
        }
        System.out.println("Current: " + existing);
        String driver = readNonEmptyString("Enter New Driver Name: ");
        String phone = readNonEmptyString("Enter New Phone Number: ");
        String vehicleNo = readNonEmptyString("Enter New Vehicle Number: ");
        String type = readNonEmptyString("Enter New Vehicle Type: ");
        System.out.print("Enter New Price Per KM: ");
        double price = readDouble();
        String status = readNonEmptyString("Enter New Status: ");

        existing.setDriverName(driver);
        existing.setPhoneNumber(phone);
        existing.setVehicleNumber(vehicleNo);
        existing.setVehicleType(type);
        existing.setPricePerKm(price);
        existing.setStatus(status);

        if (dao.updateTaxi(existing)) {
            System.out.println("SUCCESS: Taxi updated successfully!");
        } else {
            System.out.println("ERROR: Failed to update taxi.");
        }
    }

    private void deleteTaxi() {
        System.out.println("\n--- Delete Taxi ---");
        System.out.print("Enter Taxi ID to Delete: ");
        int id = readInt();
        if (dao.deleteTaxi(id)) {
            System.out.println("SUCCESS: Taxi deleted successfully!");
        } else {
            System.out.println("ERROR: Taxi not found or deletion failed.");
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
            if (input.isEmpty()) {
                System.out.println("Input cannot be blank. Please try again.");
            }
        } while (input.isEmpty());
        return input;
    }
}
