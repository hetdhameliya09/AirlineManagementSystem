package dashboard;

import dao.TicketDAO;
import model.Ticket;
import validation.DateValidator;
import validation.ValidationUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class TicketDashboard {

    private final TicketDAO dao = new TicketDAO();
    private final Scanner sc = new Scanner(System.in);

    public void menu() {
        while (true) {
            System.out.println("\n======================================");
            System.out.println("           TICKET MANAGEMENT");
            System.out.println("======================================");
            System.out.println("1. Add Ticket");
            System.out.println("2. View All Tickets");
            System.out.println("3. Search Ticket");
            System.out.println("4. Update Ticket");
            System.out.println("5. Delete Ticket");
            System.out.println("6. Back");
            System.out.print("Enter Choice: ");

            int choice = readInt();
            switch (choice) {
                case 1:
                    addTicket();
                    break;
                case 2:
                    viewAllTickets();
                    break;
                case 3:
                    searchTicket();
                    break;
                case 4:
                    updateTicket();
                    break;
                case 5:
                    deleteTicket();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid Choice. Please try again.");
            }
        }
    }

    private void addTicket() {
        System.out.println("\n--- Add New Ticket ---");
        System.out.print("Enter Booking ID: ");
        int bookingId = readInt();
        System.out.print("Enter Passenger ID: ");
        int passengerId = readInt();
        String ticketNo = readNonEmptyString("Enter Ticket Number (e.g. TKT-98765): ");
        String seatNo = readNonEmptyString("Enter Seat Number: ");
        System.out.print("Enter Ticket Price: ");
        double price = readDouble();
        
        // Auto insert current issue date
        String issueDate = DateValidator.today().toString();
        System.out.println("Issue Date (Auto): " + issueDate);

        Ticket ticket = new Ticket(bookingId, passengerId, ticketNo, seatNo, price, issueDate);
        if (dao.addTicket(ticket)) {
            System.out.println("SUCCESS: Ticket generated successfully!");
        } else {
            System.out.println("ERROR: Failed to add ticket.");
        }
    }

    private void viewAllTickets() {
        System.out.println("\n--- All Tickets ---");
        List<Ticket> list = dao.getAllTickets();
        if (list.isEmpty()) {
            System.out.println("No tickets found.");
        } else {
            for (Ticket t : list) {
                System.out.println(t);
            }
        }
    }

    private void searchTicket() {
        System.out.println("\n--- Search Ticket ---");
        String ticketNo = readNonEmptyString("Enter Ticket Number or ID keyword: ");
        Ticket t = dao.searchTicketByNumber(ticketNo);
        if (t == null) {
            try {
                int id = Integer.parseInt(ticketNo);
                t = dao.searchTicketById(id);
            } catch (NumberFormatException ignored) {}
        }
        if (t == null) {
            System.out.println("Ticket not found.");
        } else {
            System.out.println(t);
        }
    }

    private void updateTicket() {
        System.out.println("\n--- Update Ticket ---");
        System.out.print("Enter Ticket ID to Update: ");
        int id = readInt();
        Ticket existing = dao.searchTicketById(id);
        if (existing == null) {
            System.out.println("ERROR: Ticket not found with ID: " + id);
            return;
        }
        System.out.println("Current: " + existing);
        System.out.print("Enter New Booking ID: ");
        int bId = readInt();
        System.out.print("Enter New Passenger ID: ");
        int pId = readInt();
        String ticketNo = readNonEmptyString("Enter New Ticket Number: ");
        String seatNo = readNonEmptyString("Enter New Seat Number: ");
        System.out.print("Enter New Price: ");
        double price = readDouble();
        
        LocalDate issueDate = DateValidator.inputDate(sc, "Enter New Issue Date (yyyy-MM-dd): ");

        existing.setBookingId(bId);
        existing.setPassengerId(pId);
        existing.setTicketNumber(ticketNo);
        existing.setSeatNumber(seatNo);
        existing.setPrice(price);
        existing.setIssueDate(issueDate.toString());

        if (dao.updateTicket(existing)) {
            System.out.println("SUCCESS: Ticket updated successfully!");
        } else {
            System.out.println("ERROR: Failed to update ticket.");
        }
    }

    private void deleteTicket() {
        System.out.println("\n--- Delete Ticket ---");
        System.out.print("Enter Ticket ID to Delete: ");
        int id = readInt();
        if (dao.deleteTicket(id)) {
            System.out.println("SUCCESS: Ticket deleted successfully!");
        } else {
            System.out.println("ERROR: Ticket not found or deletion failed.");
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
