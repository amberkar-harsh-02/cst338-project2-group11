package database;

import android.content.Context;

import java.util.List;
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

    // --- SEED DATA (Creates testuser1 / password) ---
    public void seedData() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Check if user exists
            if (userDao.getUserByUsername("testuser1") == null) {

                // 1. Create User (Username: testuser1, Password: password)
                User user = new User("testuser1", "password", false);
                long userIdLong = userDao.insert(user);
                int userId = (int) userIdLong;

                // 2. Create Admin
                User admin = new User("admin2", "admin2", true);
                userDao.insert(admin);

                // 3. Create Account
                Account account = new Account(userId, "Checking", 1000.00);
                long accountIdLong = accountDao.insert(account);
                int accountId = (int) accountIdLong;

                // 4. Create Transactions
                Transaction t1 = new Transaction(accountId, "Deposit", 500.00, System.currentTimeMillis());
                Transaction t2 = new Transaction(accountId, "Starbucks", -12.50, System.currentTimeMillis());
                transactionDao.insert(t1);
                transactionDao.insert(t2);

                // 5. Create Goal
                SavingsGoal goal = new SavingsGoal(userId, "New Car", 20000.00, 5000.00);
                savingsGoalDao.insert(goal);
            }
        });
    }

    // --- USER OPERATIONS ---
    public void insertUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.insert(user));
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
    public List<Account> getAccounts(int userId) {
        Future<List<Account>> future = AppDatabase.databaseWriteExecutor.submit(() -> accountDao.getAccountsForUser(userId));
        try {
            return future.get();
        } catch (Exception e) { return null; }
    }

    // --- TRANSACTION OPERATIONS ---
    public List<Transaction> getTransactions(int accountId) {
        Future<List<Transaction>> future = AppDatabase.databaseWriteExecutor.submit(() -> transactionDao.getTransactionsForAccount(accountId));
        try {
            return future.get();
        } catch (Exception e) { return null; }
    }

    // --- GOAL OPERATIONS ---
    public List<SavingsGoal> getGoals(int userId) {
        Future<List<SavingsGoal>> future = AppDatabase.databaseWriteExecutor.submit(() -> savingsGoalDao.getGoalsForUser(userId));
        try {
            return future.get();
        } catch (Exception e) { return null; }
    }
}