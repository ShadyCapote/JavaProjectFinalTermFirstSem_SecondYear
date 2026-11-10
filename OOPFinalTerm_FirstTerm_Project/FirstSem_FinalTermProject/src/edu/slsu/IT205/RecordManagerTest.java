package edu.slsu.IT205;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for the RecordManager class: adding, finding and cancelling tickets,
 * the total sales, and saving to and reloading from the file.
 */
class RecordManagerTest {

    @TempDir
    Path tempDir; // a fresh temporary folder for each test

    private Path file;
    private String fileName;
    private BusManager busManager;
    private RecordManager records;

    @BeforeEach
    void setUp() {
        file = tempDir.resolve("tickets.txt");
        fileName = file.toString();
        busManager = new BusManager();
        records = new RecordManager(fileName, busManager);
    }

    // ---------- helpers ----------

    /** Books a seat and adds the ticket, the same way the program does. */
    private Ticket bookTicket(Passenger passenger, Bus bus, int seat) {
        bus.bookSeat(seat);
        Ticket ticket = new Ticket(records.nextTicketNo(), passenger, bus, seat);
        records.add(ticket);
        return ticket;
    }

    /** Simulates closing and reopening the program: new managers, same file. */
    private RecordManager reopen() {
        return new RecordManager(fileName, new BusManager());
    }

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

    // ---------- ticket numbers ----------

    @Test
    void ticketNumbersStartAt1001AndGoUpByOne() {
        assertEquals("TKT-1001", records.nextTicketNo());
        assertEquals("TKT-1002", records.nextTicketNo());
        assertEquals("TKT-1003", records.nextTicketNo());
    }

    // ---------- add and find ----------

    @Test
    void addedTicketCanBeFoundByNumber() {
        Ticket t = bookTicket(new RegularPassenger("Juan", 25), busManager.findById(1), 5);
        assertSame(t, records.findByNo(t.getTicketNo()));
    }

    @Test
    void searchIgnoresUpperAndLowerCase() {
        Ticket t = bookTicket(new RegularPassenger("Juan", 25), busManager.findById(1), 5);
        assertNotNull(records.findByNo(t.getTicketNo().toLowerCase()));
    }

    @Test
    void unknownTicketNumberReturnsNull() {
        assertNull(records.findByNo("TKT-9999"));
    }

    // ---------- cancel ----------

    @Test
    void cancellingRemovesTheTicketAndFreesTheSeat() {
        Bus bus = busManager.findById(1);
        Ticket t = bookTicket(new RegularPassenger("Juan", 25), bus, 5);
        assertTrue(bus.isSeatTaken(5));

        assertTrue(records.cancel(t.getTicketNo()));

        assertNull(records.findByNo(t.getTicketNo()));
        assertFalse(bus.isSeatTaken(5));
    }

    @Test
    void cancellingAnUnknownTicketReturnsFalse() {
        assertFalse(records.cancel("TKT-9999"));
    }

    @Test
    void cancellingTheSameTicketTwiceFailsTheSecondTime() {
        Ticket t = bookTicket(new RegularPassenger("Juan", 25), busManager.findById(1), 5);
        assertTrue(records.cancel(t.getTicketNo()));
        assertFalse(records.cancel(t.getTicketNo()));
    }

    // ---------- total sales ----------

    @Test
    void totalSalesAddsUpTheAmountsPaid() {
        Bus bus = new Bus(99, "Test Route", "06:00 AM", 100.00, 10);
        bookTicket(new RegularPassenger("Juan", 25), bus, 1); // 100
        bookTicket(new StudentPassenger("Ana", 18), bus, 2); // 80
        assertEquals(180.0, records.getTotalSales(), 0.001);
    }

    @Test
    void totalSalesIsZeroWhenThereAreNoTickets() {
        assertEquals(0.0, records.getTotalSales(), 0.001);
    }

    // ---------- displayAll ----------

    @Test
    void displayAllSaysSoWhenThereAreNoTickets() {
        String out = capturePrint(records::displayAll);
        assertTrue(out.contains("No records found."));
    }

    @Test
    void displayAllShowsTheTicketsTheCountAndTheTotalSales() {
        Bus bus = new Bus(99, "Test Route", "06:00 AM", 100.00, 10);
        bookTicket(new RegularPassenger("Juan Dela Cruz", 25), bus, 1);
        bookTicket(new StudentPassenger("Ana Reyes", 18), bus, 2);

        String out = capturePrint(records::displayAll);

        assertTrue(out.contains("ALL RECORDS"));
        assertTrue(out.contains("TKT-1001"));
        assertTrue(out.contains("Juan Dela Cruz"));
        assertTrue(out.contains("Ana Reyes"));
        assertTrue(out.contains("Total tickets: 2"));
        assertTrue(out.contains(String.format("P%.2f", 180.0)));
    }

