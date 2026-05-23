package com.example.classsync.ui.shared;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.classsync.R;
import com.example.classsync.data.UserSession;

public class NotificationsFragment extends Fragment {

    private boolean isTeacher = false;
    private int themeColor;
    private int themeContainerColor;

    private TextView tabAll, tabDeadlines, tabTasks;
    private TextView btnMarkAllRead, appTitle;

    // Rows & separators
    private LinearLayout rowDeadlineUnread, rowAssignmentUnread, rowTaskRead, rowDeadlineRead, rowTaskRead2;
    private View sep1, sep2, sep3, sep4;
    private View accentUnread1, accentUnread2;
    private FrameLayout iconBgAssignment;
    private ImageView iconAssignment;

    private LinearLayout notificationsListContainer;
    private LinearLayout emptyStateContainer;

    private enum ActiveTab { ALL, DEADLINES, TASKS }
    private ActiveTab currentTab = ActiveTab.ALL;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Fetch User Session for Role Color Scheme
        UserSession session = new UserSession(requireContext());
        isTeacher = "TEACHER".equals(session.getUserRole());

        // Setup colors based on role (Teacher: green, Student: blue)
        themeColor = ContextCompat.getColor(requireContext(), isTeacher ? R.color.secondary : R.color.primary);
        themeContainerColor = ContextCompat.getColor(requireContext(), isTeacher ? R.color.secondary_container : R.color.primary_fixed);

        // Bind Views
        appTitle = view.findViewById(R.id.app_title);
        btnMarkAllRead = view.findViewById(R.id.btn_mark_all_read);

        tabAll = view.findViewById(R.id.tab_all);
        tabDeadlines = view.findViewById(R.id.tab_deadlines);
        tabTasks = view.findViewById(R.id.tab_tasks);

        rowDeadlineUnread = view.findViewById(R.id.row_deadline_unread);
        rowAssignmentUnread = view.findViewById(R.id.row_assignment_unread);
        rowTaskRead = view.findViewById(R.id.row_task_read);
        rowDeadlineRead = view.findViewById(R.id.row_deadline_read);
        rowTaskRead2 = view.findViewById(R.id.row_task_read_2);

        sep1 = view.findViewById(R.id.sep_1);
        sep2 = view.findViewById(R.id.sep_2);
        sep3 = view.findViewById(R.id.sep_3);
        sep4 = view.findViewById(R.id.sep_4);

        accentUnread1 = view.findViewById(R.id.accent_unread_1);
        accentUnread2 = view.findViewById(R.id.accent_unread_2);

        iconBgAssignment = view.findViewById(R.id.icon_bg_assignment);
        iconAssignment = view.findViewById(R.id.icon_assignment);

        notificationsListContainer = view.findViewById(R.id.notifications_list_container);
        emptyStateContainer = view.findViewById(R.id.empty_state_container);

        // Apply Dynamic Theme Colors
        appTitle.setTextColor(themeColor);
        btnMarkAllRead.setTextColor(themeColor);
        accentUnread1.setBackgroundColor(themeColor);
        accentUnread2.setBackgroundColor(themeColor);

