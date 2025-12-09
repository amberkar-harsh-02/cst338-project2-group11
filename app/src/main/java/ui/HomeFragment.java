package ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gitissues.R;
import com.example.gitissues.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import adapters.TransactionAdapter;
import database.BankingRepository;
import models.Account;
import models.Transaction;

public class HomeFragment extends Fragment {

    private BankingRepository repository;
    private TextView tvWelcome;
    private TextView tvCheckBal, tvCheckNum;
    private TextView tvSaveBal, tvSaveNum;

    // Budget & List Views
    private ProgressBar pbBudget;
    private TextView tvAmountSpent;
    private RecyclerView rvRecent;
    private TransactionAdapter recentAdapter;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new BankingRepository(getContext());

        // Bind Views
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvCheckBal = view.findViewById(R.id.tvCheckingBalance);
        tvCheckNum = view.findViewById(R.id.tvCheckingNumber);
        tvSaveBal = view.findViewById(R.id.tvSavingsBalance);
        tvSaveNum = view.findViewById(R.id.tvSavingsNumber);

        pbBudget = view.findViewById(R.id.pbBudgetProgress);
        tvAmountSpent = view.findViewById(R.id.tvAmountSpent);
        rvRecent = view.findViewById(R.id.rvRecentTransactions);

        // Setup RecyclerView with the existing TransactionAdapter
        rvRecent.setLayoutManager(new LinearLayoutManager(getContext()));
        recentAdapter = new TransactionAdapter(new ArrayList<>());
        rvRecent.setAdapter(recentAdapter);

        loadDashboardData();
    }

    private void loadDashboardData() {
        int userId = Session.userId(getContext());
        String username = Session.username(getContext());

        tvWelcome.setText("Welcome back, " + username + "!");

        new Thread(() -> {
            // 1. Get Accounts
            List<Account> accounts = repository.getAccounts(userId);
            List<Transaction> allTransactions = new ArrayList<>();

            // 2. Calculate Spending & Collect Transactions
            double totalSpent = 0;
            if (accounts != null) {
                for (Account acc : accounts) {
                    List<Transaction> accountTrans = repository.getTransactions(acc.accountId);
                    allTransactions.addAll(accountTrans); // Collect all for the list

                    for (Transaction t : accountTrans) {
                        // Sum up withdrawals for budget tracking
                        if ("Withdrawal".equalsIgnoreCase(t.type) || "Transfer".equalsIgnoreCase(t.type)) {
                            totalSpent += t.amount;
                        }
                    }
                }
            }

            // 3. Sort & Trim Transactions (Newest First, Top 3)
            Collections.sort(allTransactions, (t1, t2) -> Long.compare(t2.timestamp, t1.timestamp));
            List<Transaction> recentThree = allTransactions.size() > 3 ? allTransactions.subList(0, 3) : allTransactions;

            double finalSpent = totalSpent;

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (accounts != null) {
                        updateAccountUI(accounts);
                    }
                    updateBudgetUI(finalSpent);

                    // Update the list
                    recentAdapter.setData(recentThree);

                    // Show a message if list is empty (Optional polish)
                    if (recentThree.isEmpty()) {
                        // You could toggle a "No recent activity" text view here if you wanted
                    }
                });
            }
        }).start();
    }

    private void updateAccountUI(List<Account> accounts) {
        tvCheckBal.setText("$0.00");
        tvSaveBal.setText("$0.00");

        for (Account acc : accounts) {
            String balText = String.format(Locale.US, "$%.2f", acc.balance);
            String numText = "...." + (acc.accountNumber.length() > 4 ? acc.accountNumber.substring(acc.accountNumber.length() - 4) : "0000");

            if ("Checking".equalsIgnoreCase(acc.accountType)) {
                tvCheckBal.setText(balText);
                tvCheckNum.setText(numText);
            } else if ("Savings".equalsIgnoreCase(acc.accountType)) {
                tvSaveBal.setText(balText);
                tvSaveNum.setText(numText);
            }
        }
    }

    private void updateBudgetUI(double spent) {
        double limit = 500.00;
        int progress = (int) ((spent / limit) * 100);

        pbBudget.setProgress(Math.min(progress, 100));
        tvAmountSpent.setText(String.format(Locale.US, "$%.2f Spent", spent));
    }
}