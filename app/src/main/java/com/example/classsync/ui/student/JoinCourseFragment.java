package com.example.classsync.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;
import com.example.classsync.data.firebase.FirestorePaths;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class JoinCourseFragment extends Fragment {

    private com.google.android.material.textfield.TextInputEditText joinCodeInput;
    private Button joinButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_join_course, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        joinCodeInput = view.findViewById(R.id.join_code_edit_text);
        joinButton = view.findViewById(R.id.btn_join);

        joinButton.setOnClickListener(v -> joinCourse());
    }

    private void joinCourse() {
        String joinCode = joinCodeInput.getText().toString().trim().toUpperCase();
        if (joinCode.isEmpty()) {
            joinCodeInput.setError("Enter a join code");
            return;
        }

        joinButton.setEnabled(false);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection(FirestorePaths.COURSES)
                .whereEqualTo("joinCode", joinCode)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        joinButton.setEnabled(true);
                        Toast.makeText(requireContext(),
                                "Course not found. Check the join code.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String courseId = querySnapshot.getDocuments().get(0).getId();

                    FirebaseFirestore.getInstance()
                            .collection(FirestorePaths.COURSES)
                            .document(courseId)
                            .update("studentIds", FieldValue.arrayUnion(uid))
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(requireContext(),
                                        "Successfully joined!",
                                        Toast.LENGTH_SHORT).show();
                                NavHostFragment.findNavController(this).popBackStack();
                            })
                            .addOnFailureListener(error -> {
                                joinButton.setEnabled(true);
                                Toast.makeText(requireContext(),
                                        "Failed to join: " + error.getLocalizedMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(error -> {
                    joinButton.setEnabled(true);
                    Toast.makeText(requireContext(),
                            "Error: " + error.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}
