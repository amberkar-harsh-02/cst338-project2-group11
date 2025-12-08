package com.example.gitissues;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import database.BankingRepository;
import models.Account;
import models.User;

public class SignUpActivity extends AppCompatActivity {

    EditText etUser, etPass;
    BankingRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        repository = new BankingRepository(getApplicationContext());

        etUser = findViewById(R.id.etNewUser);
        etPass = findViewById(R.id.etNewPass);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvBack = findViewById(R.id.tvGoBack);

        btnRegister.setOnClickListener(v -> handleRegister());
        tvBack.setOnClickListener(v -> finish());
    }

    private void handleRegister() {
        String u = etUser.getText().toString().trim();
        String p = etPass.getText().toString();

        if (u.isEmpty() || p.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            // 1. Check if username exists
            User existing = repository.getUserByUsername(u);
            if (existing != null) {
                runOnUiThread(() -> Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show());
                return;
            }

            // 2. Create User
            User newUser = new User(u, p, false, "", "", "", "", "");
            long userId = repository.registerUser(newUser);

            if (userId > 0) {
                // 3. Create MULTIPLE Accounts (Checking AND Savings)
                Account checking = new Account((int) userId, "Checking", 1000.00); // Start with $1000
                Account savings = new Account((int) userId, "Savings", 500.00);    // Start with $500

                repository.insertAccount(checking);
                repository.insertAccount(savings);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Account created! Please Login.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}