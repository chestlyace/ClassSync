package com.example.classsync.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;
import com.example.classsync.data.UserSession;
import com.example.classsync.data.cache.DataCache;
import com.example.classsync.data.firebase.FirestorePaths;
import com.example.classsync.data.firebase.GroupRepository;
import com.example.classsync.data.model.Assignment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AssignmentDetailStudentFragment extends Fragment {

    private String courseId;
    private String assignmentId;
    private String assignmentTitle;
    private int maxGroupSize;

    private String groupId;
    private String groupName;
    private boolean isLeader;
    private boolean justJoined;

    private GroupRepository groupRepository;
    private UserSession userSession;
    private FirebaseFirestore db;
    private DataCache dataCache;
    private ListenerRegistration groupStatusListener;

    private TextView tvCourseName, tvAssignmentTitleAppbar, tvAssignmentTitleMain;
    private TextView tvTypeBadge, tvDescription, tvPostedByName, tvDueDate, tvDeadlineCountdown;
    private ImageView imgTypeIcon;

    private Button btnJoinGroup, btnViewGroup;
    private TextView statusTitle, statusSubtitle;
    private ImageView statusIcon;
    private LinearLayout statusBanner;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseId = getArguments().getString("courseId", "");
            assignmentId = getArguments().getString("assignmentId", "");
            assignmentTitle = getArguments().getString("assignmentTitle", "");
            maxGroupSize = getArguments().getInt("maxGroupSize", 4);
        }
        groupRepository = new GroupRepository();
        userSession = new UserSession(requireContext());
        db = FirebaseFirestore.getInstance();
        dataCache = DataCache.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_assignment_detail_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        statusBanner = view.findViewById(R.id.group_status_banner);
        statusTitle = view.findViewById(R.id.group_status_title);
        statusSubtitle = view.findViewById(R.id.group_status_subtitle);
        statusIcon = view.findViewById(R.id.group_status_icon);

        btnJoinGroup = view.findViewById(R.id.btn_group_lobby);
        btnViewGroup = view.findViewById(R.id.btn_view_group);

        tvCourseName = view.findViewById(R.id.tv_course_name);
        tvAssignmentTitleAppbar = view.findViewById(R.id.tv_assignment_title_appbar);
        tvAssignmentTitleMain = view.findViewById(R.id.tv_assignment_title_main);
        tvTypeBadge = view.findViewById(R.id.tv_type_badge);
        tvDescription = view.findViewById(R.id.tv_description);
        tvPostedByName = view.findViewById(R.id.tv_posted_by_name);
        tvDueDate = view.findViewById(R.id.tv_due_date);
        tvDeadlineCountdown = view.findViewById(R.id.tv_deadline_countdown);
        imgTypeIcon = view.findViewById(R.id.img_type_icon);

        loadAssignmentData();

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        view.findViewById(R.id.btn_settings).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
        });

        btnJoinGroup.setOnClickListener(v -> {
            String uid = userSession.getUserUid();
            String name = userSession.getUserName();
            if (uid.isEmpty() || name.isEmpty()) return;

            btnJoinGroup.setEnabled(false);
            btnJoinGroup.setText("Joining...");

            groupRepository.joinGroup(courseId, assignmentId, maxGroupSize,
                    uid, name,
                    joinedGroupName -> {
                        justJoined = true;
                    },
                    error -> {
                        btnJoinGroup.setEnabled(true);
                        btnJoinGroup.setText("Join group");
                        Toast.makeText(requireContext(),
                                "Failed to join group. Try again.", Toast.LENGTH_SHORT).show();
                    }
            );
        });

        btnViewGroup.setOnClickListener(v -> {
            navigateToWorkspace();
        });

        view.findViewById(R.id.btn_submit_assignment).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Submit clicked", Toast.LENGTH_SHORT).show();
        });

        listenForGroupStatus();
    }

    private void listenForGroupStatus() {
        String uid = userSession.getUserUid();
        if (uid.isEmpty()) return;

        String cacheKey = "group_status_" + courseId + "_" + assignmentId + "_" + uid;

        // Populate from cache immediately
        Map<String, Object> cached = dataCache.getDocument(cacheKey);
        if (cached != null) {
            applyGroupStatus(
                    (String) cached.get("groupId"),
                    (String) cached.get("groupName"),
                    Boolean.TRUE.equals(cached.get("isLeader"))
            );
        }

        // Listen for real-time updates
        groupStatusListener = groupRepository.listenForGroupStatus(courseId, assignmentId, uid,
                new GroupRepository.StudentGroupCallback() {
                    @Override
                    public void onAlreadyInGroup(@NonNull String gid,
                                                 @NonNull String gname,
                                                 boolean leader) {
                        groupId = gid;
                        groupName = gname;
                        isLeader = leader;

                        // Update cache
                        java.util.HashMap<String, Object> info = new java.util.HashMap<>();
                        info.put("groupId", gid);
                        info.put("groupName", gname);
                        info.put("isLeader", leader);
                        dataCache.putDocument(cacheKey, info);

                        applyGroupStatus(gid, gname, leader);
                    }

                    @Override
                    public void onNotInGroup() {
                        dataCache.invalidate(cacheKey);
                        applyNoGroupStatus();
                    }
                }
        );
    }

    private void applyGroupStatus(@NonNull String gid, @NonNull String gname, boolean leader) {
        statusTitle.setText("You are in " + gname);
        statusSubtitle.setText("Your group is ready. Collaborate with your team and track your progress.");
        statusIcon.setImageResource(R.drawable.ic_groups);
        btnJoinGroup.setVisibility(View.GONE);
        btnViewGroup.setVisibility(View.VISIBLE);

        if (justJoined) {
            justJoined = false;
            navigateToWorkspace();
        }
    }

    private void applyNoGroupStatus() {
        statusTitle.setText("Not in a group yet");
        statusSubtitle.setText("This assignment requires a group of 3-4 members. Join an existing group or create a new one to begin.");
        statusIcon.setImageResource(R.drawable.ic_group_add);
        btnJoinGroup.setVisibility(View.VISIBLE);
        btnViewGroup.setVisibility(View.GONE);
    }

    private void loadAssignmentData() {
        String docPath = FirestorePaths.assignmentDocument(courseId, assignmentId);

        // Populate from cache immediately
        Assignment cached = dataCache.getDocument(docPath);
        if (cached != null) {
            populateAssignmentViews(cached);
            loadTeacherName(cached.getTeacherId());
        }

        // Fetch fresh data from Firestore
        db.document(docPath)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    String title = doc.getString("title");
                    String description = doc.getString("description");
                    String type = doc.getString("type");
                    String courseName = doc.getString("courseName");
                    String teacherId = doc.getString("teacherId");
                    Timestamp dueDate = doc.getTimestamp("dueDate");

                    Assignment fresh = new Assignment();
                    fresh.setAssignmentId(assignmentId);
                    fresh.setTitle(title);
                    fresh.setDescription(description);
                    fresh.setType(type);
                    fresh.setCourseName(courseName);
                    fresh.setTeacherId(teacherId);
                    fresh.setDueDate(dueDate);
                    dataCache.putDocument(docPath, fresh);

                    populateAssignmentViews(fresh);
                    loadTeacherName(teacherId);
                });
    }

    private void loadTeacherName(String teacherId) {
        if (teacherId == null || teacherId.isEmpty()) return;
        String userPath = FirestorePaths.userDocument(teacherId);
        String cachedName = dataCache.getDocument(userPath);
        if (cachedName != null) {
            tvPostedByName.setText(cachedName);
            return;
        }
        db.document(userPath)
                .get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        String teacherName = userDoc.getString("name");
                        if (teacherName != null) {
                            dataCache.putDocument(userPath, teacherName);
                            tvPostedByName.setText(teacherName);
                        }
                    }
                });
    }

    private void populateAssignmentViews(Assignment assignment) {
        if (assignment.getTitle() != null) {
            tvAssignmentTitleAppbar.setText(assignment.getTitle());
            tvAssignmentTitleMain.setText(assignment.getTitle());
        }
        if (assignment.getCourseName() != null) {
            tvCourseName.setText(assignment.getCourseName());
        }
        if (assignment.getDescription() != null) {
            tvDescription.setText(assignment.getDescription());
        }
        if (assignment.getType() != null) {
            boolean isGroup = assignment.getType().equals("group");
            tvTypeBadge.setText(isGroup ? "Group Assignment" : "Individual Assignment");
            imgTypeIcon.setImageResource(isGroup ? R.drawable.ic_groups : R.drawable.ic_person);
        }
        if (assignment.getDueDate() != null) {
            Date date = assignment.getDueDate().toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, hh:mm a", Locale.US);
            tvDueDate.setText(sdf.format(date));
            tvDeadlineCountdown.setText(formatCountdown(date));
        }
    }

    @NonNull
    private String formatCountdown(@NonNull Date dueDate) {
        long now = System.currentTimeMillis();
        long diff = dueDate.getTime() - now;
        if (diff <= 0) return "Due date passed";

        long days = TimeUnit.MILLISECONDS.toDays(diff);
        long hours = TimeUnit.MILLISECONDS.toHours(diff) % 24;

        if (days > 0) {
            return "Due in " + days + " day" + (days > 1 ? "s" : "");
        } else if (hours > 0) {
            return "Due in " + hours + " hour" + (hours > 1 ? "s" : "");
        } else {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            return "Due in " + minutes + " minute" + (minutes > 1 ? "s" : "");
        }
    }

    @Override
    public void onDestroyView() {
        if (groupStatusListener != null) {
            groupStatusListener.remove();
        }
        super.onDestroyView();
    }

    private void navigateToWorkspace() {
        Bundle args = new Bundle();
        args.putString("courseId", courseId);
        args.putString("assignmentId", assignmentId);
        args.putString("assignmentTitle", assignmentTitle);
        args.putString("groupId", groupId);
        args.putString("groupName", groupName);
        args.putBoolean("isLeader", isLeader);
        NavHostFragment.findNavController(this)
                .navigate(R.id.groupWorkspaceFragment, args);
    }
}
