package com.example.comp1786_logbook_ex3_connectdatabaseapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.comp1786_logbook_ex3_connectdatabaseapplication.activity.ContactsListActivity;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.ui.MainNavActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Launch the new navigation activity (contains bottom navigation and fragments)
        startActivity(new Intent(this, MainNavActivity.class));
        finish();
    }
}