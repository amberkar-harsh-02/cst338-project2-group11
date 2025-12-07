package models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "savings_goals",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "userId",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE))
public class SavingsGoal {

    @PrimaryKey(autoGenerate = true)
    public int goalId;

    public int userId;
    public String name;
    public double targetAmount;
    public double currentAmount;

    public SavingsGoal(int userId, String name, double targetAmount, double currentAmount) {
        this.userId = userId;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
    }

    // Helper method to calculate progress (0-100) for the UI
    public int getProgressPercent() {
        if (targetAmount == 0) return 0;
        return (int) ((currentAmount / targetAmount) * 100);
    }
}