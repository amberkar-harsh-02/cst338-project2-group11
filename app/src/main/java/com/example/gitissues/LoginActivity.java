package com.example.gitissues;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import database.BankingRepository;
import models.User;

public class LoginActivity extends AppCompatActivity {
    EditText etUser, etPass;
    BankingRepository repository;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_login);

        // 1. Initialize Repository
        repository = new BankingRepository(getApplicationContext());

        // 2. SEED DATA (Optional: For testing only)
        // This ensures you have a user to log in with right away.
        seedDataIfEmpty();

        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPass);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvSignup = findViewById(R.id.tvSignup);

        btnLogin.setOnClickListener(v -> {
            String u = etUser.getText().toString().trim();
            String p = etPass.getText().toString();

            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. REAL DATABASE CHECK
            // We verify the user in a background thread to avoid freezing the UI
            new Thread(() -> {
                User user = repository.getUserByUsername(u);

                // UI updates must happen on the main thread
                runOnUiThread(() -> {
                    if (user != null && user.password.equals(p)) {
                        // Login successful: Save userId and username to session
                        Session.login(this, user.userId, user.username, user.isAdmin);
                        startActivity(new Intent(this, LandingActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });

        tvSignup.setOnClickListener(v ->
                Toast.makeText(this, "Sign-up screen TBD", Toast.LENGTH_SHORT).show());
    }

    /**
     * Helper to create a test user if the DB is empty
     */
    private void seedDataIfEmpty() {
        new Thread(() -> {
            User testUser = repository.getUserByUsername("testuser1");
            if (testUser == null) {
                User newUser = new User("testuser1", "password", false);
                repository.insertUser(newUser);

                User adminUser = new User("admin2", "admin2", true);
                repository.insertUser(adminUser);
            }
        }).start();
    }
}