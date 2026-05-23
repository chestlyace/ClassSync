package com.example.classsync.ui.teacher;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;

public class TeacherCourseListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_course_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Header Actions
        view.findViewById(R.id.btn_settings).setOnClickListener(v -> {
            // Placeholder for settings
        });

        // Search Functionality
        EditText searchInput = view.findViewById(R.id.search_input);
        View card1 = view.findViewById(R.id.course_card_1);
        View card2 = view.findViewById(R.id.course_card_2);
        View card3 = view.findViewById(R.id.course_card_3);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase();
                
                // Simple search logic
                card1.setVisibility("advanced calculus ii".contains(query) ? View.VISIBLE : View.GONE);
                card2.setVisibility("intro to psychology".contains(query) ? View.VISIBLE : View.GONE);
                card3.setVisibility("quantum mechanics".contains(query) ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Course Navigation
        View.OnClickListener courseClickListener = v -> {
            NavHostFragment.findNavController(this).navigate(R.id.courseDetailTeacherFragment);
        };

        card1.setOnClickListener(courseClickListener);
        card2.setOnClickListener(courseClickListener);
        card3.setOnClickListener(courseClickListener);

        // Add Course FAB
        view.findViewById(R.id.fab_add_course).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.createCourseFragment);
        });
    }
}
