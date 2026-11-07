package edu.slsu.IT205;

/**
 * Checked exception thrown when a booking cannot be completed
 * (unknown bus, full bus, bad age, invalid or taken seat, empty name).
 * The caller MUST handle it with try/catch, so an error can never be ignored.
 */
public class BookingException extends Exception {
    private static final long serialVersionUID = 1L;

    public BookingException(String message) {
        super(message);
    }
}
