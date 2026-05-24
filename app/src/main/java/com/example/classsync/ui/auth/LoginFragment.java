package com.example.classsync.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;
import com.example.classsync.data.firebase.AuthCallback;
import com.example.classsync.data.firebase.AuthRepository;
import com.example.classsync.data.model.AppUser;

public class LoginFragment extends Fragment {

    private boolean passwordVisible = false;
    private AuthRepository authRepository;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authRepository = new AuthRepository(requireContext());

        emailEditText = view.findViewById(R.id.email_edit_text);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        ImageButton passwordToggle = view.findViewById(R.id.password_toggle);
        loginButton = view.findViewById(R.id.login_button);
        registerButton = view.findViewById(R.id.register_button);
        TextView forgotPassword = view.findViewById(R.id.forgot_password);

        passwordToggle.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                passwordToggle.setImageResource(R.drawable.ic_visibility_off);
            } else {
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                passwordToggle.setImageResource(R.drawable.ic_visibility);
            }
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        loginButton.setOnClickListener(v -> attemptLogin());

        registerButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.registerFragment);
        });

        forgotPassword.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Password reset is not implemented yet.", Toast.LENGTH_SHORT).show()
        );
    }

    private void attemptLogin() {
        String email = valueOf(emailEditText);
        String password = valueOf(passwordEditText);

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

        setFormEnabled(false);
        authRepository.login(email, password, new AuthCallback() {
            @Override
            public void onSuccess(@NonNull AppUser user) {
                if (!isAdded()) {
                    return;
                }
                setFormEnabled(true);
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
        emailEditText.setEnabled(enabled);
        passwordEditText.setEnabled(enabled);
        loginButton.setEnabled(enabled);
        registerButton.setEnabled(enabled);
    }

    @NonNull
    private String valueOf(@NonNull EditText editText) {
        return editText.getText().toString().trim();
    }
}
