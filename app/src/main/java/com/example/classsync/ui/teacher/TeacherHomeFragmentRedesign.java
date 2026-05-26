package com.example.classsync.ui.teacher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.classsync.R;
import com.example.classsync.data.UserSession;
import com.example.classsync.data.model.Assignment;
import com.example.classsync.data.model.Course;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TeacherHomeFragmentRedesign extends Fragment {

    private FirebaseFirestore firestore;
    private UserSession userSession;
    private String teacherId;

    // UI Components
    private TextView tvGreeting;
    private TextView tvCourseCount;
    private TextView tvTotalStudents;
    private TextView tvTotalAssignments;
    private TextView tvDueSoon;
    private ImageView teacherAvatar;
    private RecyclerView rvCourses;
    private LinearLayout emptyState;
    private ExtendedFloatingActionButton fabAddCourse;
    private MaterialButton btnAddCourse;

    private ModernCourseAdapter courseAdapter;
    private List<CourseData> courseDataList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_home_redesign, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase and Session
        firestore = FirebaseFirestore.getInstance();
        userSession = new UserSession(requireContext());
        teacherId = userSession.getUserUid();

        // Initialize Views
        initViews(view);
        setupRecyclerView();
        setupClickListeners();

        // Load Data
        loadTeacherData();
    }

    private void initViews(View view) {
        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvCourseCount = view.findViewById(R.id.tv_course_count);
        tvTotalStudents = view.findViewById(R.id.tv_total_students);
        tvTotalAssignments = view.findViewById(R.id.tv_total_assignments);
        tvDueSoon = view.findViewById(R.id.tv_due_soon);
        teacherAvatar = view.findViewById(R.id.teacher_avatar);
        rvCourses = view.findViewById(R.id.rv_courses);
        emptyState = view.findViewById(R.id.empty_state);
        fabAddCourse = view.findViewById(R.id.fab_add_course);
        btnAddCourse = view.findViewById(R.id.btn_add_course);

        // Initialize default texts to avoid empty string/null crashes
        tvCourseCount.setText("0 active courses");
        tvTotalStudents.setText("0");
        tvTotalAssignments.setText("0");
        tvDueSoon.setText("0");

        // Set dynamic greeting
        setDynamicGreeting();

        // Load avatar
        String avatarUrl = userSession.getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_profile)
                    .circleCrop()
                    .into(teacherAvatar);
        }
    }

    private void setupRecyclerView() {
        courseDataList = new ArrayList<>();
        courseAdapter = new ModernCourseAdapter(courseDataList, course -> {
            // Navigate to course detail
            Bundle bundle = new Bundle();
            bundle.putString("courseId", course.getCourse().getCourseId());
            NavHostFragment.findNavController(this)
                    .navigate(R.id.courseDetailTeacherFragment, bundle);
        });

        rvCourses.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCourses.setAdapter(courseAdapter);
    }

    private void setupClickListeners() {
        fabAddCourse.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.createCourseFragment));

        btnAddCourse.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.createCourseFragment));
    }

    private void setDynamicGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hour >= 5 && hour < 12) {
            greeting = "Good morning,";
        } else if (hour >= 12 && hour < 17) {
            greeting = "Good afternoon,";
        } else if (hour >= 17 && hour < 21) {
            greeting = "Good evening,";
        } else {
            greeting = "Working late,";
        }

        String userName = userSession.getUserName();
        if (userName != null && !userName.isEmpty()) {
            tvGreeting.setText(greeting + "\n" + userName);
        } else {
            tvGreeting.setText(greeting + "\nTeacher");
        }
    }

    private void loadTeacherData() {
        // Load all courses for this teacher
        firestore.collection("courses")
                .whereEqualTo("teacherId", teacherId)
                .whereEqualTo("isArchived", false)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        showEmptyState();
                        return;
                    }

                    courseDataList.clear();
                    List<Course> courses = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Course course = doc.toObject(Course.class);
                        course.setCourseId(doc.getId());
                        courses.add(course);
                    }

                    // Update course count
                    int courseCount = courses.size();
                    tvCourseCount.setText(courseCount + (courseCount == 1 ? " active course" : " active courses"));

                    // Calculate total students
                    int totalStudents = 0;
                    for (Course course : courses) {
                        totalStudents += (course.getStudentIds() != null ? course.getStudentIds().size() : 0);
                    }
                    tvTotalStudents.setText(String.valueOf(totalStudents));

                    // Load assignments for each course
                    loadAssignmentsData(courses);

                    // Show courses
                    emptyState.setVisibility(View.GONE);
                    rvCourses.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    showEmptyState();
                });
    }

    private void loadAssignmentsData(List<Course> courses) {
        // Get current time and 48 hours from now
        Timestamp now = Timestamp.now();
        Timestamp in48Hours = new Timestamp(now.getSeconds() + (48 * 60 * 60), 0);

        for (Course course : courses) {
            String courseId = course.getCourseId();

            firestore.collection("courses")
                    .document(courseId)
                    .collection("assignments")
                    .get()
                    .addOnSuccessListener(assignmentSnapshot -> {
                        int assignmentCount = assignmentSnapshot.size();
                        int dueSoon = 0;

                        List<Assignment> assignments = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : assignmentSnapshot) {
                            Assignment assignment = doc.toObject(Assignment.class);
                            assignment.setAssignmentId(doc.getId());
                            assignments.add(assignment);

                            // Check if due soon
                            if (assignment.getDueDate() != null &&
                                    assignment.getDueDate().compareTo(now) > 0 &&
                                    assignment.getDueDate().compareTo(in48Hours) <= 0) {
                                dueSoon++;
                            }
                        }

                        // Calculate completion percentage (mock calculation)
                        int completionPercentage = calculateCompletionPercentage(assignmentCount);

                        // Add to course data list
                        CourseData courseData = new CourseData(
                                course,
                                assignmentCount,
                                completionPercentage,
                                dueSoon
                        );
                        courseDataList.add(courseData);
                        courseAdapter.notifyDataSetChanged();

                        // Update stats (aggregate)
                        updateStatistics();
                    });
        }
    }

    private void updateStatistics() {
        int totalAssignments = 0;
        int totalDueSoon = 0;

        for (CourseData courseData : courseDataList) {
            totalAssignments += courseData.getAssignmentCount();
            totalDueSoon += courseData.getDueSoonCount();
        }

        tvTotalAssignments.setText(String.valueOf(totalAssignments));
        tvDueSoon.setText(String.valueOf(totalDueSoon));
    }

    private int calculateCompletionPercentage(int assignmentCount) {
        // Mock calculation - in real scenario, calculate based on submission data
        if (assignmentCount == 0) return 0;
        return (int) (Math.random() * 30) + 50; // Random between 50-80%
    }

    private void showEmptyState() {
        emptyState.setVisibility(View.VISIBLE);
        rvCourses.setVisibility(View.GONE);
        tvCourseCount.setText("0 active courses");
        tvTotalStudents.setText("0");
        tvTotalAssignments.setText("0");
        tvDueSoon.setText("0");
    }

}