        // Dynamic tints for the new assignment notification icon
        iconBgAssignment.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), isTeacher ? R.color.secondary_container : R.color.primary_fixed));
        iconAssignment.setImageTintList(ContextCompat.getColorStateList(requireContext(), isTeacher ? R.color.secondary : R.color.primary));

        // Settings Button Action
        view.findViewById(R.id.btn_settings).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
        });

        // Setup filter tab click listeners
        tabAll.setOnClickListener(v -> selectTab(ActiveTab.ALL));
        tabDeadlines.setOnClickListener(v -> selectTab(ActiveTab.DEADLINES));
        tabTasks.setOnClickListener(v -> selectTab(ActiveTab.TASKS));

        // Unread Row 1 Click (Mark read)
        rowDeadlineUnread.setOnClickListener(v -> {
            markRead(rowDeadlineUnread, accentUnread1);
        });

        // Unread Row 2 Click (Mark read)
        rowAssignmentUnread.setOnClickListener(v -> {
            markRead(rowAssignmentUnread, accentUnread2);
            // also dim the assignment icon container
            iconBgAssignment.setAlpha(0.6f);
        });

        // Other rows click (feedback toast)
        View.OnClickListener standardRowListener = v -> {
            Toast.makeText(requireContext(), "Notification details...", Toast.LENGTH_SHORT).show();
        };
        rowTaskRead.setOnClickListener(standardRowListener);
        rowDeadlineRead.setOnClickListener(standardRowListener);
        rowTaskRead2.setOnClickListener(standardRowListener);

        // Mark All as Read button
        btnMarkAllRead.setOnClickListener(v -> {
            markRead(rowDeadlineUnread, accentUnread1);
            markRead(rowAssignmentUnread, accentUnread2);
            iconBgAssignment.setAlpha(0.6f);
            Toast.makeText(requireContext(), "All notifications marked as read", Toast.LENGTH_SHORT).show();
        });

        // Initial tab styling
        updateTabStyles();
    }

    private void markRead(LinearLayout row, View accentView) {
        row.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.surface_container_lowest));
        accentView.setVisibility(View.GONE);
    }

    private void selectTab(ActiveTab selectedTab) {
        currentTab = selectedTab;
        updateTabStyles();
        applyFilter();
    }

    private void updateTabStyles() {
        // Reset all tabs to inactive
        tabAll.setBackgroundResource(R.drawable.bg_segmented_button_inactive);
        tabAll.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));
        tabAll.setElevation(0);

        tabDeadlines.setBackgroundResource(R.drawable.bg_segmented_button_inactive);
        tabDeadlines.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));
        tabDeadlines.setElevation(0);

        tabTasks.setBackgroundResource(R.drawable.bg_segmented_button_inactive);
        tabTasks.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));
        tabTasks.setElevation(0);

        // Apply active state
        TextView activeTabTextView = null;
        switch (currentTab) {
            case ALL:
                activeTabTextView = tabAll;
                break;
            case DEADLINES:
                activeTabTextView = tabDeadlines;
                break;
            case TASKS:
                activeTabTextView = tabTasks;
                break;
        }

        if (activeTabTextView != null) {
            activeTabTextView.setBackgroundResource(R.drawable.bg_segmented_active);
            activeTabTextView.setTextColor(themeColor);
            activeTabTextView.setElevation(4f); // add slight shadow/elevation
        }
    }

    private void applyFilter() {
        int visibleCount = 0;

        switch (currentTab) {
            case ALL:
                rowDeadlineUnread.setVisibility(View.VISIBLE);
                sep1.setVisibility(View.VISIBLE);
                rowAssignmentUnread.setVisibility(View.VISIBLE);
                sep2.setVisibility(View.VISIBLE);
                rowTaskRead.setVisibility(View.VISIBLE);
                sep3.setVisibility(View.VISIBLE);
                rowDeadlineRead.setVisibility(View.VISIBLE);
                sep4.setVisibility(View.VISIBLE);
                rowTaskRead2.setVisibility(View.VISIBLE);
                visibleCount = 5;
                break;

            case DEADLINES:
                rowDeadlineUnread.setVisibility(View.VISIBLE);
                sep1.setVisibility(View.GONE);
                rowAssignmentUnread.setVisibility(View.GONE);
                sep2.setVisibility(View.GONE);
                rowTaskRead.setVisibility(View.GONE);
                sep3.setVisibility(View.GONE);
                rowDeadlineRead.setVisibility(View.VISIBLE);
                sep4.setVisibility(View.GONE);
                rowTaskRead2.setVisibility(View.GONE);
                visibleCount = 2;
                break;

            case TASKS:
                rowDeadlineUnread.setVisibility(View.GONE);
                sep1.setVisibility(View.GONE);
                rowAssignmentUnread.setVisibility(View.GONE);
                sep2.setVisibility(View.GONE);
                rowTaskRead.setVisibility(View.VISIBLE);
                sep3.setVisibility(View.GONE);
                rowDeadlineRead.setVisibility(View.GONE);
                sep4.setVisibility(View.GONE);
                rowTaskRead2.setVisibility(View.VISIBLE);
                visibleCount = 2;
                break;
        }

        // Handle empty state (though in our mock, visibleCount will always be > 0)
        if (visibleCount == 0) {
            notificationsListContainer.setVisibility(View.GONE);
            emptyStateContainer.setVisibility(View.VISIBLE);
        } else {
            notificationsListContainer.setVisibility(View.VISIBLE);
            emptyStateContainer.setVisibility(View.GONE);
        }
    }
}
