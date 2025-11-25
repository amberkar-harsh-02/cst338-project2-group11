package com.example.gitissues;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    private static final String PREF = "session";
    private static final String KEY_USER = "username";
    private static final String KEY_ADMIN = "is_admin";

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public static void login(Context c, String username, boolean isAdmin) {
        prefs(c).edit()
                .putString(KEY_USER, username)
                .putBoolean(KEY_ADMIN, isAdmin)
                .apply();
    }

    public static void logout(Context c) {
        prefs(c).edit().clear().apply();
    }

    public static boolean isLoggedIn(Context c) {
        return prefs(c).contains(KEY_USER);
    }

    public static String username(Context c) {
        return prefs(c).getString(KEY_USER, "");
    }

    public static boolean isAdmin(Context c) {
        return prefs(c).getBoolean(KEY_ADMIN, false);
    }
}