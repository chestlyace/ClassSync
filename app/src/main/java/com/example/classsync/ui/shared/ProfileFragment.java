package com.example.classsync.ui.shared;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.classsync.R;
import com.example.classsync.data.UserSession;
import com.example.classsync.data.firebase.AuthRepository;

public class ProfileFragment extends Fragment {

    private boolean isTeacher = false;
    private UserSession session;
    private AuthRepository authRepository;

    // Header & Info views
    private ImageButton btnBack, btnSettings;
    private View avatarBorder, avatarBg, avatarImageContainer;
    private TextView avatarInitials;
    private ImageView avatarImage;
    private FrameLayout btnEditAvatar;
    private TextView profileName, profileRole;
    private LinearLayout roleBadge;
    private ImageView roleBadgeIcon;

    // Stats layout views
    private LinearLayout statsStudentContainer;
    private LinearLayout statsTeacherContainer;
    private TextView statStudentCoursesVal, statStudentTasksVal, statStudentAssignmentsVal;
    private TextView statTeacherCoursesVal, statTeacherAssignmentsVal, statTeacherStudentsVal;

    // Menu list views
    private ImageView menuIconProfile, menuIconNotifications, menuIconPassword, menuIconAccessibility;

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onImagePicked);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        session = new UserSession(requireContext());
        authRepository = new AuthRepository(requireContext());
        isTeacher = AuthRepository.ROLE_TEACHER.equals(session.getUserRole());

        // Dynamic theme color selection (Teacher: Green, Student: Blue)
        int themeColorRes = isTeacher ? R.color.secondary : R.color.primary;
        int themeColorStateListRes = isTeacher ? R.color.secondary : R.color.primary;
        int themeContainerColorRes = isTeacher ? R.color.secondary_container : R.color.primary_fixed;

        int themeColor = ContextCompat.getColor(requireContext(), themeColorRes);

        // Bind Views
        btnBack = view.findViewById(R.id.btn_back);
        btnSettings = view.findViewById(R.id.btn_settings);

        avatarBorder = view.findViewById(R.id.avatar_border);
        avatarBg = view.findViewById(R.id.avatar_bg);
        avatarInitials = view.findViewById(R.id.avatar_initials);
        avatarImage = view.findViewById(R.id.avatar_image);
        avatarImageContainer = view.findViewById(R.id.avatar_image_container);
        btnEditAvatar = view.findViewById(R.id.btn_edit_avatar);

        profileName = view.findViewById(R.id.profile_name);
        profileRole = view.findViewById(R.id.profile_role);
        roleBadge = view.findViewById(R.id.role_badge);
        roleBadgeIcon = view.findViewById(R.id.role_badge_icon);

        statsStudentContainer = view.findViewById(R.id.stats_student_container);
        statsTeacherContainer = view.findViewById(R.id.stats_teacher_container);

        statStudentCoursesVal = view.findViewById(R.id.stat_student_courses_val);
        statStudentTasksVal = view.findViewById(R.id.stat_student_tasks_val);
        statStudentAssignmentsVal = view.findViewById(R.id.stat_student_assignments_val);

        statTeacherCoursesVal = view.findViewById(R.id.stat_teacher_courses_val);
        statTeacherAssignmentsVal = view.findViewById(R.id.stat_teacher_assignments_val);
        statTeacherStudentsVal = view.findViewById(R.id.stat_teacher_students_val);

        menuIconProfile = view.findViewById(R.id.menu_icon_profile);
        menuIconNotifications = view.findViewById(R.id.menu_icon_notifications);
        menuIconPassword = view.findViewById(R.id.menu_icon_password);
        menuIconAccessibility = view.findViewById(R.id.menu_icon_accessibility);

        // Setup dynamic profile info & bind stats visibility
        String displayName = session.getUserName().isEmpty()
                ? (isTeacher ? "Teacher" : "Student")
                : session.getUserName();
        String initials = buildInitials(displayName);

        if (isTeacher) {
            profileName.setText(displayName);
            profileRole.setText("Teacher");
            avatarInitials.setText(initials);
            roleBadgeIcon.setImageResource(R.drawable.ic_school);

            statsStudentContainer.setVisibility(View.GONE);
            statsTeacherContainer.setVisibility(View.VISIBLE);
        } else {
            profileName.setText(displayName);
            profileRole.setText("Student");
            avatarInitials.setText(initials);
            roleBadgeIcon.setImageResource(R.drawable.ic_school);

            statsStudentContainer.setVisibility(View.VISIBLE);
            statsTeacherContainer.setVisibility(View.GONE);
        }

        // Load avatar if present
        loadAvatar(session.getAvatarUrl());

        // Apply Dynamic Theme Colors to Header elements
        btnBack.setImageTintList(ContextCompat.getColorStateList(requireContext(), themeColorStateListRes));
        btnSettings.setImageTintList(ContextCompat.getColorStateList(requireContext(), themeColorStateListRes));

        // Apply Dynamic theme colors to Layered Avatar
        avatarBorder.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), themeColorStateListRes));
        avatarBg.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), themeContainerColorRes));
        avatarInitials.setTextColor(themeColor);
        btnEditAvatar.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), themeColorStateListRes));

        // Apply Dynamic theme colors to Role Badge
        roleBadge.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), themeContainerColorRes));
        roleBadgeIcon.setImageTintList(ContextCompat.getColorStateList(requireContext(), themeColorStateListRes));
        profileRole.setTextColor(themeColor);

        // Apply Dynamic theme colors to stats numbers
        statStudentCoursesVal.setTextColor(themeColor);
        statStudentTasksVal.setTextColor(themeColor);
        statStudentAssignmentsVal.setTextColor(themeColor);
        statTeacherCoursesVal.setTextColor(themeColor);
        statTeacherAssignmentsVal.setTextColor(themeColor);
        statTeacherStudentsVal.setTextColor(themeColor);

        // Apply Dynamic theme colors to Menu Icon highlights
        menuIconProfile.setImageTintList(ContextCompat.getColorStateList(requireContext(), themeColorStateListRes));
        menuIconNotifications.setImageTintList(ContextCompat.getColorStateList(requireContext(), themeColorStateListRes));
        menuIconPassword.setImageTintList(ContextCompat.getColorStateList(requireContext(), themeColorStateListRes));
        menuIconAccessibility.setImageTintList(ContextCompat.getColorStateList(requireContext(), themeColorStateListRes));

        // Back / Close Navigation Handler
        btnBack.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        // Settings Button Action
        btnSettings.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
        });

        // Avatar edit button -> pick image
        btnEditAvatar.setOnClickListener(v -> {
            imagePicker.launch("image/*");
        });

        // Setup settings menu actions
        view.findViewById(R.id.btn_edit_profile).setOnClickListener(v -> showEditNameDialog());
        view.findViewById(R.id.btn_notification_prefs).setOnClickListener(menuClickListener);
        view.findViewById(R.id.btn_change_password).setOnClickListener(menuClickListener);
        view.findViewById(R.id.btn_accessibility).setOnClickListener(menuClickListener);

        // Setup Logout Section action
        view.findViewById(R.id.btn_logout).setOnClickListener(v -> {
            authRepository.logout();
            NavHostFragment.findNavController(this).navigate(R.id.loginFragment);
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private final View.OnClickListener menuClickListener = v -> {
        Toast.makeText(requireContext(), "Opening option...", Toast.LENGTH_SHORT).show();
    };

    private void showEditNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit profile name");

        EditText input = new EditText(requireContext());
        input.setText(session.getUserName());
        input.setSelection(input.getText().length());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            authRepository.updateProfileName(newName,
                    () -> {
                        profileName.setText(newName);
                        avatarInitials.setText(buildInitials(newName));
                        Toast.makeText(requireContext(), "Name updated", Toast.LENGTH_SHORT).show();
                    },
                    () -> Toast.makeText(requireContext(), "Failed to update name", Toast.LENGTH_SHORT).show()
            );
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void onImagePicked(Uri imageUri) {
        if (imageUri == null) return;
        authRepository.uploadAvatar(imageUri,
                () -> {
                    loadAvatar(session.getAvatarUrl());
                    Toast.makeText(requireContext(), "Avatar updated", Toast.LENGTH_SHORT).show();
                },
                () -> Toast.makeText(requireContext(), "Failed to upload avatar", Toast.LENGTH_SHORT).show()
        );
    }

    private void loadAvatar(String url) {
        if (url == null || url.isEmpty()) {
            avatarImage.setVisibility(View.GONE);
            avatarInitials.setVisibility(View.VISIBLE);
            return;
        }
        avatarImage.setVisibility(View.VISIBLE);
        avatarInitials.setVisibility(View.GONE);
        Glide.with(this)
                .load(url)
                .transform(new CircleCrop())
                .into(avatarImage);
    }

    @NonNull
    private String buildInitials(@NonNull String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) {
            return "CS";
        }

        String first = parts[0].substring(0, 1).toUpperCase();
        String second = parts.length > 1 ? parts[1].substring(0, 1).toUpperCase() : "";
        return first + second;
    }
}
