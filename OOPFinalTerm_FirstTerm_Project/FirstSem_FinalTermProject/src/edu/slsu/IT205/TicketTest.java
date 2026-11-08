package edu.slsu.IT205;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Ticket class: the amount paid, the getters, the line saved
 * in tickets.txt, and what is printed.
 */
class TicketTest {
    private Bus bus;

    @BeforeEach
    void setUp() {
        bus = new Bus(1, "Southern Leyte - Silago", "06:00 AM", 100.00, 20);
    }

    // ---------- amount paid ----------

    @Test
    void regularTicketAmountIsTheFullFare() {
        Ticket t = new Ticket("TKT-1001", new RegularPassenger("Juan", 25), bus, 5);
        assertEquals(100.0, t.getAmountPaid(), 0.001);
    }

    @Test
    void studentTicketAmountHasTwentyPercentOff() {
        Ticket t = new Ticket("TKT-1002", new StudentPassenger("Ana", 18), bus, 6);
        assertEquals(80.0, t.getAmountPaid(), 0.001);
    }

    @Test
    void seniorAndPwdTicketsAlsoHaveTwentyPercentOff() {
        Ticket senior = new Ticket("TKT-1003", new SeniorPassenger("Lola", 70), bus, 7);
        Ticket pwd = new Ticket("TKT-1004", new PWDPassenger("Ben", 30), bus, 8);
        assertEquals(80.0, senior.getAmountPaid(), 0.001);
        assertEquals(80.0, pwd.getAmountPaid(), 0.001);
    }

    @Test
    void amountFollowsTheFareOfTheBus() {
        Bus expensive = new Bus(2, "Southern Leyte - Hilongos", "08:00 PM", 220.00, 20);
        Ticket t = new Ticket("TKT-1005", new StudentPassenger("Ana", 18), expensive, 1);
        assertEquals(176.0, t.getAmountPaid(), 0.001); // 220 - 20%
    }

    // ---------- getters ----------

    @Test
    void gettersReturnTheBookingDetails() {
        Passenger p = new SeniorPassenger("Lola", 70);
        Ticket t = new Ticket("TKT-1006", p, bus, 7);
        assertEquals("TKT-1006", t.getTicketNo());
        assertSame(p, t.getPassenger());
        assertSame(bus, t.getBus());
        assertEquals(7, t.getSeatNo());
    }

    @Test
    void newTicketGetsTheCurrentDateInTheExpectedFormat() {
        Ticket t = new Ticket("TKT-1007", new RegularPassenger("Juan", 25), bus, 1);
        assertTrue(t.getDateBooked().matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}"));
    }

    @Test
    void ticketLoadedFromTheFileKeepsItsSavedAmountAndDate() {
        Ticket t = new Ticket("TKT-1008", new StudentPassenger("Ana", 18), bus, 3,
                123.45, "2026-01-02 03:04");
        assertEquals(123.45, t.getAmountPaid(), 0.001);
        assertEquals("2026-01-02 03:04", t.getDateBooked());
    }

    // ---------- the line saved in tickets.txt ----------

    @Test
    void fileLineHasEightFieldsInTheRightOrder() {
        Ticket t = new Ticket("TKT-1009", new StudentPassenger("Ana", 18), bus, 9);
        String[] f = t.toFileLine().split("\\|");
        assertEquals(8, f.length);
        assertEquals("TKT-1009", f[0]);
        assertEquals("Ana", f[1]);
        assertEquals("18", f[2]);
        assertEquals("Student", f[3]);
        assertEquals("1", f[4]); // bus id
        assertEquals("9", f[5]); // seat number
        assertEquals(80.0, Double.parseDouble(f[6]), 0.001);
        assertEquals(t.getDateBooked(), f[7]);
    }

    @Test
    void pipeInTheNameIsReplacedSoTheFileFormatStaysValid() {
        Ticket t = new Ticket("TKT-1010", new RegularPassenger("Ju|an", 30), bus, 1);
        String line = t.toFileLine();
        assertTrue(line.contains("|Ju an|"));
        assertEquals(8, line.split("\\|").length);
    }

    @Test
    void aSavedLineCanBeReadBackIntoTheSameTicket() {
        Ticket original = new Ticket("TKT-1011", new SeniorPassenger("Lola", 70), bus, 12);
        String[] f = original.toFileLine().split("\\|");

        Ticket copy = new Ticket(f[0], Passenger.create(f[3], f[1], Integer.parseInt(f[2])),
                bus, Integer.parseInt(f[5]), Double.parseDouble(f[6]), f[7]);

        assertEquals(original.toFileLine(), copy.toFileLine());
    }

    // ---------- printing ----------

    /** Runs some code and returns everything it printed to the console. */
    private String capturePrint(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            action.run();
        } finally {
            System.setOut(original);
        }
        return buffer.toString();
    }

    @Test
    void printedTicketShowsTheMainDetails() {
        Ticket t = new Ticket("TKT-1012", new StudentPassenger("Ana Cruz", 18), bus, 5);
        String out = capturePrint(t::print);
        assertTrue(out.contains("BUS TICKET"));
        assertTrue(out.contains("TKT-1012"));
        assertTrue(out.contains("Ana Cruz"));
        assertTrue(out.contains("18 / Student"));
        assertTrue(out.contains("Southern Leyte - Silago"));
        assertTrue(out.contains(String.format("P%.2f", 80.0)));
    }

    @Test
    void recordsRowShowsTheTicketNumberPassengerAndRoute() {
        Ticket t = new Ticket("TKT-1013", new RegularPassenger("Juan Dela Cruz", 25), bus, 4);
        String out = capturePrint(t::printRow);
        assertTrue(out.contains("TKT-1013"));
        assertTrue(out.contains("Juan Dela Cruz"));
        assertTrue(out.contains("Southern Leyte - Silago"));
    }

    @Test
    void printedStudentTicketShowsTwentyPercentDiscount() {
        Ticket t = new Ticket("TKT-1014", new StudentPassenger("Ana Cruz", 18), bus, 5);
        String out = capturePrint(t::print);
        assertTrue(out.contains("Discount  : 20%"));
    }

    @Test
    void printedRegularTicketShowsZeroPercentDiscount() {
        Ticket t = new Ticket("TKT-1015", new RegularPassenger("Juan", 25), bus, 6);
        String out = capturePrint(t::print);
        assertTrue(out.contains("Discount  : 0%"));
    }
}
