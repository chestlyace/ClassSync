package com.example.classsync.notification;

// WorkManager / local notifications (preserved for reference)
//
// import android.app.NotificationChannel;
// import android.app.NotificationManager;
// import android.app.PendingIntent;
// import android.content.Context;
// import android.content.Intent;
// import android.os.Build;
//
// import androidx.core.app.NotificationCompat;
// import androidx.core.app.NotificationManagerCompat;
//
// import com.example.classsync.MainActivity;
// import com.example.classsync.R;
//
// public class NotificationHelper {
//
//     private static final String CHANNEL_ID = "classsync_deadlines";
//     private static final String CHANNEL_NAME = "Deadline Reminders";
//     private static final String CHANNEL_NEW_ASSIGNMENT_ID = "classsync_new_assignments";
//     private static final String CHANNEL_NEW_ASSIGNMENT_NAME = "New Assignments";
//     private static final String CHANNEL_TASK_DONE_ID = "classsync_task_done";
//     private static final String CHANNEL_TASK_DONE_NAME = "Task Updates";
//
//     public static void createNotificationChannels(Context context) {
//         NotificationManager manager = context.getSystemService(NotificationManager.class);
//
//         NotificationChannel deadlineChannel = new NotificationChannel(
//                 CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
//         deadlineChannel.setDescription("Reminders for upcoming assignment deadlines");
//         manager.createNotificationChannel(deadlineChannel);
//
//         NotificationChannel assignmentChannel = new NotificationChannel(
//                 CHANNEL_NEW_ASSIGNMENT_ID, CHANNEL_NEW_ASSIGNMENT_NAME,
//                 NotificationManager.IMPORTANCE_HIGH);
//         assignmentChannel.setDescription("New assignments posted to your courses");
//         manager.createNotificationChannel(assignmentChannel);
//
//         NotificationChannel taskChannel = new NotificationChannel(
//                 CHANNEL_TASK_DONE_ID, CHANNEL_TASK_DONE_NAME,
//                 NotificationManager.IMPORTANCE_DEFAULT);
//         taskChannel.setDescription("Tasks completed by group members");
//         manager.createNotificationChannel(taskChannel);
//     }
//
//     public static void showNotification(Context context, String channelId, String title, String body) {
//         Intent intent = new Intent(context, MainActivity.class);
//         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//         PendingIntent pendingIntent = PendingIntent.getActivity(
//                 context, 0, intent,
//                 PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//
//         NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
//                 .setSmallIcon(R.drawable.ic_notifications)
//                 .setContentTitle(title)
//                 .setContentText(body)
//                 .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                 .setContentIntent(pendingIntent)
//                 .setAutoCancel(true);
//
//         NotificationManagerCompat.from(context).notify((int) System.currentTimeMillis(), builder.build());
//     }
// }

public class NotificationHelper {
}
