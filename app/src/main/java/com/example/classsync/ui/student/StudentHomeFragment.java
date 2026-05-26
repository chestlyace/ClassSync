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
import com.example.classsync.data.cache.DataCache;
import com.example.classsync.data.firebase.FirestorePaths;
import com.example.classsync.data.model.Assignment;
// Client-side notification imports (preserved for reference)
// import android.content.Context;
// import com.example.classsync.notification.NotificationHelper;
// import com.google.firebase.firestore.FieldValue;
// import com.google.firebase.Timestamp;
// import java.util.HashMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class StudentHomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private StudentAssignmentsAdapter adapter;
    private List<Assignment> allAssignments;
    private ListenerRegistration courseListener;
    private final Map<String, ListenerRegistration> assignmentListeners = new HashMap<>();
    private final DataCache dataCache = DataCache.getInstance();

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
            args.putString("assignmentTitle", assignment.getTitle());
            args.putInt("maxGroupSize", assignment.getMaxGroupSize());
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
                    updateAssignmentListeners(courseIds);
                    // Client-side notification check (preserved for reference):
                    // checkNewAssignments(courseIds, uid);
                });
    }

    private void updateAssignmentListeners(List<String> courseIds) {
        // Remove listeners for courses no longer in the list
        for (String id : new HashSet<>(assignmentListeners.keySet())) {
            if (!courseIds.contains(id)) {
                ListenerRegistration reg = assignmentListeners.remove(id);
                if (reg != null) reg.remove();
            }
        }

        // Add listeners for new courses
        for (String courseId : courseIds) {
            if (!assignmentListeners.containsKey(courseId)) {
                ListenerRegistration reg = FirebaseFirestore.getInstance()
                        .collection(FirestorePaths.COURSES)
                        .document(courseId)
                        .collection(FirestorePaths.ASSIGNMENTS)
                        .orderBy("dueDate", Query.Direction.ASCENDING)
                        .addSnapshotListener((snapshots, error) -> {
                            if (error != null || snapshots == null) return;

                            List<Assignment> assignments = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : snapshots) {
                                Assignment a = doc.toObject(Assignment.class);
                                if (a != null) assignments.add(a);
                            }
                            dataCache.putCollection(
                                    FirestorePaths.assignmentsCollection(courseId),
                                    assignments);
                            mergeAssignmentsFromAllCourses();
                        });
                assignmentListeners.put(courseId, reg);
            }
        }

        // Populate from cache immediately while listeners sync
        mergeAssignmentsFromAllCourses();
    }

    private void mergeAssignmentsFromAllCourses() {
        List<Assignment> merged = new ArrayList<>();
        for (String courseId : assignmentListeners.keySet()) {
            String cacheKey = FirestorePaths.assignmentsCollection(courseId);
            List<Assignment> cached = dataCache.getCollection(cacheKey);
            if (cached != null) {
                merged.addAll(cached);
            }
        }
        Collections.sort(merged,
                Comparator.comparing(a -> a.getDueDate() != null ? a.getDueDate() : new com.google.firebase.Timestamp(0, 0)));
        allAssignments.clear();
        allAssignments.addAll(merged);
        adapter.notifyDataSetChanged();
    }

    // Client-side notification logic (preserved for reference)
    // private void checkNewAssignments(List<String> courseIds, String uid) {
    //     if (courseIds.isEmpty()) return;
    //
    //     long lastCheck = requireContext()
    //             .getSharedPreferences("classsync_prefs", Context.MODE_PRIVATE)
    //             .getLong("last_assignment_check", 0);
    //
    //     Timestamp since = lastCheck > 0
    //             ? new Timestamp(new java.util.Date(lastCheck))
    //             : new Timestamp(0, 0);
    //
    //     for (String courseId : courseIds) {
    //         FirebaseFirestore.getInstance()
    //                 .collection(FirestorePaths.COURSES)
    //                 .document(courseId)
    //                 .collection(FirestorePaths.ASSIGNMENTS)
    //                 .whereGreaterThan("createdAt", since)
    //                 .get()
    //                 .addOnSuccessListener(snapshots -> {
    //                     for (QueryDocumentSnapshot doc : snapshots) {
    //                         Assignment assignment = doc.toObject(Assignment.class);
    //                         if (assignment == null) continue;
    //
    //                         HashMap<String, Object> notif = new HashMap<>();
    //                         notif.put("recipientId", uid);
    //                         notif.put("type", "new_assignment");
    //                         notif.put("title", "New assignment: " + assignment.getTitle());
    //                         notif.put("body", assignment.getCourseName());
    //                         notif.put("courseId", courseId);
    //                         notif.put("assignmentId", assignment.getAssignmentId());
    //                         notif.put("isRead", false);
    //                         notif.put("createdAt", FieldValue.serverTimestamp());
    //
    //                         FirebaseFirestore.getInstance()
    //                                 .collection(FirestorePaths.NOTIFICATIONS)
    //                                 .add(notif);
    //
    //                         NotificationHelper.showNotification(
    //                                 requireContext(),
    //                                 "classsync_new_assignments",
    //                                 "New assignment: " + assignment.getTitle(),
    //                                 assignment.getCourseName() != null ? assignment.getCourseName() : courseId
    //                         );
    //                     }
    //                 });
    //     }
    //
    //     requireContext()
    //             .getSharedPreferences("classsync_prefs", Context.MODE_PRIVATE)
    //             .edit()
    //             .putLong("last_assignment_check", System.currentTimeMillis())
    //             .apply();
    // }

    @Override
    public void onDestroyView() {
        if (courseListener != null) {
            courseListener.remove();
        }
        for (ListenerRegistration reg : assignmentListeners.values()) {
            if (reg != null) reg.remove();
        }
        assignmentListeners.clear();
        super.onDestroyView();
    }
}
