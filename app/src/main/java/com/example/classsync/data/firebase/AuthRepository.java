package com.example.classsync.data.firebase;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.example.classsync.data.UserSession;
import com.example.classsync.data.model.AppUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AuthRepository {
    public static final String ROLE_TEACHER = "TEACHER";
    public static final String ROLE_STUDENT = "STUDENT";

    private static final String USERS_COLLECTION = "users";

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final UserSession userSession;

    public AuthRepository(@NonNull Context context) {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
        this.userSession = new UserSession(context.getApplicationContext());
    }

    public void register(
            @NonNull String fullName,
            @NonNull String email,
            @NonNull String password,
            @NonNull String role,
            @NonNull AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        callback.onError("Registration failed. Please try again.");
                        return;
                    }

                    String normalizedName = fullName.trim();
                    String normalizedEmail = email.trim().toLowerCase(Locale.US);
                    String normalizedRole = role.trim().toUpperCase(Locale.US);

                    Map<String, Object> userDocument = new HashMap<>();
                    userDocument.put("uid", firebaseUser.getUid());
                    userDocument.put("fullName", normalizedName);
                    userDocument.put("email", normalizedEmail);
                    userDocument.put("role", normalizedRole);
                    userDocument.put("createdAt", FieldValue.serverTimestamp());

                    firestore.collection(USERS_COLLECTION)
                            .document(firebaseUser.getUid())
                            .set(userDocument)
                            .addOnSuccessListener(unused -> {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(normalizedName)
                                        .build();

                                firebaseUser.updateProfile(profileUpdates)
                                        .addOnCompleteListener(task -> {
                                            AppUser appUser = new AppUser(
                                                    firebaseUser.getUid(),
                                                    normalizedName,
                                                    normalizedEmail,
                                                    normalizedRole);
                                            userSession.saveUser(appUser);
                                            callback.onSuccess(appUser);
                                        });
                            })
                            .addOnFailureListener(error -> firebaseUser.delete()
                                    .addOnCompleteListener(task -> callback.onError(getErrorMessage(error,
                                            "We created the account but failed to save the profile. Please try again."))));
                })
                .addOnFailureListener(
                        error -> callback.onError(getErrorMessage(error, "Registration failed. Please try again.")));
    }

    public void login(@NonNull String email, @NonNull String password, @NonNull AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email.trim(), password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        callback.onError("Login failed. Please try again.");
                        return;
                    }
                    loadUserProfile(firebaseUser, callback);
                })
                .addOnFailureListener(error -> callback.onError(getErrorMessage(error, "Invalid email or password.")));
    }

    public void restoreSession(@NonNull AuthCallback callback) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            userSession.logout();
            callback.onError("No active session");
            return;
        }
        loadUserProfile(firebaseUser, callback);
    }

    public boolean hasAuthenticatedUser() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public void logout() {
        firebaseAuth.signOut();
        userSession.logout();
    }

    public void updateProfileName(@NonNull String newName, @NonNull Runnable onSuccess, @NonNull Runnable onFailure) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            onFailure.run();
            return;
        }
        firestore.collection(USERS_COLLECTION)
                .document(firebaseUser.getUid())
                .update("fullName", newName.trim())
                .addOnSuccessListener(unused -> {
                    userSession.setUserName(newName.trim());
                    onSuccess.run();
                })
                .addOnFailureListener(e -> onFailure.run());
    }

    public void uploadAvatar(@NonNull Uri imageUri, @NonNull Runnable onSuccess, @NonNull Runnable onFailure) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            onFailure.run();
            return;
        }
        StorageReference avatarRef = FirebaseStorage.getInstance()
                .getReference("avatars/" + firebaseUser.getUid());
        avatarRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> avatarRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String avatarUrl = uri.toString();
                            firestore.collection(USERS_COLLECTION)
                                    .document(firebaseUser.getUid())
                                    .update("avatarUrl", avatarUrl)
                                    .addOnSuccessListener(unused -> {
                                        userSession.setAvatarUrl(avatarUrl);
                                        onSuccess.run();
                                    })
                                    .addOnFailureListener(e -> onFailure.run());
                        })
                        .addOnFailureListener(e -> onFailure.run()))
                .addOnFailureListener(e -> onFailure.run());
    }

    private void loadUserProfile(@NonNull FirebaseUser firebaseUser, @NonNull AuthCallback callback) {
        firestore.collection(USERS_COLLECTION)
                .document(firebaseUser.getUid())
                .get(Source.SERVER)
                .addOnSuccessListener(documentSnapshot -> handleUserDocument(firebaseUser, documentSnapshot, callback))
                .addOnFailureListener(
                        error -> callback.onError(getErrorMessage(error, "Failed to load your profile.")));
    }

    private void handleUserDocument(
            @NonNull FirebaseUser firebaseUser,
            @NonNull DocumentSnapshot documentSnapshot,
            @NonNull AuthCallback callback) {
        String fullName = documentSnapshot.getString("fullName");
        String email = documentSnapshot.getString("email");
        String role = documentSnapshot.getString("role");

        if (!documentSnapshot.exists() || TextUtils.isEmpty(fullName) || TextUtils.isEmpty(role)) {
            String displayName = !TextUtils.isEmpty(fullName) ? fullName : safeDisplayName(firebaseUser);
            String resolvedEmail = !TextUtils.isEmpty(email) ? email : safeEmail(firebaseUser.getEmail());
            String resolvedRole = !TextUtils.isEmpty(role) ? role : ROLE_STUDENT;
            rebuildUserDocument(firebaseUser, displayName, resolvedEmail, resolvedRole, callback);
            return;
        }

        String resolvedEmail = !TextUtils.isEmpty(email)
                ? email.trim().toLowerCase(Locale.US)
                : safeEmail(firebaseUser.getEmail());

        AppUser appUser = new AppUser(
                firebaseUser.getUid(),
                fullName.trim(),
                resolvedEmail,
                role.trim().toUpperCase(Locale.US));
        userSession.saveUser(appUser);
        callback.onSuccess(appUser);
    }

    private void rebuildUserDocument(
            @NonNull FirebaseUser firebaseUser,
            @NonNull String displayName,
            @NonNull String email,
            @NonNull String role,
            @NonNull AuthCallback callback) {
        Map<String, Object> data = new HashMap<>();
        data.put("uid", firebaseUser.getUid());
        data.put("fullName", displayName);
        data.put("email", email);
        data.put("role", role);
        data.put("createdAt", FieldValue.serverTimestamp());

        firestore.collection(USERS_COLLECTION)
                .document(firebaseUser.getUid())
                .set(data)
                .addOnSuccessListener(unused -> {
                    AppUser appUser = new AppUser(firebaseUser.getUid(), displayName, email, role);
                    userSession.saveUser(appUser);
                    callback.onSuccess(appUser);
                })
                .addOnFailureListener(e ->
                        callback.onError("Your account profile is missing. Please contact support or register again."));
    }

    @NonNull
    private String getErrorMessage(@NonNull Exception error, @NonNull String fallback) {
        String message = error.getLocalizedMessage();
        return TextUtils.isEmpty(message) ? fallback : message;
    }

    @NonNull
    private String safeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.US);
    }

    @NonNull
    private String safeDisplayName(@NonNull FirebaseUser user) {
        String name = user.getDisplayName();
        if (!TextUtils.isEmpty(name)) return name.trim();
        String email = user.getEmail();
        if (!TextUtils.isEmpty(email)) return email.substring(0, email.indexOf('@')).trim();
        return "User";
    }
}
