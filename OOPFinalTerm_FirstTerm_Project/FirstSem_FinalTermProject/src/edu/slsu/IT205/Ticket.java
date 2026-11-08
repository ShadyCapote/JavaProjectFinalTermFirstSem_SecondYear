package edu.slsu.IT205;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A booked ticket.
 * <p>
 * A ticket HAS-A {@link Passenger} and HAS-A {@link Bus} (composition), plus
 * the seat number, the amount paid and the date it was booked. The amount is
 * worked out from the passenger type, so a student pays less than a regular
 * passenger for the same bus.
 *
 * @see RecordManager
 */
public class Ticket implements Printable {
    private final String ticketNo;
    private final Passenger passenger;
    private final Bus bus;
    private final int seatNo;
    private final double amountPaid;
    private final String dateBooked;

    /**
     * Creates a NEW ticket. The date is set to now, and the amount is the bus
     * fare after the passenger's discount.
     *
     * @param ticketNo  the ticket number, for example {@code "TKT-1001"}
     * @param passenger the passenger who bought the ticket
     * @param bus       the bus being booked
     * @param seatNo    the seat number, starting at 1
     */
    public Ticket(String ticketNo, Passenger passenger, Bus bus, int seatNo) {
        this(ticketNo, passenger, bus, seatNo,
                passenger.computeFare(bus.getFare()),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    /**
     * Creates a ticket from saved values. Used when loading tickets from the
     * file, so the amount and date are kept exactly as they were saved.
     *
     * @param ticketNo   the ticket number
     * @param passenger  the passenger who bought the ticket
     * @param bus        the bus that was booked
     * @param seatNo     the seat number, starting at 1
     * @param amountPaid the amount that was paid, in pesos
     * @param dateBooked the booking date and time as text
     */
    public Ticket(String ticketNo, Passenger passenger, Bus bus, int seatNo,
            double amountPaid, String dateBooked) {
        this.ticketNo = ticketNo;
        this.passenger = passenger;
        this.bus = bus;
        this.seatNo = seatNo;
        this.amountPaid = amountPaid;
        this.dateBooked = dateBooked;
    }

    /**
     * Returns the ticket number.
     *
     * @return the ticket number, for example {@code "TKT-1001"}
     */
    public String getTicketNo() {
        return ticketNo;
    }

    /**
     * Returns the passenger.
     *
     * @return the passenger who bought this ticket
     */
    public Passenger getPassenger() {
        return passenger;
    }

    /**
     * Returns the bus.
     *
     * @return the bus this ticket is for
     */
    public Bus getBus() {
        return bus;
    }

    /**
     * Returns the seat number.
     *
     * @return the seat number, starting at 1
     */
    public int getSeatNo() {
        return seatNo;
    }

    /**
     * Returns the amount paid.
     *
     * @return the amount in pesos, after any discount
     */
    public double getAmountPaid() {
        return amountPaid;
    }

    /**
     * Returns the booking date.
     *
     * @return the date and time as text, for example {@code "2026-11-07 14:30"}
     */
    public String getDateBooked() {
        return dateBooked;
    }

    /**
     * Converts this ticket to one line of text for the save file.
     * The fields are separated by the {@code |} character, in this order:
     * ticket number, passenger name, age, type, bus id, seat number, amount,
     * and date. A {@code |} inside the passenger's name is replaced by a space
     * so it cannot break the format.
     *
     * @return one line for {@code tickets.txt}
     */
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

    /**
     * Prints this ticket as one row of the "All Records" table.
     * {@link RecordManager#displayAll()} prints the header and calls this
     * method for each ticket.
     */
    public void printRow() {
        System.out.printf("%-10s %-18s %-9s %-27s %-5d P%-8.2f%n",
                ticketNo, passenger.getName(), passenger.getType(),
                bus.getRoute(), seatNo, amountPaid);
    }

    /**
     * Prints the full ticket in a box, as the passenger receives it.
     */
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