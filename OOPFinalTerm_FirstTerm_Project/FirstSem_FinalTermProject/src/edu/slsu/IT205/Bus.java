package edu.slsu.IT205;

/**
 * Represents a bus trip with its own seats.
 * The seat array is private; it can only be changed through methods.
 */
public class Bus implements Printable {
    private final int id;
    private final String route;
    private final String departure;
    private final double fare;
    private final boolean[] seats;

    public Bus(int id, String route, String departure, double fare, int capacity) {
        this.id = id;
        this.route = route;
        this.departure = departure;
        this.fare = fare;
        this.seats = new boolean[capacity];
    }

    // ---------- Getters ----------
    public int getId() {
        return id;
    }

    public String getRoute() {
        return route;
    }

    public String getDeparture() {
        return departure;
    }

    public double getFare() {
        return fare;
    }

    public int getCapacity() {
        return seats.length;
    }

    // ---------- Seat logic ----------
    public boolean isValidSeat(int seatNo) {
        return seatNo >= 1 && seatNo <= seats.length;
    }

    public boolean isSeatTaken(int seatNo) {
        return isValidSeat(seatNo) && seats[seatNo - 1];
    }

    /** Returns true if the seat was successfully reserved. */
    public boolean bookSeat(int seatNo) {
        if (!isValidSeat(seatNo) || seats[seatNo - 1])
            return false;
        seats[seatNo - 1] = true;
        return true;
    }

    public void releaseSeat(int seatNo) {
        if (isValidSeat(seatNo))
            seats[seatNo - 1] = false;
    }

    public int availableSeats() {
        int count = 0;
        for (boolean taken : seats)
            if (!taken)
                count++;
        return count;
    }

    public boolean isFull() {
        return availableSeats() == 0;
    }

    public void showSeatMap() {
        System.out.println("\nSeat Map (XX = taken)");
        for (int i = 0; i < seats.length; i++) {
            System.out.print(seats[i] ? " XX " : String.format(" %02d ", i + 1));
            if ((i + 1) % 4 == 0)
                System.out.println();
        }
        System.out.println();
    }

    // ---------- Printable ----------
    public static void printHeader() {
        System.out.printf("%-4s %-22s %-10s %-10s %-8s%n", "ID", "Route", "Departure", "Fare", "Seats");
        System.out.println("---------------------------------------------------");
    }

    @Override
    public void print() {
        System.out.printf("%-4d %-22s %-10s P%-9.2f %d/%d%n",
                id, route, departure, fare, availableSeats(), seats.length);
    }
}