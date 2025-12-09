package ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gitissues.R;
import com.example.gitissues.Session;

import java.util.List;

import database.BankingRepository;
import models.Account;
import models.User;

public class ProfileFragment extends Fragment {

    private BankingRepository repository;
    private EditText etFirst, etLast, etEmail, etPhone, etAddress, etUsername;
    private EditText etChecking, etSavings; // NEW FIELDS
    private User currentUser;

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new BankingRepository(getContext());

        // Bind Views
        etChecking = view.findViewById(R.id.etProfileChecking);
        etSavings = view.findViewById(R.id.etProfileSavings);

        etUsername = view.findViewById(R.id.etProfileUsername);
        etFirst = view.findViewById(R.id.etProfileFirst);
        etLast = view.findViewById(R.id.etProfileLast);
        etEmail = view.findViewById(R.id.etProfileEmail);
        etPhone = view.findViewById(R.id.etProfilePhone);
        etAddress = view.findViewById(R.id.etProfileAddress);
        Button btnUpdate = view.findViewById(R.id.btnUpdateProfile);

        loadUserProfile();

        btnUpdate.setOnClickListener(v -> updateUserProfile());
    }

    private void loadUserProfile() {
        String username = Session.username(getContext());
        int userId = Session.userId(getContext()); // Need ID to get accounts

        new Thread(() -> {
            // 1. Get User Details
            currentUser = repository.getUserByUsername(username);

            // 2. Get Account Details
            List<Account> accounts = repository.getAccounts(userId);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    // Populate User Info
                    if (currentUser != null) {
                        etUsername.setText(currentUser.username);
                        etFirst.setText(currentUser.firstName);
                        etLast.setText(currentUser.lastName);
                        etEmail.setText(currentUser.email);
                        etPhone.setText(currentUser.phone);
                        etAddress.setText(currentUser.address);
                    }

                    // Populate Account Info
                    if (accounts != null) {
                        for (Account acc : accounts) {
                            if ("Checking".equalsIgnoreCase(acc.accountType)) {
                                etChecking.setText("Checking: " + acc.accountNumber);
                            } else if ("Savings".equalsIgnoreCase(acc.accountType)) {
                                etSavings.setText("Savings: " + acc.accountNumber);
                            }
                        }
                    }
                });
            }
        }).start();
    }

    private void updateUserProfile() {
        if (currentUser == null) return;

        currentUser.firstName = etFirst.getText().toString().trim();
        currentUser.lastName = etLast.getText().toString().trim();
        currentUser.email = etEmail.getText().toString().trim();
        currentUser.phone = etPhone.getText().toString().trim();
        currentUser.address = etAddress.getText().toString().trim();

        new Thread(() -> {
            repository.updateUser(currentUser);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Profile Updated!", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}