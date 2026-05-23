package com.example.classsync.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;

public class StudentCourseAssignmentsFragment extends Fragment {

    private boolean isCompletedExpanded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_course_assignments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back button
        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        // Settings button
        view.findViewById(R.id.btn_settings).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
        });

        // Active assignment card clicks -> navigate to assignment detail
        View.OnClickListener assignmentClickListener = v -> {
            NavHostFragment.findNavController(this).navigate(R.id.assignmentDetailStudentFragment);
        };

        view.findViewById(R.id.card_overdue_assignment).setOnClickListener(assignmentClickListener);
        view.findViewById(R.id.card_in_progress_1).setOnClickListener(assignmentClickListener);
        view.findViewById(R.id.card_in_progress_2).setOnClickListener(assignmentClickListener);

        // Submit Now button on overdue card
        view.findViewById(R.id.btn_submit_now).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Submit assignment...", Toast.LENGTH_SHORT).show();
        });

        // Toggle completed section
        LinearLayout completedContent = view.findViewById(R.id.completed_content);
        ImageView chevron = view.findViewById(R.id.icon_chevron_completed);

        view.findViewById(R.id.btn_toggle_completed).setOnClickListener(v -> {
            isCompletedExpanded = !isCompletedExpanded;
            if (isCompletedExpanded) {
                completedContent.setVisibility(View.VISIBLE);
                chevron.animate().rotation(270).setDuration(200).start();
            } else {
                completedContent.setVisibility(View.GONE);
                chevron.animate().rotation(90).setDuration(200).start();
            }
        });
    }
}
