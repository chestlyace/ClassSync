package com.example.classsync.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;
import com.example.classsync.data.UserSession;
import com.example.classsync.data.firebase.GroupRepository;

public class AddEditTaskFragment extends Fragment {

    private String courseId;
    private String assignmentId;
    private String groupId;

    private GroupRepository groupRepository;
    private UserSession userSession;

    private EditText inputTaskName;
    private EditText inputNotes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseId = getArguments().getString("courseId", "");
            assignmentId = getArguments().getString("assignmentId", "");
            groupId = getArguments().getString("groupId", "");
        }
        groupRepository = new GroupRepository();
        userSession = new UserSession(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_edit_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputTaskName = view.findViewById(R.id.input_task_name);
        inputNotes = view.findViewById(R.id.input_notes);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        view.findViewById(R.id.btn_save_task).setOnClickListener(v -> {
            saveTask();
        });

        view.findViewById(R.id.btn_delete_task).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });
    }

    private void saveTask() {
        String title = inputTaskName.getText().toString().trim();
        if (title.isEmpty()) {
            inputTaskName.setError("Task name is required");
            return;
        }

        String notes = inputNotes.getText().toString().trim();
        String uid = userSession.getUserUid();
        String name = userSession.getUserName();

        groupRepository.addTask(courseId, assignmentId, groupId,
                title, uid, name,
                notes.isEmpty() ? null : notes,
                null);

        Toast.makeText(requireContext(), "Task added", Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(this).popBackStack();
    }
}
