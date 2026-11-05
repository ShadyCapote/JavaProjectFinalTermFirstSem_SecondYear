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
}
