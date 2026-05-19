package com.example.classsync.ui.shared;

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

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UserSession session = new UserSession(requireContext());
        TextView roleLabel = view.findViewById(R.id.profile_role);
        Button logoutButton = view.findViewById(R.id.btn_logout);

        String role = session.getUserRole();
        roleLabel.setText(role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase());

        logoutButton.setOnClickListener(v -> {
            session.logout();
            NavHostFragment.findNavController(this).navigate(R.id.loginFragment);
        });
    }
}
