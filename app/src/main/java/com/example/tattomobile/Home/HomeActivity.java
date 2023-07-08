package com.example.tattomobile.Home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tattomobile.Fragment.CalenderFragment;
import com.example.tattomobile.Fragment.HomeFragment;
import com.example.tattomobile.R;
import com.example.tattomobile.activity.CalenderActivity;
import com.example.tattomobile.activity.MyBookingActivity;
import com.example.tattomobile.activity.NotificationActivity;
import com.example.tattomobile.activity.ProfileActivity;
import com.example.tattomobile.databinding.ActivityHomeBinding;
import com.example.tattomobile.utility.Prefs;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private CalenderFragment calenderFragment;
    private ActivityHomeBinding binding;
    private FragmentManager fragmentManager;
    private boolean homeStatus=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragmentManager = getSupportFragmentManager();
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
        homeFragment = new HomeFragment();
        calenderFragment = new CalenderFragment();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
               if(homeStatus) {
                    loadFragment(HomeFragment.newInstance());
                    homeStatus=false;
                }
                return true;

            case R.id.calnder:
                startActivity(new Intent(HomeActivity.this, MyBookingActivity.class));
                return true;

            case R.id.notification:
                startActivity(new Intent(HomeActivity.this, NotificationActivity.class));
                return true;

            case R.id.settings:
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                return true;
        }
        return false;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_home_container, fragment);
        fragmentTransaction.commit();
    }

}