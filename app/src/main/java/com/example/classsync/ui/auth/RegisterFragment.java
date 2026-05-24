package com.example.classsync.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;
import com.example.classsync.data.firebase.AuthCallback;
import com.example.classsync.data.firebase.AuthRepository;
import com.example.classsync.data.model.AppUser;

public class RegisterFragment extends Fragment {

    private String currentRole = AuthRepository.ROLE_STUDENT;
    private AuthRepository authRepository;
    private Button registerButton;
    private TextView loginLink;
    private ImageButton backButton;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authRepository = new AuthRepository(requireContext());

        backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v ->
            NavHostFragment.findNavController(this).navigateUp()
        );

        LinearLayout teacherCard = view.findViewById(R.id.role_teacher);
        LinearLayout studentCard = view.findViewById(R.id.role_student);
        ImageView iconTeacher = view.findViewById(R.id.icon_teacher);
        ImageView iconStudent = view.findViewById(R.id.icon_student);
        nameEditText = view.findViewById(R.id.name_edit_text);
        emailEditText = view.findViewById(R.id.email_edit_text);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        confirmPasswordEditText = view.findViewById(R.id.confirm_password_edit_text);
        registerButton = view.findViewById(R.id.register_button);
        loginLink = view.findViewById(R.id.login_link);

        teacherCard.setOnClickListener(v -> {
            currentRole = AuthRepository.ROLE_TEACHER;
            teacherCard.setBackgroundResource(R.drawable.bg_role_card_teacher);
            iconTeacher.setColorFilter(ContextCompat.getColor(requireContext(), R.color.secondary));
            studentCard.setBackgroundResource(R.drawable.bg_role_card_default);
            iconStudent.setColorFilter(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));
            registerButton.setBackgroundResource(R.drawable.bg_button_register_secondary);
        });

        studentCard.setOnClickListener(v -> {
            currentRole = AuthRepository.ROLE_STUDENT;
            studentCard.setBackgroundResource(R.drawable.bg_role_card_student);
            iconStudent.setColorFilter(ContextCompat.getColor(requireContext(), R.color.primary));
            teacherCard.setBackgroundResource(R.drawable.bg_role_card_default);
            iconTeacher.setColorFilter(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));
            registerButton.setBackgroundResource(R.drawable.bg_button_register_primary);
        });

        registerButton.setOnClickListener(v -> attemptRegistration());

        loginLink.setOnClickListener(v ->
            NavHostFragment.findNavController(this).navigate(R.id.loginFragment)
        );
    }

    private void attemptRegistration() {
        String fullName = valueOf(nameEditText);
        String email = valueOf(emailEditText);
        String password = valueOf(passwordEditText);
        String confirmPassword = valueOf(confirmPasswordEditText);

        if (TextUtils.isEmpty(fullName)) {
            nameEditText.setError("Full name is required");
            nameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 8) {
            passwordEditText.setError("Password must be at least 8 characters");
            passwordEditText.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            confirmPasswordEditText.requestFocus();
            return;
        }

        setFormEnabled(false);
        authRepository.register(fullName, email, password, currentRole, new AuthCallback() {
            @Override
            public void onSuccess(@NonNull AppUser user) {
                if (!isAdded()) {
                    return;
                }
                setFormEnabled(true);
                Toast.makeText(requireContext(), "Account created successfully", Toast.LENGTH_SHORT).show();
                authRepository.saveFcmToken();
                navigateToHome(user.getRole());
            }

            @Override
            public void onError(@NonNull String message) {
                if (!isAdded()) {
                    return;
                }
                setFormEnabled(true);
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToHome(@NonNull String role) {
        if (AuthRepository.ROLE_TEACHER.equals(role)) {
            NavHostFragment.findNavController(this).navigate(R.id.nav_home);
        } else {
            NavHostFragment.findNavController(this).navigate(R.id.studentHomeFragment);
        }
    }

    private void setFormEnabled(boolean enabled) {
        registerButton.setEnabled(enabled);
        loginLink.setEnabled(enabled);
        backButton.setEnabled(enabled);
        nameEditText.setEnabled(enabled);
        emailEditText.setEnabled(enabled);
        passwordEditText.setEnabled(enabled);
        confirmPasswordEditText.setEnabled(enabled);
    }

    @NonNull
    private String valueOf(@NonNull EditText editText) {
        return editText.getText().toString().trim();
    }
}
