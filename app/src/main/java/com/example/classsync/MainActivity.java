package com.example.classsync;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.classsync.data.UserSession;
import com.example.classsync.data.firebase.AuthRepository;
import com.example.classsync.notification.NotificationHelper;
import com.example.classsync.worker.DeadlineReminderWorker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationHelper.createNotificationChannels(this);
        scheduleDeadlineReminders();
        requestNotificationPermission();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            bottomNav = findViewById(R.id.bottom_navigation);

            // Use a custom item selected listener for role-aware navigation
            bottomNav.setOnItemSelectedListener(item -> {
                UserSession session = new UserSession(this);
                boolean isTeacher = AuthRepository.ROLE_TEACHER.equals(session.getUserRole());
                int itemId = item.getItemId();

                // Build NavOptions to pop back to the role-appropriate home destination
                // This prevents stacking fragments when switching tabs
                int homeDestId = isTeacher ? R.id.nav_home : R.id.studentHomeFragment;
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(homeDestId, false)
                        .setLaunchSingleTop(true)
                        .build();

                if (itemId == R.id.nav_home) {
                    navController.navigate(homeDestId, null, navOptions);
                    return true;
                } else if (itemId == R.id.nav_courses) {
                    if (isTeacher) {
                        // Teachers go to their course list
                        navController.navigate(R.id.teacherCourseListFragment, null, navOptions);
                    } else {
                        navController.navigate(R.id.nav_courses, null, navOptions);
                    }
                    return true;
                } else if (itemId == R.id.nav_notifications) {
                    navController.navigate(R.id.nav_notifications, null, navOptions);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    navController.navigate(R.id.nav_profile, null, navOptions);
                    return true;
                }
                return false;
            });

            // Prevent re-navigation when tapping the already-selected tab
            bottomNav.setOnItemReselectedListener(item -> {
                // No-op: already on this destination, do nothing
            });

            // Manage bottom nav visibility and highlight the correct tab
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int id = destination.getId();

                // Hide bottom nav on auth screens
                if (id == R.id.splashFragment || id == R.id.loginFragment || id == R.id.registerFragment) {
                    bottomNav.setVisibility(View.GONE);
                    return;
                }

                // Manage bottom nav active indicator colors dynamically
                UserSession session = new UserSession(this);
                boolean isTeacher = AuthRepository.ROLE_TEACHER.equals(session.getUserRole());
                if (isTeacher) {
                    bottomNav.setItemActiveIndicatorColor(
                            androidx.core.content.ContextCompat.getColorStateList(this, R.color.secondary_container));
                } else {
                    bottomNav.setItemActiveIndicatorColor(
                            androidx.core.content.ContextCompat.getColorStateList(this, R.color.primary_fixed));
                }

                bottomNav.setVisibility(View.VISIBLE);

                // Sync the selected tab highlight with the current destination
                // This ensures the correct tab is highlighted even when navigating
                // from within fragments (e.g. back button, deep links)
                if (id == R.id.nav_home || id == R.id.studentHomeFragment) {
                    setSelectedItemSilently(R.id.nav_home);
                } else if (id == R.id.nav_courses || id == R.id.courseDetailTeacherFragment
                        || id == R.id.teacherCourseListFragment
                        || id == R.id.assignmentDetailStudentFragment
                        || id == R.id.groupLobbyFragment || id == R.id.groupWorkspaceFragment
                        || id == R.id.addEditTaskFragment
                        || id == R.id.assignmentOverviewTeacherFragment
                        || id == R.id.createAssignmentFragment) {
                    setSelectedItemSilently(R.id.nav_courses);
                } else if (id == R.id.nav_notifications) {
                    setSelectedItemSilently(R.id.nav_notifications);
                } else if (id == R.id.nav_profile) {
                    setSelectedItemSilently(R.id.nav_profile);
                }
            });
        }
    }

    /**
     * Sets the selected bottom nav item without triggering the
     * OnItemSelectedListener.
     * This prevents infinite navigation loops when syncing highlight state.
     */
    private void setSelectedItemSilently(int itemId) {
        if (bottomNav.getSelectedItemId() != itemId) {
            // Temporarily remove the listener to avoid re-triggering navigation
            bottomNav.setOnItemSelectedListener(null);
            bottomNav.setSelectedItemId(itemId);

            // Re-attach the listener
            bottomNav.setOnItemSelectedListener(item -> {
                UserSession session = new UserSession(this);
                boolean isTeacher = AuthRepository.ROLE_TEACHER.equals(session.getUserRole());
                int selectedId = item.getItemId();
                int homeDestId = isTeacher ? R.id.nav_home : R.id.studentHomeFragment;
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(homeDestId, false)
                        .setLaunchSingleTop(true)
                        .build();

                if (selectedId == R.id.nav_home) {
                    navController.navigate(homeDestId, null, navOptions);
                    return true;
                } else if (selectedId == R.id.nav_courses) {
                    if (isTeacher) {
                        navController.navigate(R.id.teacherCourseListFragment, null, navOptions);
                    } else {
                        navController.navigate(R.id.nav_courses, null, navOptions);
                    }
                    return true;
                } else if (selectedId == R.id.nav_notifications) {
                    navController.navigate(R.id.nav_notifications, null, navOptions);
                    return true;
                } else if (selectedId == R.id.nav_profile) {
                    navController.navigate(R.id.nav_profile, null, navOptions);
                    return true;
                }
                return false;
            });
        }
    }

    private void scheduleDeadlineReminders() {
        PeriodicWorkRequest reminderWork = new PeriodicWorkRequest.Builder(
                DeadlineReminderWorker.class, 15, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "deadline_reminders",
                ExistingPeriodicWorkPolicy.KEEP,
                reminderWork
        );
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
