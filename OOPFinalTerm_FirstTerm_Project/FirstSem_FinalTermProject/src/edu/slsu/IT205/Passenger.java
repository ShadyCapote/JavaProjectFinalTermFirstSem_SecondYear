package edu.slsu.IT205;

public abstract class Passenger {
    private final String name;
    private final int age;

    public Passenger(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public abstract String getType();

    public abstract String getRequiredId();

    public double getDiscountRate() {
        return 0.0;
    }

    public double computeFare(double baseFare) {
        return baseFare - baseFare * getDiscountRate();
    }

    public static Passenger create(String type, String name, int age) {
        switch (type) {
            case "Student":
                return new StudentPassenger(name, age);
            case "Senior":
                return new SeniorPassenger(name, age);
            case "PWD":
                return new PWDPassenger(name, age);
            default:
                return new RegularPassenger(name, age);
        }
    }
}
