const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();
const db = admin.firestore();

exports.onAssignmentCreated = functions.firestore
    .document("courses/{courseId}/assignments/{assignmentId}")
    .onCreate(async (snap, context) => {
        const assignment = snap.data();
        const courseId = context.params.courseId;

        const courseDoc = await db.collection("courses").doc(courseId).get();
        const studentIds = courseDoc.data().studentIds || [];

        if (studentIds.length === 0) return;

        const userDocs = await Promise.all(
            studentIds.map(uid => db.collection("users").doc(uid).get())
        );

        const tokens = userDocs
            .map(doc => doc.data().fcmToken)
            .filter(token => token && token.length > 0);

        if (tokens.length === 0) return;

        const message = {
            notification: {
                title: `New assignment: ${assignment.title}`,
                body: `Due ${formatDate(assignment.dueDate.toDate())} · ${courseDoc.data().name}`,
            },
            data: {
                type: "new_assignment",
                courseId: courseId,
                assignmentId: context.params.assignmentId,
            },
            tokens: tokens,
        };

        await admin.messaging().sendEachForMulticast(message);

        const batch = db.batch();
        for (const uid of studentIds) {
            const notifRef = db.collection("notifications").doc();
            batch.set(notifRef, {
                recipientId: uid,
                type: "new_assignment",
                title: `New assignment: ${assignment.title}`,
                body: courseDoc.data().name,
                courseId: courseId,
                assignmentId: context.params.assignmentId,
                isRead: false,
                createdAt: admin.firestore.FieldValue.serverTimestamp(),
            });
        }
        await batch.commit();
    });

exports.onTaskCompleted = functions.firestore
    .document("courses/{courseId}/assignments/{assignmentId}/groups/{groupId}/tasks/{taskId}")
    .onUpdate(async (change, context) => {
        const before = change.before.data();
        const after = change.after.data();

        if (before.isDone || !after.isDone) return;

        const { courseId, assignmentId, groupId } = context.params;

        const groupDoc = await db.collection("courses").doc(courseId)
            .collection("assignments").doc(assignmentId)
            .collection("groups").doc(groupId).get();

        const memberIds = groupDoc.data().memberIds || [];
        const completedBy = after.assignedTo;

        const recipientIds = memberIds.filter(uid => uid !== completedBy);
        if (recipientIds.length === 0) return;

        const userDocs = await Promise.all(
            recipientIds.map(uid => db.collection("users").doc(uid).get())
        );

        const tokens = userDocs
            .map(doc => doc.data().fcmToken)
            .filter(t => t && t.length > 0);

        if (tokens.length > 0) {
            await admin.messaging().sendEachForMulticast({
                notification: {
                    title: `${after.assignedName} completed a task`,
                    body: `"${after.title}" — ${groupDoc.data().name}`,
                },
                data: {
                    type: "task_done",
                    courseId,
                    assignmentId,
                    groupId,
                },
                tokens,
            });
        }

        const batch = db.batch();
        for (const uid of recipientIds) {
            const ref = db.collection("notifications").doc();
            batch.set(ref, {
                recipientId: uid,
                type: "task_done",
                title: `${after.assignedName} completed a task`,
                body: `"${after.title}"`,
                courseId,
                assignmentId,
                isRead: false,
                createdAt: admin.firestore.FieldValue.serverTimestamp(),
            });
        }
        await batch.commit();
    });

exports.sendDeadlineReminders = functions.pubsub
    .schedule("every 60 minutes")
    .onRun(async () => {
        const now = new Date();
        const in24h = new Date(now.getTime() + 24 * 60 * 60 * 1000);
        const in23h = new Date(now.getTime() + 23 * 60 * 60 * 1000);

        const coursesSnap = await db.collection("courses")
            .where("isArchived", "==", false).get();

        for (const courseDoc of coursesSnap.docs) {
            const studentIds = courseDoc.data().studentIds || [];
            if (studentIds.length === 0) continue;

            const assignmentsSnap = await courseDoc.ref
                .collection("assignments")
                .where("dueDate", ">", admin.firestore.Timestamp.fromDate(in23h))
                .where("dueDate", "<=", admin.firestore.Timestamp.fromDate(in24h))
                .get();

            for (const assignDoc of assignmentsSnap.docs) {
                const assignment = assignDoc.data();

                const userDocs = await Promise.all(
                    studentIds.map(uid => db.collection("users").doc(uid).get())
                );
                const tokens = userDocs
                    .map(d => d.data().fcmToken)
                    .filter(t => t && t.length > 0);

                if (tokens.length === 0) continue;

                await admin.messaging().sendEachForMulticast({
                    notification: {
                        title: `⏰ Due in 24 hours: ${assignment.title}`,
                        body: courseDoc.data().name,
                    },
                    data: {
                        type: "deadline_reminder",
                        courseId: courseDoc.id,
                        assignmentId: assignDoc.id,
                    },
                    tokens,
                });
            }
        }
    });

function formatDate(date) {
    return date.toLocaleDateString("en-US", { month: "short", day: "numeric" });
}
