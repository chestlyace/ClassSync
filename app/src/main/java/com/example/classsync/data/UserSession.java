package com.example.classsync.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.classsync.data.model.AppUser;

public class UserSession {
    private static final String PREF_NAME = "classsync_prefs";
    private static final String KEY_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_USER_UID = "user_uid";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_AVATAR = "user_avatar";

    private final SharedPreferences prefs;

    public UserSession(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public void setLoggedIn(boolean loggedIn) {
        prefs.edit().putBoolean(KEY_LOGGED_IN, loggedIn).apply();
    }

    public String getUserRole() {
        return prefs.getString(KEY_USER_ROLE, "");
    }

    public void setUserRole(String role) {
        prefs.edit().putString(KEY_USER_ROLE, role).apply();
    }

    public void saveUser(AppUser user) {
        prefs.edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putString(KEY_USER_UID, user.getUid())
                .putString(KEY_USER_NAME, user.getName())
                .putString(KEY_USER_EMAIL, user.getEmail())
                .putString(KEY_USER_ROLE, user.getRole())
                .putString(KEY_USER_AVATAR, user.getAvatarUrl())
                .apply();
    }

    public String getUserUid() {
        return prefs.getString(KEY_USER_UID, "");
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    public String getAvatarUrl() {
        return prefs.getString(KEY_USER_AVATAR, "");
    }

    public void setUserName(String name) {
        prefs.edit().putString(KEY_USER_NAME, name).apply();
    }

    public void setAvatarUrl(String url) {
        prefs.edit().putString(KEY_USER_AVATAR, url).apply();
    }

    public void logout() {
        prefs.edit().clear().apply();
    }
}
