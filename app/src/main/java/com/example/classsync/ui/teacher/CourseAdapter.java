package com.example.classsync.ui.teacher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classsync.R;
import com.example.classsync.data.model.Course;

import java.util.List;
import java.util.Random;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private final List<Course> courses;
    private final OnCourseClickListener listener;
    private final Random random = new Random();

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    public CourseAdapter(List<Course> courses, OnCourseClickListener listener) {
        this.courses = courses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_card_teacher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.title.setText(course.getName());
        holder.subtitle.setText(course.getDescription());
        holder.studentCount.setText(course.getStudentIds().size() + " Students");

        int progress = random.nextInt(101);
        holder.progressBar.setProgress(progress);
        holder.progressText.setText(progress + "%");

        holder.itemView.setOnClickListener(v -> listener.onCourseClick(course));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView subtitle;
        final TextView studentCount;
        final TextView progressText;
        final ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.card_title);
            subtitle = itemView.findViewById(R.id.card_subtitle);
            studentCount = itemView.findViewById(R.id.card_student_count);
            progressText = itemView.findViewById(R.id.card_progress_text);
            progressBar = itemView.findViewById(R.id.card_progress_bar);
        }
    }
}
