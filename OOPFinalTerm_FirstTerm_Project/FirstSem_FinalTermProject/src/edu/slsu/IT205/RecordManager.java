package edu.slsu.IT205;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Handles all ticket records: add, find, cancel, list, and save/load to a file.
 * <p>
 * The tickets are kept in a list and saved to a text file after every change,
 * one ticket per line (see {@link Ticket#toFileLine()}). The file is read
 * again when the program starts, so tickets are not lost when it is closed.
 *
 * @see Ticket
 * @see BusManager
 */
public class RecordManager {
    private final List<Ticket> tickets = new ArrayList<>();
    private final String fileName;
    private final BusManager busManager;
    private int ticketCounter = 1000;

    /**
     * Creates the record manager and loads any tickets saved in the file.
     * If the file does not exist yet, it starts with no tickets. The seats of
     * the loaded tickets are marked as taken on their buses.
     *
     * @param fileName   the save file, for example {@code "tickets.txt"}
     * @param busManager used to find the bus of each saved ticket
     */
    public RecordManager(String fileName, BusManager busManager) {
        this.fileName = fileName;
        this.busManager = busManager;
        load();
    }

    /**
     * Gives the next ticket number. The numbers go up by one each time,
     * starting at {@code TKT-1001}, and continue after the highest number
     * found in the save file.
     *
     * @return a new ticket number such as {@code "TKT-1001"}
     */
    public String nextTicketNo() {
        return "TKT-" + (++ticketCounter);
    }

    /**
     * Adds a ticket and saves all tickets to the file.
     *
     * @param ticket the ticket to add
     */
    public void add(Ticket ticket) {
        tickets.add(ticket);
        save();
    }

    /**
     * Finds a ticket by its number. Upper and lower case do not matter.
     *
     * @param ticketNo the ticket number to look for
     * @return the ticket, or {@code null} if there is no such ticket
     */
    public Ticket findByNo(String ticketNo) {
        for (Ticket t : tickets) {
            if (t.getTicketNo().equalsIgnoreCase(ticketNo))
                return t;
        }
        return null;
    }

    /**
     * Finds all tickets whose passenger name contains the given text.
     * Upper and lower case do not matter, so {@code "juan"} finds
     * {@code "Juan Dela Cruz"}. A blank search finds nothing.
     *
     * @param name the name, or part of a name, to look for
     * @return the matching tickets in booking order; an empty list if none match
     */
    public List<Ticket> findByPassengerName(String name) {
        List<Ticket> found = new ArrayList<>();
        if (name == null || name.trim().isEmpty())
            return found;

        String search = name.trim().toLowerCase();
        for (Ticket t : tickets) {
            if (t.getPassenger().getName().toLowerCase().contains(search)) {
                found.add(t);
            }
        }
        return found;
    }

    /**
     * Finds all tickets booked on one bus.
     *
     * @param busId the id of the bus, for example {@code 1}
     * @return the tickets for that bus in booking order; an empty list if the
     *         bus has no tickets or does not exist
     */
    public List<Ticket> getTicketsByBus(int busId) {
        List<Ticket> found = new ArrayList<>();
        for (Ticket t : tickets) {
            if (t.getBus().getId() == busId) {
                found.add(t);
            }
        }
        return found;
    }

    /**
     * Cancels a ticket: removes it, frees its seat on the bus, and saves the
     * file. Upper and lower case do not matter.
     *
     * @param ticketNo the number of the ticket to cancel
     * @return {@code true} if the ticket was cancelled; {@code false} if no
     *         ticket has that number
     */
    public boolean cancel(String ticketNo) {
        Iterator<Ticket> it = tickets.iterator();
        while (it.hasNext()) {
            Ticket t = it.next();
            if (t.getTicketNo().equalsIgnoreCase(ticketNo)) {
                t.getBus().releaseSeat(t.getSeatNo());
                it.remove();
                save();
                return true;
            }
        }
        return false;
    }

    /**
     * Adds up the amount paid on all tickets.
     *
     * @return the total sales in pesos, or {@code 0} if there are no tickets
     */
    public double getTotalSales() {
        double total = 0;
        for (Ticket t : tickets)
            total += t.getAmountPaid();
        return total;
    }

    /**
     * Prints all tickets as a table, followed by the number of tickets and
     * the total sales. Prints "No records found." if there are no tickets.
     */
    public void displayAll() {
        if (tickets.isEmpty()) {
            System.out.println("\nNo records found.");
            return;
        }
        System.out.println("\n------------------------------ ALL RECORDS ------------------------------");
        System.out.printf("%-10s %-18s %-9s %-27s %-5s %-9s%n",
                "Ticket No", "Passenger", "Type", "Route", "Seat", "Paid");
        System.out.println("--------------------------------------------------------------------------------");
        for (Ticket t : tickets) {
            t.printRow();
        }
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("Total tickets: %d   |   Total sales: P%.2f%n", tickets.size(), getTotalSales());
    }

    // ---------- File handling ----------

    /**
     * Writes all tickets to the file, one per line. If the file cannot be
     * written, an error message is printed and the program keeps running.
     */
    private void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            for (Ticket t : tickets)
                pw.println(t.toFileLine());
        } catch (IOException e) {
            System.out.println("Error saving records: " + e.getMessage());
        }
    }

    /**
     * Loads tickets from the file. A damaged line (missing field, letters where
     * a number should be, unknown bus, duplicate seat) is skipped instead of
     * stopping the whole program, and a warning is shown at the end.
     */
    private void load() {
        File file = new File(fileName);
        if (!file.exists())
            return;

        int skipped = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                try {
                    loadLine(line);
                } catch (RuntimeException e) {
                    skipped++; // NumberFormatException, ArrayIndexOutOfBounds, IllegalArgument...
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading records: " + e.getMessage());
        }

        if (skipped > 0) {
            System.out.println("Warning: " + skipped + " damaged record(s) in "
                    + fileName + " were skipped.");
        }
    }

    /** Parses one file line. Throws a RuntimeException if the line is not valid. */
    private void loadLine(String line) {
        String[] p = line.split("\\|");

        // 1) read and check everything first...
        Passenger passenger = Passenger.create(p[3], p[1], Integer.parseInt(p[2]));
        Bus bus = busManager.findById(Integer.parseInt(p[4]));
        if (bus == null)
            throw new IllegalArgumentException("Unknown bus");
        int seatNo = Integer.parseInt(p[5]);
        double amount = Double.parseDouble(p[6]);
        int number = Integer.parseInt(p[0].replace("TKT-", ""));

        // 2) ...and only then change any state
        if (!bus.bookSeat(seatNo))
            throw new IllegalArgumentException("Bad or duplicate seat");
        tickets.add(new Ticket(p[0], passenger, bus, seatNo, amount, p[7]));
        if (number > ticketCounter)
            ticketCounter = number;
    }
}