package database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import models.User;

@Dao
public interface UserDao {
    // Register a new user
    @Insert
    long insert(User user);

    // Find a user by username (for login)
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    // Get user details by ID
    @Query("SELECT * FROM users WHERE userId = :id LIMIT 1")
    User getUserById(int id);
}