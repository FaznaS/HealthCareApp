package com.s22010466.healthcare;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        drawerLayout = findViewById(R.id.drawer_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,
                toolbar,R.string.open_nav,R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.home) {
                    openFragment(new HomeFragment(), "Home");
                    return true;
                } else if (itemId == R.id.tracker) {
                    openFragment(new TrackerFragment(), "Tracker");
                    return true;
                } else if (itemId == R.id.nutrition) {
                    openFragment(new NutritionFragment(), "Nutrition");
                    return true;
                } else if (itemId == R.id.reminder) {
                    openFragment(new ReminderFragment(), "Reminder");
                    return true;
                }
                return false;
            }
        });

        //Setting default fragment to home fragment
        fragmentManager = getSupportFragmentManager();
        openFragment(new HomeFragment(), "Home");

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Using if statements instead of switch because getting the error
        //"constant expression required"
        int itemId = item.getItemId();

        if (itemId == R.id.find_health_centers) {
            openFragment(new MapFragment(), "Find Health Centers");
        } else if (itemId == R.id.preferences) {
            openFragment(new PreferencesFragment(), "Preferences");
        } else if (itemId == R.id.settings) {
            openFragment(new SettingsFragment(), "Settings");
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openFragment(Fragment fragment, String title){
        toolbar.setTitle(title);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container,fragment);
        transaction.commit();
    }
}
