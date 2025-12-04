package database;

import android.content.Context;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import models.Account;
import models.SavingsGoal;
import models.Transaction;
import models.User;

public class BankingRepository {

    private final UserDao userDao;
    private final AccountDao accountDao;
    private final TransactionDao transactionDao;
    private final SavingsGoalDao savingsGoalDao;

    public BankingRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        userDao = db.userDao();
        accountDao = db.accountDao();
        transactionDao = db.transactionDao();
        savingsGoalDao = db.savingsGoalDao();
    }

    // --- USER OPERATIONS ---

    public void insertUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.insert(user);
        });
    }

    public User getUserByUsername(String username) {
        Future<User> future = AppDatabase.databaseWriteExecutor.submit(() -> userDao.getUserByUsername(username));
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    // --- ACCOUNT OPERATIONS ---

    public void insertAccount(Account account) {
        AppDatabase.databaseWriteExecutor.execute(() -> accountDao.insert(account));
    }

    public List<Account> getAccounts(int userId) {
        Future<List<Account>> future = AppDatabase.databaseWriteExecutor.submit(() -> accountDao.getAccountsForUser(userId));
        try {
            return future.get();
        } catch (Exception e) {
            return null;
        }
    }

    // --- TRANSACTION OPERATIONS ---

    public void addTransaction(Transaction transaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> transactionDao.insert(transaction));
    }

    public List<Transaction> getTransactions(int accountId) {
        Future<List<Transaction>> future = AppDatabase.databaseWriteExecutor.submit(() -> transactionDao.getTransactionsForAccount(accountId));
        try {
            return future.get();
        } catch (Exception e) {
            return null;
        }
    }

    // --- GOAL OPERATIONS ---

    public List<SavingsGoal> getGoals(int userId) {
        Future<List<SavingsGoal>> future = AppDatabase.databaseWriteExecutor.submit(() -> savingsGoalDao.getGoalsForUser(userId));
        try {
            return future.get();
        } catch (Exception e) {
            return null;
        }
    }
}