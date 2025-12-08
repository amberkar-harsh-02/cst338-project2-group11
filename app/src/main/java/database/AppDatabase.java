package database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import models.Account;
import models.SavingsGoal;
import models.Transaction;
import models.User;

// VERSION CHANGED TO 2
@Database(entities = {User.class, Account.class, Transaction.class, SavingsGoal.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract AccountDao accountDao();
    public abstract TransactionDao transactionDao();
    public abstract SavingsGoalDao savingsGoalDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "banking_database")
                            .fallbackToDestructiveMigration() // Allows DB to wipe and rebuild on version change
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}