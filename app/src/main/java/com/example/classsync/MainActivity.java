package com.example.classsync;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            bottomNav = findViewById(R.id.bottom_navigation);
            NavigationUI.setupWithNavController(bottomNav, navController);

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int id = destination.getId();
                if (id == R.id.splashFragment || id == R.id.loginFragment || id == R.id.registerFragment) {
                    bottomNav.setVisibility(View.GONE);
                } else {
                    bottomNav.setVisibility(View.VISIBLE);

                    // Dynamic navigation for the "Courses" tab
                    View coursesTab = bottomNav.findViewById(R.id.nav_courses);
                    if (coursesTab != null) {
                        coursesTab.setOnClickListener(v -> {
                            com.example.classsync.data.UserSession session = new com.example.classsync.data.UserSession(this);
                            if ("TEACHER".equals(session.getUserRole())) {
                                navController.navigate(R.id.nav_home);
                            } else {
                                navController.navigate(R.id.nav_courses);
                            }
                        });
                    }

                    // Home tab should also be role-aware
                    View homeTab = bottomNav.findViewById(R.id.nav_home);
                    if (homeTab != null) {
                        homeTab.setOnClickListener(v -> {
                            com.example.classsync.data.UserSession session = new com.example.classsync.data.UserSession(this);
                            if ("TEACHER".equals(session.getUserRole())) {
                                navController.navigate(R.id.nav_home);
                            } else {
                                navController.navigate(R.id.studentHomeFragment);
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
