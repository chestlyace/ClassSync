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
import com.example.classsync.data.cache.DataCache;
import com.example.classsync.data.firebase.FirestorePaths;
import com.example.classsync.data.model.Assignment;
import com.example.classsync.data.model.Course;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherHomeFragment extends Fragment {

    private FirebaseFirestore firestore;
    private UserSession userSession;
    private String teacherId;
    private DataCache dataCache;

    private ListenerRegistration coursesListener;
    private final Map<String, ListenerRegistration> assignmentListeners = new HashMap<>();

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
        dataCache = DataCache.getInstance();

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

        // Set dynamic greeting with entrance animation
        setDynamicGreeting();
        animateGreeting();

        // Load avatar with fade-in animation
        String avatarUrl = userSession.getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_profile)
                    .circleCrop()
                    .into(teacherAvatar);
        }

        // Animate FAB entrance
        fabAddCourse.setAlpha(0f);
        fabAddCourse.setTranslationY(50f);
        fabAddCourse.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay(400)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();
    }

    private void animateGreeting() {
        tvGreeting.setAlpha(0f);
        tvGreeting.setTranslationY(20f);
        tvGreeting.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();

        tvCourseCount.setAlpha(0f);
        tvCourseCount.setTranslationY(20f);
        tvCourseCount.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay(100)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();
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
        showLoadingState();
        animateStatsEntrance();

        // Populate from cache immediately while listener syncs
        List<Course> cachedCourses = dataCache.getCollection("courses_teacher_" + teacherId);
        if (cachedCourses != null && !cachedCourses.isEmpty()) {
            populateCourseData(cachedCourses);
        }

        // Listen for courses in real-time
        coursesListener = firestore.collection("courses")
                .whereEqualTo("teacherId", teacherId)
                .whereEqualTo("isArchived", false)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        showEmptyState();
                        return;
                    }
                    if (querySnapshot == null || querySnapshot.isEmpty()) {
                        showEmptyState();
                        return;
                    }

                    List<Course> courses = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Course course = doc.toObject(Course.class);
                        course.setCourseId(doc.getId());
                        courses.add(course);
                    }
                    dataCache.putCollection("courses_teacher_" + teacherId, courses);

                    populateCourseData(courses);
                    updateAssignmentListeners(courses);
                });
    }

    private void populateCourseData(List<Course> courses) {
        courseDataList.clear();

        int totalStudents = 0;
        for (Course course : courses) {
            totalStudents += (course.getStudentIds() != null ? course.getStudentIds().size() : 0);
        }

        animateTextChange(tvCourseCount, courses.size() + (courses.size() == 1 ? " active course" : " active courses"));
        animateCountUp(tvTotalStudents, 0, totalStudents, 800);

        // Build course data from cache if available
        boolean hasCachedData = false;
        for (Course course : courses) {
            String cacheKey = FirestorePaths.assignmentsCollection(course.getCourseId());
            List<Assignment> cachedAssignments = dataCache.getCollection(cacheKey);
            if (cachedAssignments != null) {
                hasCachedData = true;
                int dueSoon = countDueSoon(cachedAssignments);
                courseDataList.add(new CourseData(course, cachedAssignments.size(),
                        calculateCompletionPercentage(cachedAssignments.size()), dueSoon));
            } else {
                courseDataList.add(new CourseData(course, 0, 0, 0));
            }
        }
        courseAdapter.notifyDataSetChanged();
        updateStatistics();

        emptyState.setVisibility(View.GONE);
        rvCourses.setVisibility(View.VISIBLE);

        // If no cached data, listeners will populate when data arrives
        if (!hasCachedData) {
            courseDataList.clear();
            for (Course course : courses) {
                courseDataList.add(new CourseData(course, 0, 0, 0));
            }
            courseAdapter.notifyDataSetChanged();
        }
    }

    private void updateAssignmentListeners(List<Course> courses) {
        Timestamp now = Timestamp.now();
        Timestamp in48Hours = new Timestamp(now.getSeconds() + (48 * 60 * 60), 0);

        // Remove listeners for removed courses
        List<String> currentIds = new ArrayList<>();
        for (Course c : courses) currentIds.add(c.getCourseId());
        for (String id : new HashMap<>(assignmentListeners).keySet()) {
            if (!currentIds.contains(id)) {
                ListenerRegistration reg = assignmentListeners.remove(id);
                if (reg != null) reg.remove();
            }
        }

        // Add listeners for each course's assignments
        for (Course course : courses) {
            String courseId = course.getCourseId();
            if (!assignmentListeners.containsKey(courseId)) {
                ListenerRegistration reg = firestore
                        .collection("courses").document(courseId)
                        .collection("assignments")
                        .addSnapshotListener((snapshots, e) -> {
                            if (e != null || snapshots == null) return;

                            List<Assignment> assignments = new ArrayList<>();
                            int dueSoon = 0;
                            for (QueryDocumentSnapshot doc : snapshots) {
                                Assignment a = doc.toObject(Assignment.class);
                                if (a != null) {
                                    a.setAssignmentId(doc.getId());
                                    assignments.add(a);
                                    if (a.getDueDate() != null
                                            && a.getDueDate().compareTo(now) > 0
                                            && a.getDueDate().compareTo(in48Hours) <= 0) {
                                        dueSoon++;
                                    }
                                }
                            }

                            String cacheKey = FirestorePaths.assignmentsCollection(courseId);
                            dataCache.putCollection(cacheKey, assignments);

                            // Update this course's data in the list
                            for (int i = 0; i < courseDataList.size(); i++) {
                                if (courseDataList.get(i).getCourse().getCourseId().equals(courseId)) {
                                    courseDataList.set(i, new CourseData(course,
                                            assignments.size(),
                                            calculateCompletionPercentage(assignments.size()),
                                            dueSoon));
                                    break;
                                }
                            }
                            courseAdapter.notifyDataSetChanged();
                            updateStatistics();
                        });
                assignmentListeners.put(courseId, reg);
            }
        }
    }

    private int countDueSoon(List<Assignment> assignments) {
        Timestamp now = Timestamp.now();
        Timestamp in48Hours = new Timestamp(now.getSeconds() + (48 * 60 * 60), 0);
        int count = 0;
        for (Assignment a : assignments) {
            if (a.getDueDate() != null
                    && a.getDueDate().compareTo(now) > 0
                    && a.getDueDate().compareTo(in48Hours) <= 0) {
                count++;
            }
        }
        return count;
    }

    private void showLoadingState() {
        rvCourses.setVisibility(View.VISIBLE);
    }

    private void animateStatsEntrance() {
        View statsContainer = requireView().findViewById(R.id.stats_container);
        if (statsContainer != null) {
            statsContainer.setAlpha(0f);
            statsContainer.setTranslationY(30f);
            statsContainer.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(200)
                    .setInterpolator(new android.view.animation.DecelerateInterpolator())
                    .start();
        }
    }

    private void animateTextChange(TextView textView, String newText) {
        textView.animate()
                .alpha(0f)
                .setDuration(150)
                .withEndAction(() -> {
                    textView.setText(newText);
                    textView.animate()
                            .alpha(1f)
                            .setDuration(150)
                            .start();
                })
                .start();
    }

    private void animateCountUp(TextView textView, int start, int end, long duration) {
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofInt(start, end);
        animator.setDuration(duration);
        animator.setInterpolator(new android.view.animation.DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            textView.setText(String.valueOf(animation.getAnimatedValue()));
        });
        animator.setStartDelay(300);
        animator.start();
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

    @Override
    public void onDestroyView() {
        if (coursesListener != null) coursesListener.remove();
        for (ListenerRegistration reg : assignmentListeners.values()) {
            if (reg != null) reg.remove();
        }
        assignmentListeners.clear();
        super.onDestroyView();
    }

}
