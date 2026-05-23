package com.example.classsync.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;

public class GroupLobbyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_lobby, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Header Action
        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        // Group Actions
        view.findViewById(R.id.btn_join_alpha).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.groupWorkspaceFragment);
        });

        view.findViewById(R.id.btn_create_group).setOnClickListener(v -> {
            // Note: Normally would navigate to create group dialog/fragment, pointing to workspace for flow completion.
            NavHostFragment.findNavController(this).navigate(R.id.groupWorkspaceFragment);
        });

        view.findViewById(R.id.group_card_zeta).setOnClickListener(v -> {
            // Clicking "My Group" takes them to the workspace
            NavHostFragment.findNavController(this).navigate(R.id.groupWorkspaceFragment);
        });


    }
}
