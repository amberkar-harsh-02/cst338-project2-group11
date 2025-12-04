package database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import models.SavingsGoal;

@Dao
public interface SavingsGoalDao {
    @Insert
    void insert(SavingsGoal goal);

    @Update
    void update(SavingsGoal goal);

    @Query("SELECT * FROM savings_goals WHERE userId = :userId")
    List<SavingsGoal> getGoalsForUser(int userId);
}