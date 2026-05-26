package com.example.classsync.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classsync.R;
import com.example.classsync.data.UserSession;
import com.example.classsync.data.cache.DataCache;
import com.example.classsync.data.firebase.FirestorePaths;
import com.example.classsync.data.firebase.GroupRepository;
import com.example.classsync.data.model.TaskItem;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupWorkspaceFragment extends Fragment {

    private String courseId;
    private String assignmentId;
    private String groupId;
    private String groupName;

    private View renameButton;
    private View fabAddTask;
    private TextView progressText;
    private ProgressBar progressBar;
    private RecyclerView tasksRecycler;
    private LinearLayout membersContainer;
    private TextView groupNameText;

    private FirebaseFirestore db;
    private UserSession userSession;
    private GroupRepository groupRepository;
    private DataCache dataCache;

    private final List<TaskItem> tasks = new ArrayList<>();
    private TaskItemAdapter taskAdapter;
    private ListenerRegistration taskListener;
    private ListenerRegistration groupDocListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseId = getArguments().getString("courseId", "");
            assignmentId = getArguments().getString("assignmentId", "");
            groupId = getArguments().getString("groupId", "");
            groupName = getArguments().getString("groupName", "");
        }
        db = FirebaseFirestore.getInstance();
        userSession = new UserSession(requireContext());
        groupRepository = new GroupRepository();
        dataCache = DataCache.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_workspace, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        renameButton = view.findViewById(R.id.btn_rename_group);
        fabAddTask = view.findViewById(R.id.fab_add_task);
        progressText = view.findViewById(R.id.progress_text);
        progressBar = view.findViewById(R.id.progress_bar);
        tasksRecycler = view.findViewById(R.id.tasks_recycler);
        membersContainer = view.findViewById(R.id.members_container);
        groupNameText = view.findViewById(R.id.group_name_text);

        groupNameText.setText(!groupName.isEmpty() ? groupName : "Group");

        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp());

        view.findViewById(R.id.btn_settings).setOnClickListener(v ->
                Toast.makeText(requireContext(), "Workspace Settings", Toast.LENGTH_SHORT).show());

        fabAddTask.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("courseId", courseId);
            args.putString("assignmentId", assignmentId);
            args.putString("groupId", groupId);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.addEditTaskFragment, args);
        });

        renameButton.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Rename group", Toast.LENGTH_SHORT).show());

        tasksRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        taskAdapter = new TaskItemAdapter(tasks, task ->
                groupRepository.completeTask(courseId, assignmentId, groupId, task.getTaskId()));
        tasksRecycler.setAdapter(taskAdapter);

        listenForGroupDoc();
        listenForTasks();
    }

    private void listenForGroupDoc() {
        String docPath = FirestorePaths.groupDocument(courseId, assignmentId, groupId);

        // Populate from cache immediately
        DocumentSnapshot cached = dataCache.getDocument(docPath);
        if (cached != null && cached.exists()) {
            applyGroupData(cached);
        }

        groupDocListener = db.document(docPath)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null || !snapshot.exists()) return;

                    dataCache.putDocument(docPath, snapshot);
                    applyGroupData(snapshot);
                });
    }

    private void applyGroupData(@NonNull DocumentSnapshot snapshot) {
        // Group name
        String name = snapshot.getString("name");
        if (name != null && !name.isEmpty()) {
            groupNameText.setText(name);
        }

        // Leader check — show rename button only for leader
        String currentUid = userSession.getUserUid();
        String leaderId = snapshot.getString("leaderId");
        boolean isLeader = currentUid.equals(leaderId);
        renameButton.setVisibility(isLeader ? View.VISIBLE : View.GONE);

        // Members
        populateMembers(snapshot);
    }

    @SuppressWarnings("unchecked")
    private void populateMembers(@NonNull DocumentSnapshot snapshot) {
        membersContainer.removeAllViews();

        Map<String, String> memberNames = (Map<String, String>) snapshot.get("memberNames");
        if (memberNames == null) return;

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        int marginPx = (int) (16 * getResources().getDisplayMetrics().density);
        int index = 0;
        for (Map.Entry<String, String> entry : memberNames.entrySet()) {
            String memberName = entry.getValue();

            View memberView = inflater.inflate(R.layout.item_group_member, membersContainer, false);
            TextView nameText = memberView.findViewById(R.id.member_name);
            nameText.setText(memberName);

            if (index > 0) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) memberView.getLayoutParams();
                lp.leftMargin = marginPx;
                memberView.setLayoutParams(lp);
            }

            membersContainer.addView(memberView);
            index++;
        }
    }

    private void listenForTasks() {
        String collectionPath = FirestorePaths.tasksCollection(courseId, assignmentId, groupId);

        // Populate from cache immediately
        List<TaskItem> cached = dataCache.getCollection(collectionPath);
        if (cached != null) {
            tasks.clear();
            tasks.addAll(cached);
            taskAdapter.notifyDataSetChanged();
            updateProgress();
        }

        taskListener = db
                .collection(FirestorePaths.COURSES).document(courseId)
                .collection(FirestorePaths.ASSIGNMENTS).document(assignmentId)
                .collection(FirestorePaths.GROUPS).document(groupId)
                .collection(FirestorePaths.TASKS)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;

                    tasks.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        TaskItem task = doc.toObject(TaskItem.class);
                        if (task != null) {
                            tasks.add(task);
                        }
                    }
                    dataCache.putCollection(collectionPath, new ArrayList<>(tasks));
                    taskAdapter.notifyDataSetChanged();
                    updateProgress();
                });
    }

    private void updateProgress() {
        int total = tasks.size();
        int completedCount = 0;
        for (TaskItem task : tasks) {
            if (task.isDone()) completedCount++;
        }
        progressText.setText(completedCount + " of " + total + " tasks completed");
        progressBar.setMax(Math.max(total, 1));
        progressBar.setProgress(completedCount);
    }

    @Override
    public void onDestroyView() {
        if (taskListener != null) taskListener.remove();
        if (groupDocListener != null) groupDocListener.remove();
        super.onDestroyView();
    }
}
