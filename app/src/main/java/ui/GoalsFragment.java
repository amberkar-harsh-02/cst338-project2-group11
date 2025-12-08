package ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

    public GoalsFragment() { super(R.layout.fragment_goals); }

    @Override
    public void onViewCreated(@NonNull View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        repository = new BankingRepository(getContext());

        RecyclerView rv = v.findViewById(R.id.rvGoals);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        // Pass "this::showAddOrEditDialog" as the listener for clicks
        adapter = new SavingsGoalAdapter(new ArrayList<>(), this::showAddOrEditDialog);
        rv.setAdapter(adapter);

        // Clicking "New Goal" opens dialog with null (meaning create mode)
        v.findViewById(R.id.btnNewGoal).setOnClickListener(view -> showAddOrEditDialog(null));

        loadData();
    }

    private void loadData() {
        int userId = Session.userId(getContext());
        new Thread(() -> {
            List<SavingsGoal> goals = repository.getGoals(userId);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (goals != null) adapter.setData(goals);
                });
            }
        }).start();
    }

    // Reused for both Adding (goal == null) and Editing (goal != null)
    private void showAddOrEditDialog(SavingsGoal existingGoal) {
        if (getContext() == null) return;

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_goal, null);

        TextView tvTitle = view.findViewById(R.id.tvTitle); // Ensure your XML has this ID on the top TextView, or find it by text logic
        EditText etName = view.findViewById(R.id.etGoalName);
        EditText etTarget = view.findViewById(R.id.etTargetAmount);
        EditText etCurrent = view.findViewById(R.id.etCurrentAmount);
        Button btnSave = view.findViewById(R.id.btnSaveGoal);
        Button btnDelete = view.findViewById(R.id.btnDeleteGoal);

        // SETUP MODE (Create vs Edit)
        if (existingGoal != null) {
            // Edit Mode
            etName.setText(existingGoal.name);
            etTarget.setText(String.valueOf(existingGoal.targetAmount));
            etCurrent.setText(String.valueOf(existingGoal.currentAmount));
            btnSave.setText("Update Goal");
            btnDelete.setVisibility(View.VISIBLE); // Show Delete Button
        } else {
            // Create Mode
            btnSave.setText("Create Goal");
            btnDelete.setVisibility(View.GONE); // Hide Delete Button
        }

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .create();

        // SAVE / UPDATE Logic
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String targetStr = etTarget.getText().toString().trim();
            String currentStr = etCurrent.getText().toString().trim();

            if (name.isEmpty() || targetStr.isEmpty()) {
                Toast.makeText(getContext(), "Enter name and target", Toast.LENGTH_SHORT).show();
                return;
            }

            double target = Double.parseDouble(targetStr);
            double current = currentStr.isEmpty() ? 0.0 : Double.parseDouble(currentStr);
            int userId = Session.userId(getContext());

            new Thread(() -> {
                if (existingGoal == null) {
                    // Create New
                    SavingsGoal newGoal = new SavingsGoal(userId, name, target, current);
                    repository.addSavingsGoal(newGoal);
                } else {
                    // Update Existing
                    existingGoal.name = name;
                    existingGoal.targetAmount = target;
                    existingGoal.currentAmount = current;
                    repository.updateSavingsGoal(existingGoal);
                }
                refreshUI(dialog);
            }).start();
        });

        // DELETE Logic
        btnDelete.setOnClickListener(v -> {
            new Thread(() -> {
                if (existingGoal != null) {
                    repository.deleteSavingsGoal(existingGoal);
                }
                refreshUI(dialog);
            }).start();
        });

        dialog.show();
    }

    private void refreshUI(AlertDialog dialog) {
        try { Thread.sleep(100); } catch (Exception e) {}
        loadData();
        if (getActivity() != null) {
            getActivity().runOnUiThread(dialog::dismiss);
        }
    }
}