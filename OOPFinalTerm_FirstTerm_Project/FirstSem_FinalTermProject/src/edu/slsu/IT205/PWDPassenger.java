package edu.slsu.IT205;

/**
 * A passenger who is a person with disability.
 * Gets the standard 20% discount from {@link DiscountedPassenger}.
 */
public class PWDPassenger extends DiscountedPassenger {

    /**
     * Creates a PWD passenger.
     *
     * @param name the passenger's full name
     * @param age  the passenger's age in years
     */
    public PWDPassenger(String name, int age) {
        super(name, age);
    }

    /**
     * Returns the type name.
     *
     * @return {@code "PWD"}
     */
    @Override
    public String getType() {
        return "PWD";
    }

    /**
     * Returns the ID this passenger must show.
     *
     * @return {@code "PWD ID"}
     */
    @Override
    public String getRequiredId() {
        return "PWD ID";
    }
}
