package edu.slsu.IT205;

public class StudentPassenger extends DiscountedPassenger {

    public StudentPassenger(String name, int age) {
        super(name, age);
    }

    @Override
    public String getType() {
        return "Student";
    }
}