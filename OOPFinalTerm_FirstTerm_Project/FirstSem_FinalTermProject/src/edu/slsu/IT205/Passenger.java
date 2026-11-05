package edu.slsu.IT205;

/**
 * Abstract parent class of all passengers.
 * Subclasses decide their own type and discount (polymorphism).
 */
public abstract class Passenger {
    private final String name; // encapsulation: private fields
    private final int age;

    protected Passenger(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    // Each subclass must define these
    public abstract String getType();

    public abstract double getDiscountRate();

    // Shared behavior, but the result depends on the subclass's discount
    public double computeFare(double baseFare) {
        return baseFare * (1 - getDiscountRate());
    }

    /** Factory method: builds the correct Passenger subclass from a type name. */
    public static Passenger create(String type, String name, int age) {
        switch (type.toLowerCase()) {
            case "student":
                return new StudentPassenger(name, age);
            case "senior":
                return new SeniorPassenger(name, age);
            case "pwd":
                return new PWDPassenger(name, age);
            default:
                return new RegularPassenger(name, age);
        }
    }
}