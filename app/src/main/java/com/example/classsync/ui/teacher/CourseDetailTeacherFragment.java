package com.example.classsync.ui.teacher;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
        
        // Navigation - Back button
        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        // Navigation - Assignment card clicks
        View.OnClickListener assignmentClickListener = v -> {
            NavHostFragment.findNavController(this).navigate(R.id.assignmentOverviewTeacherFragment);
        };
        view.findViewById(R.id.assignment_card_1).setOnClickListener(assignmentClickListener);
        view.findViewById(R.id.assignment_card_2).setOnClickListener(assignmentClickListener);
        view.findViewById(R.id.assignment_card_3).setOnClickListener(assignmentClickListener);
        
        // Navigation - Add assignment buttons
        View.OnClickListener addAssignmentListener = v -> {
            NavHostFragment.findNavController(this).navigate(R.id.createAssignmentFragment);
        };
        view.findViewById(R.id.fab_add_assignment).setOnClickListener(addAssignmentListener);
        view.findViewById(R.id.btn_add_assignment_small).setOnClickListener(addAssignmentListener);

        // Join Code Copy functionality
        ImageButton copyBtn = view.findViewById(R.id.btn_copy_code);
        copyBtn.setOnClickListener(v -> {
            // Copy code to clipboard
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Join Code", "CS-8294");
            clipboard.setPrimaryClip(clip);

            // Visual feedback: switch icon to check
            copyBtn.setImageResource(R.drawable.ic_check);
            Toast.makeText(requireContext(), "Code copied!", Toast.LENGTH_SHORT).show();

            // Revert icon after 2 seconds
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (isAdded()) {
                    copyBtn.setImageResource(R.drawable.ic_content_copy);
                }
            }, 2000);
        });


    }
}
