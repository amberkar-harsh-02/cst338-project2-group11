package com.example.gitissues;

import android.content.Context;
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
import ui.AdminHomeFragment;
import ui.HistoryFragment;
import ui.HomeFragment;
import ui.ProfileFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * The main activity hosting the application's primary fragments (Home, Goals, History).
 * This activity manages the Bottom Navigation Bar and the top-right Profile Menu.
 */
public class LandingActivity extends AppCompatActivity {

    // --- INTENT FACTORY ---
    /**
     * Factory method to create an Intent to start this activity.
     * This is the recommended practice for navigating to activities.
     * @param context The context from which the Intent is started.
     * @return An Intent configured to launch LandingActivity.
     */
    public static Intent getIntent(Context context) {
        return new Intent(context, LandingActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.landing_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView nav = findViewById(R.id.bottom_nav);

        if (savedInstanceState == null) {
            Fragment start;
            if (Session.isAdmin(this)) {
                // Admins See Admin panel as Home
                start = new AdminHomeFragment();
            } else {
                // Normal users see account home
                start = new HomeFragment();

            }


            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, start)
                    .commit();

            nav.setSelectedItemId(R.id.nav_home);
        }

        nav.setOnItemSelectedListener(item -> {
            Fragment f = null;
            int id = item.getItemId();

            if (id == R.id.nav_home){
                if (Session.isAdmin(this)){
                    f = new AdminHomeFragment(); //Adnim home
                } else {
                    f = new HomeFragment(); // normal user home
                }
            }
            else if (id == R.id.nav_goals) f = new GoalsFragment();
            else if (id == R.id.nav_history) f = new HistoryFragment();

            if (f != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, f)
                        .commit();
            }
            return true;
        });

        ImageButton btnMenu = findViewById(R.id.btnProfileMenu);
        btnMenu.setOnClickListener(this::showPopupMenu);
    }

    /**
     * Creates and displays the PopupMenu for user options (Profile, Contact, Logout, Admin).
     * @param view The anchor view for the popup (the ImageButton).
     */
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
                // USE FACTORY
                Intent intent = LoginActivity.getIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            }
            else if (id == R.id.menu_admin) {
                // USE FACTORY
                startActivity(AdminActivity.getIntent(this));
                return true;
            }
            return false;
        });

        popup.show();
    }
}