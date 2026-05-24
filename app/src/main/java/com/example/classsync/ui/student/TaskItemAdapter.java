package com.example.classsync.ui.student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classsync.R;
import com.example.classsync.data.model.TaskItem;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskItemAdapter extends RecyclerView.Adapter<TaskItemAdapter.ViewHolder> {

    private final List<TaskItem> tasks;
    private final OnTaskToggleListener toggleListener;

    public interface OnTaskToggleListener {
        void onTaskToggled(TaskItem task);
    }

    public TaskItemAdapter(List<TaskItem> tasks, OnTaskToggleListener toggleListener) {
        this.tasks = tasks;
        this.toggleListener = toggleListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task_workspace, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskItem task = tasks.get(position);

        holder.title.setText(task.getTitle());
        holder.assignedName.setText(task.getAssignedName());

        if (task.isDone()) {
            holder.checkboxContainer.setBackgroundResource(R.drawable.bg_checkbox_checked);
            holder.checkIcon.setVisibility(View.VISIBLE);
            holder.title.setAlpha(0.6f);
        } else {
            holder.checkboxContainer.setBackgroundResource(R.drawable.bg_checkbox_unchecked);
            holder.checkIcon.setVisibility(View.GONE);
            holder.title.setAlpha(1f);
        }

        Timestamp deadline = task.getMiniDeadline();
        if (deadline != null) {
            holder.deadlineContainer.setVisibility(View.VISIBLE);
            Date date = deadline.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.US);
            holder.deadlineText.setText(sdf.format(date));

            boolean isOverdue = date.before(new Date()) && !task.isDone();
            if (isOverdue) {
                holder.deadlineText.setTextColor(
                        holder.itemView.getContext().getColor(R.color.error));
            } else {
                holder.deadlineText.setTextColor(
                        holder.itemView.getContext().getColor(R.color.on_surface_variant));
            }
        } else {
            holder.deadlineContainer.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (!task.isDone()) {
                toggleListener.onTaskToggled(task);
            }
        });

        holder.checkboxContainer.setOnClickListener(v -> {
            if (!task.isDone()) {
                toggleListener.onTaskToggled(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout checkboxContainer;
        final ImageView checkIcon;
        final TextView title;
        final TextView assignedName;
        final LinearLayout deadlineContainer;
        final TextView deadlineText;

        ViewHolder(View itemView) {
            super(itemView);
            checkboxContainer = itemView.findViewById(R.id.checkbox_container);
            checkIcon = itemView.findViewById(R.id.check_icon);
            title = itemView.findViewById(R.id.task_title);
            assignedName = itemView.findViewById(R.id.assigned_name);
            deadlineContainer = itemView.findViewById(R.id.deadline_container);
            deadlineText = itemView.findViewById(R.id.deadline_text);
        }
    }
}
