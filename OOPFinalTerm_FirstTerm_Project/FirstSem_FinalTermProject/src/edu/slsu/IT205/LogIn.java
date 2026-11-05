package edu.slsu.IT205;

import java.util.ArrayList;
import java.util.List;

/**
 * Login class: stores the user accounts and verifies username/password.
 * Gives the user up to 3 attempts.
 *
 * Default accounts:
 * admin / admin123
 * cashier / cashier123
 */
public class LogIn {
    private static final int MAX_ATTEMPTS = 3;

    private final List<User> users = new ArrayList<>();
    private final ConsoleInput input;

    public LogIn(ConsoleInput input) {
        this.input = input;
        users.add(new AdminUser("admin", "admin123"));
        users.add(new CashierUser("cashier", "cashier123"));
    }

    /**
     * Shows the login screen.
     * 
     * @return the logged-in User, or null if the person typed "exit"
     *         or failed too many times.
     */
    public User start() {
        int attempts = 0;

        while (attempts < MAX_ATTEMPTS) {
            System.out.println("\n==========================================");
            System.out.println("              LOGIN");
            System.out.println("==========================================");
            String username = input.readLine("Username (type 'exit' to quit): ");
            if (username.equalsIgnoreCase("exit"))
                return null;
            String password = input.readPassword("Password: ");

            User user = authenticate(username, password);
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
