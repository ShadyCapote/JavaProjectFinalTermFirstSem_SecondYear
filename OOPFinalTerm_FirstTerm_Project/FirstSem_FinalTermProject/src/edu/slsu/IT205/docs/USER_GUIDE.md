User Guide

This guide shows every screen of the Bus Ticketing System and what to type. Text after a prompt, like Username (type 'exit' to quit): admin, is what you type.

1. Starting the program

From the project folder:

javac -d out src/edu/slsu/IT205/*.java
java -cp out edu.slsu.IT205.BusTicketingSystem

In an IDE (NetBeans, Eclipse, IntelliJ), run the BusTicketingSystem class.

2. Logging in

The program starts at the login screen.

==========================================
              LOGIN
==========================================
Username (type 'exit' to quit): admin
Password: 
Welcome, admin (Admin)!
Username	Password	Role
admin	admin123	Admin
cashier	cashier123	Cashier
The username is not case sensitive. The password is.
In a normal terminal the password is hidden as you type. In some IDE consoles it is visible.
A wrong username or password shows how many attempts are left: Incorrect username or password. Attempts left: 2
After 3 wrong attempts the program closes: Too many failed attempts. The system is now closing.
Type exit as the username to quit.
3. The main menu

After logging in you see the main menu. It always shows who is logged in.

==========================================
        BUS TICKETING SYSTEM
  Logged in as: admin (Admin)
==========================================
  [1] View Available Buses
  [2] Book a Ticket
  [3] View All Records
  [4] Search Record (by Ticket No.)
  [5] Cancel a Ticket
  [6] View Passengers on a Bus
  [7] Sales Summary per Route (Admin only)
  [8] Logout
==========================================
Enter choice:

Type the number of the option and press Enter.

Option	What it does	Admin	Cashier
1	Shows the buses and their free seats	Yes	Yes
2	Books a ticket	Yes	Yes
3	Shows all tickets and the total sales	Yes	Yes
4	Finds one ticket by its number	Yes	Yes
5	Cancels a ticket	Yes	No
6	Logs out and returns to the login screen	Yes	Yes
4. Walkthrough of each option
4.1 View Available Buses (option 1)
----------------- AVAILABLE BUSES -----------------
ID   Route                       Departure  Fare       Seats   
--------------------------------------------------------
1    Southern Leyte - Silago     06:00 AM   P200.00    20/20
2    Southern Leyte - Sogod      08:00 AM   P150.00    20/20
3    Southern Leyte - Maasin     09:30 AM   P180.00    20/20
4    Southern Leyte - Hilongos   08:00 PM   P220.00    20/20
ID is the number you type when booking.
Fare is the full fare before any discount.
Seats is free seats out of total seats. 18/20 means 2 seats are taken.
4.2 Book a Ticket (option 2)

The program asks for six things, one at a time:

Step	Prompt	What to type
1	Enter Bus ID:	the ID from the bus table, for example 1
2	Passenger name:	the passenger's name, for example Juan Dela Cruz
3	Age:	a whole number from 1 to 120
4	Choose type:	1 Regular, 2 Student, 3 Senior, or 4 PWD
5	Choose seat number (1-20):	a free seat shown in the seat map
6	(automatic)	the ticket is printed and saved

Seat map. Free seats show their number, and taken seats show XX:

Seat Map (XX = taken)
 01  02  03  04 
 05  06  07  08 
 09  10  11  12 
 13  14  15  16 
 17  18  19  20

The finished ticket:

Booking successful!

+------------------------------------------+
|            BUS TICKET                    |
+------------------------------------------+
| Ticket No : TKT-1001                     |
| Passenger : Juan Dela Cruz               |
| Age/Type  : 20 / Student                 |
| Route     : Southern Leyte - Silago      |
| Departure : 06:00 AM                     |
| Bus No.   : Bus 1                        |
| Seat No.  : 5                            |
| Amount    : P160.00                      |
| Booked on : 2026-11-07 14:30             |
+------------------------------------------+
|     Thank you and have a safe trip!      |
+------------------------------------------+

Discounts. Students, seniors and PWD passengers get 20% off the full fare.

Passenger type	Discount	Fare on a P200.00 bus
Regular	none	P200.00
Student	20%	P160.00
Senior	20%	P160.00
PWD	20%	P160.00

If something is wrong, the program shows a message and returns to the main menu. Start the booking again with option 2. Nothing is saved until the ticket is printed.

4.3 View All Records (option 3)
------------------------------ ALL RECORDS ------------------------------
Ticket No  Passenger          Type      Route                       Seat  Paid     
--------------------------------------------------------------------------------
TKT-1001   Juan Dela Cruz     Student   Southern Leyte - Silago     5     P160.00  
--------------------------------------------------------------------------------
Total tickets: 1   |   Total sales: P160.00

With no tickets yet, it shows No records found.

4.4 Search Record (option 4)

Type the ticket number, for example TKT-1001 (upper or lower case both work). The full ticket is printed, the same as when it was booked. If there is no such ticket, you see Ticket not found.

Enter Ticket No. (e.g. TKT-1001): TKT-1001
4.5 Cancel a Ticket (option 5, Admin only)
Enter Ticket No. to cancel: TKT-1001
Cancel ticket for Juan Dela Cruz? (y/n): y
Ticket cancelled.
Type y to confirm. Anything else shows Cancellation aborted. and keeps the ticket.
Cancelling frees the seat, so it can be booked again.
A Cashier who chooses this option sees: Access denied. Only an Admin can cancel tickets.