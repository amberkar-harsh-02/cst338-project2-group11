package ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gitissues.R;
import com.example.gitissues.Session;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import adapters.TransactionAdapter;
import database.BankingRepository;
import models.Account;
import models.Transaction;

public class HistoryFragment extends Fragment {

    private TransactionAdapter adapter;
    private BankingRepository repository;

    // We need to track which account we are viewing to add transactions to it
    private int currentAccountId = -1;

    public HistoryFragment() { super(R.layout.fragment_history); }

    @Override public void onViewCreated(@NonNull View v, Bundle b) {
        super.onViewCreated(v, b);

        RecyclerView rv = v.findViewById(R.id.rvHistory);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TransactionAdapter(new ArrayList<>());
        rv.setAdapter(adapter);

        repository = new BankingRepository(getContext());

        // Setup the Floating Action Button
        FloatingActionButton fab = v.findViewById(R.id.fabNewTransaction);
        fab.setOnClickListener(view -> showTransactionDialog());

        loadData();
    }

    private void loadData() {
        int userId = Session.userId(getContext());

        new Thread(() -> {
            // 1. Get User's Accounts
            List<Account> accounts = repository.getAccounts(userId);

            if (accounts != null && !accounts.isEmpty()) {
                // Store the ID so we can use it for new transactions
                currentAccountId = accounts.get(0).accountId;

                // 2. Get Transactions
                List<Transaction> transactions = repository.getTransactions(currentAccountId);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (transactions != null) {
                            adapter.setData(transactions);
                        }
                    });
                }
            }
        }).start();
    }

    private void showTransactionDialog() {
        if (currentAccountId == -1) {
            Toast.makeText(getContext(), "Loading account...", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Inflate the Dialog Layout
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_transaction, null);
        EditText etAmount = dialogView.findViewById(R.id.etAmount);
        RadioGroup rgType = dialogView.findViewById(R.id.rgType);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);

        // 2. Build the Alert Dialog
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnSubmit.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString();
            if (amountStr.isEmpty()) {
                Toast.makeText(getContext(), "Enter an amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            String type = (rgType.getCheckedRadioButtonId() == R.id.rbDeposit) ? "Deposit" : "Withdrawal";

            // 3. Create Transaction Object
            Transaction t = new Transaction(currentAccountId, type, amount, System.currentTimeMillis());

            // 4. Save to DB in Background
            new Thread(() -> {
                repository.performTransaction(t);

                // Reload data to show the new item
                try { Thread.sleep(100); } catch (Exception e) {} // Slight delay for DB write
                loadData();

                getActivity().runOnUiThread(dialog::dismiss);
            }).start();
        });

        dialog.show();
    }
}