package edu.slsu.IT205;

/**
 * Abstract middle class for passengers who get the standard 20% discount.
 * (Student, Senior, and PWD all inherit from this.)
 */
public abstract class DiscountedPassenger extends Passenger {
    public static final double DISCOUNT_RATE = 0.20;

    protected DiscountedPassenger(String name, int age) {
        super(name, age);
    }

    @Override
    public double getDiscountRate() {
        return DISCOUNT_RATE;
    }

    /**
     * Applies the discount to a base fare.
     *
     * @param baseFare the full fare before discount
     * @return the fare after the discount
     */
    public double applyDiscount(double baseFare) {
        return baseFare * (1 - DISCOUNT_RATE);
    }

    /**
     * Gives the ID a discounted passenger must show when boarding,
     * for example "Student ID" or "Senior Citizen ID".
     * Each subclass says which one it needs.
     */
    public abstract String getRequiredId();
}