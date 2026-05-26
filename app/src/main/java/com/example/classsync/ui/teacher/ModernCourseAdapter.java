package com.example.classsync.ui.teacher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classsync.R;
import com.example.classsync.data.model.Course;

import java.util.List;

public class ModernCourseAdapter extends RecyclerView.Adapter<ModernCourseAdapter.CourseViewHolder> {

    private List<CourseData> courseDataList;
    private OnCourseClickListener listener;

    public interface OnCourseClickListener {
        void onCourseClick(CourseData courseData);
    }

    public ModernCourseAdapter(List<CourseData> courseDataList, OnCourseClickListener listener) {
        this.courseDataList = courseDataList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_modern, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        CourseData courseData = courseDataList.get(position);
        Course course = courseData.getCourse();

        // Entrance animation with stagger
        holder.itemView.setAlpha(0f);
        holder.itemView.setTranslationY(30f);
        holder.itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(position * 80L)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();

        // Set course name
        holder.tvCourseName.setText(course.getName());

        // Set course description or default text
        if (course.getDescription() != null && !course.getDescription().isEmpty()) {
            holder.tvCourseDescription.setText(course.getDescription());
            holder.tvCourseDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvCourseDescription.setVisibility(View.GONE);
        }

        // Set student count
        int studentCount = course.getStudentIds() != null ? course.getStudentIds().size() : 0;
        holder.tvStudentCount.setText(String.valueOf(studentCount));

        // Set assignment count
        holder.tvAssignmentCount.setText(String.valueOf(courseData.getAssignmentCount()));

        // Set join code
        if (course.getJoinCode() != null && !course.getJoinCode().isEmpty()) {
            holder.tvJoinCode.setText(course.getJoinCode().toUpperCase());
        } else {
            holder.tvJoinCode.setText("N/A");
        }

        // Set completion percentage
        int completionPercentage = courseData.getCompletionPercentage();
        holder.tvCompletionPercentage.setText(completionPercentage + "%");

        // Animate progress fill
        ViewGroup.LayoutParams params = holder.progressFill.getLayoutParams();
        int maxWidth = holder.progressFill.getParent() instanceof View ?
                ((View) holder.progressFill.getParent()).getWidth() : 1000;

        // Calculate progress width
        float progressWidth = (completionPercentage / 100f) * maxWidth;
        params.width = (int) progressWidth;
        holder.progressFill.setLayoutParams(params);

        // Set click listener with scale feedback
        holder.itemView.setOnClickListener(v -> {
            // Subtle press animation
            v.animate()
                    .scaleX(0.97f)
                    .scaleY(0.97f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();

                        if (listener != null) {
                            // Delay click to let animation complete
                            v.postDelayed(() -> listener.onCourseClick(courseData), 50);
                        }
                    })
                    .start();
        });

        // Menu button click
        holder.btnCourseMenu.setOnClickListener(v -> {
            // TODO: Show popup menu for course options
        });
    }

    @Override
    public int getItemCount() {
        return courseDataList.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName;
        TextView tvCourseDescription;
        TextView tvStudentCount;
        TextView tvAssignmentCount;
        TextView tvJoinCode;
        TextView tvCompletionPercentage;
        View progressFill;
        ImageButton btnCourseMenu;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tv_course_name);
            tvCourseDescription = itemView.findViewById(R.id.tv_course_description);
            tvStudentCount = itemView.findViewById(R.id.tv_student_count);
            tvAssignmentCount = itemView.findViewById(R.id.tv_assignment_count);
            tvJoinCode = itemView.findViewById(R.id.tv_join_code);
            tvCompletionPercentage = itemView.findViewById(R.id.tv_completion_percentage);
            progressFill = itemView.findViewById(R.id.progress_fill);
            btnCourseMenu = itemView.findViewById(R.id.btn_course_menu);
        }
    }
}
