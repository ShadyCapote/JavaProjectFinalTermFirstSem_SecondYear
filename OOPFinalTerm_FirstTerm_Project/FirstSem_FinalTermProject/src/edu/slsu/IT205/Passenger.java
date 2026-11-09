package edu.slsu.IT205;

/**
 * Abstract parent class of all passengers.
 * <p>
 * A passenger has a name and an age. Each subclass decides its own type and
 * discount rate, so the same call to {@link #computeFare(double)} gives a
 * different price for each kind of passenger (polymorphism).
 *
 * @see RegularPassenger
 * @see DiscountedPassenger
 */
public abstract class Passenger {
    private final String name; // encapsulation: private fields
    private final int age;

    /**
     * Creates a passenger. Only subclasses can call this constructor.
     *
     * @param name the passenger's full name
     * @param age  the passenger's age in years
     * @throws IllegalArgumentException if the name is {@code null}, empty,
     *                                  or contains only spaces
     */
    protected Passenger(String name, int age) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Passenger name cannot be empty.");
        }
        this.name = name;
        this.age = age;
    }

    /**
     * Returns the passenger's name.
     *
     * @return the full name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the passenger's age.
     *
     * @return the age in years
     */
    public int getAge() {
        return age;
    }

    /**
     * Returns the passenger type shown on tickets and records.
     *
     * @return a type name such as {@code "Regular"} or {@code "Student"}
     */
    public abstract String getType();

    /**
     * Returns the discount rate of this passenger type.
     *
     * @return the discount as a fraction, for example {@code 0.20} for 20%
     */
    public abstract double getDiscountRate();

    /**
     * Calculates the fare this passenger has to pay.
     * The result depends on the subclass through {@link #getDiscountRate()}.
     *
     * @param baseFare the full fare of the bus
     * @return the fare after the discount is applied
     */
    public double computeFare(double baseFare) {
        return baseFare * (1 - getDiscountRate());
    }

    /**
     * Factory method: creates the correct Passenger subclass from a type name.
     * The type is not case sensitive. An unknown type gives a
     * {@link RegularPassenger}.
     *
     * @param type "student", "senior", "pwd", or anything else for regular
     * @param name the passenger's full name
     * @param age  the passenger's age in years
     * @return a new passenger of the matching subclass
     * @throws IllegalArgumentException if the name is {@code null}, empty,
     *                                  or contains only spaces
     */
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