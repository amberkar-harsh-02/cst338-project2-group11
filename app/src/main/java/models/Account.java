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
    public String accountType; // e.g., "Checking", "Savings"
    public double balance;

    public Account(int userId, String accountType, double balance) {
        this.userId = userId;
        this.accountType = accountType;
        this.balance = balance;
    }
}