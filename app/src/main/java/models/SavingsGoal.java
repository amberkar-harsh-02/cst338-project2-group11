package models;

public class SavingsGoal {
    public final String name;
    public final int progressPct; // 0-100
    public final String amountText; // e.g., "$250 / $1000"

    public SavingsGoal(String name, int progressPct, String amountText) {
        this.name = name; this.progressPct = progressPct; this.amountText = amountText;
    }
}