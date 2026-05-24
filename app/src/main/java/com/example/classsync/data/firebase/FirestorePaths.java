package com.example.classsync.data.firebase;

import androidx.annotation.NonNull;

public final class FirestorePaths {
    public static final String USERS = "users";
    public static final String COURSES = "courses";
    public static final String ASSIGNMENTS = "assignments";
    public static final String GROUPS = "groups";
    public static final String TASKS = "tasks";
    public static final String NOTIFICATIONS = "notifications";

    private FirestorePaths() {
    }

    @NonNull
    public static String userDocument(@NonNull String uid) {
        return USERS + "/" + uid;
    }

    @NonNull
    public static String courseDocument(@NonNull String courseId) {
        return COURSES + "/" + courseId;
    }

    @NonNull
    public static String assignmentsCollection(@NonNull String courseId) {
        return courseDocument(courseId) + "/" + ASSIGNMENTS;
    }

    @NonNull
    public static String assignmentDocument(@NonNull String courseId, @NonNull String assignmentId) {
        return assignmentsCollection(courseId) + "/" + assignmentId;
    }

    @NonNull
    public static String groupsCollection(@NonNull String courseId, @NonNull String assignmentId) {
        return assignmentDocument(courseId, assignmentId) + "/" + GROUPS;
    }

    @NonNull
    public static String groupDocument(
            @NonNull String courseId,
            @NonNull String assignmentId,
            @NonNull String groupId
    ) {
        return groupsCollection(courseId, assignmentId) + "/" + groupId;
    }

    @NonNull
    public static String tasksCollection(
            @NonNull String courseId,
            @NonNull String assignmentId,
            @NonNull String groupId
    ) {
        return groupDocument(courseId, assignmentId, groupId) + "/" + TASKS;
    }

    @NonNull
    public static String taskDocument(
            @NonNull String courseId,
            @NonNull String assignmentId,
            @NonNull String groupId,
            @NonNull String taskId
    ) {
        return tasksCollection(courseId, assignmentId, groupId) + "/" + taskId;
    }

    @NonNull
    public static String notificationDocument(@NonNull String notificationId) {
        return NOTIFICATIONS + "/" + notificationId;
    }
}
