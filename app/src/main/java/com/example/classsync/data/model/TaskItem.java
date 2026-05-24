package com.example.classsync.data.model;

import com.google.firebase.Timestamp;

public class TaskItem {
    private String taskId;
    private String title;
    private String assignedTo;
    private String assignedName;
    private String notes;
    private Timestamp miniDeadline;
    private boolean isDone;
    private Timestamp createdAt;
    private Timestamp completedAt;

    public TaskItem() {
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getAssignedName() {
        return assignedName;
    }

    public void setAssignedName(String assignedName) {
        this.assignedName = assignedName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getMiniDeadline() {
        return miniDeadline;
    }

    public void setMiniDeadline(Timestamp miniDeadline) {
        this.miniDeadline = miniDeadline;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
    }
}
