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

public class AssignmentDetailStudentFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_assignment_detail_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Header Actions
        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });
        
        view.findViewById(R.id.btn_settings).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
        });

        // Group Action
        view.findViewById(R.id.btn_group_lobby).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.groupLobbyFragment);
        });
        
        // Submit Button (Currently disabled in XML, but keeping listener just in case)
        view.findViewById(R.id.btn_submit_assignment).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Submit clicked", Toast.LENGTH_SHORT).show();
        });


    }
}
