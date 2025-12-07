package com.example.gitissues;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView; // We are switching to RecyclerView
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import adapters.UserAdapter;
import database.BankingRepository;
import models.User;

public class AdminActivity extends AppCompatActivity {

    private BankingRepository repository;
    private UserAdapter adapter;
    private int currentAdminId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Security Check
        if (!Session.isAdmin(this)) {
            Toast.makeText(this, "Access Denied", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_admin);

        repository = new BankingRepository(getApplicationContext());
        currentAdminId = Session.userId(this);

        // Setup RecyclerView (Note: You need to update your XML layout to use RecyclerView too!)
        // For now, if your XML still has a ListView, this might crash.
        // SEE STEP 6 BELOW for the XML fix.
        RecyclerView rv = findViewById(R.id.rvUsers);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserAdapter(new ArrayList<>(), this::confirmDeleteUser);
        rv.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        loadUsers();
    }

    private void loadUsers() {
        new Thread(() -> {
            List<User> users = repository.getAllUsers();
            runOnUiThread(() -> adapter.setUsers(users));
        }).start();
    }

    private void confirmDeleteUser(User user) {
        // Prevent suicide (deleting yourself)
        if (user.userId == currentAdminId) {
            Toast.makeText(this, "You cannot delete yourself!", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete User?")
                .setMessage("Are you sure you want to delete " + user.username + "? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteUser(user))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteUser(User user) {
        new Thread(() -> {
            repository.deleteUser(user);

            // Reload list to show changes
            loadUsers();

            runOnUiThread(() ->
                    Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show()
            );
        }).start();
    }
}