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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;
import com.example.classsync.data.UserSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CreateCourseFragment extends Fragment {

    private EditText nameInput;
    private EditText descriptionInput;
    private TextView joinCodeText;
    private Button createButton;
    private ImageButton copyButton;
    private String generatedJoinCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_course, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameInput = view.findViewById(R.id.course_name_input);
        descriptionInput = view.findViewById(R.id.course_description_input);
        joinCodeText = view.findViewById(R.id.join_code_text);
        createButton = view.findViewById(R.id.btn_create);
        copyButton = view.findViewById(R.id.btn_copy_code);

        generatedJoinCode = generateJoinCode();
        joinCodeText.setText(generatedJoinCode);

        createButton.setOnClickListener(v -> createCourse());

        view.findViewById(R.id.btn_cancel).setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack());

        view.findViewById(R.id.scrim).setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack());

        copyButton.setOnClickListener(v -> copyJoinCode());
    }

    private String generateJoinCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

    private void createCourse() {
        String name = nameInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.setError("Course name is required");
            return;
        }

        createButton.setEnabled(false);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserSession session = new UserSession(requireContext());
        String teacherName = session.getUserName();

        String courseId = FirebaseFirestore.getInstance().collection("courses").document().getId();

        Map<String, Object> course = new HashMap<>();
        course.put("courseId", courseId);
        course.put("name", name);
        course.put("description", description);
        course.put("teacherId", uid);
        course.put("teacherName", teacherName);
        course.put("joinCode", generatedJoinCode);
        course.put("studentIds", new ArrayList<>());
        course.put("isArchived", false);
        course.put("createdAt", FieldValue.serverTimestamp());

        FirebaseFirestore.getInstance().collection("courses")
                .document(courseId)
                .set(course)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(requireContext(),
                            "Course created! Share code: " + generatedJoinCode,
                            Toast.LENGTH_LONG).show();
                    NavHostFragment.findNavController(this).popBackStack();
                })
                .addOnFailureListener(error -> {
                    createButton.setEnabled(true);
                    Toast.makeText(requireContext(),
                            "Failed to create course: " + error.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void copyJoinCode() {
        ClipboardManager clipboard = (ClipboardManager) requireContext()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Join Code", generatedJoinCode);
        clipboard.setPrimaryClip(clip);

        copyButton.setImageResource(R.drawable.ic_check);
        copyButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.secondary));
        Toast.makeText(requireContext(), "Code copied!", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded()) {
                copyButton.setImageResource(R.drawable.ic_content_copy);
                copyButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));
            }
        }, 2000);
    }
}
