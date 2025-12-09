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

    // ==========================================
    // SNEHA'S TESTS (Transaction Model Logic)
    // ==========================================

    @Test
    public void testTransactionCreation() {
        // Goal: Verify Transaction object creation
        long now = System.currentTimeMillis();
        Transaction t = new Transaction(10, "Deposit", 200.00, now);

        assertEquals("Amount should be 200.00", 200.00, t.amount, 0.001);
        assertEquals("Type should be Deposit", "Deposit", t.type);
        assertEquals("Timestamp should match", now, t.timestamp);
    }

    @Test
    public void testWithdrawalTransaction() {
        // Goal: Verify negative/withdrawal logic storage
        // Note: In our app we store positive numbers for amount and use "type" to determine sign,
        // but this test ensures the model holds exactly what we give it.
        long now = System.currentTimeMillis();
        Transaction t = new Transaction(10, "Withdrawal", 50.00, now);

        assertEquals("Type should be Withdrawal", "Withdrawal", t.type);
        assertEquals("Account ID should match", 10, t.accountId);
    }

}