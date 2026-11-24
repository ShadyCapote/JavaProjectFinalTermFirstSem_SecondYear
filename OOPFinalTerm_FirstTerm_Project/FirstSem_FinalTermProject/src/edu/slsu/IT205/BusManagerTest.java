package edu.slsu.IT205;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for the BusManager class: finding buses by id, showing the bus table,
 * and adding, checking, saving and loading bus routes.
 */
class BusManagerTest {
    @TempDir
    Path tempDir; // a fresh temporary folder for each test

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
            assertEquals(bus.getCapacity(), bus.getAvailableSeats());
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

    // ================= adding a route =================

    private String busFile() {
        return tempDir.resolve("buses.txt").toString();
    }

    private Bus addMaasinCebu(BusManager m) {
        return m.addRoute("Maasin City - Cebu City", "6:30 am", 250, 30);
    }

    @Test
    void anAddedRouteGetsTheNextFreeBusId() {
        Bus bus = addMaasinCebu(manager);
        assertEquals(5, bus.getId());
        assertSame(bus, manager.findById(5));
    }

    @Test
    void eachNewRouteGetsTheNextId() {
        addMaasinCebu(manager);
        Bus second = manager.addRoute("Sogod - Maasin City", "10:00 AM", 90, 20);
        assertEquals(6, second.getId());
    }

    @Test
    void theNewRouteKeepsTheValuesThatWereTyped() {
        Bus bus = addMaasinCebu(manager);
        assertEquals("Maasin City - Cebu City", bus.getRoute());
        assertEquals("06:30 AM", bus.getDeparture());
        assertEquals(250.0, bus.getFare(), 0.001);
        assertEquals(30, bus.getCapacity());
    }

    @Test
    void aNewRouteStartsWithEverySeatFree() {
        Bus bus = addMaasinCebu(manager);
        assertEquals(30, bus.getAvailableSeats());
        assertFalse(bus.isFull());
    }

    @Test
    void aNewRouteCanBeBookedLikeAnyOtherBus() {
        Bus bus = addMaasinCebu(manager);
        assertTrue(bus.bookSeat(30));
        assertFalse(bus.bookSeat(31));
    }

    @Test
    void theNewRouteIsInTheListOfBusesAndInTheBusTable() {
        Bus bus = addMaasinCebu(manager);
        assertTrue(manager.getBuses().contains(bus));
        String out = capturePrint(manager::displayAll);
        assertTrue(out.contains("Maasin City - Cebu City"));
        assertTrue(out.contains("06:30 AM"));
        assertTrue(out.contains("P250.00"));
    }

    @Test
    void theBuiltInBusesAreNotChangedByAddingARoute() {
        addMaasinCebu(manager);
        assertEquals(5, manager.getBuses().size());
        for (int id = 1; id <= 4; id++)
            assertNotNull(manager.findById(id));
    }

    // ---------- the route name ----------

    @Test
    void extraSpacesInTheRouteNameAreRemoved() {
        assertEquals("Maasin City - Cebu City", BusManager.cleanRoute("   Maasin    City  -   Cebu City  "));
    }

    @Test
    void aPipeInTheRouteNameBecomesASpace() {
        assertEquals("Maasin Cebu", BusManager.cleanRoute("Maasin|Cebu"));
    }

