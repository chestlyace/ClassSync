package com.example.classsync.data.firebase;

import androidx.annotation.NonNull;

import com.example.classsync.data.model.AppUser;

public interface AuthCallback {
    void onSuccess(@NonNull AppUser user);

    void onError(@NonNull String message);
}
