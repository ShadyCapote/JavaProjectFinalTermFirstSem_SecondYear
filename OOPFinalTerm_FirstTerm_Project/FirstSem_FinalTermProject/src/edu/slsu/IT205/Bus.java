package edu.slsu.IT205;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents one bus trip with its own seats.
 * <p>
 * A bus knows its route, departure time, fare and which seats are taken.
 * The seat array is private (encapsulation), so seats can only be changed
 * through {@link #bookSeat(int)} and {@link #releaseSeat(int)}. Seat numbers
 * start at 1, not 0.
 *
 * @see Ticket
 * @see BusManager
 */
public class Bus implements Printable {
    /** How many seats are shown in one row of the seat map. */
    private static final int SEATS_PER_ROW = 4;

    private final int id;
    private final String route;
    private final String departure;
    private final double fare;
    private final boolean[] seats;

    /**
     * Creates a bus with all seats free.
     *
     * @param id        the bus number
     * @param route     the route, for example {@code "Southern Leyte - Maasin"}
     * @param departure the departure time, for example {@code "06:00 AM"}
     * @param fare      the full fare in pesos
     * @param capacity  the number of seats
     * @throws IllegalArgumentException if the capacity is less than 1
     *                                  or the fare is negative
     * @see #create(int, String, String, double, int)
     */
    public Bus(int id, String route, String departure, double fare, int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Bus capacity must be at least 1.");
        }
        if (fare < 0) {
            throw new IllegalArgumentException("Fare cannot be negative.");
        }
        this.id = id;
        this.route = route;
        this.departure = departure;
        this.fare = fare;
        this.seats = new boolean[capacity];
    }

    /**
     * Safe way to create a bus.
     * If a value is not accepted (no seats, negative fare), it prints the
     * reason and returns null instead of crashing the program.
     *
     * @param id        the bus number
     * @param route     the route
     * @param departure the departure time
     * @param fare      the full fare in pesos
     * @param capacity  the number of seats
     * @return the new bus, or {@code null} if a value was not accepted
     */
    public static Bus create(int id, String route, String departure, double fare, int capacity) {
        try {
            return new Bus(id, route, departure, fare, capacity);
        } catch (IllegalArgumentException e) {
            System.out.println("Could not create bus " + id + ": " + e.getMessage());
            return null;
        }
    }

    // ---------- Getters ----------

    /**
     * Returns the bus number.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the route.
     *
     * @return the route name
     */
    public String getRoute() {
        return route;
    }

    /**
     * Returns the departure time.
     *
     * @return the departure time as text
     */
    public String getDeparture() {
        return departure;
    }

    /**
     * Returns the full fare, before any passenger discount.
     *
     * @return the fare in pesos
     */
    public double getFare() {
        return fare;
    }

    /**
     * Returns the total number of seats, taken or free.
     *
     * @return the capacity
     */
    public int getCapacity() {
        return seats.length;
    }

    // ---------- Seat logic ----------

    /**
     * Checks that a seat number exists on this bus.
     *
     * @param seatNo the seat number, starting at 1
     * @return {@code true} if the number is from 1 up to the capacity
     */
    public boolean isValidSeat(int seatNo) {
        return seatNo >= 1 && seatNo <= seats.length;
    }

    /**
     * Checks whether a seat is already taken.
     *
     * @param seatNo the seat number, starting at 1
     * @return {@code true} if the seat exists and is taken;
     *         {@code false} if it is free or does not exist
     */
    public boolean isSeatTaken(int seatNo) {
        return isValidSeat(seatNo) && seats[seatNo - 1];
    }

    /**
     * Reserves a seat.
     *
     * @param seatNo the seat number, starting at 1
     * @return {@code true} if the seat was reserved; {@code false} if the
     *         number is invalid or the seat is already taken
     */
    public boolean bookSeat(int seatNo) {
        if (!isValidSeat(seatNo) || seats[seatNo - 1])
            return false;
        seats[seatNo - 1] = true;
        return true;
    }

    /**
     * Frees a seat, for example when a ticket is cancelled.
     * An invalid seat number is ignored.
     *
     * @param seatNo the seat number, starting at 1
     */
    public void releaseSeat(int seatNo) {
        if (isValidSeat(seatNo))
            seats[seatNo - 1] = false;
    }

    /**
     * Counts the free seats.
     *
     * @return the number of seats that are not taken
     */
    public int availableSeats() {
        int count = 0;
        for (boolean taken : seats)
            if (!taken)
                count++;
        return count;
    }

    /**
     * Lists the seats that are still free, lowest number first.
     * Changing the returned list does not change the bus.
     *
     * @return a new list of the free seat numbers; empty if the bus is full
     */
    public List<Integer> getFreeSeats() {
        List<Integer> free = new ArrayList<>();
        for (int seat = 1; seat <= seats.length; seat++) {
            if (!seats[seat - 1])
                free.add(seat);
        }
        return free;
    }

    /**
     * Checks whether every seat is taken.
     *
     * @return {@code true} if there are no free seats left
     */
    public boolean isFull() {
        return availableSeats() == 0;
    }

    /**
     * Prints the seat map to the console, with a fixed number of seats per row.
     * Free seats show their number and taken seats show {@code XX}.
     */
    public void showSeatMap() {
        System.out.println("\nSeat Map (XX = taken)");
        for (int i = 0; i < seats.length; i++) {
            System.out.print(seats[i] ? " XX " : String.format(" %02d ", i + 1));
            if ((i + 1) % SEATS_PER_ROW == 0)
                System.out.println();
        }
        System.out.println();
    }

    // ---------- Printable ----------

    /**
     * Prints the column titles of the bus table.
     * Call this once before printing several buses with {@link #print()}.
     */
    public static void printHeader() {
        System.out.printf("%-4s %-27s %-10s %-10s %-8s%n", "ID", "Route", "Departure", "Fare", "Seats");
        System.out.println("--------------------------------------------------------");
    }

    /**
     * Prints this bus as one row of the bus table: id, route, departure,
     * fare, and free seats out of the total.
     */
    @Override
    public void print() {
        System.out.printf("%-4d %-27s %-10s P%-9.2f %d/%d%n",
                id, route, departure, fare, availableSeats(), seats.length);
    }
}