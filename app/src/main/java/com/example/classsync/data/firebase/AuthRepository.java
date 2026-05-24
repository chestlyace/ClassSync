package com.example.classsync.data.firebase;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.example.classsync.data.UserSession;
import com.example.classsync.data.model.AppUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AuthRepository {
    private static final String USERS_COLLECTION = "users";
    public static final String ROLE_TEACHER = "teacher";
    public static final String ROLE_STUDENT = "student";

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final FirebaseStorage storage;
    private final UserSession userSession;

    public AuthRepository(@NonNull Context context) {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
        this.userSession = new UserSession(context.getApplicationContext());
    }

    public void register(
            @NonNull String fullName,
            @NonNull String email,
            @NonNull String password,
            @NonNull String role,
            @NonNull AuthCallback callback
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        callback.onError("Registration failed. Please try again.");
                        return;
                    }

                    String normalizedName = fullName.trim();
                    String normalizedEmail = email.trim().toLowerCase(Locale.US);
                    String normalizedRole = normalizeRole(role);

                    Map<String, Object> userDocument = new HashMap<>();
                    userDocument.put("uid", firebaseUser.getUid());
                    userDocument.put("name", normalizedName);
                    userDocument.put("email", normalizedEmail);
                    userDocument.put("role", normalizedRole);
                    userDocument.put("avatarUrl", "");
                    userDocument.put("fcmToken", "");
                    userDocument.put("createdAt", FieldValue.serverTimestamp());

                    firestore.collection(USERS_COLLECTION)
                            .document(firebaseUser.getUid())
                            .set(userDocument)
                            .addOnSuccessListener(unused -> {
                                AppUser appUser = new AppUser(
                                        firebaseUser.getUid(),
                                        normalizedName,
                                        normalizedEmail,
                                        normalizedRole,
                                        "",
                                        "",
                                        null
                                );
                                userSession.saveUser(appUser);
                                callback.onSuccess(appUser);
                            })
                            .addOnFailureListener(error ->
                                    firebaseUser.delete()
                                            .addOnCompleteListener(task ->
                                                    callback.onError(getErrorMessage(error, "We created the account but failed to save the profile. Please try again."))
                                            )
                            );
                })
                .addOnFailureListener(error ->
                        callback.onError(getErrorMessage(error, "Registration failed. Please try again."))
                );
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
                .addOnFailureListener(error ->
                        callback.onError(getErrorMessage(error, "Wrong credentials. Please try again."))
                );
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

    private void loadUserProfile(@NonNull FirebaseUser firebaseUser, @NonNull AuthCallback callback) {
        firestore.collection(USERS_COLLECTION)
                .document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> handleUserDocument(firebaseUser, documentSnapshot, callback))
                .addOnFailureListener(error ->
                        callback.onError(getErrorMessage(error, "Failed to load your profile."))
                );
    }

    private void handleUserDocument(
            @NonNull FirebaseUser firebaseUser,
            @NonNull DocumentSnapshot documentSnapshot,
            @NonNull AuthCallback callback
    ) {
        String fullName = readDisplayName(documentSnapshot);
        String email = documentSnapshot.getString("email");
        String role = normalizeRole(documentSnapshot.getString("role"));
        String avatarUrl = valueOrEmpty(documentSnapshot.getString("avatarUrl"));
        String fcmToken = valueOrEmpty(documentSnapshot.getString("fcmToken"));
        Timestamp createdAt = documentSnapshot.getTimestamp("createdAt");

        if (!documentSnapshot.exists() || TextUtils.isEmpty(fullName) || TextUtils.isEmpty(role)) {
            callback.onError("Your account profile is missing. Please contact support or register again.");
            return;
        }

        String resolvedEmail = !TextUtils.isEmpty(email)
                ? email.trim().toLowerCase(Locale.US)
                : safeEmail(firebaseUser.getEmail());

        AppUser appUser = new AppUser(
                firebaseUser.getUid(),
                fullName.trim(),
                resolvedEmail,
                role,
                avatarUrl,
                fcmToken,
                createdAt
        );
        userSession.saveUser(appUser);
        callback.onSuccess(appUser);
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
    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    @NonNull
    private String normalizeRole(String role) {
        if (TextUtils.isEmpty(role)) {
            return "";
        }
        String normalizedRole = role.trim().toLowerCase(Locale.US);
        if (ROLE_TEACHER.equals(normalizedRole)) {
            return ROLE_TEACHER;
        }
        return ROLE_STUDENT.equals(normalizedRole) ? ROLE_STUDENT : normalizedRole;
    }

    public void saveFcmToken() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) return;

        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token ->
                        firestore.collection(USERS_COLLECTION)
                                .document(user.getUid())
                                .update("fcmToken", token)
                );
    }

    public void updateProfileName(@NonNull String newName, @Nullable Runnable onSuccess, @Nullable Runnable onError) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            if (onError != null) onError.run();
            return;
        }
        firestore.collection(USERS_COLLECTION)
                .document(user.getUid())
                .update("name", newName.trim())
                .addOnSuccessListener(unused -> {
                    userSession.setUserName(newName.trim());
                    if (onSuccess != null) onSuccess.run();
                })
                .addOnFailureListener(unused -> {
                    if (onError != null) onError.run();
                });
    }

    public void uploadAvatar(@NonNull android.net.Uri imageUri, @Nullable Runnable onSuccess, @Nullable Runnable onError) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            if (onError != null) onError.run();
            return;
        }
        StorageReference ref = storage.getReference("avatars/" + user.getUid() + ".jpg");
        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        ref.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            String url = downloadUri.toString();
                            firestore.collection(USERS_COLLECTION)
                                    .document(user.getUid())
                                    .update("avatarUrl", url)
                                    .addOnSuccessListener(unused -> {
                                        userSession.setAvatarUrl(url);
                                        if (onSuccess != null) onSuccess.run();
                                    })
                                    .addOnFailureListener(unused -> {
                                        if (onError != null) onError.run();
                                    });
                        })
                )
                .addOnFailureListener(unused -> {
                    if (onError != null) onError.run();
                });
    }

    @NonNull
    private String readDisplayName(@NonNull DocumentSnapshot documentSnapshot) {
        String name = documentSnapshot.getString("name");
        if (!TextUtils.isEmpty(name)) {
            return name;
        }

        String legacyFullName = documentSnapshot.getString("fullName");
        return legacyFullName == null ? "" : legacyFullName;
    }
}
