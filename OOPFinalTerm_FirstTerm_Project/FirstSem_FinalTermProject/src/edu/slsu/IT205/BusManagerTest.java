package edu.slsu.IT205;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the BusManager class: finding buses by id and showing the bus
 * table.
 */
class BusManagerTest {
    private BusManager manager;

    @BeforeEach
    void setUp() {
        manager = new BusManager();
    }

    // ---------- findById ----------

    @Test
    void findsAnExistingBusById() {
        Bus bus = manager.findById(1);
        assertNotNull(bus);
        assertEquals(1, bus.getId());
    }

    @Test
    void allFourBusesExistAndHaveTheIdThatWasAskedFor() {
        for (int id = 1; id <= 4; id++) {
            Bus bus = manager.findById(id);
            assertNotNull(bus, "Bus " + id + " should exist");
            assertEquals(id, bus.getId());
        }
    }

    @Test
    void returnsNullForAnUnknownBusId() {
        assertNull(manager.findById(5));
        assertNull(manager.findById(999));
    }

    @Test
    void returnsNullForZeroOrNegativeIds() {
        assertNull(manager.findById(0));
        assertNull(manager.findById(-1));
    }

    @Test
    void askingTwiceGivesTheSameBusObject() {
        assertSame(manager.findById(2), manager.findById(2));
    }

    @Test
    void differentIdsGiveDifferentBuses() {
        assertNotSame(manager.findById(1), manager.findById(2));
    }

    // ---------- the buses themselves ----------

    @Test
    void everyBusHasARouteADepartureTimeAFareAndSeats() {
        for (int id = 1; id <= 4; id++) {
            Bus bus = manager.findById(id);
            assertFalse(bus.getRoute().isEmpty());
            assertFalse(bus.getDeparture().isEmpty());
            assertTrue(bus.getFare() > 0);
            assertTrue(bus.getCapacity() > 0);
        }
    }

    @Test
    void newBusesStartWithAllSeatsFree() {
        for (int id = 1; id <= 4; id++) {
            Bus bus = manager.findById(id);
            assertEquals(bus.getCapacity(), bus.availableSeats());
        }
    }

    @Test
    void aSeatBookedOnABusStaysBookedWhenTheBusIsFoundAgain() {
        manager.findById(1).bookSeat(5);
        assertTrue(manager.findById(1).isSeatTaken(5));
    }

    @Test
    void aNewManagerStartsWithFreshBuses() {
        manager.findById(1).bookSeat(5);
        BusManager another = new BusManager();
        assertFalse(another.findById(1).isSeatTaken(5));
    }

    // ---------- displayAll ----------

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
    void displayAllShowsTheTableTitleAndHeader() {
        String out = capturePrint(manager::displayAll);
        assertTrue(out.contains("AVAILABLE BUSES"));
        assertTrue(out.contains("Route"));
        assertTrue(out.contains("Departure"));
    }

    @Test
    void displayAllShowsEveryBus() {
        String out = capturePrint(manager::displayAll);
        for (int id = 1; id <= 4; id++) {
            assertTrue(out.contains(manager.findById(id).getRoute()));
        }
    }

    @Test
    void displayAllShowsTheFreeSeatsOutOfTheTotal() {
        Bus bus = manager.findById(1);
        bus.bookSeat(1);
        bus.bookSeat(2);
        String out = capturePrint(manager::displayAll);
        int free = bus.getCapacity() - 2;
        assertTrue(out.contains(free + "/" + bus.getCapacity()));
    }
}