package com.example.classsync.ui.auth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.classsync.R;
import com.example.classsync.data.UserSession;

public class SplashFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded()) {
                UserSession session = new UserSession(requireContext());
                if (session.isLoggedIn()) {
                    String role = session.getUserRole();
                    if ("TEACHER".equals(role)) {
                        NavHostFragment.findNavController(this).navigate(R.id.nav_home);
                    } else {
                        NavHostFragment.findNavController(this).navigate(R.id.studentHomeFragment);
                    }
                } else {
                    NavHostFragment.findNavController(this).navigate(R.id.loginFragment);
                }
            }
        }, 2000);
    }
}
