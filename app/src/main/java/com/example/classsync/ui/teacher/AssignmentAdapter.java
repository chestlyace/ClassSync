package com.example.classsync.ui.teacher;

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

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

    private final List<Assignment> assignments;
    private final OnAssignmentClickListener listener;

    public interface OnAssignmentClickListener {
        void onAssignmentClick(Assignment assignment);
    }

    public AssignmentAdapter(List<Assignment> assignments, OnAssignmentClickListener listener) {
        this.assignments = assignments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_assignment_card_teacher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Assignment a = assignments.get(position);
        holder.title.setText(a.getTitle());

        boolean isGroup = "group".equals(a.getType());
        holder.badge.setText(isGroup ? "Group" : "Individual");
        holder.badge.setBackgroundResource(isGroup
                ? R.drawable.bg_badge_group : R.drawable.bg_badge_individual);
        holder.badge.setTextColor(holder.itemView.getContext().getColor(
                isGroup ? R.color.on_secondary_fixed : R.color.on_primary_fixed));

        Timestamp ts = a.getDueDate();
        if (ts != null) {
            Date dueDate = ts.toDate();
            SimpleDateFormat fmt = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            holder.dueDate.setText(fmt.format(dueDate));

            boolean isOverdue = dueDate.before(new Date());
            holder.overdueBadge.setVisibility(isOverdue ? View.VISIBLE : View.GONE);
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
        final TextView dueDate;
        final TextView overdueBadge;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.card_title);
            badge = itemView.findViewById(R.id.card_badge);
            dueDate = itemView.findViewById(R.id.card_due_date);
            overdueBadge = itemView.findViewById(R.id.card_overdue_badge);
        }
    }
}
