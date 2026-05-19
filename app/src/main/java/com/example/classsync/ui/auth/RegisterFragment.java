package com.example.classsync.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;
import com.example.classsync.data.UserSession;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class RegisterFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButtonToggleGroup roleToggle = view.findViewById(R.id.role_toggle_group);
        Button registerButton = view.findViewById(R.id.register_button);
        TextView loginLink = view.findViewById(R.id.login_link);

        registerButton.setOnClickListener(v -> {
            UserSession session = new UserSession(requireContext());
            session.setLoggedIn(true);

            if (roleToggle.getCheckedButtonId() == R.id.role_teacher) {
                session.setUserRole("TEACHER");
                NavHostFragment.findNavController(this).navigate(R.id.nav_home);
            } else {
                session.setUserRole("STUDENT");
                NavHostFragment.findNavController(this).navigate(R.id.studentHomeFragment);
            }
        });

        loginLink.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.loginFragment);
        });
    }
}
