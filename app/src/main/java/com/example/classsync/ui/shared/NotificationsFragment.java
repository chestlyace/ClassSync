package com.example.classsync.ui.shared;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classsync.R;
import com.example.classsync.data.UserSession;
import com.example.classsync.data.firebase.AuthRepository;
import com.example.classsync.data.firebase.FirestorePaths;
import com.example.classsync.data.model.AppNotification;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private boolean isTeacher = false;
    private int themeColor;

    private TextView tabAll, tabDeadlines, tabTasks;
    private RecyclerView recycler;
    private View emptyState;

    private final List<AppNotification> allNotifications = new ArrayList<>();
    private final List<AppNotification> filteredNotifications = new ArrayList<>();
    private NotificationAdapter adapter;
    private ListenerRegistration listener;

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

        UserSession session = new UserSession(requireContext());
        isTeacher = AuthRepository.ROLE_TEACHER.equals(session.getUserRole());
        themeColor = ContextCompat.getColor(requireContext(),
                isTeacher ? R.color.secondary : R.color.primary);

        view.<TextView>findViewById(R.id.app_title).setTextColor(themeColor);
        view.<TextView>findViewById(R.id.btn_mark_all_read).setTextColor(themeColor);

        tabAll = view.findViewById(R.id.tab_all);
        tabDeadlines = view.findViewById(R.id.tab_deadlines);
        tabTasks = view.findViewById(R.id.tab_tasks);

        recycler = view.findViewById(R.id.notifications_recycler);
        emptyState = view.findViewById(R.id.empty_state_container);

        view.findViewById(R.id.btn_settings).setOnClickListener(v ->
                Toast.makeText(requireContext(), "Settings clicked", Toast.LENGTH_SHORT).show());

        tabAll.setOnClickListener(v -> selectTab(ActiveTab.ALL));
        tabDeadlines.setOnClickListener(v -> selectTab(ActiveTab.DEADLINES));
        tabTasks.setOnClickListener(v -> selectTab(ActiveTab.TASKS));

        view.findViewById(R.id.btn_mark_all_read).setOnClickListener(v -> {
            markAllRead();
        });

        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new NotificationAdapter(filteredNotifications,
                notification -> markRead(notification));
        recycler.setAdapter(adapter);

        updateTabStyles();
        listenForNotifications();
    }

    private void listenForNotifications() {
        String uid = new UserSession(requireContext()).getUserUid();
        if (uid.isEmpty()) return;

        listener = FirebaseFirestore.getInstance()
                .collection(FirestorePaths.NOTIFICATIONS)
                .whereEqualTo("recipientId", uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(50)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;

                    allNotifications.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        AppNotification notif = doc.toObject(AppNotification.class);
                        if (notif != null) {
                            notif.setNotificationId(doc.getId());
                            allNotifications.add(notif);
                        }
                    }
                    applyFilter();
                });
    }

    private void selectTab(ActiveTab tab) {
        currentTab = tab;
        updateTabStyles();
        applyFilter();
    }

    private void updateTabStyles() {
        resetTab(tabAll);
        resetTab(tabDeadlines);
        resetTab(tabTasks);

        TextView active = currentTab == ActiveTab.ALL ? tabAll
                : currentTab == ActiveTab.DEADLINES ? tabDeadlines : tabTasks;
        active.setBackgroundResource(R.drawable.bg_segmented_active);
        active.setTextColor(themeColor);
        active.setElevation(4f);
    }

    private void resetTab(TextView tab) {
        tab.setBackgroundResource(R.drawable.bg_segmented_button_inactive);
        tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));
        tab.setElevation(0);
    }

    private void applyFilter() {
        filteredNotifications.clear();
        for (AppNotification notif : allNotifications) {
            switch (currentTab) {
                case ALL:
                    filteredNotifications.add(notif);
                    break;
                case DEADLINES:
                    if ("deadline_reminder".equals(notif.getType())) {
                        filteredNotifications.add(notif);
                    }
                    break;
                case TASKS:
                    if ("task_done".equals(notif.getType())) {
                        filteredNotifications.add(notif);
                    }
                    break;
            }
        }
        adapter.notifyDataSetChanged();

        boolean empty = filteredNotifications.isEmpty();
        recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    private void markRead(AppNotification notification) {
        if (notification.isRead()) return;
        FirebaseFirestore.getInstance()
                .collection(FirestorePaths.NOTIFICATIONS)
                .document(notification.getNotificationId())
                .update("isRead", true);
    }

    private void markAllRead() {
        String uid = new UserSession(requireContext()).getUserUid();
        if (uid.isEmpty()) return;

        FirebaseFirestore.getInstance()
                .collection(FirestorePaths.NOTIFICATIONS)
                .whereEqualTo("recipientId", uid)
                .whereEqualTo("isRead", false)
                .get()
                .addOnSuccessListener(snapshots -> {
                    WriteBatch batch = FirebaseFirestore.getInstance().batch();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        batch.update(doc.getReference(), "isRead", true);
                    }
                    batch.commit();
                    Toast.makeText(requireContext(), "All marked as read", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        if (listener != null) listener.remove();
        super.onDestroyView();
    }
}
