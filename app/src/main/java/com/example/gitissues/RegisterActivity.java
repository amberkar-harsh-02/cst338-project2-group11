package com.example.gitissues;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import database.BankingRepository;
import models.Account;
import models.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFirst, etLast, etEmail, etPhone, etAddress;
    private EditText etUser, etPass;
    private CheckBox cbIsAdmin;
    private BankingRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        repository = new BankingRepository(this);

        // Bind Views
        etFirst = findViewById(R.id.etFirstName);
        etLast = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);

        etUser = findViewById(R.id.etRegUsername);
        etPass = findViewById(R.id.etRegPassword);
        cbIsAdmin = findViewById(R.id.cbIsAdmin);

        Button btnRegister = findViewById(R.id.btnRegisterUser);
        Button btnBack = findViewById(R.id.btnRegBack);

        btnRegister.setOnClickListener(v -> registerUser());

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void registerUser() {
        // Get Inputs
        String first = etFirst.getText().toString().trim();
        String last = etLast.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String username = etUser.getText().toString().trim();
        String password = etPass.getText().toString().trim();
        boolean isAdmin = cbIsAdmin.isChecked();

        // Validation
        if (TextUtils.isEmpty(first) || TextUtils.isEmpty(last) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) ||
                TextUtils.isEmpty(address) || TextUtils.isEmpty(username) ||
                TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            if (repository.getUserByUsername(username) != null) {
                runOnUiThread(() -> Toast.makeText(this, "Username taken", Toast.LENGTH_SHORT).show());
                return;
            }

            // Create User
            User newUser = new User(username, password, isAdmin, first, last, email, phone, address);
            long userId = repository.registerUser(newUser);

            if (userId > 0) {
                // --- GENERATE UNIQUE ACCOUNT NUMBERS ---
                String checkNum = repository.generateNewAccountNumber();
                String saveNum = repository.generateNewAccountNumber();

                // Create Accounts with these numbers
                repository.insertAccount(new Account((int)userId, checkNum, "Checking", 0.0));
                repository.insertAccount(new Account((int)userId, saveNum, "Savings", 0.0));

                runOnUiThread(() -> {
                    Toast.makeText(this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }).start();
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, RegisterActivity.class);
    }
}