package com.example.gitissues;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Safety check: if someone somehow gets here but is not admin, kick them out
        if (!Session.isAdmin(this)) {
            Toast.makeText(this, "Admin access only", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_admin);

        ListView listUsers = findViewById(R.id.listUsers);
        Button btnBack = findViewById(R.id.btnBack);

        // user list – later this can come from the Room database
        String[] users = new String[]{
                "testuser1  -  regular user",
                "admin2     -  ADMIN",
                Session.username(this) + "  -  (you)"
        };

        listUsers.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                users
        ));

        btnBack.setOnClickListener(v -> finish());
    }
}