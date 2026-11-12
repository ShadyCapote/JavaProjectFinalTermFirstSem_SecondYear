package edu.slsu.IT205;

import java.util.Comparator;
import java.util.List;
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

    private void displayWelcomeBanner() {
        System.out.println();
        System.out.println("======================================================");
        System.out.println("                                                      ");
        System.out.println("             WELCOME TO OUR BUS TERMINAL              ");
        System.out.println("                                                      ");
        System.out.println("                  BUS TICKETING SYSTEM                ");
        System.out.println("                                                      ");
        System.out.println("             Your Journey, Our Priority!              ");
        System.out.println("                                                      ");
        System.out.println("======================================================");
        System.out.println("          BUS RESERVATION & MANAGEMENT SYSTEM         ");
        System.out.println("======================================================");
        System.out.println();
    }

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

        displayWelcomeBanner();

        while (true) {
            currentUser = login.start();

            if (currentUser == null)
                break;

            runMenu();
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
                        viewPassengersOnBus();
                        break;
                    case 7:
                        viewSalesSummary();
                        break;
                    case 0:
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
        } while (choice != 0);
    }

    private void showMainMenu() {

        System.out.println("\n==================================================");
        System.out.println("               BUS TICKETING SYSTEM");
        System.out.println("==================================================");

        // Logged-in user information
        System.out.println("  LOGGED-IN USER INFORMATION");
        System.out.println("  Username : " + currentUser.getUsername());
        System.out.println("  Role     : " + currentUser.getRole());

        System.out.println("--------------------------------------------------");
        System.out.println("                    MAIN MENU");
        System.out.println("--------------------------------------------------");

        System.out.println("  [1] View Available Buses");
        System.out.println("  [2] Book a Ticket");
        System.out.println("  [3] View All Records");
        System.out.println("  [4] Search Record (by Ticket No.)");
        System.out.println("  [5] Cancel a Ticket");
        System.out.println("  [6] View Passengers on a Bus");
        System.out.println("  [7] Sales Summary per Route (Admin only)");
        System.out.println("  [0] Logout");

        System.out.println("==================================================");
    }

    // ===================== ACTIONS =====================

    private void bookTicket() throws BookingException {

        System.out.println("\n============== SELECT YOUR BUS ==============");

        List<Bus> buses = busManager.getBuses();

        for (int i = 0; i < buses.size(); i++) {
            Bus b = buses.get(i);

            System.out.println("[" + (i + 1) + "] " + b.getRoute());
            System.out.println("    Departure : " + b.getDeparture());
            System.out.printf("    Fare      : P%.2f%n", b.getFare());
            System.out.println("    Bus ID    : " + b.getId());
            System.out.println("---------------------------------------------");
        }

        int choice = input.readInt("Select bus number: ");

        if (choice < 1 || choice > buses.size()) {
            throw new BookingException("Invalid bus selection.");
        }

        Bus bus = buses.get(choice - 1);

        if (bus == null) {
            throw new BookingException("Bus not found.");
        }
        if (bus.isFull()) {
            throw new BookingException("This bus is already full.");
        }

        String name = input.readLine("Enter passenger name: ");
        if (name.isEmpty()) {
            throw new BookingException("Name cannot be empty.");
        }

        int age = input.readInt("Enter age: ");
        if (age < Passenger.MIN_AGE || age > Passenger.MAX_AGE) {
            throw new BookingException("Invalid age. Please enter an age from "
                    + Passenger.MIN_AGE + " to " + Passenger.MAX_AGE + ".");
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
            throw new BookingException("Invalid seat number. Please choose a seat from 1 to "
                    + bus.getCapacity() + ".");
        }
        if (bus.isSeatTaken(seatNo)) {
            throw new BookingException(seatTakenMessage(bus, seatNo));
        }

        // Show a summary and ask for confirmation BEFORE anything is saved
        System.out.println("\n------------------- BOOKING SUMMARY -------------------");
        System.out.println("Route      : " + bus.getRoute());
        System.out.println("Departure  : " + bus.getDeparture());
        System.out.println("Passenger  : " + passenger.getName());
        System.out.println("Age/Type   : " + passenger.getAge() + " / " + passenger.getType());
        System.out.println("Seat No.   : " + seatNo);
        System.out.printf("Full fare  : P%.2f%n", bus.getFare());
        System.out.printf("Discount   : %.0f%%%n", passenger.getDiscountRate() * 100);
        System.out.printf("You pay    : P%.2f%n", passenger.computeFare(bus.getFare()));
        System.out.println("--------------------------------------------------------");
        String confirm = input.readLine("Confirm booking? (y/n): ");
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Booking aborted. Nothing was saved.");
            return;
        }

        // Only now is the seat reserved and the ticket saved
        if (!bus.bookSeat(seatNo)) {
            throw new BookingException(seatTakenMessage(bus, seatNo));
        }
        Ticket ticket = new Ticket(recordManager.nextTicketNo(), passenger, bus, seatNo);
        recordManager.add(ticket);

        System.out.println("\nBooking successful.");
        ticket.print();
    }

    private void viewPassengersOnBus() {
        busManager.displayAll();
        int busId = input.readInt("\nEnter Bus ID: ");
        Bus bus = busManager.findById(busId);
        if (bus == null) {
            System.out.println("Bus not found.");
            return;
        }

        List<Ticket> tickets = recordManager.getTicketsByBus(busId);
        System.out.println("\n----------------- PASSENGERS ON BUS -----------------");
        System.out.println("Bus " + bus.getId() + ": " + bus.getRoute() + " (" + bus.getDeparture() + ")");
        if (tickets.isEmpty()) {
            System.out.println("No passengers found.");
            return;
        }

        tickets.sort(Comparator.comparingInt(Ticket::getSeatNo)); // lowest seat first
        System.out.printf("%-5s %-18s %-9s %-10s %-9s%n", "Seat", "Passenger", "Type", "Ticket No", "Paid");
        System.out.println("-------------------------------------------------------");
        for (Ticket t : tickets) {
            System.out.printf("%-5d %-18s %-9s %-10s P%-8.2f%n",
                    t.getSeatNo(), t.getPassenger().getName(), t.getPassenger().getType(),
                    t.getTicketNo(), t.getAmountPaid());
        }
        System.out.println("-------------------------------------------------------");
        System.out.println(tickets.size() + " of " + bus.getCapacity() + " seats booked.");
    }

    private void viewSalesSummary() {
        // Only an Admin may see the sales
        if (!isAdmin()) {
            System.out.println("\nAccess denied. Only an Admin can view the sales summary.");
            return;
        }

        System.out.println("\n----------------- SALES SUMMARY PER ROUTE -----------------");
        System.out.printf("%-4s %-27s %-8s %-10s%n", "ID", "Route", "Tickets", "Sales");
        System.out.println("--------------------------------------------------------");
        int totalTickets = 0;
        for (Bus bus : busManager.getBuses()) {
            int count = recordManager.getTicketsByBus(bus.getId()).size();
            totalTickets += count;
            System.out.printf("%-4d %-27s %-8d P%-9.2f%n",
                    bus.getId(), bus.getRoute(), count,
                    recordManager.getSalesByBus(bus.getId()));
        }
        System.out.println("--------------------------------------------------------");
        System.out.printf("%-4s %-27s %-8d P%-9.2f%n", "", "TOTAL",
                totalTickets, recordManager.getTotalSales());

        pressEnterToContinue();
    }

    /** Returns true if the logged-in user is an Admin. */
    private boolean isAdmin() {
        return "Admin".equals(currentUser.getRole());
    }

    /**
     * Builds the message for a seat that is already taken, listing the seats that
     * are still free.
     */
    private String seatTakenMessage(Bus bus, int seatNo) {
        List<Integer> free = bus.getFreeSeats();
        if (free.isEmpty()) {
            return "Seat " + seatNo + " is already taken. This bus has no free seats left.";
        }
        StringBuilder list = new StringBuilder();
        for (int seat : free) {
            if (list.length() > 0)
                list.append(", ");
            list.append(seat);
        }
        return "Seat " + seatNo + " is already taken. Free seats: " + list + ".";
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
            System.out.println("Cancellation aborted. The ticket was kept.");
        }
    }

    private void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        input.readLine("");
    }

}
