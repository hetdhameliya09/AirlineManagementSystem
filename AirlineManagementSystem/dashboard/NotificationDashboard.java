package dashboard;

import dao.NotificationDAO;
import model.Notification;
import validation.DateValidator;
import validation.ValidationUtils;
import java.util.List;
import java.util.Scanner;

public class NotificationDashboard {

    private final NotificationDAO dao = new NotificationDAO();
    private final Scanner sc = new Scanner(System.in);

    public void menu() {
        while (true) {
            System.out.println("\n======================================");
            System.out.println("       NOTIFICATION MANAGEMENT");
            System.out.println("======================================");
            System.out.println("1. Add Notification");
            System.out.println("2. View All Notifications");
            System.out.println("3. Search Notification");
            System.out.println("4. Update Notification");
            System.out.println("5. Delete Notification");
            System.out.println("6. Back");
            System.out.print("Enter Choice: ");

            int choice = readInt();
            switch (choice) {
                case 1:
                    addNotification();
                    break;
                case 2:
                    viewAllNotifications();
                    break;
                case 3:
                    searchNotification();
                    break;
                case 4:
                    updateNotification();
                    break;
                case 5:
                    deleteNotification();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid Choice. Please try again.");
            }
        }
    }

    private void addNotification() {
        System.out.println("\n--- Send Notification ---");
        System.out.print("Enter Target Customer ID: ");
        int customerId = readInt();
        String message = readNonEmptyString("Enter Notification Message: ");
        
        // Auto insert current timestamp
        String date = DateValidator.nowTimestamp();
        System.out.println("Created Timestamp (Auto): " + date);

        String status = readNonEmptyString("Enter Status (UNREAD/READ): ");

        Notification notification = new Notification(customerId, message, date, status);
        if (dao.addNotification(notification)) {
            System.out.println("SUCCESS: Notification sent successfully!");
        } else {
            System.out.println("ERROR: Failed to send notification.");
        }
    }

    private void viewAllNotifications() {
        System.out.println("\n--- All Notifications ---");
        List<Notification> list = dao.getAllNotifications();
        if (list.isEmpty()) {
            System.out.println("No notifications found.");
        } else {
            for (Notification n : list) {
                System.out.println(n);
            }
        }
    }

    private void searchNotification() {
        System.out.println("\n--- Search Notification ---");
        System.out.print("Enter Notification ID: ");
        int id = readInt();
        Notification n = dao.searchNotificationById(id);
        if (n == null) {
            System.out.println("No notification found with ID: " + id);
        } else {
            System.out.println(n);
        }
    }

    private void updateNotification() {
        System.out.println("\n--- Update Notification ---");
        System.out.print("Enter Notification ID to Update: ");
        int id = readInt();
        Notification existing = dao.searchNotificationById(id);
        if (existing == null) {
            System.out.println("ERROR: Notification not found with ID: " + id);
            return;
        }
        System.out.println("Current: " + existing);
        System.out.print("Enter New Customer ID: ");
        int custId = readInt();
        String message = readNonEmptyString("Enter New Message: ");
        String date = DateValidator.nowTimestamp();
        String status = readNonEmptyString("Enter New Status (READ/UNREAD): ");

        existing.setCustomerId(custId);
        existing.setMessage(message);
        existing.setCreatedAt(date);
        existing.setStatus(status);

        if (dao.updateNotification(existing)) {
            System.out.println("SUCCESS: Notification updated successfully!");
        } else {
            System.out.println("ERROR: Failed to update notification.");
        }
    }

    private void deleteNotification() {
        System.out.println("\n--- Delete Notification ---");
        System.out.print("Enter Notification ID to Delete: ");
        int id = readInt();
        if (dao.deleteNotification(id)) {
            System.out.println("SUCCESS: Notification deleted successfully!");
        } else {
            System.out.println("ERROR: Notification not found or deletion failed.");
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
            if (!ValidationUtils.isNotEmpty(input)) {
                System.out.println("Input cannot be blank. Please try again.");
            }
        } while (!ValidationUtils.isNotEmpty(input));
        return input;
    }
}
