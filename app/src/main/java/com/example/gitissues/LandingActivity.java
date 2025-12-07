package com.example.gitissues;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.gitissues.ui.GoalsFragment;
import ui.HistoryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // Load default fragment: Goals
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new GoalsFragment())
                    .commit();
        }

        // Handle bottom navigation switching
        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setOnItemSelectedListener(item -> {
            Fragment f;
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

        // --- Admin button (only visible for admins) ---
        Button btnAdmin = findViewById(R.id.btnAdmin);
        btnAdmin.setVisibility(Session.isAdmin(this) ? View.VISIBLE : View.INVISIBLE);

        btnAdmin.setOnClickListener(v ->
                startActivity(new Intent(this, AdminActivity.class)));

        // --- Logout button (visible to ALL users) ---
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            Session.logout(this);               // clears SharedPreferences
            startActivity(new Intent(this, LoginActivity.class));
            finish();                           // prevent back navigation
        });
    }
}