package edu.slsu.IT205;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class BusManagerTest {

    @Test
    void findsAnExistingBusById() {
        BusManager manager = new BusManager();
        Bus bus = manager.findById(1);
        assertNotNull(bus);
        assertEquals(1, bus.getId());
    }

    @Test
    void returnsNullForAnUnknownBusId() {
        BusManager manager = new BusManager();
        assertNull(manager.findById(999));
        assertNull(manager.findById(0));
    }

    @Test
    void allFourBusesExist() {
        BusManager manager = new BusManager();
        for (int id = 1; id <= 4; id++) {
            assertNotNull(manager.findById(id), "Bus " + id + " should exist");
        }
    }
}