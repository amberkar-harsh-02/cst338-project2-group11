package com.example.gitissues;

public class Transaction {

    private String title;
    private String date;
    //sneha
    private double amount; // positive = credit, negative = debit

    public Transaction(String title, String date, double amount) {
        this.title = title;
        this.date = date;
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }
}
