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
        tvBack.setOnClickListener(v -> finish()); // Go back to Login
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
            User newUser = new User(u, p, false);
            long userId = repository.registerUser(newUser);

            if (userId > 0) {
                // 3. Create Default Account (Checking)
                Account newAccount = new Account((int) userId, "Checking", 0.00);
                repository.insertAccount(newAccount);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Account created! Please Login.", Toast.LENGTH_SHORT).show();
                    finish(); // Close screen and return to Login
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}