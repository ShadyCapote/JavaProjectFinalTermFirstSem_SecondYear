package edu.slsu.IT205;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests for the Passenger classes: fares, discounts, types and the factory.
 */
class PassengerTest {

    @Test
    void regularPassengerPaysFullFare() {
        Passenger p = new RegularPassenger("Juan", 25);
        assertEquals(100.0, p.computeFare(100.0), 0.001);
        assertEquals(0.0, p.getDiscountRate(), 0.001);
        assertEquals("Regular", p.getType());
    }

    @Test
    void studentGetsTwentyPercentDiscount() {
        Passenger p = new StudentPassenger("Ana", 18);
        assertEquals(80.0, p.computeFare(100.0), 0.001);
        assertEquals(0.20, p.getDiscountRate(), 0.001);
        assertEquals("Student", p.getType());
    }

    @Test
    void seniorGetsTwentyPercentDiscount() {
        Passenger p = new SeniorPassenger("Lola", 70);
        assertEquals(160.0, p.computeFare(200.0), 0.001);
        assertEquals("Senior", p.getType());
    }

    @Test
    void pwdGetsTwentyPercentDiscount() {
        Passenger p = new PWDPassenger("Ben", 30);
        assertEquals(120.0, p.computeFare(150.0), 0.001);
        assertEquals("PWD", p.getType());
    }

    @Test
    void sameBaseFareGivesDifferentPricesForDifferentTypes() {
        double base = 220.0;
        double regular = new RegularPassenger("A", 30).computeFare(base);
        double student = new StudentPassenger("B", 20).computeFare(base);
        assertTrue(student < regular);
        assertEquals(176.0, student, 0.001); // 220 - 20%
    }

    @Test
    void zeroFareStaysZero() {
        assertEquals(0.0, new StudentPassenger("Ana", 18).computeFare(0.0), 0.001);
    }

    @Test
    void gettersReturnNameAndAge() {
        Passenger p = new RegularPassenger("Juan", 25);
        assertEquals("Juan", p.getName());
        assertEquals(25, p.getAge());
    }

    @Test
    void factoryCreatesTheCorrectSubclass() {
        assertTrue(Passenger.create("Student", "A", 20) instanceof StudentPassenger);
        assertTrue(Passenger.create("SENIOR", "B", 65) instanceof SeniorPassenger);
        assertTrue(Passenger.create("pwd", "C", 40) instanceof PWDPassenger);
        assertTrue(Passenger.create("Regular", "D", 30) instanceof RegularPassenger);
    }

    @Test
    void factoryFallsBackToRegularForAnUnknownType() {
        assertTrue(Passenger.create("something else", "E", 30) instanceof RegularPassenger);
    }

    @Test
    void factoryKeepsNameAndAge() {
        Passenger p = Passenger.create("Student", "Maria", 19);
        assertEquals("Maria", p.getName());
        assertEquals(19, p.getAge());
    }
}