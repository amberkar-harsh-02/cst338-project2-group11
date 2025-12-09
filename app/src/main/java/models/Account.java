package models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Represents a single bank account record in the Room database.
 * This class is a standard Java POJO (Plain Old Java Object) annotated
 * with Room Persistence Library metadata.
 */
@Entity(tableName = "accounts",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "userId",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE))
public class Account {

    /**
     * Primary key for the Account table.
     * The database automatically generates a unique ID for each new account.
     */
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