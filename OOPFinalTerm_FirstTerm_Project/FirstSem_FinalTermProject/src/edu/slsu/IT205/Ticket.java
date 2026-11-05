package edu.slsu.IT205;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A booked ticket. It HAS-A Passenger and HAS-A Bus (composition).
 */
public class Ticket implements Printable {
    private final String ticketNo;
    private final Passenger passenger;
    private final Bus bus;
    private final int seatNo;
    private final double amountPaid;
    private final String dateBooked;

    // Used for a NEW booking (date = now, amount computed from passenger type)
    public Ticket(String ticketNo, Passenger passenger, Bus bus, int seatNo) {
        this(ticketNo, passenger, bus, seatNo,
                passenger.computeFare(bus.getFare()),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    // Used when loading an existing ticket from the file
    public Ticket(String ticketNo, Passenger passenger, Bus bus, int seatNo,
            double amountPaid, String dateBooked) {
        this.ticketNo = ticketNo;
        this.passenger = passenger;
        this.bus = bus;
        this.seatNo = seatNo;
        this.amountPaid = amountPaid;
        this.dateBooked = dateBooked;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Bus getBus() {
        return bus;
    }   

    public int getSeatNo() {
        return seatNo;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public String getDateBooked() {
        return dateBooked;
    }

    /** One line used for saving to the text file. */
    public String toFileLine() {
        return String.join("|",
                ticketNo,
                passenger.getName().replace("|", " "),
                String.valueOf(passenger.getAge()),
                passenger.getType(),
                String.valueOf(bus.getId()),
                String.valueOf(seatNo),
                String.valueOf(amountPaid),
                dateBooked);
    }

    /** One row used in the "All Records" table. */
    public void printRow() {
        System.out.printf("%-10s %-18s %-9s %-20s %-5d P%-8.2f%n",
                ticketNo, passenger.getName(), passenger.getType(),
                bus.getRoute(), seatNo, amountPaid);
    }

    /** Full printable ticket. */
    @Override
    public void print() {
        System.out.println();
        System.out.println("+------------------------------------------+");
        System.out.println("|            BUS TICKET                    |");
        System.out.println("+------------------------------------------+");
        System.out.printf("| Ticket No : %-28s |%n", ticketNo);
        System.out.printf("| Passenger : %-28s |%n", passenger.getName());
        System.out.printf("| Age/Type  : %-28s |%n", passenger.getAge() + " / " + passenger.getType());
        System.out.printf("| Route     : %-28s |%n", bus.getRoute());
        System.out.printf("| Departure : %-28s |%n", bus.getDeparture());
        System.out.printf("| Bus No.   : %-28s |%n", "Bus " + bus.getId());
        System.out.printf("| Seat No.  : %-28d |%n", seatNo);
        System.out.printf("| Amount    : %-28s |%n", String.format("P%.2f", amountPaid));
        System.out.printf("| Booked on : %-28s |%n", dateBooked);
        System.out.println("+------------------------------------------+");
        System.out.println("|     Thank you and have a safe trip!      |");
        System.out.println("+------------------------------------------+");
    }
}