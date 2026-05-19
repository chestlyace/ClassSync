package com.example.classsync.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;
import com.example.classsync.data.UserSession;

public class LoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button loginButton = view.findViewById(R.id.login_button);
        Button registerButton = view.findViewById(R.id.register_button);

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
