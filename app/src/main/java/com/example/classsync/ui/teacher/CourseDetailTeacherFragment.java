package com.example.classsync.ui.teacher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;

public class CourseDetailTeacherFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_detail_teacher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.assignment_card_1).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.assignmentOverviewTeacherFragment);
        });
        view.findViewById(R.id.fab_add_assignment).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.createAssignmentFragment);
        });
    }
}
