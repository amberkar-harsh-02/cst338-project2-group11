package database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import models.User;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    @Query("SELECT * FROM users WHERE userId = :id LIMIT 1")
    User getUserById(int id);
}