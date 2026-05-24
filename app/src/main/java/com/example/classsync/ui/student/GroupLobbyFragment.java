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

import com.example.classsync.R;
import com.example.classsync.data.UserSession;
import com.example.classsync.data.firebase.GroupRepository;

public class GroupLobbyFragment extends Fragment {

    private String courseId;
    private String assignmentId;
    private String assignmentTitle;
    private int maxGroupSize;

    private GroupRepository groupRepository;
    private UserSession userSession;

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_lobby, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        view.findViewById(R.id.btn_create_group).setOnClickListener(v -> {
            String uid = userSession.getUserUid();
            String name = userSession.getUserName();
            if (uid.isEmpty()) return;

            groupRepository.createGroup(courseId, assignmentId, maxGroupSize,
                    uid, name,
                    groupName -> {
                        Toast.makeText(requireContext(),
                                "You created " + groupName, Toast.LENGTH_SHORT).show();
                        navigateToWorkspace(groupName);
                    },
                    error -> {
                        Toast.makeText(requireContext(),
                                "Failed to create group. Try again.", Toast.LENGTH_SHORT).show();
                    }
            );
        });

        view.findViewById(R.id.btn_join_alpha).setOnClickListener(v -> {
            joinGroup();
        });

        view.findViewById(R.id.group_card_zeta).setOnClickListener(v -> {
            navigateToWorkspace("My Group");
        });
    }

    private void joinGroup() {
        String uid = userSession.getUserUid();
        String name = userSession.getUserName();
        if (uid.isEmpty()) return;

        groupRepository.joinGroup(courseId, assignmentId, maxGroupSize,
                uid, name,
                groupName -> {
                    Toast.makeText(requireContext(),
                            "You joined " + groupName, Toast.LENGTH_SHORT).show();
                    navigateToWorkspace(groupName);
                },
                error -> {
                    Toast.makeText(requireContext(),
                            "Failed to join group. Try again.", Toast.LENGTH_SHORT).show();
                }
        );
    }

    private void navigateToWorkspace(String groupName) {
        Bundle args = new Bundle();
        args.putString("courseId", courseId);
        args.putString("assignmentId", assignmentId);
        args.putString("assignmentTitle", assignmentTitle);
        args.putString("groupName", groupName);
        NavHostFragment.findNavController(this)
                .navigate(R.id.groupWorkspaceFragment, args);
    }
}
