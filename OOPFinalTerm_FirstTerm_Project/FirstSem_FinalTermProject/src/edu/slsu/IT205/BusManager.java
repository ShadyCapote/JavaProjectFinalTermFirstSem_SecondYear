package edu.slsu.IT205;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the list of buses.
 *
 * Exception handling used in this class:
 * - IllegalArgumentException -> a bus has invalid values or a duplicate ID
 * (addBus)
 */
public class BusManager {
    private final List<Bus> buses = new ArrayList<>();

    public BusManager() {
        addBus(1, "Southern Leyte - Silago", "06:00 AM", 200.00, 20);
        addBus(2, "Southern Leyte - Sogod", "08:00 AM", 150.00, 20);
        addBus(3, "Southern Leyte - Maasin", "09:30 AM", 180.00, 20);
        addBus(4, "Southern Leyte - Hilongos", "08:00 PM", 220.00, 20);
    }

    /**
     * Adds one bus. If the bus is not valid (no seats, negative fare, or an ID
     * that already exists), the problem is reported and ONLY that bus is skipped,
     * so the other buses still load.
     */
    private void addBus(int id, String route, String departure, double fare, int capacity) {
        try {
            if (findById(id) != null) {
                throw new IllegalArgumentException("Bus ID " + id + " already exists.");
            }
            buses.add(new Bus(id, route, departure, fare, capacity));
        } catch (IllegalArgumentException e) {
            System.out.println("Skipped bus " + id + ": " + e.getMessage());
        }
    }

    /**
     * Returns all the buses. The list is read-only, so other classes can look
     * at the buses but cannot add or remove them.
     *
     * @return the list of buses
     */
    public List<Bus> getBuses() {
        return Collections.unmodifiableList(buses);
    }

    /** Returns the bus with this ID, or null if none. */
    public Bus findById(int id) {
        for (Bus b : buses) {
            if (b.getId() == id) {
                return b;
            }
        }
        return null;
    }

    public void displayAll() {
        System.out.println("\n----------------- AVAILABLE BUSES -----------------");
        Bus.printHeader();
        for (Bus b : buses) {
            b.print();
        }
    }
}