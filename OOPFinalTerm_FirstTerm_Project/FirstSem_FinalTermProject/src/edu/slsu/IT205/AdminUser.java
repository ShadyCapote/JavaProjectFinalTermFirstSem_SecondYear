package edu.slsu.IT205;

/** Administrator: full access, including cancelling tickets. */
public class AdminUser extends User {
 
    public AdminUser(String username, String password) {
        super(username, password);
    }
 
    @Override
    public String getRole() { return "Admin"; }
 
    @Override
    public boolean canCancelTickets() { return true; }
}