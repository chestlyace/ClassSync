package com.example.classsync.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classsync.R;
import com.example.classsync.data.firebase.FirestorePaths;
import com.example.classsync.data.model.Assignment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StudentHomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private StudentAssignmentsAdapter adapter;
    private List<Assignment> allAssignments;
    private com.google.firebase.firestore.ListenerRegistration courseListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allAssignments = new ArrayList<>();

        recyclerView = view.findViewById(R.id.assignment_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new StudentAssignmentsAdapter(allAssignments, assignment -> {
            Bundle args = new Bundle();
            args.putString("assignmentId", assignment.getAssignmentId());
            args.putString("courseId", assignment.getCourseId());
            NavHostFragment.findNavController(this)
                    .navigate(R.id.assignmentDetailStudentFragment, args);
        });
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.btn_settings).setOnClickListener(v ->
                Toast.makeText(requireContext(), "Settings clicked", Toast.LENGTH_SHORT).show());

        listenForStudentCourses();
    }

    private void listenForStudentCourses() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        courseListener = FirebaseFirestore.getInstance()
                .collection(FirestorePaths.COURSES)
                .whereArrayContains("studentIds", uid)
                .whereEqualTo("isArchived", false)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;

                    List<String> courseIds = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        courseIds.add(doc.getId());
                    }
                    fetchAllAssignmentsForStudent(courseIds);
                });
    }

    private void fetchAllAssignmentsForStudent(List<String> courseIds) {
        if (courseIds.isEmpty()) {
            allAssignments.clear();
            adapter.notifyDataSetChanged();
            return;
        }

        List<Assignment> merged = new ArrayList<>();
        AtomicInteger remaining = new AtomicInteger(courseIds.size());

        for (String courseId : courseIds) {
            FirebaseFirestore.getInstance()
                    .collection(FirestorePaths.COURSES)
                    .document(courseId)
                    .collection(FirestorePaths.ASSIGNMENTS)
                    .orderBy("dueDate", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener(snapshots -> {
                        for (QueryDocumentSnapshot doc : snapshots) {
                            merged.add(doc.toObject(Assignment.class));
                        }
                        if (remaining.decrementAndGet() == 0) {
                            Collections.sort(merged,
                                    Comparator.comparing(a -> a.getDueDate() != null ? a.getDueDate() : new com.google.firebase.Timestamp(0, 0)));
                            allAssignments.clear();
                            allAssignments.addAll(merged);
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    @Override
    public void onDestroyView() {
        if (courseListener != null) {
            courseListener.remove();
        }
        super.onDestroyView();
    }
}
