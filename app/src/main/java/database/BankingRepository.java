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

    /**
     * Repository layer for the banking application.
     * This class abstracts the data source (Room Database DAOs) and ensures all
     * database operations are performed on a background thread (via AppDatabase.databaseWriteExecutor).
     * It provides synchronous-like blocking access to the data by using Future.get()
     * for read operations, and non-blocking asynchronous execution for writes.
     */
    public BankingRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        userDao = db.userDao();
        accountDao = db.accountDao();
        transactionDao = db.transactionDao();
        savingsGoalDao = db.savingsGoalDao();
    }

    // --- HELPER: GENERATE UNIQUE 11-DIGIT NUMBER ---
    // Must be called from a background thread
    public String generateNewAccountNumber() {
        String number = "";
        boolean unique = false;

        while (!unique) {
            // Generate random 11-digit number (10 billion to 99 billion)
            long randomNum = 10000000000L + (long)(Math.random() * 90000000000L);
            number = String.valueOf(randomNum);

            // Check if it exists
            if (accountDao.getAccountByNumber(number) == null) {
                unique = true;
            }
        }
        return number;
    }

    // --- SEED DATA (Updated for Account Number) ---
    /**
     * Seeds the database with initial user, account, transaction, and goal data.
     * Executes asynchronously on the database write executor.
     */
    public void seedData() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (userDao.getUserByUsername("testuser1") == null) {

                // 1. Create Users
                User user = new User("testuser1", "password", false,
                        "John", "Doe", "john@test.com", "555-0100", "123 User Lane");
                long userIdLong = userDao.insert(user);
                int userId = (int) userIdLong;

                User admin = new User("admin2", "admin2", true,
                        "System", "Admin", "admin@bank.com", "555-9999", "HQ Building 1");
                userDao.insert(admin);

                // 2. Create Account (With generated number)
                String accNum = generateNewAccountNumber();
                Account account = new Account(userId, accNum, "Checking", 1000.00);
                long accountIdLong = accountDao.insert(account);
                int accountId = (int) accountIdLong;

                // 3. Transactions & Goals
                Transaction t1 = new Transaction(accountId, "Deposit", 500.00, System.currentTimeMillis());
                Transaction t2 = new Transaction(accountId, "Starbucks", -12.50, System.currentTimeMillis());
                transactionDao.insert(t1);
                transactionDao.insert(t2);

                SavingsGoal goal = new SavingsGoal(userId, "New Car", 20000.00, 5000.00);
                savingsGoalDao.insert(goal);
            }
        });
    }

    // --- USER OPERATIONS ---
    public long registerUser(User user) {
        try {
            return AppDatabase.databaseWriteExecutor.submit(() -> userDao.insert(user)).get();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public User getUserByUsername(String username) {
        Future<User> future = AppDatabase.databaseWriteExecutor.submit(() -> userDao.getUserByUsername(username));
        try { return future.get(); } catch (Exception e) { return null; }
    }

    public void updateUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.update(user));
    }

    public List<User> getAllUsers() {
        Future<List<User>> future = AppDatabase.databaseWriteExecutor.submit(userDao::getAllUsers);
        try { return future.get(); } catch (Exception e) { return null; }
    }

    public void deleteUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.delete(user));
    }

    // --- ACCOUNT OPERATIONS ---
    public void insertAccount(Account account) {
        AppDatabase.databaseWriteExecutor.execute(() -> accountDao.insert(account));
    }
    public List<Account> getAccounts(int userId) {
        Future<List<Account>> future = AppDatabase.databaseWriteExecutor.submit(() -> accountDao.getAccountsForUser(userId));
        try { return future.get(); } catch (Exception e) { return null; }
    }

    // --- TRANSACTION OPERATIONS ---
    public List<Transaction> getTransactions(int accountId) {
        Future<List<Transaction>> future = AppDatabase.databaseWriteExecutor.submit(() -> transactionDao.getTransactionsForAccount(accountId));
        try { return future.get(); } catch (Exception e) { return null; }
    }
    public void performTransaction(Transaction t) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Account account = accountDao.getAccountById(t.accountId);
            if (account == null) return;

            if ("Deposit".equals(t.type)) {
                account.balance += t.amount;
            } else if ("Withdrawal".equals(t.type)) {
                if (account.balance >= t.amount) account.balance -= t.amount;
                else return;
            }
            accountDao.update(account);
            transactionDao.insert(t);
        });
    }

    // --- GOAL OPERATIONS ---
    public List<SavingsGoal> getGoals(int userId) {
        Future<List<SavingsGoal>> future = AppDatabase.databaseWriteExecutor.submit(() -> savingsGoalDao.getGoalsForUser(userId));
        try { return future.get(); } catch (Exception e) { return null; }
    }
    public void addSavingsGoal(SavingsGoal goal) {
        AppDatabase.databaseWriteExecutor.execute(() -> savingsGoalDao.insert(goal));
    }
    public void updateSavingsGoal(SavingsGoal goal) {
        AppDatabase.databaseWriteExecutor.execute(() -> savingsGoalDao.update(goal));
    }
    public void deleteSavingsGoal(SavingsGoal goal) {
        AppDatabase.databaseWriteExecutor.execute(() -> savingsGoalDao.delete(goal));
    }


    // --- TRANSFER LOGIC ---

    // Type 1: Internal Transfer (Checking <-> Savings)
    public boolean transferInternal(int fromAccountId, int toAccountId, double amount) {
        try {
            return AppDatabase.databaseWriteExecutor.submit(() -> {
                Account from = accountDao.getAccountById(fromAccountId);
                Account to = accountDao.getAccountById(toAccountId);

                if (from == null || to == null) return false;
                if (from.balance < amount) return false; // Insufficient funds

                // Perform Transfer
                from.balance -= amount;
                to.balance += amount;

                accountDao.update(from);
                accountDao.update(to);

                // Record Transactions
                transactionDao.insert(new Transaction(fromAccountId, "Transfer Out", amount, System.currentTimeMillis()));
                transactionDao.insert(new Transaction(toAccountId, "Transfer In", amount, System.currentTimeMillis()));

                return true;
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Type 2: Peer-to-Peer Transfer (User to User)
    public String transferToUser(int fromAccountId, String targetAccNum, String targetUsername, double amount) {
        try {
            return AppDatabase.databaseWriteExecutor.submit(() -> {
                // 1. Validate Source
                Account from = accountDao.getAccountById(fromAccountId);
                if (from == null) return "Source account error";
                if (from.balance < amount) return "Insufficient funds";

                // 2. Validate Target Account
                Account targetAccount = accountDao.getAccountByNumber(targetAccNum);
                if (targetAccount == null) return "Target account number not found";

                // 3. Prevent self-transfer via external method (Optional safety)
                if (targetAccount.accountId == fromAccountId) return "Cannot send to same account";

                // 4. Validate Target Username (Security Check)
                User targetUser = userDao.getUserById(targetAccount.userId);
                if (targetUser == null || !targetUser.username.equalsIgnoreCase(targetUsername)) {
                    return "Account number does not match username";
                }

                // 5. Perform Transfer
                from.balance -= amount;
                targetAccount.balance += amount;

                accountDao.update(from);
                accountDao.update(targetAccount);

                // 6. Record Transactions
                // Get sender name for the record
                User sender = userDao.getUserById(from.userId);
                String senderName = (sender != null) ? sender.username : "Unknown";

                transactionDao.insert(new Transaction(fromAccountId, "Sent to " + targetUser.username, amount, System.currentTimeMillis()));
                transactionDao.insert(new Transaction(targetAccount.accountId, "Received from " + senderName, amount, System.currentTimeMillis()));

                return "Success";
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            return "System error occurred";
        }
    }
}