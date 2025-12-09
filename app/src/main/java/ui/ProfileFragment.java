package ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gitissues.R;
import com.example.gitissues.Session;

import database.BankingRepository;
import models.User;
import database.UserDao; // Only if using Room directly, but Repository handles it

public class ProfileFragment extends Fragment {

    private BankingRepository repository;
    private EditText etFirst, etLast, etEmail, etPhone, etAddress, etUsername;
    private User currentUser;

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new BankingRepository(getContext());

        // Bind Views
        etUsername = view.findViewById(R.id.etProfileUsername);
        etFirst = view.findViewById(R.id.etProfileFirst);
        etLast = view.findViewById(R.id.etProfileLast);
        etEmail = view.findViewById(R.id.etProfileEmail);
        etPhone = view.findViewById(R.id.etProfilePhone);
        etAddress = view.findViewById(R.id.etProfileAddress);
        Button btnUpdate = view.findViewById(R.id.btnUpdateProfile);

        // Hide "Logout" button inside fragment since it's now in the top menu
        View btnLogout = view.findViewById(R.id.btnLogoutProfile);
        if(btnLogout != null) btnLogout.setVisibility(View.GONE);

        loadUserProfile();

        btnUpdate.setOnClickListener(v -> updateUserProfile());
    }

    private void loadUserProfile() {
        String username = Session.username(getContext());

        new Thread(() -> {
            currentUser = repository.getUserByUsername(username);

            if (currentUser != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    etUsername.setText(currentUser.username);
                    etFirst.setText(currentUser.firstName);
                    etLast.setText(currentUser.lastName);
                    etEmail.setText(currentUser.email);
                    etPhone.setText(currentUser.phone);
                    etAddress.setText(currentUser.address);
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
            // Room's DAO "insert" (onConflict = Replace) or we need an update method
            // Since we don't have a specific update method in Repository,
            // we'll rely on our Dao being smart or adding a specific update method.

            // NOTE: We need to make sure BankingRepository has an update method
            // Use a repository method:
            repository.updateUser(currentUser);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Profile Updated!", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}