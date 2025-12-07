package ui; // Ensure this matches your package structure (e.g., ui or com.example...)

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gitissues.R;
import com.example.gitissues.Session;

import java.util.ArrayList;
import java.util.List;

import adapters.SavingsGoalAdapter;
import database.BankingRepository;
import models.SavingsGoal;

public class GoalsFragment extends Fragment {

    private SavingsGoalAdapter adapter;
    private BankingRepository repository;

    public GoalsFragment() {
        super(R.layout.fragment_goals);
    }

    @Override
    public void onViewCreated(@NonNull View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        RecyclerView rv = v.findViewById(R.id.rvGoals);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize with empty list first
        adapter = new SavingsGoalAdapter(new ArrayList<>());
        rv.setAdapter(adapter);

        repository = new BankingRepository(getContext());

        v.findViewById(R.id.btnNewGoal).setOnClickListener(x ->
                Toast.makeText(getContext(), "New Goal screen TBD", Toast.LENGTH_SHORT).show()
        );

        loadData();
    }

    private void loadData() {
        int userId = Session.userId(getContext());

        // Run database query in background
        new Thread(() -> {
            List<SavingsGoal> goals = repository.getGoals(userId);

            // Update UI on main thread
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (goals != null && !goals.isEmpty()) {
                        adapter.setData(goals);
                    } else {
                        // Optional: Handle empty state (e.g., show "No goals yet")
                    }
                });
            }
        }).start();
    }
}