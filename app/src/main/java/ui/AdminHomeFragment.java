package ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gitissues.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import adapters.UserAdapter;
import database.AppDatabase;
import models.User;

import java.util.List;

public class AdminHomeFragment extends Fragment {

    private RecyclerView rvUsers;
    private UserAdapter adapter;

    public AdminHomeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.activity_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        // --- Setup RecyclerView ---
        rvUsers = root.findViewById(R.id.rvUsers);
        rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Load users from your Room database
        loadUsers();

        /* --- Back button ---
        Button btnBack = root.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            // Return to normal HomeFragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        });

         */

        // --- Add User button ---
        FloatingActionButton fabAddUser = root.findViewById(R.id.fabAddUser);
        fabAddUser.setOnClickListener(v -> {
            // You can open a dialog or new screen here
            // Example:
            // new AddUserDialog().show(getParentFragmentManager(), "addUser");
        });
    }

    /**
     * Loads the list of users from the database and displays them.
     */
    private void loadUsers() {
        // Get DB instance
        AppDatabase db = AppDatabase.getDatabase(requireContext());

        // Run DB query on background thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<User> users = db.userDao().getAllUsers();

            // Switch back to UI thread to update RecyclerView
            requireActivity().runOnUiThread(() -> {
                if (adapter == null) {
                    // First time: create adapter & set it
                    adapter = new UserAdapter(users, user -> {
                        // This runs when trash can is tapped
                        AppDatabase.databaseWriteExecutor.execute(() -> {
                            db.userDao().delete(user);

                            // After deleting, reload list on UI thread
                            requireActivity().runOnUiThread(this::loadUsers);
                        });
                    });
                    rvUsers.setAdapter(adapter);
                } else {
                    // Later: just update existing adapter's data
                    adapter.setUsers(users);
                }
            });
        });
    }
}