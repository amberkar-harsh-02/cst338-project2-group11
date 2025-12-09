package com.example.gitissues;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import ui.GoalsFragment;
import ui.HistoryFragment;
import ui.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // --- FIX FOR STATUS BAR OVERLAP ---
        // This pushes the app content down so it doesn't sit under the camera/battery
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.landing_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // ----------------------------------

        // Load Default Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new GoalsFragment())
                    .commit();
        }

        // Bottom Nav Logic
        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setOnItemSelectedListener(item -> {
            Fragment f = null;
            if (item.getItemId() == R.id.nav_goals) f = new GoalsFragment();
            else if (item.getItemId() == R.id.nav_history) f = new HistoryFragment();

            if (f != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, f)
                        .commit();
            }
            return true;
        });

        // Profile Popup Menu Logic
        ImageButton btnMenu = findViewById(R.id.btnProfileMenu);
        btnMenu.setOnClickListener(this::showPopupMenu);
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.profile_options, popup.getMenu());

        if (Session.isAdmin(this)) {
            popup.getMenu().findItem(R.id.menu_admin).setVisible(true);
        }

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_profile) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment())
                        .addToBackStack(null)
                        .commit();
                return true;
            }
            else if (id == R.id.menu_contact) {
                new AlertDialog.Builder(this)
                        .setTitle("Contact Us")
                        .setMessage("Customer Support:\n1-800-555-BANK\nsupport@bank.com")
                        .setPositiveButton("OK", null)
                        .show();
                return true;
            }
            else if (id == R.id.menu_logout) {
                Session.logout(this);
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            }
            else if (id == R.id.menu_admin) {
                startActivity(new Intent(this, AdminActivity.class));
                return true;
            }
            return false;
        });

        popup.show();
    }
}