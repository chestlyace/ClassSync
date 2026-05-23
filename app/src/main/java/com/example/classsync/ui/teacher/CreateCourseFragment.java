package com.example.classsync.ui.teacher;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;

public class CreateCourseFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_course, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Create course button
        view.findViewById(R.id.btn_create).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        // Cancel button
        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        // Scrim tap to dismiss
        view.findViewById(R.id.scrim).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        // Copy join code button
        ImageButton copyBtn = view.findViewById(R.id.btn_copy_code);
        copyBtn.setOnClickListener(v -> {
            // Copy code to clipboard
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Join Code", "CS-8294");
            clipboard.setPrimaryClip(clip);

            // Visual feedback: switch icon to check
            copyBtn.setImageResource(R.drawable.ic_check);
            copyBtn.setColorFilter(ContextCompat.getColor(requireContext(), R.color.secondary));
            Toast.makeText(requireContext(), "Code copied!", Toast.LENGTH_SHORT).show();

            // Revert icon after 2 seconds
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (isAdded()) {
                    copyBtn.setImageResource(R.drawable.ic_content_copy);
                    copyBtn.setColorFilter(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));
                }
            }, 2000);
        });
    }
}
