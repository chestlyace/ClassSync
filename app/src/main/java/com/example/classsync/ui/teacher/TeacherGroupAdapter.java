package com.example.classsync.ui.teacher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classsync.R;
import com.example.classsync.data.model.Group;

import java.util.List;
import java.util.Map;

public class TeacherGroupAdapter extends RecyclerView.Adapter<TeacherGroupAdapter.ViewHolder> {

    private final List<Group> groups;

    public TeacherGroupAdapter(List<Group> groups) {
        this.groups = groups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_teacher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = groups.get(position);

        holder.groupName.setText(group.getName());

        Map<String, String> memberNames = group.getMemberNames();
        int memberCount = group.getMemberIds() != null ? group.getMemberIds().size() : 0;
        if (memberNames != null && !memberNames.isEmpty()) {
            StringBuilder names = new StringBuilder();
            for (String name : memberNames.values()) {
                if (names.length() > 0) names.append(", ");
                names.append(name);
            }
            holder.groupMembers.setText(names + " (" + memberCount + ")");
        } else {
            holder.groupMembers.setText(memberCount + " member" + (memberCount == 1 ? "" : "s"));
        }

        String leaderName = group.getLeaderName();
        if (leaderName != null && !leaderName.isEmpty()) {
            holder.leaderBadge.setVisibility(View.VISIBLE);
            holder.leaderName.setText(leaderName + " (leader)");
        } else {
            holder.leaderBadge.setVisibility(View.GONE);
        }

        int completed = group.getCompletedTaskCount();
        int total = group.getTaskCount();
        holder.taskProgressText.setText(completed + "/" + total + " tasks done");
        holder.taskProgressBar.setProgress(total > 0 ? (completed * 100 / total) : 0);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView groupName;
        final TextView groupMembers;
        final TextView leaderName;
        final View leaderBadge;
        final TextView taskProgressText;
        final ProgressBar taskProgressBar;

        ViewHolder(View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_name);
            groupMembers = itemView.findViewById(R.id.group_members);
            leaderName = itemView.findViewById(R.id.leader_name);
            leaderBadge = itemView.findViewById(R.id.leader_badge);
            taskProgressText = itemView.findViewById(R.id.task_progress_text);
            taskProgressBar = itemView.findViewById(R.id.task_progress_bar);
        }
    }
}
