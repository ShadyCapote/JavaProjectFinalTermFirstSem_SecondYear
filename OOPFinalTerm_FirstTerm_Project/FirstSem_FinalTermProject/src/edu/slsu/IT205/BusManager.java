package edu.slsu.IT205;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the list of buses.
 */
public class BusManager {
    private final List<Bus> buses = new ArrayList<>();

    public BusManager() {
        buses.add(new Bus(1, "Southern Leyte - Silago", "06:00 AM", 200.00, 20));
        buses.add(new Bus(2, "Southern Leyte - Sogod", "08:00 AM", 150.00, 20));
        buses.add(new Bus(3, "Southern Leyte - Maasin", "09:30 AM", 180.00, 20));
        buses.add(new Bus(4, "Southern Leyte - Hilongos", "08:00 PM", 220.00, 20));
    }

    /** Returns the bus with this ID, or null if none. */
    public Bus findById(int id) {
        for (Bus b : buses) {
            if (b.getId() == id)
                return b;
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