package dashboard;

import dao.PaymentDAO;
import model.Payment;
import validation.DateValidator;
import validation.ValidationUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class PaymentDashboard {

    private final PaymentDAO dao = new PaymentDAO();
    private final Scanner sc = new Scanner(System.in);

    public void menu() {
        while (true) {
            System.out.println("\n======================================");
            System.out.println("          PAYMENT MANAGEMENT");
            System.out.println("======================================");
            System.out.println("1. Add Payment");
            System.out.println("2. View All Payments");
            System.out.println("3. Search Payment");
            System.out.println("4. Update Payment");
            System.out.println("5. Delete Payment");
            System.out.println("6. Back");
            System.out.print("Enter Choice: ");

            int choice = readInt();
            switch (choice) {
                case 1:
                    addPayment();
                    break;
                case 2:
                    viewAllPayments();
                    break;
                case 3:
                    searchPayment();
                    break;
                case 4:
                    updatePayment();
                    break;
                case 5:
                    deletePayment();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid Choice. Please try again.");
            }
        }
    }

    private void addPayment() {
        System.out.println("\n--- Process New Payment ---");
        System.out.print("Enter Booking ID: ");
        int bookingId = readInt();
        System.out.print("Enter Amount: ");
        double amount = readDouble();
        
        // Auto insert current payment date
        String date = DateValidator.today().toString();
        System.out.println("Payment Date (Auto): " + date);

        String method = readNonEmptyString("Enter Payment Method (Credit Card/Debit Card/UPI/NetBanking): ");
        String status = readNonEmptyString("Enter Payment Status (COMPLETED/PENDING/FAILED): ");

        Payment payment = new Payment(bookingId, amount, date, method, status);
        if (dao.addPayment(payment)) {
            System.out.println("SUCCESS: Payment record created!");
        } else {
            System.out.println("ERROR: Failed to add payment record.");
        }
    }

    private void viewAllPayments() {
        System.out.println("\n--- All Payments ---");
        List<Payment> list = dao.getAllPayments();
        if (list.isEmpty()) {
            System.out.println("No payments found.");
        } else {
            for (Payment p : list) {
                System.out.println(p);
            }
        }
    }

    private void searchPayment() {
        System.out.println("\n--- Search Payment ---");
        System.out.print("Enter Payment ID: ");
        int id = readInt();
        Payment p = dao.searchPaymentById(id);
        if (p == null) {
            System.out.println("Payment record not found for ID: " + id);
        } else {
            System.out.println(p);
        }
    }

    private void updatePayment() {
        System.out.println("\n--- Update Payment ---");
        System.out.print("Enter Payment ID to Update: ");
        int id = readInt();
        Payment existing = dao.searchPaymentById(id);
        if (existing == null) {
            System.out.println("ERROR: Payment not found with ID: " + id);
            return;
        }
        System.out.println("Current: " + existing);
        System.out.print("Enter New Booking ID: ");
        int bId = readInt();
        System.out.print("Enter New Amount: ");
        double amount = readDouble();
        
        LocalDate date = DateValidator.inputDate(sc, "Enter New Payment Date (yyyy-MM-dd): ");
        String method = readNonEmptyString("Enter New Payment Method: ");
        String status = readNonEmptyString("Enter New Status: ");

        existing.setBookingId(bId);
        existing.setAmount(amount);
        existing.setPaymentDate(date.toString());
        existing.setPaymentMethod(method);
        existing.setStatus(status);

        if (dao.updatePayment(existing)) {
            System.out.println("SUCCESS: Payment updated successfully!");
        } else {
            System.out.println("ERROR: Failed to update payment.");
        }
    }

    private void deletePayment() {
        System.out.println("\n--- Delete Payment ---");
        System.out.print("Enter Payment ID to Delete: ");
        int id = readInt();
        if (dao.deletePayment(id)) {
            System.out.println("SUCCESS: Payment record deleted!");
        } else {
            System.out.println("ERROR: Payment not found or deletion failed.");
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
