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
    /** The youngest age accepted for a passenger. */
    public static final int MIN_AGE = 1;

    /** The oldest age accepted for a passenger. */
    public static final int MAX_AGE = 120;

    private final String name; // encapsulation: private fields
    private final int age;

    /**
     * Creates a passenger. Only subclasses can call this constructor.
     * Extra spaces in the name are removed: spaces at the start and the end
     * are dropped, and several spaces (or tabs) in a row become one space.
     *
     * @param name the passenger's full name
     * @param age  the passenger's age in years, from {@link #MIN_AGE} to
     *             {@link #MAX_AGE}
     * @throws IllegalArgumentException if the name is {@code null}, empty,
     *                                  or contains only spaces, or if the age
     *                                  is outside {@link #MIN_AGE} to
     *                                  {@link #MAX_AGE}
     */
    protected Passenger(String name, int age) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Passenger name cannot be empty.");
        }
        if (age < MIN_AGE || age > MAX_AGE) {
            throw new IllegalArgumentException("Age must be from " + MIN_AGE + " to " + MAX_AGE + ".");
        }
        this.name = name.trim().replaceAll("\\s+", " ");
        this.age = age;
    }

    /**
     * Returns the passenger's name, without extra spaces.
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
     * Returns the ID the passenger must show to get this fare.
     *
     * @return the name of the ID, for example {@code "Student ID"}
     */
    public abstract String getRequiredId();

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
     * @param age  the passenger's age in years, from {@link #MIN_AGE} to
     *             {@link #MAX_AGE}
     * @return a new passenger of the matching subclass
     * @throws IllegalArgumentException if the name is {@code null}, empty,
     *                                  or contains only spaces, or if the age
     *                                  is outside {@link #MIN_AGE} to
     *                                  {@link #MAX_AGE}
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
