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

public class AssignmentDetailStudentFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_assignment_detail_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        view.findViewById(R.id.btn_settings).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.btn_group_lobby).setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("courseId", courseId);
            args.putString("assignmentId", assignmentId);
            args.putString("assignmentTitle", assignmentTitle);
            args.putInt("maxGroupSize", maxGroupSize);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.groupLobbyFragment, args);
        });

        view.findViewById(R.id.btn_submit_assignment).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Submit clicked", Toast.LENGTH_SHORT).show();
        });

        String uid = userSession.getUserUid();
        if (!uid.isEmpty()) {
            groupRepository.checkStudentGroupStatus(courseId, assignmentId, uid,
                    new GroupRepository.StudentGroupCallback() {
                        @Override
                        public void onAlreadyInGroup(@NonNull String groupId,
                                                     @NonNull String groupName,
                                                     boolean isLeader) {
                            Bundle args = new Bundle();
                            args.putString("courseId", courseId);
                            args.putString("assignmentId", assignmentId);
                            args.putString("assignmentTitle", assignmentTitle);
                            args.putString("groupId", groupId);
                            args.putString("groupName", groupName);
                            args.putBoolean("isLeader", isLeader);
                            NavHostFragment.findNavController(AssignmentDetailStudentFragment.this)
                                    .navigate(R.id.groupWorkspaceFragment, args);
                        }

                        @Override
                        public void onNotInGroup() {
                            View btn = getView() != null
                                    ? getView().findViewById(R.id.btn_group_lobby)
                                    : null;
                            if (btn != null) {
                                btn.setVisibility(View.VISIBLE);
                            }
                        }
                    }
            );
        }
    }
}