    // ---------- saving ----------

    @Test
    void theFileHasOneLinePerTicket() throws IOException {
        bookTicket(new RegularPassenger("Juan", 25), busManager.findById(1), 1);
        bookTicket(new StudentPassenger("Ana", 18), busManager.findById(1), 2);

        List<String> lines = Files.readAllLines(file);
        assertEquals(2, lines.size());
        assertTrue(lines.get(0).startsWith("TKT-1001|Juan|25|Regular|1|1|"));
        assertTrue(lines.get(1).startsWith("TKT-1002|Ana|18|Student|1|2|"));
    }

    // ---------- reloading ----------

    @Test
    void ticketsAreSavedAndReloadedFromTheFile() {
        Ticket saved = bookTicket(new StudentPassenger("Ana", 18), busManager.findById(2), 7);

        RecordManager reloaded = reopen();

        Ticket loaded = reloaded.findByNo(saved.getTicketNo());
        assertNotNull(loaded);
        assertEquals("Ana", loaded.getPassenger().getName());
        assertEquals(18, loaded.getPassenger().getAge());
        assertEquals("Student", loaded.getPassenger().getType());
        assertEquals(2, loaded.getBus().getId());
        assertEquals(7, loaded.getSeatNo());
        assertEquals(saved.getAmountPaid(), loaded.getAmountPaid(), 0.001);
        assertEquals(saved.getDateBooked(), loaded.getDateBooked());
    }

    @Test
    void reloadedTicketsMarkTheirSeatsAsTaken() {
        bookTicket(new RegularPassenger("Juan", 25), busManager.findById(2), 7);

        BusManager newBusManager = new BusManager();
        new RecordManager(fileName, newBusManager);

        assertTrue(newBusManager.findById(2).isSeatTaken(7));
        assertFalse(newBusManager.findById(2).isSeatTaken(8));
    }

    @Test
    void ticketNumbersContinueAfterReloading() {
        bookTicket(new RegularPassenger("Juan", 25), busManager.findById(1), 1); // TKT-1001
        bookTicket(new RegularPassenger("Ana", 18), busManager.findById(1), 2); // TKT-1002

        assertEquals("TKT-1003", reopen().nextTicketNo());
    }

    @Test
    void cancelledTicketStaysGoneAfterReloading() {
        Ticket t = bookTicket(new RegularPassenger("Juan", 25), busManager.findById(1), 3);
        records.cancel(t.getTicketNo());

        assertNull(reopen().findByNo(t.getTicketNo()));
    }

    @Test
    void aCancelledSeatIsFreeAfterReloading() {
        Ticket t = bookTicket(new RegularPassenger("Juan", 25), busManager.findById(1), 3);
        records.cancel(t.getTicketNo());

        BusManager newBusManager = new BusManager();
        new RecordManager(fileName, newBusManager);
        assertFalse(newBusManager.findById(1).isSeatTaken(3));
    }

    @Test
    void aMissingFileStartsWithNoTickets() {
        // setUp used a file name that does not exist yet
        assertEquals(0.0, records.getTotalSales(), 0.001);
        assertNull(records.findByNo("TKT-1001"));
    }

    // ---------- damaged files ----------

    @Test
    void damagedLinesAreSkippedAndGoodTicketsStillLoad() throws IOException {
        Ticket good = bookTicket(new RegularPassenger("Juan", 25), busManager.findById(1), 3);

        Files.write(file, Arrays.asList(
                "garbage line",
                "TKT-1005|Bob|abc|Regular|1|4|100.0|2026-01-01 10:00",
                "TKT-1006|Eve|30|Regular|77|4|100.0|2026-01-01 10:00",
                "TKT-1007|Sam"), StandardOpenOption.APPEND);

        RecordManager reloaded = reopen();

        assertNotNull(reloaded.findByNo(good.getTicketNo()));
        assertNull(reloaded.findByNo("TKT-1005")); // letters in the age
        assertNull(reloaded.findByNo("TKT-1006")); // bus 77 does not exist
        assertNull(reloaded.findByNo("TKT-1007")); // fields are missing
    }

    @Test
    void aWarningIsShownWhenDamagedLinesAreSkipped() throws IOException {
        Files.write(file, Arrays.asList("garbage line", "TKT-1007|Sam"));

        String out = capturePrint(() -> new RecordManager(fileName, new BusManager()));

        assertTrue(out.contains("2 damaged record(s)"));
    }

    @Test
    void noWarningIsShownForAGoodFile() {
        bookTicket(new RegularPassenger("Juan", 25), busManager.findById(1), 3);

        String out = capturePrint(() -> new RecordManager(fileName, new BusManager()));

        assertFalse(out.contains("damaged"));
    }

