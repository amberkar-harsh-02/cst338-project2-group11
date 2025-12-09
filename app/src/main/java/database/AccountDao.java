package database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import models.Account;

@Dao
public interface AccountDao {
    @Insert
    long insert(Account account);

    @Update
    void update(Account account);

    @Query("SELECT * FROM accounts WHERE userId = :userId")
    List<Account> getAccountsForUser(int userId);

    @Query("SELECT * FROM accounts WHERE accountId = :accountId LIMIT 1")
    Account getAccountById(int accountId);

    // NEW: Used to check if a generated number already exists
    @Query("SELECT * FROM accounts WHERE accountNumber = :number LIMIT 1")
    Account getAccountByNumber(String number);
}