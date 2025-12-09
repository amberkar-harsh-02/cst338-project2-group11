package ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView tvWelcome, tvCheckBal, tvCheckNum, tvSaveBal, tvSaveNum, tvAmountSpent;
    private ProgressBar pbBudget;
    private RecyclerView rvRecent;
    private TransactionAdapter recentAdapter;

    // Cache the user's accounts so we can use them in the dialog
    private List<Account> myAccounts = new ArrayList<>();

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

        // Setup List
        rvRecent.setLayoutManager(new LinearLayoutManager(getContext()));
        recentAdapter = new TransactionAdapter(new ArrayList<>());
        rvRecent.setAdapter(recentAdapter);

        // --- NEW: Transfer Button Logic ---
        View btnTransfer = view.findViewById(R.id.btnQuickTransfer);
        if (btnTransfer != null) {
            btnTransfer.setOnClickListener(v -> showTransferDialog());
        }

        loadDashboardData();
    }

    // =========================================================
    // TRANSFER DIALOG LOGIC
    // =========================================================
    private void showTransferDialog() {
        if (myAccounts == null || myAccounts.isEmpty()) {
            Toast.makeText(getContext(), "Loading accounts...", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_transfer, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Bind Dialog Views
        RadioGroup rgType = dialogView.findViewById(R.id.rgTransferType);
        LinearLayout layoutInternal = dialogView.findViewById(R.id.layoutInternal);
        LinearLayout layoutExternal = dialogView.findViewById(R.id.layoutExternal);

        Spinner spFrom = dialogView.findViewById(R.id.spFromAccount);
        Spinner spTo = dialogView.findViewById(R.id.spToAccount); // For Internal

        EditText etTargetUser = dialogView.findViewById(R.id.etTargetUser);
        EditText etTargetAcc = dialogView.findViewById(R.id.etTargetAccount);
        EditText etAmount = dialogView.findViewById(R.id.etTransferAmount);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirmTransfer);

        // 1. POPULATE SPINNERS
        List<String> accountLabels = new ArrayList<>();
        for (Account acc : myAccounts) {
            accountLabels.add(acc.accountType + " (Bal: $" + acc.balance + ")");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, accountLabels);
        spFrom.setAdapter(adapter);
        spTo.setAdapter(adapter);

        // Pre-select different accounts for internal transfer (e.g. Checking -> Savings)
        if (myAccounts.size() > 1) spTo.setSelection(1);

        // 2. HANDLE RADIO TOGGLE
        rgType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbSelf) {
                layoutInternal.setVisibility(View.VISIBLE);
                layoutExternal.setVisibility(View.GONE);
            } else {
                layoutInternal.setVisibility(View.GONE);
                layoutExternal.setVisibility(View.VISIBLE);
            }
        });

        // 3. HANDLE TRANSFER CLICK
        btnConfirm.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(getContext(), "Enter amount", Toast.LENGTH_SHORT).show();
                return;
            }
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                Toast.makeText(getContext(), "Amount must be positive", Toast.LENGTH_SHORT).show();
                return;
            }

            int fromIndex = spFrom.getSelectedItemPosition();
            Account fromAccount = myAccounts.get(fromIndex);

            // Determine Transfer Type
            if (rgType.getCheckedRadioButtonId() == R.id.rbSelf) {
                // INTERNAL
                int toIndex = spTo.getSelectedItemPosition();
                Account toAccount = myAccounts.get(toIndex);

                if (fromAccount.accountId == toAccount.accountId) {
                    Toast.makeText(getContext(), "Select different accounts", Toast.LENGTH_SHORT).show();
                    return;
                }

                new Thread(() -> {
                    boolean success = repository.transferInternal(fromAccount.accountId, toAccount.accountId, amount);
                    handleTransferResult(success, "Transfer Successful", "Transfer Failed", dialog);
                }).start();

            } else {
                // EXTERNAL (Send to User)
                String targetUser = etTargetUser.getText().toString().trim();
                String targetAccNum = etTargetAcc.getText().toString().trim();

                if (TextUtils.isEmpty(targetUser) || TextUtils.isEmpty(targetAccNum)) {
                    Toast.makeText(getContext(), "Fill all recipient details", Toast.LENGTH_SHORT).show();
                    return;
                }

                new Thread(() -> {
                    String result = repository.transferToUser(fromAccount.accountId, targetAccNum, targetUser, amount);
                    boolean success = "Success".equals(result);
                    handleTransferResult(success, "Money Sent!", result, dialog);
                }).start();
            }
        });

        dialog.show();
    }

    private void handleTransferResult(boolean success, String successMsg, String errorMsg, AlertDialog dialog) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(getContext(), successMsg, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadDashboardData(); // Refresh UI to show new balance
                } else {
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    // =========================================================
    // EXISTING DASHBOARD LOAD LOGIC
    // =========================================================
    private void loadDashboardData() {
        int userId = Session.userId(getContext());
        String username = Session.username(getContext());
        tvWelcome.setText("Welcome back, " + username + "!");

        new Thread(() -> {
            // 1. Get Accounts (and store in member variable for the dialog)
            myAccounts = repository.getAccounts(userId);

            // 2. Calculate Spending & History
            List<Transaction> allTransactions = new ArrayList<>();
            double totalSpent = 0;

            if (myAccounts != null) {
                for (Account acc : myAccounts) {
                    List<Transaction> accountTrans = repository.getTransactions(acc.accountId);
                    allTransactions.addAll(accountTrans);

                    for (Transaction t : accountTrans) {
                        if ("Withdrawal".equalsIgnoreCase(t.type) || "Transfer".equalsIgnoreCase(t.type) || t.type.startsWith("Sent to")) {
                            totalSpent += t.amount;
                        }
                    }
                }
            }

            Collections.sort(allTransactions, (t1, t2) -> Long.compare(t2.timestamp, t1.timestamp));
            List<Transaction> recentThree = allTransactions.size() > 3 ? allTransactions.subList(0, 3) : allTransactions;
            double finalSpent = totalSpent;

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (myAccounts != null) updateAccountUI(myAccounts);
                    updateBudgetUI(finalSpent);
                    recentAdapter.setData(recentThree);
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