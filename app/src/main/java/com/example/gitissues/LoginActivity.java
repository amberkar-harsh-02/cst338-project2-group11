package com.example.gitissues;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText etUser, etPass;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_login);

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

            boolean isAdmin = false;
            boolean valid = false;

            // Temporary hard-coded users (matches assignment)
            if (u.equals("testuser1") && p.equals("testuser1")) {
                valid = true;
                isAdmin = false;
            } else if (u.equals("admin2") && p.equals("admin2")) {
                valid = true;
                isAdmin = true;
            }

            if (!valid) {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                return;
            }

            Session.login(this, u, isAdmin);

            startActivity(new Intent(this, LandingActivity.class));
            finish();
        });

        tvSignup.setOnClickListener(v ->
                Toast.makeText(this, "Sign-up screen TBD", Toast.LENGTH_SHORT).show());
    }
}