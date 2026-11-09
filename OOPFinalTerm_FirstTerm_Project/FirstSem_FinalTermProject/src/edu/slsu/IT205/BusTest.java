package edu.slsu.IT205;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Bus class: creating a bus and booking, releasing and
 * counting seats.
 */
class BusTest {
    private Bus bus;

    @BeforeEach
    void setUp() {
        // a small bus with 4 seats, so "full" is easy to test
        bus = new Bus(1, "Southern Leyte - Maasin", "06:00 AM", 150.00, 4);
    }

    // ---------- creating a bus ----------

    @Test
    void newBusHasAllSeatsFree() {
        assertEquals(4, bus.getCapacity());
        assertEquals(4, bus.availableSeats());
        assertFalse(bus.isFull());
    }

    @Test
    void gettersReturnTheConstructorValues() {
        assertEquals(1, bus.getId());
        assertEquals("Southern Leyte - Maasin", bus.getRoute());
        assertEquals("06:00 AM", bus.getDeparture());
        assertEquals(150.00, bus.getFare(), 0.001);
    }

    @Test
    void busWithNoSeatsIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Bus(2, "Route", "08:00 AM", 100.00, 0));
    }

    @Test
    void busWithNegativeFareIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Bus(2, "Route", "08:00 AM", -1.00, 10));
    }

    @Test
    void createReturnsABusForValidValues() {
        Bus created = Bus.create(5, "Route", "09:00 AM", 120.00, 10);
        assertNotNull(created);
        assertEquals(10, created.getCapacity());
    }

    @Test
    void createReturnsNullInsteadOfCrashingForInvalidValues() {
        assertNull(Bus.create(6, "Route", "09:00 AM", 120.00, 0));
        assertNull(Bus.create(7, "Route", "09:00 AM", -5.00, 10));
    }

    // ---------- booking seats ----------

    @Test
    void bookingASeatMarksItTaken() {
        assertTrue(bus.bookSeat(2));
        assertTrue(bus.isSeatTaken(2));
        assertEquals(3, bus.availableSeats());
    }

    @Test
    void otherSeatsStayFreeWhenOneIsBooked() {
        bus.bookSeat(2);
        assertFalse(bus.isSeatTaken(1));
        assertFalse(bus.isSeatTaken(3));
        assertFalse(bus.isSeatTaken(4));
    }

    @Test
    void cannotBookTheSameSeatTwice() {
        assertTrue(bus.bookSeat(3));
        assertFalse(bus.bookSeat(3));
        assertEquals(3, bus.availableSeats());
    }

    @Test
    void firstAndLastSeatCanBeBooked() {
        assertTrue(bus.bookSeat(1));
        assertTrue(bus.bookSeat(4));
        assertEquals(2, bus.availableSeats());
    }

    // ---------- invalid seat numbers ----------

    @Test
    void seatNumbersOutsideTheBusAreInvalid() {
        assertFalse(bus.isValidSeat(0));
        assertFalse(bus.isValidSeat(-1));
        assertFalse(bus.isValidSeat(5));
        assertTrue(bus.isValidSeat(1));
        assertTrue(bus.isValidSeat(4));
    }

    @Test
    void invalidSeatNumbersCannotBeBooked() {
        assertFalse(bus.bookSeat(0));
        assertFalse(bus.bookSeat(5));
        assertEquals(4, bus.availableSeats());
    }

    @Test
    void anInvalidSeatIsNeverReportedAsTaken() {
        assertFalse(bus.isSeatTaken(0));
        assertFalse(bus.isSeatTaken(99));
    }

    // ---------- releasing seats ----------

    @Test
    void releasingASeatMakesItFreeAgain() {
        bus.bookSeat(1);
        bus.releaseSeat(1);
        assertFalse(bus.isSeatTaken(1));
        assertEquals(4, bus.availableSeats());
    }

    @Test
    void aReleasedSeatCanBeBookedAgain() {
        bus.bookSeat(2);
        bus.releaseSeat(2);
        assertTrue(bus.bookSeat(2));
    }

    @Test
    void releasingAnInvalidSeatDoesNothing() {
        bus.bookSeat(1);
        bus.releaseSeat(0);
        bus.releaseSeat(99);
        assertEquals(3, bus.availableSeats());
    }

    // ---------- full bus ----------

    @Test
    void busIsFullWhenEverySeatIsBooked() {
        for (int seat = 1; seat <= 4; seat++)
            bus.bookSeat(seat);
        assertTrue(bus.isFull());
        assertEquals(0, bus.availableSeats());
    }

    @Test
    void busIsNoLongerFullAfterACancellation() {
        for (int seat = 1; seat <= 4; seat++)
            bus.bookSeat(seat);
        bus.releaseSeat(3);
        assertFalse(bus.isFull());
        assertEquals(1, bus.availableSeats());
    }

    // ---------- free seats ----------

    @Test
    void aNewBusHasEverySeatFree() {
        assertEquals(Arrays.asList(1, 2, 3, 4), bus.getFreeSeats());
    }

    @Test
    void aBookedSeatIsNotInTheFreeList() {
        bus.bookSeat(2);
        assertEquals(Arrays.asList(1, 3, 4), bus.getFreeSeats());
    }

    @Test
    void theFreeSeatsAreInSeatOrder() {
        bus.bookSeat(3);
        bus.bookSeat(1);
        assertEquals(Arrays.asList(2, 4), bus.getFreeSeats());
    }

    @Test
    void aReleasedSeatIsInTheFreeListAgain() {
        bus.bookSeat(2);
        bus.releaseSeat(2);
        assertTrue(bus.getFreeSeats().contains(2));
    }

    @Test
    void aFullBusHasNoFreeSeats() {
        for (int seat = 1; seat <= 4; seat++)
            bus.bookSeat(seat);
        assertTrue(bus.getFreeSeats().isEmpty());
    }

    @Test
    void theNumberOfFreeSeatsMatchesAvailableSeats() {
        bus.bookSeat(1);
        bus.bookSeat(4);
        assertEquals(bus.availableSeats(), bus.getFreeSeats().size());
    }

    @Test
    void changingTheReturnedListDoesNotChangeTheBus() {
        List<Integer> free = bus.getFreeSeats();
        free.clear();
        assertEquals(4, bus.availableSeats());
        assertFalse(bus.isSeatTaken(1));
    }
}