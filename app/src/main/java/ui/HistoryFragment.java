package ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gitissues.R;
import com.example.gitissues.Session;

import java.util.ArrayList;
import java.util.List;

import adapters.TransactionAdapter;
import database.BankingRepository;
import models.Account;
import models.Transaction;

public class HistoryFragment extends Fragment {

    private TransactionAdapter adapter;
    private BankingRepository repository;

    public HistoryFragment() { super(R.layout.fragment_history); }

    @Override public void onViewCreated(@NonNull View v, Bundle b) {
        super.onViewCreated(v, b);

        RecyclerView rv = v.findViewById(R.id.rvHistory);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        // Init with empty list
        adapter = new TransactionAdapter(new ArrayList<>());
        rv.setAdapter(adapter);

        repository = new BankingRepository(getContext());

        loadData();
    }

    private void loadData() {
        int userId = Session.userId(getContext());

        new Thread(() -> {
            // 1. Get User's Accounts first
            List<Account> accounts = repository.getAccounts(userId);

            if (accounts != null && !accounts.isEmpty()) {
                // For simplicity, just show history for the first account found
                int firstAccountId = accounts.get(0).accountId;

                // 2. Get Transactions for that account
                List<Transaction> transactions = repository.getTransactions(firstAccountId);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (transactions != null) {
                            adapter.setData(transactions);
                        }
                    });
                }
            } else {
                // User has no accounts yet. You might want to auto-create one here
                // or handle this case in the future.
            }
        }).start();
    }
}