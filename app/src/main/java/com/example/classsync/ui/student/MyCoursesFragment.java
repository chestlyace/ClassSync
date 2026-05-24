package com.example.classsync.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classsync.R;
import com.example.classsync.data.firebase.FirestorePaths;
import com.example.classsync.data.model.Course;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyCoursesFragment extends Fragment {

    private RecyclerView recyclerView;
    private StudentCourseAdapter adapter;
    private List<Course> courses;
    private com.google.firebase.firestore.ListenerRegistration listenerRegistration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_courses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        courses = new ArrayList<>();

        recyclerView = view.findViewById(R.id.course_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new StudentCourseAdapter(courses, course ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.studentCourseAssignmentsFragment));
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.btn_join_course).setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.joinCourseFragment));

        listenForCourses();
    }

    private void listenForCourses() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        listenerRegistration = FirebaseFirestore.getInstance()
                .collection(FirestorePaths.COURSES)
                .whereArrayContains("studentIds", uid)
                .whereEqualTo("isArchived", false)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;

                    courses.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        courses.add(doc.toObject(Course.class));
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onDestroyView() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
        super.onDestroyView();
    }
}
