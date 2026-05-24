package com.example.classsync.ui.student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classsync.R;
import com.example.classsync.data.model.Assignment;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentAssignmentsAdapter extends RecyclerView.Adapter<StudentAssignmentsAdapter.ViewHolder> {

    private final List<Assignment> assignments;
    private final OnAssignmentClickListener listener;

    public interface OnAssignmentClickListener {
        void onAssignmentClick(Assignment assignment);
    }

    public StudentAssignmentsAdapter(List<Assignment> assignments, OnAssignmentClickListener listener) {
        this.assignments = assignments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_assignment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Assignment a = assignments.get(position);
        holder.title.setText(a.getTitle());
        holder.courseName.setText(a.getCourseName());

        boolean isGroup = "group".equals(a.getType());
        holder.badge.setText(isGroup ? "Group" : "Individual");
        holder.badge.setBackgroundResource(isGroup
                ? R.drawable.bg_badge_group : R.drawable.bg_badge_individual);

        Timestamp ts = a.getDueDate();
        if (ts != null) {
            Date dueDate = ts.toDate();
            SimpleDateFormat fmt = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            holder.dueDate.setText(fmt.format(dueDate));

            boolean isOverdue = dueDate.before(new Date());
            holder.dueDate.setTextColor(holder.itemView.getContext().getColor(
                    isOverdue ? R.color.error : R.color.on_surface_variant));
        }

        holder.itemView.setOnClickListener(v -> listener.onAssignmentClick(a));
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView badge;
        final TextView courseName;
        final TextView dueDate;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.card_title);
            badge = itemView.findViewById(R.id.card_badge);
            courseName = itemView.findViewById(R.id.card_course_name);
            dueDate = itemView.findViewById(R.id.card_due_date);
        }
    }
}
