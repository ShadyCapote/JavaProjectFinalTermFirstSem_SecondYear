package edu.slsu.IT205;

/**
 * Main class: login, then the menu that connects all the other objects.
 *
 * Run: javac *.java
 * java BusTicketingSystem
 */
public class BusTicketingSystem {
    private final BusManager busManager = new BusManager();
    private final RecordManager recordManager = new RecordManager("tickets.txt", busManager);
    private final ConsoleInput input = new ConsoleInput();
    private final LogIn login = new LogIn(input);
    private User currentUser;

    public static void main(String[] args) {
        new BusTicketingSystem().run();
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

    private void bookTicket() {
        busManager.displayAll();
        Bus bus = busManager.findById(input.readInt("\nEnter Bus ID: "));
        if (bus == null) {
            System.out.println("Bus not found.");
            return;
        }
        if (bus.isFull()) {
            System.out.println("Sorry, this bus is already full.");
            return;
        }

        String name = input.readLine("Passenger name: ");
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        int age = input.readInt("Age: ");
        if (age <= 0 || age > 120) {
            System.out.println("Invalid age.");
            return;
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
            System.out.println("Invalid seat number.");
            return;
        }
        if (!bus.bookSeat(seatNo)) {
            System.out.println("Seat already taken.");
            return;
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