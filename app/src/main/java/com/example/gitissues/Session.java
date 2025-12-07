package com.example.gitissues;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    private static final String PREF = "session";
    private static final String KEY_USER = "username";
    private static final String KEY_USER_ID = "user_id"; // New field
    private static final String KEY_ADMIN = "is_admin";

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    // Updated login to take userId
    public static void login(Context c, int userId, String username, boolean isAdmin) {
        prefs(c).edit()
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_USER, username)
                .putBoolean(KEY_ADMIN, isAdmin)
                .apply();
    }

    public static void logout(Context c) {
        prefs(c).edit().clear().apply(); // This wipes ALL data
    }

    public static boolean isLoggedIn(Context c) {
        return prefs(c).contains(KEY_USER_ID);
    }

    public static String username(Context c) {
        return prefs(c).getString(KEY_USER, "");
    }
    // New getter for User ID
    public static int userId(Context c) {
        return prefs(c).getInt(KEY_USER_ID, -1);
    }

    public static boolean isAdmin(Context c) {
        return prefs(c).getBoolean(KEY_ADMIN, false);
    }
}