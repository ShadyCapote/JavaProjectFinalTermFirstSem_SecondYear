package edu.slsu.IT205;

/**
 * A passenger who is a student.
 * Gets the standard 20% discount from {@link DiscountedPassenger}.
 */
public class StudentPassenger extends DiscountedPassenger {

    /**
     * Creates a Student passenger.
     *
     * @param name the passenger's full name
     * @param age  the passenger's age in years
     */
    public StudentPassenger(String name, int age) {
        super(name, age);
    }

    /**
     * Returns the type name.
     *
     * @return {@code "Student"}
     */
    @Override
    public String getType() {
        return "Student";
    }

    /**
     * Returns the ID this passenger must show.
     *
     * @return {@code "Student ID"}
     */
    @Override
    public String getRequiredId() {
        return "Student ID";
    }
}
