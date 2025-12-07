package database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import models.Transaction;

@Dao
public interface TransactionDao {
    @Insert
    void insert(Transaction transaction);

    // Get all transactions for a specific account, newest first
    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY timestamp DESC")
    List<Transaction> getTransactionsForAccount(int accountId);
}