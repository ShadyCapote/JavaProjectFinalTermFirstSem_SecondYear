package edu.slsu.IT205;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Handles all ticket records: add, find, cancel, list, and save/load to a file.
 */
public class RecordManager {
    private final List<Ticket> tickets = new ArrayList<>();
    private final String fileName;
    private final BusManager busManager;
    private int ticketCounter = 1000;

    public RecordManager(String fileName, BusManager busManager) {
        this.fileName = fileName;
        this.busManager = busManager;
        load();
    }

    public String nextTicketNo() {
        return "TKT-" + (++ticketCounter);
    }

    public void add(Ticket ticket) {
        tickets.add(ticket);
        save();
    }

    public Ticket findByNo(String ticketNo) {
        for (Ticket t : tickets) {
            if (t.getTicketNo().equalsIgnoreCase(ticketNo))
                return t;
        }
        return null;
    }

    /** Removes the ticket and frees its seat. Returns false if not found. */
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

    public double getTotalSales() {
        double total = 0;
        for (Ticket t : tickets)
            total += t.getAmountPaid();
        return total;
    }

    public void displayAll() {
        if (tickets.isEmpty()) {
            System.out.println("\nNo records found.");
            return;
        }
        System.out.println("\n------------------------------ ALL RECORDS ------------------------------");
        System.out.printf("%-10s %-18s %-9s %-20s %-5s %-9s%n",
                "Ticket No", "Passenger", "Type", "Route", "Seat", "Paid");
        System.out.println("-------------------------------------------------------------------------");
        for (Ticket t : tickets) {
            t.printRow();
        }
        System.out.println("-------------------------------------------------------------------------");
        System.out.printf("Total tickets: %d   |   Total sales: P%.2f%n", tickets.size(), getTotalSales());
    }

    // ---------- File handling ----------
    private void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            for (Ticket t : tickets)
                pw.println(t.toFileLine());
        } catch (IOException e) {
            System.out.println("Error saving records: " + e.getMessage());
        }
    }

    private void load() {
        File file = new File(fileName);
        if (!file.exists())
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                String[] p = line.split("\\|");
                Passenger passenger = Passenger.create(p[3], p[1], Integer.parseInt(p[2]));
                Bus bus = busManager.findById(Integer.parseInt(p[4]));
                int seatNo = Integer.parseInt(p[5]);
                if (bus == null)
                    continue;

                bus.bookSeat(seatNo);
                tickets.add(new Ticket(p[0], passenger, bus, seatNo,
                        Double.parseDouble(p[6]), p[7]));

                int num = Integer.parseInt(p[0].replace("TKT-", ""));
                if (num > ticketCounter)
                    ticketCounter = num;
            }
        } catch (Exception e) {
            System.out.println("Error loading records: " + e.getMessage());
        }
    }
}