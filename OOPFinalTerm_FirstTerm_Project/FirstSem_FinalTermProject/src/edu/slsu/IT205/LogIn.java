package edu.slsu.IT205;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Login class: stores the user accounts and verifies username/password.
 * Gives the user up to 3 attempts.
 *
 * Default accounts:
 * admin / admin123
 * cashier / cashier123
 *
 * Exception handling used in this class:
 * - IllegalArgumentException -> an account could not be created (constructor)
 * - NoSuchElementException -> the keyboard input was closed (start)
 * - RuntimeException -> the password could not be checked (start)
 */
public class LogIn {
    private static final int MAX_ATTEMPTS = 3;

    private final List<User> users = new ArrayList<>();
    private final ConsoleInput input;

    public LogIn(ConsoleInput input) {
        this.input = input;
        try {
            users.add(new AdminUser("admin", "admin123"));
            users.add(new CashierUser("cashier", "cashier123"));
        } catch (IllegalArgumentException e) {
            // Thrown when a username or password is empty
            // (only if your User class checks this).
            System.out.println("Could not create the default accounts: " + e.getMessage());
        }
    }

    /**
     * Shows the login screen.
     * 
     * @return the logged-in User, or null if the person typed "exit",
     *         failed too many times, or the input was closed.
     */
    public User start() {
        int attempts = 0;

        while (attempts < MAX_ATTEMPTS) {
            System.out.println("\n==========================================");
            System.out.println("              LOGIN");
            System.out.println("==========================================");

            String username;
            String password;
            try {
                username = input.readLine("Enter username (type 'exit' to quit): ");
                if (username.equalsIgnoreCase("exit"))
                    return null;
                password = input.readPassword("Enter password: ");
            } catch (NoSuchElementException e) {
                // The keyboard input was closed (Ctrl+D / Ctrl+Z),
                // so nothing more can be read. Leave the login politely.
                System.out.println("\nInput was closed. Closing the login.");
                return null;
            }

            User user = null;
            try {
                user = authenticate(username, password);
            } catch (RuntimeException e) {
                // For example, the password could not be checked.
                // This counts as a failed attempt instead of crashing the program.
                System.out.println("Login error: " + e.getMessage());
            }

            if (user != null) {
                System.out.println("\nWelcome, " + user.getUsername() + " (" + user.getRole() + ")!");
                return user;
            }

            attempts++;
            int left = MAX_ATTEMPTS - attempts;
            if (left > 0) {
                System.out.println("Incorrect username or password. Attempts left: " + left);
            }
        }

        System.out.println("\nToo many failed attempts. The system is now closing.");
        return null;
    }

    /** Returns the matching User, or null if the credentials are wrong. */
    private User authenticate(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username) && u.checkPassword(password)) {
                return u;
            }
        }
        return null;
    }
}
