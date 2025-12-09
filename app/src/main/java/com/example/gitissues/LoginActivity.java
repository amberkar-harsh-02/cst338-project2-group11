package com.example.gitissues;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

    // --- INTENT FACTORY (Rubric Requirement) ---
    public static Intent getIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_login);

        repository = new BankingRepository(getApplicationContext());
        repository.seedData();

        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPass);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvSignup = findViewById(R.id.tvSignup);

        // --- FIX: HIDE SIGN UP OPTION ---
        // Since we only want Admins to create users, we hide this button.
        if (tvSignup != null) {
            tvSignup.setVisibility(View.GONE);
        }

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

                        // USE FACTORY
                        startActivity(LandingActivity.getIntent(this));
                        finish();
                    } else {
                        Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }
}