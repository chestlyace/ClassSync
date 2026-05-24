package com.example.classsync.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.classsync.data.firebase.FirestorePaths;
import com.example.classsync.data.firebase.GroupRepository;
import com.example.classsync.data.model.TaskItem;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

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

    private FirebaseFirestore db;
    private UserSession userSession;
    private GroupRepository groupRepository;

    private final List<TaskItem> tasks = new ArrayList<>();
    private TaskItemAdapter taskAdapter;
    private ListenerRegistration taskListener;

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

        view.<TextView>findViewById(R.id.group_name_text).setText(
                !groupName.isEmpty() ? groupName : "Group");

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

        determineRoles();
        listenForTasks();
    }

    private void determineRoles() {
        String currentUid = userSession.getUserUid();
        if (currentUid.isEmpty() || groupId.isEmpty()) return;

        db.collection(FirestorePaths.COURSES).document(courseId)
                .collection(FirestorePaths.ASSIGNMENTS).document(assignmentId)
                .collection(FirestorePaths.GROUPS).document(groupId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) return;

                    String leaderId = snapshot.getString("leaderId");
                    boolean isLeader = currentUid.equals(leaderId);

                    renameButton.setVisibility(isLeader ? View.VISIBLE : View.GONE);
                });
    }

    private void listenForTasks() {
        taskListener = db
                .collection(FirestorePaths.COURSES).document(courseId)
                .collection(FirestorePaths.ASSIGNMENTS).document(assignmentId)
                .collection(FirestorePaths.GROUPS).document(groupId)
                .collection(FirestorePaths.TASKS)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;

                    tasks.clear();
                    int completedCount = 0;
                    for (QueryDocumentSnapshot doc : snapshots) {
                        TaskItem task = doc.toObject(TaskItem.class);
                        if (task != null) {
                            tasks.add(task);
                            if (task.isDone()) completedCount++;
                        }
                    }
                    taskAdapter.notifyDataSetChanged();

                    int total = tasks.size();
                    progressText.setText(completedCount + " of " + total + " tasks completed");
                    progressBar.setMax(Math.max(total, 1));
                    progressBar.setProgress(completedCount);
                });
    }

    @Override
    public void onDestroyView() {
        if (taskListener != null) {
            taskListener.remove();
        }
        super.onDestroyView();
    }
}
