package edu.slsu.IT205;

/**
 * A passenger who is a senior citizen.
 * Gets the standard 20% discount from {@link DiscountedPassenger}.
 */
public class SeniorPassenger extends DiscountedPassenger {

    /**
     * Creates a Senior passenger.
     *
     * @param name the passenger's full name
     * @param age  the passenger's age in years
     */
    public SeniorPassenger(String name, int age) {
        super(name, age);
    }

    /**
     * Returns the type name.
     *
     * @return {@code "Senior"}
     */
    @Override
    public String getType() {
        return "Senior";
    }

    /**
     * Returns the ID this passenger must show.
     *
     * @return {@code "Senior Citizen ID"}
     */
    @Override
    public String getRequiredId() {
        return "Senior Citizen ID";
    }
}
