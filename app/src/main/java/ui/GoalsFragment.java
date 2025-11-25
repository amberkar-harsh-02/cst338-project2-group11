package com.example.gitissues.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gitissues.R;
import adapters.SavingsGoalAdapter;
import models.SavingsGoal;

import java.util.Arrays;
import java.util.List;

public class GoalsFragment extends Fragment {

    public GoalsFragment() {
        super(R.layout.fragment_goals);
    }

    @Override
    public void onViewCreated(@NonNull View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        RecyclerView rv = v.findViewById(R.id.rvGoals);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        // Mock data
        List<SavingsGoal> demo = Arrays.asList(
                new SavingsGoal("Emergency Fund", 40, "$400 / $1,000"),
                new SavingsGoal("New Laptop", 70, "$700 / $1,000")
        );

        rv.setAdapter(new SavingsGoalAdapter(demo));

        v.findViewById(R.id.btnNewGoal).setOnClickListener(x ->
                Toast.makeText(getContext(), "New Goal screen TBD", Toast.LENGTH_SHORT).show()
        );
    }
}