package dashboard;

import model.Admin;
import java.util.Scanner;

public class AdminDashboard {

    private final Scanner sc = new Scanner(System.in);

    public void menu(Admin admin) {
        while (true) {
            System.out.println("\n======================================");
            System.out.println("      ADMIN DASHBOARD - Welcome " + admin.getName().toUpperCase());
            System.out.println("======================================");
            System.out.println("1. Manage Airports");
            System.out.println("2. Manage Flights");
            System.out.println("3. Manage Customers");
            System.out.println("4. Manage Passengers");
            System.out.println("5. Manage Bookings");
            System.out.println("6. Manage Tickets");
            System.out.println("7. Manage Payments");
            System.out.println("8. Manage Hotels");
            System.out.println("9. Manage Hotel Booking");
            System.out.println("10. Manage Taxi");
            System.out.println("11. Manage Notifications");
            System.out.println("12. Manage Feedback");
            System.out.println("13. Logout");
            System.out.print("\nEnter Choice: ");

            int choice = readInt();
            switch (choice) {
                case 1:
                    new AirportDashboard().menu();
                    break;
                case 2:
                    new FlightDashboard().menu();
                    break;
                case 3:
                    new CustomerDashboard().adminMenu();
                    break;
                case 4:
                    new PassengerDashboard().menu();
                    break;
                case 5:
                    new BookingDashboard().menu();
                    break;
                case 6:
                    new TicketDashboard().menu();
                    break;
                case 7:
                    new PaymentDashboard().menu();
                    break;
                case 8:
                    new HotelDashboard().menu();
                    break;
                case 9:
                    new HotelBookingDashboard().menu();
                    break;
                case 10:
                    new TaxiDashboard().menu();
                    break;
                case 11:
                    new NotificationDashboard().menu();
                    break;
                case 12:
                    new FeedbackDashboard().menu();
                    break;
                case 13:
                    System.out.println("Admin logged out successfully.");
                    return;
                default:
                    System.out.println("Invalid Choice. Please try again.");
            }
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
}
