# OOP Concepts in the Bus Ticketing System

This document shows where the four main object-oriented ideas are used in the project: encapsulation, inheritance, polymorphism and abstraction. For the overall design, see [ARCHITECTURE.md](ARCHITECTURE.md).

| Concept | In one sentence | Where it is used |
|---------|-----------------|------------------|
| Encapsulation | Keep data private and expose it only through methods | `Passenger`, `Ticket` |
| Inheritance | A class reuses and extends another class | `Passenger` and its subclasses |
| Polymorphism | The same call behaves differently for different objects | `getDiscountRate()`, `computeFare()` |
| Abstraction | Show what something does and hide how | `Passenger`, `DiscountedPassenger`, `Printable` |

## 1. Encapsulation

**Definition:** Encapsulation means keeping an object's data private and letting other code reach it only through methods. The object controls how its data is read and changed.

**In this project:** `Passenger` keeps the name and age in private fields. Other classes read them with getters, and nothing can change them after the passenger is created.

```java
public abstract class Passenger {
    private final String name;
    private final int age;

    public Passenger(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
}
```

`Ticket` does the same. Its ticket number, passenger, bus, seat, amount and date are all `private final`, so a ticket cannot be altered after it is booked:

```java
private final String ticketNo;
private final Passenger passenger;
private final Bus bus;
private final int seatNo;
private final double amountPaid;
private final String dateBooked;
```

**Why it helps:**
- Nobody can set a ticket's amount to a wrong value by accident.
- `toFileLine()` controls how a ticket is written to `tickets.txt`. It replaces a `|` in a name with a space, so the file format cannot be broken from outside.
- The inside of a class can change later without breaking the code that uses it.

## 2. Inheritance

**Definition:** Inheritance lets a class take the fields and methods of another class and add its own. The new class is a kind of the old one (an "is-a" relationship).

**In this project:** every passenger type extends `Passenger`. The three discounted types share a middle class:

```
Passenger (abstract)
├── RegularPassenger
└── DiscountedPassenger (abstract)
    ├── StudentPassenger
    ├── SeniorPassenger
    └── PWDPassenger
```

A subclass is short, because it reuses everything from its parents. It only sends the name and age up with `super(...)` and adds what is different:

```java
public class StudentPassenger extends DiscountedPassenger {

    public StudentPassenger(String name, int age) {
        super(name, age);
    }

    @Override
    public String getType() {
        return "Student";
    }

    @Override
    public String getRequiredId() {
        return "Student ID";
    }
}
```

`StudentPassenger` never declares `getName()`, `getAge()`, `computeFare()` or the 20% discount, yet all of them work.

**Why it helps:**
- The 20% rate is written once, in `DiscountedPassenger`. Changing it changes the discount for students, seniors and PWD passengers together.
- Adding a new passenger type takes only a few lines.

**Is-a versus has-a:** a `StudentPassenger` *is a* `Passenger` (inheritance). A `Ticket` *has a* `Passenger` and *has a* `Bus` (composition). The ticket stores them in fields instead of extending them.

## 3. Polymorphism

**Definition:** Polymorphism means one method call can behave differently depending on the real type of the object. Java picks the right version while the program is running.

**In this project:** `Passenger` says a passenger pays no discount, and `DiscountedPassenger` overrides that with 20%:

```java
// Passenger.java
public double getDiscountRate() {
    return 0.0;
}

public double computeFare(double baseFare) {
    return baseFare - baseFare * getDiscountRate();
}

// DiscountedPassenger.java
@Override
public double getDiscountRate() {
    return DISCOUNT_RATE;   // 0.20
}
```

`computeFare()` is written only once, in `Passenger`, but it calls `getDiscountRate()`. Which version runs depends on the object:

```java
Passenger[] group = {
    new RegularPassenger("Juan", 25),
    new StudentPassenger("Ana", 18),
    new SeniorPassenger("Lola", 70)
};

for (Passenger p : group) {
    System.out.println(p.getType() + ": " + p.computeFare(100.0));
}
```

Output:

```
Regular: 100.0
Student: 80.0
Senior: 80.0
```

The most important use is in `Ticket`. Its constructor takes any `Passenger` and never checks which kind it is:

```java
passenger.computeFare(bus.getFare())
```

A regular passenger pays P200.00 and a student pays P160.00 on the same bus, with no `if` statements for the types.

**Other examples:**
- **Overriding:** `getType()` returns a different text in each subclass.
- **Overloading:** `Ticket` has two constructors with different parameters. One creates a new booking, and the other loads a saved one.

## 4. Abstraction

**Definition:** Abstraction means showing only what is needed and hiding the details. Abstract classes and interfaces say *what* must be done and leave *how* to the subclasses.

**In this project:**

- **`Passenger` is abstract.** You cannot write `new Passenger(...)`, because there is no such thing as a plain passenger. It declares one method that every subclass must provide:

```java
public abstract String getType();
```

- **`DiscountedPassenger` is abstract too.** It adds a second requirement. Each discounted type must say which ID the passenger shows when boarding:

```java
public abstract String getRequiredId();
```

A class that forgets to implement it does not compile, so the rule cannot be skipped by mistake.

- **`Printable` is an interface.** It only promises that an object can print itself. `Ticket` implements it and decides how the ticket looks:

```java
public class Ticket implements Printable {
    @Override
    public void print() { ... }
}
```

- **`Passenger.create(...)` hides which subclass is built.** The code that loads `tickets.txt` only has the text `"Student"`. It calls the factory and gets back a `Passenger`, without needing to know about `StudentPassenger`:

```java
Passenger p = Passenger.create("Student", "Ana", 18);
```

**Why it helps:** the rest of the program depends on the simple idea of a `Passenger` or a `Printable`, not on the details of each class.

## How the four ideas work together

When a student books a ticket:

1. `Passenger.create("Student", ...)` builds a `StudentPassenger`. *(abstraction)*
2. The object has a name, age and `computeFare()` it got from its parents. *(inheritance)*
3. The name and age are private and are read through getters. *(encapsulation)*
4. `Ticket` calls `computeFare()`, and the student's 20% rate is used. *(polymorphism)*