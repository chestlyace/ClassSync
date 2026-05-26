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
import com.example.classsync.data.firebase.AuthCallback;
import com.example.classsync.data.firebase.AuthRepository;
import com.example.classsync.data.model.AppUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashFragment extends Fragment {
    private AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authRepository = new AuthRepository(requireContext());

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded()) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    authRepository.restoreSession(new AuthCallback() {
                        @Override
                        public void onSuccess(@NonNull AppUser user) {
                            if (!isAdded()) {
                                return;
                            }
                            navigateToHome(user.getRole());
                        }

                        @Override
                        public void onError(@NonNull String message) {
                            if (!isAdded()) {
                                return;
                            }
                            authRepository.logout();
                            NavHostFragment.findNavController(SplashFragment.this).navigate(R.id.loginFragment);
                        }
                    });
                } else {
                    NavHostFragment.findNavController(this).navigate(R.id.loginFragment);
                }
            }
        }, 2000);
    }

    private void navigateToHome(@NonNull String role) {
        if (AuthRepository.ROLE_TEACHER.equals(role)) {
            NavHostFragment.findNavController(this).navigate(R.id.nav_home);
        } else {
            NavHostFragment.findNavController(this).navigate(R.id.studentHomeFragment);
        }
    }
}
