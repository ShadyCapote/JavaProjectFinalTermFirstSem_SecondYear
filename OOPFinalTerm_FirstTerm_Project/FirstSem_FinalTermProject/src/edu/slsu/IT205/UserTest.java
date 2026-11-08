package edu.slsu.IT205;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the User classes: passwords, roles and permissions.
 */
class UserTest {
    private User admin;
    private User cashier;

    @BeforeEach
    void setUp() {
        admin = new AdminUser("admin", "admin123");
        cashier = new CashierUser("cashier", "cashier123");
    }

    // ---------- passwords ----------

    @Test
    void correctPasswordIsAccepted() {
        assertTrue(admin.checkPassword("admin123"));
        assertTrue(cashier.checkPassword("cashier123"));
    }

    @Test
    void wrongPasswordIsRejected() {
        assertFalse(admin.checkPassword("wrong"));
        assertFalse(admin.checkPassword(""));
    }

    @Test
    void passwordIsCaseSensitive() {
        assertFalse(admin.checkPassword("ADMIN123"));
        assertFalse(admin.checkPassword("Admin123"));
    }

    @Test
    void passwordWithAnExtraSpaceIsRejected() {
        assertFalse(admin.checkPassword("admin123 "));
        assertFalse(admin.checkPassword(" admin123"));
    }

    @Test
    void eachUserOnlyAcceptsItsOwnPassword() {
        assertFalse(admin.checkPassword("cashier123"));
        assertFalse(cashier.checkPassword("admin123"));
    }

    @Test
    void twoUsersWithTheSamePasswordBothWork() {
        User first = new CashierUser("maria", "samepass");
        User second = new CashierUser("pedro", "samepass");
        assertTrue(first.checkPassword("samepass"));
        assertTrue(second.checkPassword("samepass"));
    }

    // ---------- roles and permissions ----------

    @Test
    void adminHasTheAdminRoleAndCanCancelTickets() {
        assertEquals("Admin", admin.getRole());
        assertTrue(admin.canCancelTickets());
    }

    @Test
    void cashierHasTheCashierRoleAndCannotCancelTickets() {
        assertEquals("Cashier", cashier.getRole());
        assertFalse(cashier.canCancelTickets());
    }

    @Test
    void theSameCallGivesDifferentAnswersForDifferentUsers() {
        // polymorphism: the code only knows "User", but each subclass answers
        // differently
        User[] users = { admin, cashier };
        String[] roles = { "Admin", "Cashier" };
        boolean[] canCancel = { true, false };
        for (int i = 0; i < users.length; i++) {
            assertEquals(roles[i], users[i].getRole());
            assertEquals(canCancel[i], users[i].canCancelTickets());
        }
    }

    @Test
    void usernameIsStored() {
        assertEquals("admin", admin.getUsername());
        assertEquals("cashier", cashier.getUsername());
    }

    // ---------- invalid accounts ----------

    @Test
    void emptyUsernameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AdminUser("", "pass"));
        assertThrows(IllegalArgumentException.class, () -> new AdminUser("   ", "pass"));
        assertThrows(IllegalArgumentException.class, () -> new AdminUser(null, "pass"));
    }

    @Test
    void emptyPasswordIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new CashierUser("maria", ""));
        assertThrows(IllegalArgumentException.class, () -> new CashierUser("maria", null));
    }

    // ---------- CashierUser.create ----------

    @Test
    void createReturnsACashierForValidValues() {
        CashierUser created = CashierUser.create("maria", "pass123");
        assertNotNull(created);
        assertEquals("maria", created.getUsername());
        assertTrue(created.checkPassword("pass123"));
    }

    @Test
    void createReturnsNullInsteadOfCrashingForInvalidValues() {
        assertNull(CashierUser.create("", "pass123"));
        assertNull(CashierUser.create("maria", ""));
    }
}