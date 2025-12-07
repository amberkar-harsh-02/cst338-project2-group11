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

        repository = new BankingRepository(getApplicationContext());

        // Setup seed data immediately
        repository.seedData();

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

            new Thread(() -> {
                User user = repository.getUserByUsername(u);

                runOnUiThread(() -> {
                    if (user != null && user.password.equals(p)) {
                        Session.login(this, user.userId, user.username, user.isAdmin);
                        startActivity(new Intent(this, LandingActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });

        tvSignup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
    }
}