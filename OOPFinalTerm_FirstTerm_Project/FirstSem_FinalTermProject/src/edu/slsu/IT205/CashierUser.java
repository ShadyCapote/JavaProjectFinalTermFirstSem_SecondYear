package edu.slsu.IT205;

/** Cashier: can book, view and search tickets, but cannot cancel them. */
public class CashierUser extends User {

    public CashierUser(String username, String password) {
        super(username, password);
    }

    /**
     * Safe way to create a cashier account.
     * If the username or password is not accepted (for example, empty),
     * it prints the reason and returns null instead of crashing the program.
     */
    public static CashierUser create(String username, String password) {
        try {
            return new CashierUser(username, password);
        } catch (IllegalArgumentException e) {
            System.out.println("Could not create cashier account: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String getRole() {
        return "Cashier";
    }

    @Override
    public boolean canCancelTickets() {
        return false;
    }
}