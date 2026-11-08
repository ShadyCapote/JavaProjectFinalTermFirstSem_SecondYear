package edu.slsu.IT205;

/** Regular passenger: pays the full fare. */
public class RegularPassenger extends Passenger {

    public RegularPassenger(String name, int age) {
        super(name, age);
    }

    @Override
    public String getType() {
        return "Regular";
    }

    @Override
    public double getDiscountRate() {
        return 0.0;
    }

    @Override
    public String getRequiredId() {
        return "Regular ID";
    }
}
