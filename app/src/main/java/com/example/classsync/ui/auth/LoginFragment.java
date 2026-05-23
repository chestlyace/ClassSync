package com.example.classsync.ui.auth;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;
import com.example.classsync.data.UserSession;

public class LoginFragment extends Fragment {

    private boolean passwordVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText passwordEditText = view.findViewById(R.id.password_edit_text);
        ImageButton passwordToggle = view.findViewById(R.id.password_toggle);
        Button loginButton = view.findViewById(R.id.login_button);
        Button registerButton = view.findViewById(R.id.register_button);

        // Password visibility toggle
        passwordToggle.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                passwordToggle.setImageResource(R.drawable.ic_visibility_off);
            } else {
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                passwordToggle.setImageResource(R.drawable.ic_visibility);
            }
            // Move cursor to end of text
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        loginButton.setOnClickListener(v -> {
            // Mock login as Teacher for now, or based on some toggle
            // In a real app, we'd check credentials
            UserSession session = new UserSession(requireContext());
            session.setLoggedIn(true);
            // Let's assume teacher for now if not specified
            if (session.getUserRole().isEmpty()) {
                session.setUserRole("TEACHER");
            }

            if ("TEACHER".equals(session.getUserRole())) {
                NavHostFragment.findNavController(this).navigate(R.id.nav_home);
            } else {
                NavHostFragment.findNavController(this).navigate(R.id.studentHomeFragment);
            }
        });

        registerButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.registerFragment);
        });
    }
}
