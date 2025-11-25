package models;

public class Transaction {
    public final String type;   // Deposit, Withdrawal, Transfer...
    public final String amount; // "$120.00"
    public final String date;   // "2025-11-12"
    public Transaction(String type, String amount, String date) {
        this.type = type; this.amount = amount; this.date = date;
    }
}