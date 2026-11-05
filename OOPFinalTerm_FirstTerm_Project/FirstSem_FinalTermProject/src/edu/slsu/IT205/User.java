package edu.slsu.IT205;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Abstract parent class of all system users.
 * Passwords are never stored as plain text; only a SHA-256 hash is kept.
 */
public abstract class User {
    private final String username;
    private final String passwordHash;

    protected User(String username, String password) {
        this.username = username;
        this.passwordHash = hash(password);
    }

    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String password) {
        return passwordHash.equals(hash(password));
    }

    // Each role decides its own name and permissions (polymorphism)
    public abstract String getRole();

    public abstract boolean canCancelTickets();

    private static String hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
            return String.format("%064x", new BigInteger(1, bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
