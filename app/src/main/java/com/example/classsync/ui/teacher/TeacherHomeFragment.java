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

public class TeacherHomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.course_card_1).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.courseDetailTeacherFragment);
        });

        view.findViewById(R.id.fab_add_course).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.createCourseFragment);
        });

        view.findViewById(R.id.btn_add_course_small).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.createCourseFragment);
        });
    }
}
