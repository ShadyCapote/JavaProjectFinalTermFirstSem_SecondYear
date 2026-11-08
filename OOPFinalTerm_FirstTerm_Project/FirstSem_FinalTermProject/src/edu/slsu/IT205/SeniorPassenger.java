package edu.slsu.IT205;

public class SeniorPassenger extends DiscountedPassenger {

    public SeniorPassenger(String name, int age) {
        super(name, age);
    }

    @Override
    public String getType() {
        return "Senior";
    }

    @Override
    public String getRequiredId() {
        return "Senior Citizen ID";
    }
}
