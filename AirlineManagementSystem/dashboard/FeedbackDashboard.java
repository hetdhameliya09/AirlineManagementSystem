package dashboard;

import dao.FeedbackDAO;
import model.Feedback;
import validation.DateValidator;
import validation.ValidationUtils;
import java.util.List;
import java.util.Scanner;

public class FeedbackDashboard {

    private final FeedbackDAO dao = new FeedbackDAO();
    private final Scanner sc = new Scanner(System.in);

    public void menu() {
        while (true) {
            System.out.println("\n======================================");
            System.out.println("          FEEDBACK MANAGEMENT");
            System.out.println("======================================");
            System.out.println("1. Add Feedback");
            System.out.println("2. View All Feedback");
            System.out.println("3. Search Feedback");
            System.out.println("4. Update Feedback");
            System.out.println("5. Delete Feedback");
            System.out.println("6. Back");
            System.out.print("Enter Choice: ");

            int choice = readInt();
            switch (choice) {
                case 1:
                    addFeedback();
                    break;
                case 2:
                    viewAllFeedback();
                    break;
                case 3:
                    searchFeedback();
                    break;
                case 4:
                    updateFeedback();
                    break;
                case 5:
                    deleteFeedback();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid Choice. Please try again.");
            }
        }
    }

    private void addFeedback() {
        System.out.println("\n--- Add Feedback ---");
        System.out.print("Enter Customer ID: ");
        int customerId = readInt();
        System.out.print("Enter Rating (1 to 5): ");
        int rating = readInt();
        String comments = readNonEmptyString("Enter Comments: ");
        
        // Auto insert current date
        String date = DateValidator.today().toString();
        System.out.println("Date (Auto): " + date);

        Feedback feedback = new Feedback(customerId, rating, comments, date);
        if (dao.addFeedback(feedback)) {
            System.out.println("SUCCESS: Feedback submitted successfully!");
        } else {
            System.out.println("ERROR: Failed to add feedback.");
        }
    }

    private void viewAllFeedback() {
        System.out.println("\n--- All Feedback ---");
        List<Feedback> list = dao.getAllFeedback();
        if (list.isEmpty()) {
            System.out.println("No feedback found.");
        } else {
            for (Feedback f : list) {
                System.out.println(f);
            }
        }
    }

    private void searchFeedback() {
        System.out.println("\n--- Search Feedback ---");
        System.out.print("Enter Feedback ID: ");
        int id = readInt();
        Feedback f = dao.searchFeedbackById(id);
        if (f == null) {
            System.out.println("No feedback found with ID: " + id);
        } else {
            System.out.println(f);
        }
    }

    private void updateFeedback() {
        System.out.println("\n--- Update Feedback ---");
        System.out.print("Enter Feedback ID to Update: ");
        int id = readInt();
        Feedback existing = dao.searchFeedbackById(id);
        if (existing == null) {
            System.out.println("ERROR: Feedback not found with ID: " + id);
            return;
        }
        System.out.println("Current: " + existing);
        System.out.print("Enter New Customer ID: ");
        int custId = readInt();
        System.out.print("Enter New Rating (1 to 5): ");
        int rating = readInt();
        String comments = readNonEmptyString("Enter New Comments: ");
        String date = DateValidator.today().toString();

        existing.setCustomerId(custId);
        existing.setRating(rating);
        existing.setComments(comments);
        existing.setCreatedAt(date);

        if (dao.updateFeedback(existing)) {
            System.out.println("SUCCESS: Feedback updated successfully!");
        } else {
            System.out.println("ERROR: Failed to update feedback.");
        }
    }

    private void deleteFeedback() {
        System.out.println("\n--- Delete Feedback ---");
        System.out.print("Enter Feedback ID to Delete: ");
        int id = readInt();
        if (dao.deleteFeedback(id)) {
            System.out.println("SUCCESS: Feedback deleted successfully!");
        } else {
            System.out.println("ERROR: Feedback not found or deletion failed.");
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
