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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classsync.R;
import com.example.classsync.data.firebase.FirestorePaths;
import com.example.classsync.data.model.Assignment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CourseDetailTeacherFragment extends Fragment {

    private String courseId;
    private String courseName;
    private String joinCode;

    private RecyclerView recyclerView;
    private AssignmentAdapter adapter;
    private List<Assignment> assignments;
    private TextView topBarTitle;
    private TextView courseTitleText;
    private TextView courseDescText;
    private TextView studentCountText;
    private TextView joinCodeText;
    private com.google.firebase.firestore.ListenerRegistration listenerRegistration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_detail_teacher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            courseId = getArguments().getString("courseId", "");
            courseName = getArguments().getString("courseName", "");
        }

        assignments = new ArrayList<>();

        topBarTitle = view.findViewById(R.id.top_bar_title);
        courseTitleText = view.findViewById(R.id.course_title_text);
        courseDescText = view.findViewById(R.id.course_desc_text);
        studentCountText = view.findViewById(R.id.student_count_text);
        joinCodeText = view.findViewById(R.id.join_code_text);

        if (!courseName.isEmpty()) {
            topBarTitle.setText(courseName);
            courseTitleText.setText(courseName);
        }

        recyclerView = view.findViewById(R.id.assignment_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AssignmentAdapter(assignments, assignment -> {
            Bundle args = new Bundle();
            args.putString("courseId", courseId);
            args.putString("assignmentId", assignment.getAssignmentId());
            args.putString("assignmentTitle", assignment.getTitle());
            NavHostFragment.findNavController(this)
                    .navigate(R.id.assignmentOverviewTeacherFragment, args);
        });
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp());

        View.OnClickListener addAssignmentListener = v -> {
            Bundle args = new Bundle();
            args.putString("courseId", courseId);
            args.putString("courseName", courseName);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.createAssignmentFragment, args);
        };
        view.findViewById(R.id.fab_add_assignment).setOnClickListener(addAssignmentListener);
        view.findViewById(R.id.btn_add_assignment_small).setOnClickListener(addAssignmentListener);

        ImageButton copyBtn = view.findViewById(R.id.btn_copy_code);
        copyBtn.setOnClickListener(v -> copyJoinCode());

        loadCourseDetails();
        listenForAssignments();
    }

    private void loadCourseDetails() {
        FirebaseFirestore.getInstance()
                .collection(FirestorePaths.COURSES)
                .document(courseId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    String desc = doc.getString("description");
                    if (desc != null) courseDescText.setText(desc);

                    joinCode = doc.getString("joinCode");
                    if (joinCode != null) joinCodeText.setText(joinCode);

                    List<String> studentIds = (List<String>) doc.get("studentIds");
                    int count = studentIds != null ? studentIds.size() : 0;
                    studentCountText.setText(count + " Student" + (count == 1 ? "" : "s"));

                    String name = doc.getString("name");
                    if (name != null && courseName.isEmpty()) {
                        courseTitleText.setText(name);
                    }
                });
    }

    private void listenForAssignments() {
        listenerRegistration = FirebaseFirestore.getInstance()
                .collection(FirestorePaths.COURSES)
                .document(courseId)
                .collection(FirestorePaths.ASSIGNMENTS)
                .orderBy("dueDate", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;

                    assignments.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        assignments.add(doc.toObject(Assignment.class));
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void copyJoinCode() {
        if (joinCode == null) return;

        ClipboardManager clipboard = (ClipboardManager) requireContext()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Join Code", joinCode);
        clipboard.setPrimaryClip(clip);

        ImageButton copyBtn = getView() != null
                ? getView().findViewById(R.id.btn_copy_code) : null;
        if (copyBtn != null) {
            copyBtn.setImageResource(R.drawable.ic_check);
            Toast.makeText(requireContext(), "Code copied!", Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (isAdded() && getView() != null) {
                    copyBtn.setImageResource(R.drawable.ic_content_copy);
                }
            }, 2000);
        }
    }

    @Override
    public void onDestroyView() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
        super.onDestroyView();
    }
}