    @Test
    void anEmptyOrBlankRouteIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> BusManager.cleanRoute(""));
        assertThrows(IllegalArgumentException.class, () -> BusManager.cleanRoute("     "));
        assertThrows(IllegalArgumentException.class, () -> BusManager.cleanRoute(null));
    }

    @Test
    void aRouteOfExactlyTheLongestLengthIsAcceptedAndOneMoreIsRejected() {
        String longest = "A".repeat(BusManager.MAX_ROUTE_LENGTH);
        assertEquals(longest, BusManager.cleanRoute(longest));
        assertThrows(IllegalArgumentException.class, () -> BusManager.cleanRoute(longest + "B"));
    }

    // ---------- the departure time ----------

    @Test
    void departureTimesAreWrittenInTheUsualForm() {
        assertEquals("06:30 AM", BusManager.cleanDeparture("6:30 am"));
        assertEquals("06:30 AM", BusManager.cleanDeparture("06:30 AM"));
        assertEquals("06:30 PM", BusManager.cleanDeparture("  6:30pm "));
        assertEquals("12:00 PM", BusManager.cleanDeparture("12:00 PM"));
        assertEquals("01:05 AM", BusManager.cleanDeparture("1:05 Am"));
    }

    @Test
    void wrongDepartureTimesAreRejectedWithAClearMessage() {
        for (String bad : new String[] { "", "6:30", "13:00 PM", "0:30 AM", "6:60 AM", "6.30 AM", "six thirty",
                "6:3 AM" }) {
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                    () -> BusManager.cleanDeparture(bad));
            assertTrue(e.getMessage().contains("06:30 AM"));
        }
    }

    // ---------- fare and seats ----------

    @Test
    void aFareFromOneCentavoToTheMaximumIsAccepted() {
        assertEquals(0.01, BusManager.cleanFare(0.01), 0.0001);
        assertEquals(BusManager.MAX_FARE, BusManager.cleanFare(BusManager.MAX_FARE), 0.0001);
    }

    @Test
    void aFareIsRoundedToTwoDecimals() {
        assertEquals(250.46, BusManager.cleanFare(250.456), 0.0001);
    }

    @Test
    void zeroNegativeTooHighAndNonNumberFaresAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> BusManager.cleanFare(0));
        assertThrows(IllegalArgumentException.class, () -> BusManager.cleanFare(-5));
        assertThrows(IllegalArgumentException.class, () -> BusManager.cleanFare(10000));
        assertThrows(IllegalArgumentException.class, () -> BusManager.cleanFare(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> BusManager.cleanFare(Double.POSITIVE_INFINITY));
    }

    @Test
    void fromOneSeatToTheMaximumIsAccepted() {
        BusManager.checkSeats(1);
        BusManager.checkSeats(BusManager.MAX_SEATS);
    }

    @Test
    void noSeatsNegativeOrTooManySeatsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> BusManager.checkSeats(0));
        assertThrows(IllegalArgumentException.class, () -> BusManager.checkSeats(-1));
        assertThrows(IllegalArgumentException.class, () -> BusManager.checkSeats(BusManager.MAX_SEATS + 1));
    }

    @Test
    void addRouteChecksEveryValue() {
        assertThrows(IllegalArgumentException.class, () -> manager.addRoute("", "6:30 AM", 100, 20));
        assertThrows(IllegalArgumentException.class, () -> manager.addRoute("A - B", "6:99 AM", 100, 20));
        assertThrows(IllegalArgumentException.class, () -> manager.addRoute("A - B", "6:30 AM", 0, 20));
        assertThrows(IllegalArgumentException.class, () -> manager.addRoute("A - B", "6:30 AM", 100, 0));
        assertEquals(4, manager.getBuses().size()); // nothing was added
    }

    // ---------- the same route twice ----------

    @Test
    void theSameRouteAndTimeCannotBeAddedTwice() {
        addMaasinCebu(manager);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> manager.addRoute("maasin city - cebu city", "06:30 AM", 300, 20));
        assertTrue(e.getMessage().contains("already exist"));
        assertEquals(5, manager.getBuses().size());
    }

    @Test
    void aBuiltInRouteAndTimeCannotBeAddedAgain() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.addRoute("Southern Leyte - Silago", "6:00 am", 200, 20));
    }

    @Test
    void theSameRouteAtAnotherTimeIsAllowed() {
        addMaasinCebu(manager);
        Bus later = manager.addRoute("Maasin City - Cebu City", "2:30 PM", 250, 30);
        assertEquals(6, later.getId());
    }

    // ================= saving and loading =================

    @Test
    void anAddedRouteIsSavedAndLoadedAgain() {
        BusManager first = new BusManager(busFile());
        addMaasinCebu(first);

        BusManager reopened = new BusManager(busFile()); // like closing and reopening the program

        Bus bus = reopened.findById(5);
        assertNotNull(bus);
        assertEquals("Maasin City - Cebu City", bus.getRoute());
        assertEquals("06:30 AM", bus.getDeparture());
        assertEquals(250.0, bus.getFare(), 0.001);
        assertEquals(30, bus.getCapacity());
    }

    @Test
    void busIdsContinueAfterReloading() {
        BusManager first = new BusManager(busFile());
        addMaasinCebu(first);
        first.addRoute("Sogod - Maasin City", "10:00 AM", 90, 20);

        BusManager reopened = new BusManager(busFile());
        Bus third = reopened.addRoute("Hilongos - Maasin City", "1:00 PM", 120, 20);

        assertEquals(7, third.getId());
        assertEquals(7, reopened.getBuses().size());
    }

    @Test
    void onlyTheAddedRoutesAreWrittenToTheFile() throws IOException {
        BusManager first = new BusManager(busFile());
        addMaasinCebu(first);

        assertEquals(Arrays.asList("5|Maasin City - Cebu City|06:30 AM|250.0|30"),
                Files.readAllLines(tempDir.resolve("buses.txt")));
    }

    @Test
    void aManagerWithoutAFileNeverCreatesOne() throws IOException {
        addMaasinCebu(manager);   // 'manager' was made with new BusManager()
        try (java.util.stream.Stream<Path> files = Files.list(tempDir)) {
            assertEquals(0, files.count());
        }
    }

    @Test
    void aMissingFileStartsWithTheFourBuiltInBuses() {
        assertEquals(4, new BusManager(busFile()).getBuses().size());
    }

    @Test
    void noWarningIsShownForAGoodFile() {
        addMaasinCebu(new BusManager(busFile()));
        String out = capturePrint(() -> new BusManager(busFile()));
        assertFalse(out.contains("Warning"));
    }

    @Test
    void damagedLinesAreSkippedAndGoodRoutesStillLoad() throws IOException {
        Files.write(tempDir.resolve("buses.txt"), Arrays.asList(
                "garbage line",
                "5|Maasin City - Cebu City|06:30 AM|250.0|30",
                "six|Bad Id|06:30 AM|250.0|30",
                "7|Bad Time|25:99 AM|250.0|30",
                "8|Bad Fare|06:30 AM|-5|30",
                "9|Bad Seats|06:30 AM|250.0|0",
                "10|Too Few Fields"));

        String printed = capturePrint(() -> new BusManager(busFile()));
        BusManager reopened = new BusManager(busFile());

        assertNotNull(reopened.findById(5));
        assertNull(reopened.findById(7));
        assertNull(reopened.findById(8));
        assertNull(reopened.findById(9));
        assertNull(reopened.findById(10));
        assertEquals(5, reopened.getBuses().size());
        assertTrue(printed.contains("6 damaged bus route(s)"));
    }

    @Test
    void aSavedLineThatReusesABuiltInIdIsSkipped() throws IOException {
        Files.write(tempDir.resolve("buses.txt"), Arrays.asList("1|Fake Route|06:30 AM|10.0|10"));
        BusManager reopened = new BusManager(busFile());
        assertEquals("Southern Leyte - Silago", reopened.findById(1).getRoute());
        assertEquals(4, reopened.getBuses().size());
    }

    @Test
    void aFriendlyMessageIsShownWhenTheRoutesCannotBeSaved() {
        String missingFolder = tempDir.resolve("no-such-folder").resolve("buses.txt").toString();
        BusManager broken = new BusManager(missingFolder);

        String out = capturePrint(() -> addMaasinCebu(broken));

        assertTrue(out.contains("Warning: Could not save the bus routes to"));
        assertTrue(out.contains("kept only until you close the program"));
        assertFalse(out.contains("Exception"));
        assertNotNull(broken.findById(5)); // the route is kept in memory
    }

    @Test
    void aFriendlyMessageIsShownWhenTheFileCannotBeRead() throws IOException {
        Files.createDirectory(tempDir.resolve("buses.txt")); // a folder named buses.txt cannot be read as a file

        String out = capturePrint(() -> new BusManager(busFile()));

        assertTrue(out.contains("Warning: Could not read the saved bus routes from"));
        assertTrue(out.contains("may be missing"));
        assertFalse(out.contains("Exception"));
    }

    // ================= tickets on an added route =================

    @Test
    void aTicketBookedOnAnAddedRouteIsStillThereAfterRestarting() {
        String ticketsFile = tempDir.resolve("tickets.txt").toString();
        BusManager buses = new BusManager(busFile());
        Bus bus = addMaasinCebu(buses);
        RecordManager records = new RecordManager(ticketsFile, buses);
        bus.bookSeat(30);
        records.add(new Ticket(records.nextTicketNo(), new StudentPassenger("Ana Lopez", 19), bus, 30));

        // close and reopen the program: buses first, then the tickets
        BusManager newBuses = new BusManager(busFile());
        RecordManager newRecords = new RecordManager(ticketsFile, newBuses);

        Ticket loaded = newRecords.findByNo("TKT-1001");
        assertNotNull(loaded);
        assertEquals(5, loaded.getBus().getId());
        assertEquals(200.0, loaded.getAmountPaid(), 0.001); // 250 less 20%
        assertTrue(newBuses.findById(5).isSeatTaken(30));
    }
}