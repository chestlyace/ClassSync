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

public class GroupWorkspaceFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_workspace, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Header Actions
        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });
        
        view.findViewById(R.id.btn_settings).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Workspace Settings", Toast.LENGTH_SHORT).show();
        });

        // FAB Action
        view.findViewById(R.id.fab_add_task).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.addEditTaskFragment);
        });
        
        // Unchecked task interactions
        view.findViewById(R.id.task_card_analyze).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.addEditTaskFragment);
        });
        view.findViewById(R.id.task_card_format).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.addEditTaskFragment);
        });


    }
}
