package models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "userId",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE))
public class Account {

    @PrimaryKey(autoGenerate = true)
    public int accountId;

    public int userId;
    public String accountNumber; // NEW FIELD (11 digits)
    public String accountType;   // "Checking" or "Savings"
    public double balance;

    // Updated Constructor
    public Account(int userId, String accountNumber, String accountType, double balance) {
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
    }
}