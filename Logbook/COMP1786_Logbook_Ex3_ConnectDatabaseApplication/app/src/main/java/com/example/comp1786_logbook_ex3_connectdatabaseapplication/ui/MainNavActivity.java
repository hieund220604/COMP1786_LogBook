package com.example.comp1786_logbook_ex3_connectdatabaseapplication.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainNavActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);

        toolbar = findViewById(R.id.toolbar_main_nav);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            // Use if/else because R.id values may not be compile-time constants in some setups
            if (id == R.id.nav_contacts) {
                setTitle("Contacts");
                replaceFragment(new ContactsFragment());
                return true;
            } else if (id == R.id.nav_favorites) {
                setTitle("Favorites");
                replaceFragment(new FavoritesFragment());
                return true;
            } else if (id == R.id.nav_settings) {
                setTitle("Settings");
                replaceFragment(new SettingsFragment());
                return true;
            }
            return false;
        });

        // default
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_contacts);
        }
    }

    private void replaceFragment(Fragment f) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, f)
                .commitAllowingStateLoss();
    }
}
