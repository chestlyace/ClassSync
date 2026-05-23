package com.example.classsync.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;

public class MyCoursesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_courses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Header Actions
        view.findViewById(R.id.btn_join_course).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.joinCourseFragment);
        });

        // Course Cards Navigation
        View.OnClickListener cardClickListener = v -> {
            NavHostFragment.findNavController(this).navigate(R.id.studentCourseAssignmentsFragment);
        };

        view.findViewById(R.id.course_card_1).setOnClickListener(cardClickListener);
        view.findViewById(R.id.course_card_2).setOnClickListener(cardClickListener);
        view.findViewById(R.id.course_card_3).setOnClickListener(cardClickListener);


    }
}
