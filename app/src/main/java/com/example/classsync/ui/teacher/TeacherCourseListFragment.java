package com.example.classsync.ui.teacher;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TeacherCourseListFragment extends Fragment {

    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private List<Course> allCourses;
    private List<Course> filteredCourses;
    private TextView courseCountText;
    private com.google.firebase.firestore.ListenerRegistration listenerRegistration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_course_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allCourses = new ArrayList<>();
        filteredCourses = new ArrayList<>();

        courseCountText = view.findViewById(R.id.tv_course_count);

        recyclerView = view.findViewById(R.id.course_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CourseAdapter(filteredCourses, course -> {
            Bundle args = new Bundle();
            args.putString("courseId", course.getCourseId());
            args.putString("courseName", course.getName());
            NavHostFragment.findNavController(this)
                    .navigate(R.id.courseDetailTeacherFragment, args);
        });
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.btn_settings).setOnClickListener(v -> {
        });

        EditText searchInput = view.findViewById(R.id.search_input);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCourses(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        view.findViewById(R.id.fab_add_course).setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.createCourseFragment));

        listenForCourses();
    }

    private void listenForCourses() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        listenerRegistration = FirebaseFirestore.getInstance()
                .collection(FirestorePaths.COURSES)
                .whereEqualTo("teacherId", uid)
                .whereEqualTo("isArchived", false)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;

                    allCourses.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Course course = doc.toObject(Course.class);
                        allCourses.add(course);
                    }
                    filterCourses("");
                });
    }

    private void filterCourses(String query) {
        filteredCourses.clear();
        if (TextUtils.isEmpty(query)) {
            filteredCourses.addAll(allCourses);
        } else {
            String q = query.toLowerCase();
            for (Course course : allCourses) {
                if (course.getName().toLowerCase().contains(q)
                        || course.getDescription().toLowerCase().contains(q)) {
                    filteredCourses.add(course);
                }
            }
        }
        courseCountText.setText(filteredCourses.size() + " Course" + (filteredCourses.size() == 1 ? "" : "s") + " Total");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
        super.onDestroyView();
    }
}
