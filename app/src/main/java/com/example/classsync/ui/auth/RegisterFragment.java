package com.example.classsync.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;
import com.example.classsync.data.UserSession;

public class RegisterFragment extends Fragment {

    private String currentRole = "STUDENT";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Header back button
        ImageButton backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v ->
            NavHostFragment.findNavController(this).navigateUp()
        );

        // Role selector cards
        LinearLayout teacherCard = view.findViewById(R.id.role_teacher);
        LinearLayout studentCard = view.findViewById(R.id.role_student);
        ImageView iconTeacher = view.findViewById(R.id.icon_teacher);
        ImageView iconStudent = view.findViewById(R.id.icon_student);
        Button registerButton = view.findViewById(R.id.register_button);
        TextView loginLink = view.findViewById(R.id.login_link);

        // Role selection handlers
        teacherCard.setOnClickListener(v -> {
            currentRole = "TEACHER";
            // Update teacher card to active state
            teacherCard.setBackgroundResource(R.drawable.bg_role_card_teacher);
            iconTeacher.setColorFilter(ContextCompat.getColor(requireContext(), R.color.secondary));

            // Reset student card to default state
            studentCard.setBackgroundResource(R.drawable.bg_role_card_default);
            iconStudent.setColorFilter(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));

            // Switch button to secondary (green) color
            registerButton.setBackgroundResource(R.drawable.bg_button_register_secondary);
        });

        studentCard.setOnClickListener(v -> {
            currentRole = "STUDENT";
            // Update student card to active state
            studentCard.setBackgroundResource(R.drawable.bg_role_card_student);
            iconStudent.setColorFilter(ContextCompat.getColor(requireContext(), R.color.primary));

            // Reset teacher card to default state
            teacherCard.setBackgroundResource(R.drawable.bg_role_card_default);
            iconTeacher.setColorFilter(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));

            // Switch button to primary (blue) color
            registerButton.setBackgroundResource(R.drawable.bg_button_register_primary);
        });

        // Register button
        registerButton.setOnClickListener(v -> {
            UserSession session = new UserSession(requireContext());
            session.setLoggedIn(true);
            session.setUserRole(currentRole);

            if ("TEACHER".equals(currentRole)) {
                NavHostFragment.findNavController(this).navigate(R.id.nav_home);
            } else {
                NavHostFragment.findNavController(this).navigate(R.id.studentHomeFragment);
            }
        });

        // Login link
        loginLink.setOnClickListener(v ->
            NavHostFragment.findNavController(this).navigate(R.id.loginFragment)
        );
    }
}
