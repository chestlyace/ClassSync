package com.example.classsync.ui.teacher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;

public class AssignmentOverviewTeacherFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_assignment_overview_teacher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Header Navigation
        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });



        // Interactive Group Cards (just showing toast for demo)
        view.findViewById(R.id.group_beta).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Group Beta selected", Toast.LENGTH_SHORT).show();
        });
        
        view.findViewById(R.id.group_gamma).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Group Gamma selected", Toast.LENGTH_SHORT).show();
        });
    }
}
