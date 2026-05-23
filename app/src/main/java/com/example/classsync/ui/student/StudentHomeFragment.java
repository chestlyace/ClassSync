package com.example.classsync.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;

public class StudentHomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Header Actions
        view.findViewById(R.id.btn_settings).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
        });

        // Filter Chips Toggle Logic
        TextView chipAll = view.findViewById(R.id.chip_all);
        TextView chipToday = view.findViewById(R.id.chip_today);
        TextView chipWeek = view.findViewById(R.id.chip_week);
        TextView chipCourse = view.findViewById(R.id.chip_course);

        TextView[] chips = {chipAll, chipToday, chipWeek, chipCourse};

        View.OnClickListener chipClickListener = v -> {
            for (TextView chip : chips) {
                chip.setBackgroundResource(R.drawable.bg_chip_inactive);
                chip.setTextColor(requireContext().getColor(R.color.on_surface));
            }
            TextView clickedChip = (TextView) v;
            clickedChip.setBackgroundResource(R.drawable.bg_chip_active);
            clickedChip.setTextColor(requireContext().getColor(R.color.on_primary));
        };

        for (TextView chip : chips) {
            chip.setOnClickListener(chipClickListener);
        }

        // Card Navigation
        View.OnClickListener cardClickListener = v -> {
            // Navigate to detail view (assuming assignmentDetailStudentFragment exists)
            // NavHostFragment.findNavController(this).navigate(R.id.assignmentDetailStudentFragment);
            Toast.makeText(requireContext(), "Opening assignment...", Toast.LENGTH_SHORT).show();
        };

        view.findViewById(R.id.card_overdue).setOnClickListener(cardClickListener);
        view.findViewById(R.id.card_today).setOnClickListener(cardClickListener);
        view.findViewById(R.id.card_this_week).setOnClickListener(cardClickListener);
        view.findViewById(R.id.card_upcoming).setOnClickListener(cardClickListener);


    }
}
