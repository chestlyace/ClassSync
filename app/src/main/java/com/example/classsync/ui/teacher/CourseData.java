package com.example.classsync.ui.teacher;

import com.example.classsync.data.model.Course;

public class CourseData {
    private Course course;
    private int assignmentCount;
    private int completionPercentage;
    private int dueSoonCount;

    public CourseData(Course course, int assignmentCount, int completionPercentage, int dueSoonCount) {
        this.course = course;
        this.assignmentCount = assignmentCount;
        this.completionPercentage = completionPercentage;
        this.dueSoonCount = dueSoonCount;
    }

    public Course getCourse() {
        return course;
    }

    public int getAssignmentCount() {
        return assignmentCount;
    }

    public int getCompletionPercentage() {
        return completionPercentage;
    }

    public int getDueSoonCount() {
        return dueSoonCount;
    }
}
