package edu.slsu.IT205;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages the list of buses.
 * <p>
 * Four buses are built in. The Admin can add more routes with
 * {@link #addRoute(String, String, double, int)}. The added routes are saved
 * in a text file (one bus per line: {@code id|route|departure|fare|seats}) and
 * loaded again when the program starts, so they are not lost and the tickets
 * booked on them can be loaded too.
 * <p>
 * Exception handling used in this class:
 * <ul>
 * <li>IllegalArgumentException: a bus or route has invalid values (a built-in
 * bus, a new route, or a damaged line in the file).</li>
 * <li>IOException: the route file cannot be read or written; a friendly warning
 * is shown and the program keeps running.</li>
 * </ul>
 */
public class BusManager {
    /**
     * The longest route name allowed. It fits the column of the bus and records
     * tables.
     */
    public static final int MAX_ROUTE_LENGTH = 27;

    /** The most seats a bus can have. */
    public static final int MAX_SEATS = 60;

    /** The highest fare allowed, in pesos. */
    public static final double MAX_FARE = 9999.99;

    /** A time like 6:30 am or 06:30 PM. */
    private static final Pattern TIME = Pattern.compile("^(\\d{1,2}):(\\d{2})\\s*([AaPp][Mm])$");

    private final List<Bus> buses = new ArrayList<>();
    private final List<Bus> addedRoutes = new ArrayList<>(); // only the routes added by the Admin
    private final String fileName; // null = the added routes are not saved

    /**
     * Creates the manager with the four built-in buses only. Nothing is read or
     * saved.
     */
    public BusManager() {
        this(null);
    }

    /**
     * Creates the manager with the four built-in buses, then loads the routes
     * saved in the file.
     *
     * @param fileName the file for the added routes, for example
     *                 {@code "buses.txt"};
     *                 {@code null} to keep the routes in memory only
     */
    public BusManager(String fileName) {
        this.fileName = fileName;
        addBus(1, "Southern Leyte - Silago", "06:00 AM", 200.00, 20);
        addBus(2, "Southern Leyte - Sogod", "08:00 AM", 150.00, 20);
        addBus(3, "Southern Leyte - Maasin", "09:30 AM", 180.00, 20);
        addBus(4, "Southern Leyte - Hilongos", "08:00 PM", 220.00, 20);
        load();
    }

    /**
     * Adds one built-in bus. If the bus is not valid (no seats, negative fare, or
     * an ID
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

    // ---------- checking what the Admin types ----------

    /**
     * Cleans and checks a route name: extra spaces are removed, and a {@code |}
     * (used in the save file) becomes a space.
     *
     * @param route the route as typed, for example
     *              {@code "Maasin City  -  Cebu City"}
     * @return the cleaned route, for example {@code "Maasin City - Cebu City"}
     * @throws IllegalArgumentException if the route is empty or longer than
     *                                  {@link #MAX_ROUTE_LENGTH}
     */
    public static String cleanRoute(String route) {
        String cleaned = route == null ? "" : route.replace("|", " ").trim().replaceAll("\\s+", " ");
        if (cleaned.isEmpty()) {
            throw new IllegalArgumentException("Route cannot be empty.");
        }
        if (cleaned.length() > MAX_ROUTE_LENGTH) {
            throw new IllegalArgumentException("Route must be at most " + MAX_ROUTE_LENGTH + " characters.");
        }
        return cleaned;
    }

    /**
     * Cleans and checks a departure time such as {@code 6:30 am}.
     *
     * @param departure the time as typed: hour 1 to 12, minutes 00 to 59, then AM
     *                  or PM
     * @return the time in the usual form, for example {@code "06:30 AM"}
     * @throws IllegalArgumentException if the time does not have that form
     */
    public static String cleanDeparture(String departure) {
        Matcher m = TIME.matcher(departure == null ? "" : departure.trim());
        if (m.matches()) {
            int hour = Integer.parseInt(m.group(1));
            int minute = Integer.parseInt(m.group(2));
            if (hour >= 1 && hour <= 12 && minute <= 59) {
                return String.format("%02d:%02d %s", hour, minute, m.group(3).toUpperCase());
            }
        }
        throw new IllegalArgumentException(
                "Departure time must look like 06:30 AM (hour 1 to 12, minutes 00 to 59, then AM or PM).");
    }

    /**
     * Checks a fare and rounds it to centavos.
     *
     * @param fare the fare in pesos
     * @return the fare rounded to two decimals
     * @throws IllegalArgumentException if the fare is not more than 0 and at most
     *                                  {@link #MAX_FARE}
     */
    public static double cleanFare(double fare) {
        if (Double.isNaN(fare) || fare <= 0 || fare > MAX_FARE) {
            throw new IllegalArgumentException("Fare must be more than 0 and at most " + MAX_FARE + ".");
        }
        return Math.round(fare * 100) / 100.0;
    }

    /**
     * Checks the number of seats.
     *
     * @param seats the number of seats
     * @throws IllegalArgumentException if it is not from 1 to {@link #MAX_SEATS}
     */
    public static void checkSeats(int seats) {
        if (seats < 1 || seats > MAX_SEATS) {
            throw new IllegalArgumentException("Number of seats must be from 1 to " + MAX_SEATS + ".");
        }
    }

    // ---------- adding a route ----------

    /**
     * Adds a new bus route. The new bus gets the next free ID, starts with every
     * seat free, and is saved in the route file.
     *
     * @param route     the route, for example {@code "Maasin City - Cebu City"}
     * @param departure the departure time, for example {@code "06:30 AM"}
     * @param fare      the full fare in pesos
     * @param seats     the number of seats
     * @return the new bus
     * @throws IllegalArgumentException if a value is not valid, or if the same
     *                                  route
     *                                  and departure time already exist
     */
    public Bus addRoute(String route, String departure, double fare, int seats) {
        String cleanRoute = cleanRoute(route);
        String cleanTime = cleanDeparture(departure);
        double cleanFare = cleanFare(fare);
        checkSeats(seats);

        for (Bus b : buses) {
            if (b.getRoute().equalsIgnoreCase(cleanRoute) && b.getDeparture().equalsIgnoreCase(cleanTime)) {
                throw new IllegalArgumentException(
                        "That route and departure time already exist (Bus " + b.getId() + ").");
            }
        }

        Bus bus = new Bus(nextId(), cleanRoute, cleanTime, cleanFare, seats);
        buses.add(bus);
        addedRoutes.add(bus);
        save();
        return bus;
    }

    /** One more than the highest bus ID in use. */
    private int nextId() {
        int highest = 0;
        for (Bus b : buses)
            highest = Math.max(highest, b.getId());
        return highest + 1;
    }

    // ---------- looking buses up ----------

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

    // ---------- the route file ----------

    /**
     * Writes the added routes to the file. If that is not possible, a friendly
     * warning is shown and the program keeps running with the routes in memory.
     */
    private void save() {
        if (fileName == null)
            return;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Bus b : addedRoutes) {
                writer.write(String.join("|", String.valueOf(b.getId()), b.getRoute(), b.getDeparture(),
                        String.valueOf(b.getFare()), String.valueOf(b.getCapacity())));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not save the bus routes to " + fileName + ". Reason: " + reasonOf(e));
            System.out.println("The new route is kept only until you close the program. "
                    + "Check that the file is not open in another program and that its folder can be written to.");
        }
    }

    /**
     * Loads the added routes from the file. A damaged line is skipped instead of
     * stopping the program, and a warning is shown at the end.
     */
    private void load() {
        if (fileName == null)
            return;
        File file = new File(fileName);
        if (!file.exists())
            return;

        int skipped = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                try {
                    loadLine(line);
                } catch (RuntimeException e) {
                    skipped++; // NumberFormatException, ArrayIndexOutOfBounds, IllegalArgument...
                }
            }
        } catch (IOException e) {
            System.out.println(
                    "Warning: Could not read the saved bus routes from " + fileName + ". Reason: " + reasonOf(e));
            System.out.println("Some or all of the saved routes may be missing.");
        }
        if (skipped > 0) {
            System.out.println("Warning: " + skipped + " damaged bus route(s) in " + fileName + " were skipped.");
        }
    }

    /**
     * Reads one line of the file. Throws a RuntimeException if the line is not
     * valid.
     */
    private void loadLine(String line) {
        String[] p = line.split("\\|");
        int id = Integer.parseInt(p[0].trim());
        String route = cleanRoute(p[1]);
        String departure = cleanDeparture(p[2]);
        double fare = cleanFare(Double.parseDouble(p[3]));
        int seats = Integer.parseInt(p[4].trim());
        checkSeats(seats);
        if (id < 1 || findById(id) != null) {
            throw new IllegalArgumentException("Bad or duplicate bus id");
        }
        Bus bus = new Bus(id, route, departure, fare, seats);
        buses.add(bus);
        addedRoutes.add(bus);
    }

    /**
     * Gets the reason of a file problem in plain words (the part in brackets,
     * without the file name).
     */
    private static String reasonOf(IOException e) {
        String message = e.getMessage();
        if (message == null || message.isEmpty())
            return "unknown problem";
        int open = message.lastIndexOf('(');
        if (message.endsWith(")") && open >= 0) {
            return message.substring(open + 1, message.length() - 1);
        }
        return message;
    }
}