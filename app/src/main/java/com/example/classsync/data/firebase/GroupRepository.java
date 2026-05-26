package com.example.classsync.data.firebase;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.Timestamp;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupRepository {
    private final FirebaseFirestore db;

    public GroupRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void joinGroup(
            @NonNull String courseId,
            @NonNull String assignmentId,
            int maxSize,
            @NonNull String studentUid,
            @NonNull String studentName,
            @NonNull OnSuccessListener<String> onSuccess,
            @NonNull OnFailureListener onFailure
    ) {
        CollectionReference groupsRef = db
                .collection(FirestorePaths.COURSES).document(courseId)
                .collection(FirestorePaths.ASSIGNMENTS).document(assignmentId)
                .collection(FirestorePaths.GROUPS);

        groupsRef.whereEqualTo("isFull", false)
                .orderBy("groupNumber", Query.Direction.ASCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (!querySnapshot.isEmpty()) {
                        DocumentReference openGroupRef =
                                querySnapshot.getDocuments().get(0).getReference();

                        db.runTransaction(transaction -> {
                            DocumentSnapshot groupSnap = transaction.get(openGroupRef);

                            boolean isFull = Boolean.TRUE.equals(groupSnap.getBoolean("isFull"));
                            if (isFull) {
                                throw new FirebaseFirestoreException(
                                        "Group just became full, retry",
                                        FirebaseFirestoreException.Code.ABORTED);
                            }

                            List<String> members = (List<String>) groupSnap.get("memberIds");
                            int currentSize = members != null ? members.size() : 0;
                            boolean willBeFull = (currentSize + 1) >= maxSize;

                            transaction.update(openGroupRef,
                                    "memberIds", FieldValue.arrayUnion(studentUid),
                                    "memberNames." + studentUid, studentName,
                                    "isFull", willBeFull
                            );

                            return groupSnap.getString("name");
                        })
                        .addOnSuccessListener(groupName -> onSuccess.onSuccess(groupName))
                        .addOnFailureListener(e -> {
                            if (e instanceof FirebaseFirestoreException &&
                                    ((FirebaseFirestoreException) e).getCode() ==
                                            FirebaseFirestoreException.Code.ABORTED) {
                                joinGroup(courseId, assignmentId, maxSize,
                                        studentUid, studentName, onSuccess, onFailure);
                            } else {
                                onFailure.onFailure(e);
                            }
                        });

                    } else {
                        groupsRef.orderBy("groupNumber", Query.Direction.DESCENDING)
                                .limit(1)
                                .get()
                                .addOnSuccessListener(lastGroupSnap -> {

                                    int nextNumber = 1;
                                    if (!lastGroupSnap.isEmpty()) {
                                        Long last = lastGroupSnap.getDocuments()
                                                .get(0).getLong("groupNumber");
                                        nextNumber = (last != null ? last.intValue() : 0) + 1;
                                    }

                                    String newGroupId = groupsRef.document().getId();
                                    DocumentReference newGroupRef =
                                            groupsRef.document(newGroupId);

                                    String groupName = "Group " + nextNumber;
                                    boolean startsAsFull = (maxSize == 1);

                                    Map<String, Object> newGroup = new HashMap<>();
                                    newGroup.put("groupId", newGroupId);
                                    newGroup.put("name", groupName);
                                    newGroup.put("groupNumber", nextNumber);
                                    newGroup.put("assignmentId", assignmentId);
                                    newGroup.put("courseId", courseId);
                                    newGroup.put("memberIds", Arrays.asList(studentUid));
                                    newGroup.put("memberNames",
                                            Collections.singletonMap(studentUid, studentName));
                                    newGroup.put("leaderId", studentUid);
                                    newGroup.put("leaderName", studentName);
                                    newGroup.put("maxSize", maxSize);
                                    newGroup.put("isFull", startsAsFull);
                                    newGroup.put("taskCount", 0);
                                    newGroup.put("completedTaskCount", 0);
                                    newGroup.put("createdAt", FieldValue.serverTimestamp());

                                    newGroupRef.set(newGroup)
                                            .addOnSuccessListener(v -> onSuccess.onSuccess(groupName))
                                            .addOnFailureListener(onFailure);
                                })
                                .addOnFailureListener(onFailure);
                    }
                })
                .addOnFailureListener(onFailure);
    }

    public void createGroup(
            @NonNull String courseId,
            @NonNull String assignmentId,
            int maxSize,
            @NonNull String studentUid,
            @NonNull String studentName,
            @NonNull OnSuccessListener<String> onSuccess,
            @NonNull OnFailureListener onFailure
    ) {
        CollectionReference groupsRef = db
                .collection(FirestorePaths.COURSES).document(courseId)
                .collection(FirestorePaths.ASSIGNMENTS).document(assignmentId)
                .collection(FirestorePaths.GROUPS);

        groupsRef.orderBy("groupNumber", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(lastGroupSnap -> {

                    int nextNumber = 1;
                    if (!lastGroupSnap.isEmpty()) {
                        Long last = lastGroupSnap.getDocuments()
                                .get(0).getLong("groupNumber");
                        nextNumber = (last != null ? last.intValue() : 0) + 1;
                    }

                    String newGroupId = groupsRef.document().getId();
                    DocumentReference newGroupRef = groupsRef.document(newGroupId);

                    String groupName = "Group " + nextNumber;
                    boolean startsAsFull = (maxSize == 1);

                    Map<String, Object> newGroup = new HashMap<>();
                    newGroup.put("groupId", newGroupId);
                    newGroup.put("name", groupName);
                    newGroup.put("groupNumber", nextNumber);
                    newGroup.put("assignmentId", assignmentId);
                    newGroup.put("courseId", courseId);
                    newGroup.put("memberIds", Arrays.asList(studentUid));
                    newGroup.put("memberNames",
                            Collections.singletonMap(studentUid, studentName));
                    newGroup.put("leaderId", studentUid);
                    newGroup.put("leaderName", studentName);
                    newGroup.put("maxSize", maxSize);
                    newGroup.put("isFull", startsAsFull);
                    newGroup.put("taskCount", 0);
                    newGroup.put("completedTaskCount", 0);
                    newGroup.put("createdAt", FieldValue.serverTimestamp());

                    newGroupRef.set(newGroup)
                            .addOnSuccessListener(v -> onSuccess.onSuccess(groupName))
                            .addOnFailureListener(onFailure);
                })
                .addOnFailureListener(onFailure);
    }

    public interface StudentGroupCallback {
        void onAlreadyInGroup(@NonNull String groupId, @NonNull String groupName, boolean isLeader);
        void onNotInGroup();
    }

    public void checkStudentGroupStatus(
            @NonNull String courseId,
            @NonNull String assignmentId,
            @NonNull String studentUid,
            @NonNull StudentGroupCallback callback
    ) {
        db.collection(FirestorePaths.COURSES).document(courseId)
                .collection(FirestorePaths.ASSIGNMENTS).document(assignmentId)
                .collection(FirestorePaths.GROUPS)
                .whereArrayContains("memberIds", studentUid)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        DocumentSnapshot groupDoc = snapshot.getDocuments().get(0);
                        String groupId = groupDoc.getId();
                        String groupName = groupDoc.getString("name");
                        boolean isLeader = studentUid.equals(groupDoc.getString("leaderId"));
                        callback.onAlreadyInGroup(groupId, groupName, isLeader);
                    } else {
                        callback.onNotInGroup();
                    }
                })
                .addOnFailureListener(e -> callback.onNotInGroup());
    }

    @NonNull
    public ListenerRegistration listenForGroupStatus(
            @NonNull String courseId,
            @NonNull String assignmentId,
            @NonNull String studentUid,
            @NonNull StudentGroupCallback callback
    ) {
        return db.collection(FirestorePaths.COURSES).document(courseId)
                .collection(FirestorePaths.ASSIGNMENTS).document(assignmentId)
                .collection(FirestorePaths.GROUPS)
                .whereArrayContains("memberIds", studentUid)
                .limit(1)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) {
                        callback.onNotInGroup();
                        return;
                    }
                    if (!snapshots.isEmpty()) {
                        DocumentSnapshot groupDoc = snapshots.getDocuments().get(0);
                        String groupId = groupDoc.getId();
                        String groupName = groupDoc.getString("name");
                        boolean isLeader = studentUid.equals(groupDoc.getString("leaderId"));
                        callback.onAlreadyInGroup(groupId, groupName, isLeader);
                    } else {
                        callback.onNotInGroup();
                    }
                });
    }

    public void renameGroup(
            @NonNull String courseId,
            @NonNull String assignmentId,
            @NonNull String groupId,
            @NonNull String newName,
            @NonNull String requestingUid,
            @NonNull Context context
    ) {
        DocumentReference groupRef = db
                .collection(FirestorePaths.COURSES).document(courseId)
                .collection(FirestorePaths.ASSIGNMENTS).document(assignmentId)
                .collection(FirestorePaths.GROUPS).document(groupId);

        groupRef.get().addOnSuccessListener(snapshot -> {
            String leaderId = snapshot.getString("leaderId");

            if (!requestingUid.equals(leaderId)) {
                Toast.makeText(context,
                        "Only the group leader can rename the group.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            groupRef.update("name", newName);
        });
    }

    public void addTask(
            @NonNull String courseId,
            @NonNull String assignmentId,
            @NonNull String groupId,
            @NonNull String title,
            @NonNull String assignedToUid,
            @NonNull String assignedName,
            @Nullable String notes,
            @Nullable Date miniDeadline
    ) {
        CollectionReference tasksRef = db
                .collection(FirestorePaths.COURSES).document(courseId)
                .collection(FirestorePaths.ASSIGNMENTS).document(assignmentId)
                .collection(FirestorePaths.GROUPS).document(groupId)
                .collection(FirestorePaths.TASKS);

        String taskId = tasksRef.document().getId();

        Map<String, Object> task = new HashMap<>();
        task.put("taskId", taskId);
        task.put("title", title);
        task.put("assignedTo", assignedToUid);
        task.put("assignedName", assignedName);
        task.put("notes", notes != null ? notes : "");
        task.put("miniDeadline", miniDeadline != null ? new Timestamp(miniDeadline) : null);
        task.put("isDone", false);
        task.put("createdAt", FieldValue.serverTimestamp());
        task.put("completedAt", null);

        WriteBatch batch = db.batch();

        DocumentReference taskRef = tasksRef.document(taskId);
        batch.set(taskRef, task);

        DocumentReference groupRef = db
                .collection(FirestorePaths.COURSES).document(courseId)
                .collection(FirestorePaths.ASSIGNMENTS).document(assignmentId)
                .collection(FirestorePaths.GROUPS).document(groupId);
        batch.update(groupRef, "taskCount", FieldValue.increment(1));

        batch.commit();
    }

    public void completeTask(
            @NonNull String courseId,
            @NonNull String assignmentId,
            @NonNull String groupId,
            @NonNull String taskId
    ) {
        WriteBatch batch = db.batch();

        DocumentReference taskRef = db
                .collection(FirestorePaths.COURSES).document(courseId)
                .collection(FirestorePaths.ASSIGNMENTS).document(assignmentId)
                .collection(FirestorePaths.GROUPS).document(groupId)
                .collection(FirestorePaths.TASKS).document(taskId);

        batch.update(taskRef, "isDone", true);
        batch.update(taskRef, "completedAt", FieldValue.serverTimestamp());

        DocumentReference groupRef = db
                .collection(FirestorePaths.COURSES).document(courseId)
                .collection(FirestorePaths.ASSIGNMENTS).document(assignmentId)
                .collection(FirestorePaths.GROUPS).document(groupId);
        batch.update(groupRef, "completedTaskCount", FieldValue.increment(1));

        batch.commit();
    }
}
