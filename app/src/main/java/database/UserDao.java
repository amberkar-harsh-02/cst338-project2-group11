package database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import models.User;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    // New: Get a list of everyone
    @Query("SELECT * FROM users ORDER BY userId DESC")
    List<User> getAllUsers();

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    @Query("SELECT * FROM users WHERE userId = :id LIMIT 1")
    User getUserById(int id);

    // New: Remove a user
    @Delete
    void delete(User user);
}