    @Test
    void aDuplicateSeatInTheFileIsSkipped() throws IOException {
        Ticket first = bookTicket(new RegularPassenger("Juan", 25), busManager.findById(1), 3);
        String duplicate = first.toFileLine().replace("TKT-1001", "TKT-1002");
        Files.write(file, Arrays.asList(duplicate), StandardOpenOption.APPEND);

        RecordManager reloaded = reopen();

        assertNotNull(reloaded.findByNo("TKT-1001"));
        assertNull(reloaded.findByNo("TKT-1002"));
    }

    // ---------- files that cannot be saved or read ----------

    /** A file inside a folder that does not exist, so every save must fail. */
    private RecordManager managerThatCannotSave() {
        String missingFolder = tempDir.resolve("no-such-folder").resolve("tickets.txt").toString();
        return new RecordManager(missingFolder, new BusManager());
    }

    /** Returns the text that follows "Reason: " on the same line. */
    private String reasonShownIn(String printed) {
        int start = printed.indexOf("Reason: ");
        assertTrue(start >= 0, "the message should have a Reason");
        return printed.substring(start + "Reason: ".length()).split("\\R")[0];
    }

    private Ticket anyTicket() {
        return new Ticket("TKT-1001", new RegularPassenger("Juan", 25), busManager.findById(1), 5);
    }

    @Test
    void aFriendlyMessageIsShownWhenTheFileCannotBeSaved() {
        RecordManager broken = managerThatCannotSave();

        String out = capturePrint(() -> broken.add(anyTicket()));

        assertTrue(out.contains("Warning: Could not save the tickets to"));
        assertTrue(out.contains("tickets.txt"));
        assertTrue(out.contains("kept only until you close the program"));
    }

    @Test
    void theReasonIsShownInPlainWordsWithoutRepeatingTheFileName() {
        RecordManager broken = managerThatCannotSave();

        String out = capturePrint(() -> broken.add(anyTicket()));

        String reason = reasonShownIn(out);
        assertFalse(reason.isEmpty());
        assertFalse(reason.contains("tickets.txt")); // the file name is already in the first part
        assertFalse(reason.contains("no-such-folder")); // and so is the path
        assertFalse(reason.contains("("));
    }

    @Test
    void theSaveMessageDoesNotShowATechnicalExceptionName() {
        RecordManager broken = managerThatCannotSave();

        String out = capturePrint(() -> broken.add(anyTicket()));

        assertFalse(out.contains("Exception"));
        assertFalse(out.contains("\tat "));
    }

    @Test
    void theTicketIsStillKeptInMemoryAfterASaveError() {
        RecordManager broken = managerThatCannotSave();
        capturePrint(() -> broken.add(anyTicket()));

        assertNotNull(broken.findByNo("TKT-1001"));
        assertEquals(200.0, broken.getTotalSales(), 0.001); // bus 1 fare; the program keeps working
    }

    @Test
    void cancellingAlsoShowsTheMessageWhenSavingFails() {
        RecordManager broken = managerThatCannotSave();
        capturePrint(() -> broken.add(anyTicket()));

        String out = capturePrint(() -> broken.cancel("TKT-1001"));

        assertTrue(out.contains("Warning: Could not save the tickets to"));
        assertNull(broken.findByNo("TKT-1001"));
    }

    @Test
    void noWarningIsShownWhenSavingWorks() {
        String out = capturePrint(() -> records.add(anyTicket()));
        assertFalse(out.contains("Warning"));
    }

    @Test
    void aFriendlyMessageIsShownWhenTheFileCannotBeRead() throws IOException {
        Files.createDirectory(file); // a folder named tickets.txt cannot be read as a file

        String out = capturePrint(() -> new RecordManager(fileName, new BusManager()));

        assertTrue(out.contains("Warning: Could not read the saved tickets from"));
        assertTrue(out.contains("may be missing"));
        assertFalse(reasonShownIn(out).contains("tickets.txt"));
        assertFalse(out.contains("Exception"));
    }

    @Test
    void theProgramStartsWithNoTicketsWhenTheFileCannotBeRead() throws IOException {
        Files.createDirectory(file);
        RecordManager unreadable = new RecordManager(fileName, new BusManager());

        assertEquals(0.0, unreadable.getTotalSales(), 0.001);
        assertNull(unreadable.findByNo("TKT-1001"));
    }

    @Test
    void theSavedFileHasTheSameLinesAsBefore() throws IOException {
        records.add(anyTicket());

        List<String> lines = Files.readAllLines(file);
        assertEquals(1, lines.size());
        assertEquals(anyTicket().toFileLine().split("\\|")[0], lines.get(0).split("\\|")[0]);
        assertTrue(lines.get(0).startsWith("TKT-1001|Juan|25|Regular|1|5|"));
    }
}
