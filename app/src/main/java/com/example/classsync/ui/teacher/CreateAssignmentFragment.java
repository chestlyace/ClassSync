package com.example.classsync.ui.teacher;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;
import com.example.classsync.data.firebase.FirestorePaths;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateAssignmentFragment extends Fragment {

    private String courseId;
    private String courseName;

    private EditText titleInput;
    private EditText descriptionInput;
    private TextView dueDateText;
    private Button btnIndividual;
    private Button btnGroup;
    private LinearLayout groupSection;
    private TextView stepperValue;
    private Button postButton;
    private Button saveButton;

    private int stepperCount = 2;
    private Date selectedDueDate;
    private boolean isGroupType = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_assignment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            courseId = getArguments().getString("courseId", "");
            courseName = getArguments().getString("courseName", "");
        }

        titleInput = view.findViewById(R.id.input_title);
        descriptionInput = view.findViewById(R.id.input_description);
        dueDateText = view.findViewById(R.id.text_due_date);
        btnIndividual = view.findViewById(R.id.btn_type_individual);
        btnGroup = view.findViewById(R.id.btn_type_group);
        groupSection = view.findViewById(R.id.group_section);
        stepperValue = view.findViewById(R.id.text_stepper_value);
        postButton = view.findViewById(R.id.btn_post);
        saveButton = view.findViewById(R.id.btn_save);

        view.findViewById(R.id.btn_close).setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack());

        saveButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Draft saved", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).popBackStack();
        });

        view.findViewById(R.id.btn_due_date).setOnClickListener(v -> showDatePicker());

        btnIndividual.setOnClickListener(v -> {
            setActiveType(true);
            groupSection.setVisibility(View.GONE);
            isGroupType = false;
        });

        btnGroup.setOnClickListener(v -> {
            setActiveType(false);
            groupSection.setVisibility(View.VISIBLE);
            isGroupType = true;
        });

        view.findViewById(R.id.btn_stepper_minus).setOnClickListener(v -> {
            stepperCount = Math.max(2, stepperCount - 1);
            stepperValue.setText(String.valueOf(stepperCount));
        });

        view.findViewById(R.id.btn_stepper_plus).setOnClickListener(v -> {
            stepperCount = Math.min(10, stepperCount + 1);
            stepperValue.setText(String.valueOf(stepperCount));
        });

        view.findViewById(R.id.btn_add_attachment).setOnClickListener(v ->
                Toast.makeText(requireContext(), "Open file picker", Toast.LENGTH_SHORT).show());

        postButton.setOnClickListener(v -> createAssignment());
    }

    private void setActiveType(boolean individual) {
        if (individual) {
            btnIndividual.setBackgroundResource(R.drawable.bg_segmented_button_active);
            btnIndividual.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_secondary));
            btnGroup.setBackgroundResource(R.drawable.bg_segmented_button_inactive);
            btnGroup.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));
        } else {
            btnGroup.setBackgroundResource(R.drawable.bg_segmented_button_active);
            btnGroup.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_secondary));
            btnIndividual.setBackgroundResource(R.drawable.bg_segmented_button_inactive);
            btnIndividual.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));
        }
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);

            new TimePickerDialog(requireContext(), (timeView, hourOfDay, minute) -> {
                selected.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selected.set(Calendar.MINUTE, minute);
                selectedDueDate = selected.getTime();

                SimpleDateFormat fmt = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US);
                dueDateText.setText(fmt.format(selectedDueDate));
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void createAssignment() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        if (title.isEmpty()) {
            titleInput.setError("Title is required");
            return;
        }
        if (selectedDueDate == null) {
            Toast.makeText(requireContext(), "Please select a due date", Toast.LENGTH_SHORT).show();
            return;
        }

        postButton.setEnabled(false);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String type = isGroupType ? "group" : "individual";

        String assignmentId = FirebaseFirestore.getInstance()
                .collection(FirestorePaths.COURSES)
                .document(courseId)
                .collection(FirestorePaths.ASSIGNMENTS)
                .document().getId();

        Map<String, Object> assignment = new HashMap<>();
        assignment.put("assignmentId", assignmentId);
        assignment.put("title", title);
        assignment.put("description", description);
        assignment.put("dueDate", new Timestamp(selectedDueDate));
        assignment.put("type", type);
        assignment.put("maxGroupSize", isGroupType ? stepperCount : 0);
        assignment.put("courseId", courseId);
        assignment.put("courseName", courseName);
        assignment.put("teacherId", uid);
        assignment.put("createdAt", FieldValue.serverTimestamp());

        FirebaseFirestore.getInstance()
                .collection(FirestorePaths.COURSES)
                .document(courseId)
                .collection(FirestorePaths.ASSIGNMENTS)
                .document(assignmentId)
                .set(assignment)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(requireContext(), "Assignment posted!", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(this).popBackStack();
                })
                .addOnFailureListener(error -> {
                    postButton.setEnabled(true);
                    Toast.makeText(requireContext(),
                            "Failed: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
