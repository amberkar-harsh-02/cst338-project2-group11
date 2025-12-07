package com.example.gitissues;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import ui.GoalsFragment;
import ui.HistoryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // This pushes the content down/up so it doesn't overlap the battery or home bar.
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.landing_root), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // -----------------------------------------------------

        // Load default fragment: Goals
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new GoalsFragment())
                    .commit();
        }

        // Handle bottom navigation switching
        com.google.android.material.bottomnavigation.BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setOnItemSelectedListener(item -> {
            androidx.fragment.app.Fragment f;
            if (item.getItemId() == R.id.nav_goals) {
                f = new GoalsFragment();
            } else {
                f = new HistoryFragment();
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, f)
                    .commit();
            return true;
        });

        // --- Logout Logic ---
        android.widget.Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            Session.logout(this);
            android.content.Intent intent = new android.content.Intent(this, LoginActivity.class);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // --- Admin Logic ---
        android.widget.Button btnAdmin = findViewById(R.id.btnAdmin);
        // Only show if Admin
        if (Session.isAdmin(this)) {
            btnAdmin.setVisibility(android.view.View.VISIBLE);
            btnAdmin.setOnClickListener(v ->
                    startActivity(new android.content.Intent(this, AdminActivity.class)));
        } else {
            btnAdmin.setVisibility(android.view.View.GONE);
        }
    }
}