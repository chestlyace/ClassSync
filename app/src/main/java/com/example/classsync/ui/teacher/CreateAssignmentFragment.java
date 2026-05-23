package com.example.classsync.ui.teacher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;

public class CreateAssignmentFragment extends Fragment {

    private int stepperCount = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_assignment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Header actions
        view.findViewById(R.id.btn_close).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });
        view.findViewById(R.id.btn_save).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Draft saved", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).popBackStack();
        });

        // Due date picker placeholder
        view.findViewById(R.id.btn_due_date).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Open Date Picker", Toast.LENGTH_SHORT).show();
        });

        // Segmented Control Logic
        Button btnIndividual = view.findViewById(R.id.btn_type_individual);
        Button btnGroup = view.findViewById(R.id.btn_type_group);
        LinearLayout groupSection = view.findViewById(R.id.group_section);

        btnIndividual.setOnClickListener(v -> {
            btnIndividual.setBackgroundResource(R.drawable.bg_segmented_button_active);
            btnIndividual.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_secondary));

            btnGroup.setBackgroundResource(R.drawable.bg_segmented_button_inactive);
            btnGroup.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));

            groupSection.setVisibility(View.GONE);
        });

        btnGroup.setOnClickListener(v -> {
            btnGroup.setBackgroundResource(R.drawable.bg_segmented_button_active);
            btnGroup.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_secondary));

            btnIndividual.setBackgroundResource(R.drawable.bg_segmented_button_inactive);
            btnIndividual.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));

            groupSection.setVisibility(View.VISIBLE);
        });

        // Group Size Stepper
        TextView textStepperValue = view.findViewById(R.id.text_stepper_value);
        view.findViewById(R.id.btn_stepper_minus).setOnClickListener(v -> {
            stepperCount = Math.max(2, stepperCount - 1);
            textStepperValue.setText(String.valueOf(stepperCount));
        });
        view.findViewById(R.id.btn_stepper_plus).setOnClickListener(v -> {
            stepperCount = Math.min(10, stepperCount + 1);
            textStepperValue.setText(String.valueOf(stepperCount));
        });

        // Attachment Button
        view.findViewById(R.id.btn_add_attachment).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Open file picker", Toast.LENGTH_SHORT).show();
        });

        // Post Button
        view.findViewById(R.id.btn_post).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });
    }
}
