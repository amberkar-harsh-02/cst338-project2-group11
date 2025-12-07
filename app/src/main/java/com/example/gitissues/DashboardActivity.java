package com.example.gitissues;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private RecyclerView rvTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        rvTransactions = findViewById(R.id.rvTransactions);

        setSupportActionBar(toolbar);
        toolbar.setTitle("Welcome User");
        // Set up hamburger icon and drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toolbar.setNavigationOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        // Optional: set dynamic account data here
        TextView tvCheckingBalance = findViewById(R.id.tvCheckingBalance);
        TextView tvSavingsBalance = findViewById(R.id.tvSavingsBalance);

        tvCheckingBalance.setText("$5,234.56");
        tvSavingsBalance.setText("$12,890.23");

        setupTransactions();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // This is your new back press logic
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // If the drawer is NOT open, disable this callback
                    // and trigger the default back behavior (e.g., exit the Activity)
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        });
    }

    private void setupTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("Salary Deposit", "2025-11-01", 3500.00));
        transactions.add(new Transaction("Grocery Store", "2025-10-30", -87.32));
        transactions.add(new Transaction("Online Payment", "2025-10-29", -54.99));
        transactions.add(new Transaction("Monthly Transfer", "2025-10-28", -300.00));
        transactions.add(new Transaction("Interest Earned", "2025-10-27", 12.41));

        TransactionAdaptor adapter = new TransactionAdaptor(this, transactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // already here
        } else if (id == R.id.nav_transfer) {
            Toast.makeText(this, "Transfer Funds clicked", Toast.LENGTH_SHORT).show();
            // TODO: start TransferFundsActivity
        } else if (id == R.id.nav_history) {
            Toast.makeText(this, "Transaction History clicked", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_goals) {
            Toast.makeText(this, "Savings Goals clicked", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Sign Out clicked", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
