package com.example.gitissues;

import org.junit.Test;
import static org.junit.Assert.*;
import models.Account;
import models.Transaction;
import models.User;

public class BankingUnitTests {

    // ==========================================
    // HARSH'S TESTS (User Model Logic)
    // ==========================================

    @Test
    public void testUserCreation() {
        // Goal: Verify a standard user is created with correct fields
        User u = new User("harsh1", "pass123", false, "Harsh", "A", "test@test.com", "555-0000", "123 St");

        assertEquals("Username should match", "harsh1", u.username);
        assertEquals("First name should match", "Harsh", u.firstName);
        assertFalse("User should not be admin", u.isAdmin);
    }

    @Test
    public void testAdminCreation() {
        // Goal: Verify an admin user is correctly flagged
        User admin = new User("adminUser", "adminPass", true, "Admin", "User", "admin@bank.com", "555-9999", "HQ");

        assertTrue("User should be an admin", admin.isAdmin);
        assertEquals("Email should match", "admin@bank.com", admin.email);
    }


}