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
import com.example.classsync.data.firebase.GroupRepository;

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
                        checkGroupStatus();
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

        checkGroupStatus();
    }

    private void checkGroupStatus() {
        String uid = userSession.getUserUid();
        if (uid.isEmpty()) return;

        groupRepository.checkStudentGroupStatus(courseId, assignmentId, uid,
                new GroupRepository.StudentGroupCallback() {
                    @Override
                    public void onAlreadyInGroup(@NonNull String gid,
                                                 @NonNull String gname,
                                                 boolean leader) {
                        groupId = gid;
                        groupName = gname;
                        isLeader = leader;

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

                    @Override
                    public void onNotInGroup() {
                        statusTitle.setText("Not in a group yet");
                        statusSubtitle.setText("This assignment requires a group of 3-4 members. Join an existing group or create a new one to begin.");
                        statusIcon.setImageResource(R.drawable.ic_group_add);
                        btnJoinGroup.setVisibility(View.VISIBLE);
                        btnViewGroup.setVisibility(View.GONE);
                    }
                }
        );
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
