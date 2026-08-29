package dashboard;

import dao.HotelDAO;
import model.Hotel;
import java.util.List;
import java.util.Scanner;

public class HotelDashboard {

    private final HotelDAO dao = new HotelDAO();
    private final Scanner sc = new Scanner(System.in);

    public void menu() {
        while (true) {
            System.out.println("\n======================================");
            System.out.println("           HOTEL MANAGEMENT");
            System.out.println("======================================");
            System.out.println("1. Add Hotel");
            System.out.println("2. View All Hotels");
            System.out.println("3. Search Hotel");
            System.out.println("4. Update Hotel");
            System.out.println("5. Delete Hotel");
            System.out.println("6. Back");
            System.out.print("Enter Choice: ");

            int choice = readInt();
            switch (choice) {
                case 1:
                    addHotel();
                    break;
                case 2:
                    viewAllHotels();
                    break;
                case 3:
                    searchHotel();
                    break;
                case 4:
                    updateHotel();
                    break;
                case 5:
                    deleteHotel();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid Choice. Please try again.");
            }
        }
    }

    private void addHotel() {
        System.out.println("\n--- Add New Hotel ---");
        String name = readNonEmptyString("Enter Hotel Name: ");
        String city = readNonEmptyString("Enter City: ");
        System.out.print("Enter Price Per Night: ");
        double price = readDouble();
        System.out.print("Enter Available Rooms: ");
        int rooms = readInt();
        System.out.print("Enter Rating (1.0 to 5.0): ");
        double rating = readDouble();

        Hotel hotel = new Hotel(name, city, price, rooms, rating);
        if (dao.addHotel(hotel)) {
            System.out.println("SUCCESS: Hotel added successfully!");
        } else {
            System.out.println("ERROR: Failed to add hotel.");
        }
    }

    private void viewAllHotels() {
        System.out.println("\n--- All Hotels ---");
        List<Hotel> list = dao.getAllHotels();
        if (list.isEmpty()) {
            System.out.println("No hotels found.");
        } else {
            for (Hotel h : list) {
                System.out.println(h);
            }
        }
    }

    private void searchHotel() {
        System.out.println("\n--- Search Hotel ---");
        String city = readNonEmptyString("Enter Hotel Name or City keyword: ");
        List<Hotel> list = dao.searchHotelsByCity(city);
        if (list.isEmpty()) {
            System.out.println("No matching hotels found.");
        } else {
            for (Hotel h : list) {
                System.out.println(h);
            }
        }
    }

    private void updateHotel() {
        System.out.println("\n--- Update Hotel ---");
        System.out.print("Enter Hotel ID to Update: ");
        int id = readInt();
        Hotel existing = dao.searchHotelById(id);
        if (existing == null) {
            System.out.println("ERROR: Hotel not found with ID: " + id);
            return;
        }
        System.out.println("Current: " + existing);
        String name = readNonEmptyString("Enter New Hotel Name: ");
        String city = readNonEmptyString("Enter New City: ");
        System.out.print("Enter New Price Per Night: ");
        double price = readDouble();
        System.out.print("Enter New Available Rooms: ");
        int rooms = readInt();
        System.out.print("Enter New Rating: ");
        double rating = readDouble();

        existing.setHotelName(name);
        existing.setCity(city);
        existing.setPricePerNight(price);
        existing.setAvailableRooms(rooms);
        existing.setRating(rating);

        if (dao.updateHotel(existing)) {
            System.out.println("SUCCESS: Hotel updated successfully!");
        } else {
            System.out.println("ERROR: Failed to update hotel.");
        }
    }

    private void deleteHotel() {
        System.out.println("\n--- Delete Hotel ---");
        System.out.print("Enter Hotel ID to Delete: ");
        int id = readInt();
        if (dao.deleteHotel(id)) {
            System.out.println("SUCCESS: Hotel deleted successfully!");
        } else {
            System.out.println("ERROR: Hotel not found or deletion failed.");
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
