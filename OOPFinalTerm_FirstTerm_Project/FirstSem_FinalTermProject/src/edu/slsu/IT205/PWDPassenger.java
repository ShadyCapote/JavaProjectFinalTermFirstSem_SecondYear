package edu.slsu.IT205;

public class PWDPassenger extends DiscountedPassenger {

    public PWDPassenger(String name, int age) {
        super(name, age);
    }

    @Override
    public String getType() {
        return "PWD";
    }

    @Override
    public String getRequiredId() {
        return "PWD ID";
    }
}