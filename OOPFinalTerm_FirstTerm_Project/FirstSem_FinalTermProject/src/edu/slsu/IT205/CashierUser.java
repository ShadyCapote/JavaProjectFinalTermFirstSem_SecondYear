package edu.slsu.IT205;

/** Cashier: can book, view and search tickets, but cannot cancel them. */
public class CashierUser extends User {

    public CashierUser(String username, String password) {
        super(username, password);
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