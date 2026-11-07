package edu.slsu.IT205;

import java.util.NoSuchElementException;

/**
 * Main class: login, then the menu that connects all the other objects.
 *
 * Exception handling used in this class:
 * - BookingException (checked) -> caught in runMenu(), message shown to the
 * user
 * - RuntimeException -> caught in runMenu(), so one bug cannot crash the
 * program
 * - NoSuchElementException -> input stream closed (Ctrl+D / Ctrl+Z), caught in
 * main()
 *
 * Run: javac *.java
 * java edu.slsu.IT205.BusTicketingSystem
 */
public class BusTicketingSystem {
    private final BusManager busManager = new BusManager();
    private final RecordManager recordManager = new RecordManager("tickets.txt", busManager);
    private final ConsoleInput input = new ConsoleInput();
    private final LogIn login = new LogIn(input);
    private User currentUser;

    public static void main(String[] args) {
        try {
            new BusTicketingSystem().run();
        } catch (NoSuchElementException e) {
            // The keyboard input was closed, so there is nothing more to read.
            System.out.println("\nInput was closed. Exiting the program.");
        }
    }

    // ===================== PROGRAM FLOW =====================

    public void run() {
        while (true) {
            currentUser = login.start(); // null = exit / too many attempts
            if (currentUser == null)
                break;
            runMenu(); // returns when the user logs out
        }
        System.out.println("\nThank you for using the Bus Ticketing System. Goodbye!");
    }

    private void runMenu() {
        int choice;
        do {
            showMainMenu();
            choice = input.readInt("Enter choice: ");
            try {
                switch (choice) {
                    case 1:
                        busManager.displayAll();
                        break;
                    case 2:
                        bookTicket();
                        break;
                    case 3:
                        recordManager.displayAll();
                        break;
                    case 4:
                        searchRecord();
                        break;
                    case 5:
                        cancelTicket();
                        break;
                    case 6:
                        System.out.println("\nLogged out.");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (BookingException e) {
                // Expected problem (bad bus, age, seat...): just tell the user.
                System.out.println(e.getMessage());
            } catch (NoSuchElementException e) {
                throw e; // input closed: let main() handle it
            } catch (RuntimeException e) {
                // Unexpected problem: report it and return to the menu instead of crashing.
                System.out.println("Something went wrong: " + e.getMessage());
            }
        } while (choice != 6);
    }

    private void showMainMenu() {
        System.out.println("\n==========================================");
        System.out.println("        BUS TICKETING SYSTEM");
        System.out.println("  Logged in as: " + currentUser.getUsername()
                + " (" + currentUser.getRole() + ")");
        System.out.println("==========================================");
        System.out.println("  [1] View Available Buses");
        System.out.println("  [2] Book a Ticket");
        System.out.println("  [3] View All Records");
        System.out.println("  [4] Search Record (by Ticket No.)");
        System.out.println("  [5] Cancel a Ticket");
        System.out.println("  [6] Logout");
        System.out.println("==========================================");
    }

    // ===================== ACTIONS =====================

    private void bookTicket() throws BookingException {
        busManager.displayAll();
        Bus bus = busManager.findById(input.readInt("\nEnter Bus ID: "));
        if (bus == null) {
            throw new BookingException("Bus not found.");
        }
        if (bus.isFull()) {
            throw new BookingException("Sorry, this bus is already full.");
        }

        String name = input.readLine("Passenger name: ");
        if (name.isEmpty()) {
            throw new BookingException("Name cannot be empty.");
        }

        int age = input.readInt("Age: ");
        if (age <= 0 || age > 120) {
            throw new BookingException("Invalid age.");
        }

        System.out.println("Passenger type: [1] Regular  [2] Student  [3] Senior  [4] PWD");
        String type;
        switch (input.readInt("Choose type: ")) {
            case 2:
                type = "Student";
                break;
            case 3:
                type = "Senior";
                break;
            case 4:
                type = "PWD";
                break;
            default:
                type = "Regular";
        }
        Passenger passenger = Passenger.create(type, name, age);

        bus.showSeatMap();
        int seatNo = input.readInt("Choose seat number (1-" + bus.getCapacity() + "): ");
        if (!bus.isValidSeat(seatNo)) {
            throw new BookingException("Invalid seat number.");
        }
        if (!bus.bookSeat(seatNo)) {
            throw new BookingException("Seat already taken.");
        }

        Ticket ticket = new Ticket(recordManager.nextTicketNo(), passenger, bus, seatNo);
        recordManager.add(ticket);

        System.out.println("\nBooking successful!");
        ticket.print();
    }

    private void searchRecord() {
        Ticket ticket = recordManager.findByNo(input.readLine("\nEnter Ticket No. (e.g. TKT-1001): "));
        if (ticket == null) {
            System.out.println("Ticket not found.");
        } else {
            ticket.print();
        }
    }

    private void cancelTicket() {
        // Permission depends on the logged-in user's role (polymorphism)
        if (!currentUser.canCancelTickets()) {
            System.out.println("\nAccess denied. Only an Admin can cancel tickets.");
            return;
        }

        String ticketNo = input.readLine("\nEnter Ticket No. to cancel: ");
        Ticket ticket = recordManager.findByNo(ticketNo);
        if (ticket == null) {
            System.out.println("Ticket not found.");
            return;
        }
        String confirm = input.readLine("Cancel ticket for " + ticket.getPassenger().getName() + "? (y/n): ");
        if (confirm.equalsIgnoreCase("y")) {
            recordManager.cancel(ticketNo);
            System.out.println("Ticket cancelled.");
        } else {
            System.out.println("Cancellation aborted.");
        }
    }
}
