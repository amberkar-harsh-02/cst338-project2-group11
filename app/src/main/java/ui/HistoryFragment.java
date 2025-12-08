package ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
    private Spinner spinnerAccounts;

    // Track accounts and current selection
    private List<Account> myAccounts = new ArrayList<>();
    private int currentAccountId = -1;

    public HistoryFragment() { super(R.layout.fragment_history); }

    @Override public void onViewCreated(@NonNull View v, Bundle b) {
        super.onViewCreated(v, b);

        repository = new BankingRepository(getContext());
        spinnerAccounts = v.findViewById(R.id.spinnerAccounts);

        RecyclerView rv = v.findViewById(R.id.rvHistory);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionAdapter(new ArrayList<>());
        rv.setAdapter(adapter);

        FloatingActionButton fab = v.findViewById(R.id.fabNewTransaction);
        fab.setOnClickListener(view -> showTransactionDialog());

        // Initial Load
        loadAccounts();
    }

    private void loadAccounts() {
        int userId = Session.userId(getContext());

        new Thread(() -> {
            // 1. Fetch all accounts for this user
            myAccounts = repository.getAccounts(userId);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (myAccounts != null && !myAccounts.isEmpty()) {
                        setupSpinner();
                    } else {
                        Toast.makeText(getContext(), "No accounts found", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    private void setupSpinner() {
        // Create labels for the dropdown (e.g., "Checking - $1000.0")
        List<String> labels = new ArrayList<>();
        for (Account acc : myAccounts) {
            labels.add(acc.accountType + " ($" + acc.balance + ")");
        }

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, labels);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccounts.setAdapter(spinAdapter);

        // Listen for changes
        spinnerAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Update current ID and reload transactions
                currentAccountId = myAccounts.get(position).accountId;
                loadTransactions(currentAccountId);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadTransactions(int accountId) {
        new Thread(() -> {
            List<Transaction> transactions = repository.getTransactions(accountId);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (transactions != null) {
                        adapter.setData(transactions);
                    }
                });
            }
        }).start();
    }

    private void showTransactionDialog() {
        if (currentAccountId == -1) {
            Toast.makeText(getContext(), "Please select an account first", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_transaction, null);
        EditText etAmount = dialogView.findViewById(R.id.etAmount);
        RadioGroup rgType = dialogView.findViewById(R.id.rgType);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnSubmit.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString();
            if (amountStr.isEmpty()) return;

            double amount = Double.parseDouble(amountStr);
            String type = (rgType.getCheckedRadioButtonId() == R.id.rbDeposit) ? "Deposit" : "Withdrawal";

            Transaction t = new Transaction(currentAccountId, type, amount, System.currentTimeMillis());

            new Thread(() -> {
                repository.performTransaction(t);

                // Refresh data
                try { Thread.sleep(100); } catch (Exception e) {}
                loadAccounts(); // Reload accounts to update balance in spinner labels
                loadTransactions(currentAccountId); // Reload list

                getActivity().runOnUiThread(dialog::dismiss);
            }).start();
        });

        dialog.show();
    }
}