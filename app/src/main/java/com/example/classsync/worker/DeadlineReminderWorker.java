package com.example.classsync.worker;

// WorkManager / local notifications (preserved for reference)
//
// import android.content.Context;
//
// import androidx.annotation.NonNull;
// import androidx.work.Worker;
// import androidx.work.WorkerParameters;
//
// import com.example.classsync.notification.NotificationHelper;
// import com.google.android.gms.tasks.Tasks;
// import com.google.firebase.auth.FirebaseAuth;
// import com.google.firebase.firestore.FirebaseFirestore;
// import com.google.firebase.firestore.QueryDocumentSnapshot;
// import com.google.firebase.firestore.QuerySnapshot;
// import com.google.firebase.Timestamp;
//
// import java.util.Date;
// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.Set;
//
// public class DeadlineReminderWorker extends Worker {
//
//     public DeadlineReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
//         super(context, params);
//     }
//
//     @NonNull
//     @Override
//     public Result doWork() {
//         String uid = FirebaseAuth.getInstance().getCurrentUser() != null
//                 ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
//         if (uid == null) return Result.failure();
//
//         FirebaseFirestore db = FirebaseFirestore.getInstance();
//         Date now = new Date();
//         Date in24h = new Date(now.getTime() + 24 * 60 * 60 * 1000);
//         Timestamp start = new Timestamp(now);
//         Timestamp end = new Timestamp(in24h);
//
//         try {
//             QuerySnapshot courseSnap = Tasks.await(
//                     db.collection("courses")
//                             .whereArrayContains("studentIds", uid)
//                             .whereEqualTo("isArchived", false)
//                             .get()
//             );
//
//             Set<String> alreadyNotified = new HashSet<>();
//
//             for (QueryDocumentSnapshot courseDoc : courseSnap) {
//                 String courseId = courseDoc.getId();
//                 String courseName = courseDoc.getString("name");
//
//                 QuerySnapshot assignSnap = Tasks.await(
//                         courseDoc.getReference()
//                                 .collection("assignments")
//                                 .whereGreaterThan("dueDate", start)
//                                 .whereLessThanOrEqualTo("dueDate", end)
//                                 .get()
//                 );
//
//                 for (QueryDocumentSnapshot assignDoc : assignSnap) {
//                     String assignmentId = assignDoc.getId();
//                     String title = assignDoc.getString("title");
//
//                     String key = courseId + "_" + assignmentId;
//                     if (alreadyNotified.contains(key)) continue;
//                     alreadyNotified.add(key);
//
//                     HashMap<String, Object> notifData = new HashMap<>();
//                     notifData.put("recipientId", uid);
//                     notifData.put("type", "deadline_reminder");
//                     notifData.put("title", "Due in 24 hours: " + title);
//                     notifData.put("body", courseName);
//                     notifData.put("courseId", courseId);
//                     notifData.put("assignmentId", assignmentId);
//                     notifData.put("isRead", false);
//                     notifData.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp());
//
//                     Tasks.await(db.collection("notifications").add(notifData));
//
//                     NotificationHelper.showNotification(
//                             getApplicationContext(),
//                             "classsync_deadlines",
//                             "Due in 24 hours: " + title,
//                             courseName
//                     );
//                 }
//             }
//
//             return Result.success();
//         } catch (Exception e) {
//             return Result.retry();
//         }
//     }
// }

public class DeadlineReminderWorker {
}
