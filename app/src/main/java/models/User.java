package models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    public int userId;

    public String username;
    public String password;
    public boolean isAdmin;

    // --- NEW FIELDS ---
    public String firstName;
    public String lastName;
    public String email;
    public String phone;
    public String address;

    // Updated Constructor
    public User(String username, String password, boolean isAdmin,
                String firstName, String lastName, String email, String phone, String address) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
}