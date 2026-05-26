package com.example.classsync.ui.teacher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.example.classsync.data.model.Group;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssignmentOverviewTeacherFragment extends Fragment {

    private String courseId;
    private String assignmentId;
    private String assignmentTitle;

    private TextView titleText;
    private TextView statusBadge;
    private TextView descriptionText;
    private TextView dueDateText;
    private TextView groupsProgressText;
    private ProgressBar groupsProgressBar;
    private RecyclerView groupsRecycler;

    private final List<Group> groups = new ArrayList<>();
    private TeacherGroupAdapter adapter;
    private ListenerRegistration groupListener;
    private Assignment currentAssignment;
    private DataCache dataCache;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseId = getArguments().getString("courseId", "");
            assignmentId = getArguments().getString("assignmentId", "");
            assignmentTitle = getArguments().getString("assignmentTitle", "");
        }
        dataCache = DataCache.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_assignment_overview_teacher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleText = view.findViewById(R.id.title_text);
        statusBadge = view.findViewById(R.id.status_badge);
        descriptionText = view.findViewById(R.id.description_text);
        dueDateText = view.findViewById(R.id.due_date_text);
        groupsProgressText = view.findViewById(R.id.groups_progress_text);
        groupsProgressBar = view.findViewById(R.id.groups_progress_bar);
        groupsRecycler = view.findViewById(R.id.groups_recycler);

        titleText.setText(!assignmentTitle.isEmpty() ? assignmentTitle : "Assignment");

        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp());

        groupsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TeacherGroupAdapter(groups);
        groupsRecycler.setAdapter(adapter);

        loadAssignmentDetails();
        listenForGroups();
    }

    private void loadAssignmentDetails() {
        String docPath = FirestorePaths.assignmentDocument(courseId, assignmentId);

        // Populate from cache immediately
        Assignment cached = dataCache.getDocument(docPath);
        if (cached != null) {
            populateAssignmentDetails(cached);
        }

        // Fetch fresh data from Firestore
        FirebaseFirestore.getInstance()
                .collection(FirestorePaths.COURSES)
                .document(courseId)
                .collection(FirestorePaths.ASSIGNMENTS)
                .document(assignmentId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    currentAssignment = doc.toObject(Assignment.class);
                    if (currentAssignment == null) return;

                    dataCache.putDocument(docPath, currentAssignment);
                    populateAssignmentDetails(currentAssignment);
                });
    }

    private void populateAssignmentDetails(Assignment assignment) {
        String desc = assignment.getDescription();
        if (desc != null && !desc.isEmpty()) {
            descriptionText.setText(desc);
        }

        Timestamp due = assignment.getDueDate();
        if (due != null) {
            Date date = due.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.US);
            dueDateText.setText(sdf.format(date));

            if (date.before(new Date())) {
                statusBadge.setText("Overdue");
                statusBadge.setBackgroundResource(R.drawable.bg_badge_overdue);
            } else {
                statusBadge.setText("Upcoming");
                statusBadge.setBackgroundResource(R.drawable.bg_badge_upcoming);
            }
        }
    }

    private void listenForGroups() {
        groupListener = FirebaseFirestore.getInstance()
                .collection(FirestorePaths.COURSES)
                .document(courseId)
                .collection(FirestorePaths.ASSIGNMENTS)
                .document(assignmentId)
                .collection(FirestorePaths.GROUPS)
                .orderBy("groupNumber", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;

                    groups.clear();
                    int totalStudents = 0;
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Group group = doc.toObject(Group.class);
                        if (group != null) {
                            groups.add(group);
                            if (group.getMemberIds() != null) {
                                totalStudents += group.getMemberIds().size();
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();

                    int groupCount = groups.size();
                    groupsProgressText.setText(groups.size() + " group" + (groupCount == 1 ? "" : "s") + " \u00b7 " + totalStudents + " student" + (totalStudents == 1 ? "" : "s"));
                    groupsProgressBar.setProgress(0);
                });
    }

    @Override
    public void onDestroyView() {
        if (groupListener != null) {
            groupListener.remove();
        }
        super.onDestroyView();
    }
}
