package ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gitissues.R;

import java.util.Arrays;
import java.util.List;

import adapters.TransactionAdapter;
import models.Transaction;

public class HistoryFragment extends Fragment {
    public HistoryFragment() { super(R.layout.fragment_history); }

    @Override public void onViewCreated(@NonNull View v, Bundle b) {
        RecyclerView rv = v.findViewById(R.id.rvHistory);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Transaction> demo = Arrays.asList(
                new Transaction("Deposit", "$500.00", "2025-11-01"),
                new Transaction("Withdrawal", "$45.99", "2025-11-03"),
                new Transaction("Transfer", "$200.00", "2025-11-05"),
                new Transaction("Deposit", "$150.00", "2025-11-10")
        );
        rv.setAdapter(new TransactionAdapter(demo));
    }
}