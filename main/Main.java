package main;

import dao.AdminDAO;
import dao.CustomerDAO;
import dashboard.AdminDashboard;
import dashboard.CustomerDashboard;
import model.Admin;
import model.Customer;
import validation.ValidationUtils;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final AdminDAO adminDAO = new AdminDAO();
    private static final CustomerDAO customerDAO = new CustomerDAO();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n==============================");
            System.out.println("  AIRLINE RESERVATION SYSTEM");
            System.out.println("==============================");
            System.out.println("1. Admin Login");
            System.out.println("2. Customer Registration");
            System.out.println("3. Customer Login");
            System.out.println("4. Exit");
            System.out.print("Enter Choice: ");

            int choice = readInt();
            switch (choice) {
                case 1:
                    adminLogin();
                    break;
                case 2:
                    customerRegistration();
                    break;
                case 3:
                    customerLogin();
                    break;
                case 4:
                    System.out.println("Thank you for using Airline Reservation System. Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid Choice. Please try again.");
            }
        }
    }

    private static void adminLogin() {
        System.out.println("\n--- Admin Login ---");
        String username = readNonEmptyString("Username: ");
        String password = readNonEmptyString("Password: ");

        Admin admin = adminDAO.login(username, password);
        if (admin != null) {
            System.out.println("SUCCESS: Admin Login Successful!");
            new AdminDashboard().menu(admin);
        } else {
            System.out.println("ERROR: Invalid Admin Username or Password.");
        }
    }

    private static void customerRegistration() {
        System.out.println("\n--- Customer Registration ---");
        
        String username;
        do {
            username = readNonEmptyString("Enter Username (min 3 characters): ");
            if (!ValidationUtils.isValidUsername(username)) {
                System.out.println("Invalid username. Must be at least 3 characters.");
            }
        } while (!ValidationUtils.isValidUsername(username));

        String password;
        do {
            password = readNonEmptyString("Enter Password (min 4 characters): ");
            if (!ValidationUtils.isValidPassword(password)) {
                System.out.println("Invalid password. Must be at least 4 characters.");
            }
        } while (!ValidationUtils.isValidPassword(password));

        String name = readNonEmptyString("Enter Full Name: ");

        String email;
        do {
            email = readNonEmptyString("Enter Email (e.g. user@example.com): ");
            if (!ValidationUtils.isValidEmail(email)) {
                System.out.println("Invalid email format. Please try again.");
            }
        } while (!ValidationUtils.isValidEmail(email));

        String phone;
        do {
            phone = readNonEmptyString("Enter Phone Number (10 digits, e.g. 9876543210): ");
            if (!ValidationUtils.isValidPhone(phone)) {
                System.out.println("Invalid phone number format. Please enter a valid phone number.");
            }
        } while (!ValidationUtils.isValidPhone(phone));

        String passport;
        do {
            passport = readNonEmptyString("Enter Passport Number (e.g. K1234567): ");
            if (!ValidationUtils.isValidPassport(passport)) {
                System.out.println("Invalid passport format (6-12 alphanumeric characters required).");
            }
        } while (!ValidationUtils.isValidPassport(passport));

        Customer c = new Customer(username, password, name, email, phone, passport);
        if (customerDAO.addCustomer(c)) {
            System.out.println("SUCCESS: Customer registration successful! You can now log in.");
        } else {
            System.out.println("ERROR: Registration failed. Username may already exist.");
        }
    }

    private static void customerLogin() {
        System.out.println("\n--- Customer Login ---");
        String username = readNonEmptyString("Username: ");
        String password = readNonEmptyString("Password: ");

        Customer customer = customerDAO.login(username, password);
        if (customer != null) {
            System.out.println("SUCCESS: Customer Login Successful!");
            new CustomerDashboard().customerMenu(customer);
        } else {
            System.out.println("ERROR: Invalid Customer Username or Password.");
        }
    }

    private static int readInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            sc.next();
        }
        int val = sc.nextInt();
        sc.nextLine();
        return val;
    }

    private static String readNonEmptyString(String prompt) {
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
