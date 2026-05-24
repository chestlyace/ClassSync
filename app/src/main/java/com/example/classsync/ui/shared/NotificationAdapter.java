package com.example.classsync.ui.shared;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classsync.R;
import com.example.classsync.data.model.AppNotification;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<AppNotification> notifications;
    private final OnNotificationClickListener clickListener;
    public interface OnNotificationClickListener {
        void onNotificationClicked(AppNotification notification);
    }

    public NotificationAdapter(List<AppNotification> notifications,
                               OnNotificationClickListener clickListener) {
        this.notifications = notifications;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppNotification notif = notifications.get(position);
        boolean isUnread = !notif.isRead();

        if (isUnread) {
            holder.row.setBackgroundColor(
                    holder.itemView.getContext().getColor(R.color.surface_container_low));
            holder.accentBar.setVisibility(View.VISIBLE);
        } else {
            holder.row.setBackgroundColor(
                    holder.itemView.getContext().getColor(R.color.surface));
            holder.accentBar.setVisibility(View.GONE);
        }

        holder.title.setText(notif.getTitle());
        String body = notif.getBody();
        holder.body.setText(body != null ? body : "");
        holder.body.setVisibility(body != null && !body.isEmpty() ? View.VISIBLE : View.GONE);

        String type = notif.getType();
        if ("deadline_reminder".equals(type)) {
            holder.icon.setImageResource(R.drawable.ic_calendar);
        } else if ("task_done".equals(type)) {
            holder.icon.setImageResource(R.drawable.ic_check_circle);
        } else {
            holder.icon.setImageResource(R.drawable.ic_notifications);
        }

        holder.iconContainer.setBackgroundTintList(
                androidx.core.content.ContextCompat.getColorStateList(
                        holder.itemView.getContext(), R.color.primary_fixed));
        holder.icon.setImageTintList(
                androidx.core.content.ContextCompat.getColorStateList(
                        holder.itemView.getContext(), R.color.primary));

        Timestamp createdAt = notif.getCreatedAt();
        if (createdAt != null) {
            Date date = createdAt.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.US);
            holder.time.setText(sdf.format(date));
            holder.time.setVisibility(View.VISIBLE);
        } else {
            holder.time.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> clickListener.onNotificationClicked(notif));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout row;
        final View accentBar;
        final FrameLayout iconContainer;
        final ImageView icon;
        final TextView title;
        final TextView body;
        final TextView time;

        ViewHolder(View itemView) {
            super(itemView);
            row = itemView.findViewById(R.id.notification_row);
            accentBar = itemView.findViewById(R.id.accent_bar);
            iconContainer = itemView.findViewById(R.id.icon_container);
            icon = itemView.findViewById(R.id.notification_icon);
            title = itemView.findViewById(R.id.notification_title);
            body = itemView.findViewById(R.id.notification_body);
            time = itemView.findViewById(R.id.notification_time);
        }
    }
}
