package models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions",
        foreignKeys = @ForeignKey(entity = Account.class,
                parentColumns = "accountId",
                childColumns = "accountId",
                onDelete = ForeignKey.CASCADE))
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    public int transactionId;

    public int accountId;
    public String type;    // Deposit, Withdrawal, Transfer
    public double amount;  // stored as number
    public long timestamp; // stored as milliseconds

    public Transaction(int accountId, String type, double amount, long timestamp) {
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }
}