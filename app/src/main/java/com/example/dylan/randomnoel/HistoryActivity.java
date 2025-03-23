package com.example.dylan.randomnoel;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.dylan.randomnoel.databinding.ActivityHistoryBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseHelper databaseHelper;
    private HistoryAdapter historyAdapter;
    private DrawerLayout drawerLayout;
    private Spinner dateSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.dylan.randomnoel.databinding.ActivityHistoryBinding binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Initialize RecyclerView
        RecyclerView recyclerViewHistory = binding.recyclerViewHistory;
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new HistoryAdapter(new ArrayList<>());
        recyclerViewHistory.setAdapter(historyAdapter);

        //Initialize Spinner
        dateSpinner = binding.dateSpinner;
        initializeSpinner();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    private void initializeSpinner() {
        // Load all distinct dates from the database
        List<String> allDates = databaseHelper.getAllDates();

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allDates);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        dateSpinner.setAdapter(spinnerAdapter);

        // Set the selection listener for the spinner
        dateSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedDate = (String) parent.getItemAtPosition(position);
                loadHistoryByDate(selectedDate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // If nothing is selected, you might want to show all history or do nothing
            }
        });
    }

    private void loadHistoryByDate(String date) {
        List<String> history = databaseHelper.getAssociationsByDate(date);
        historyAdapter.updateHistory(history);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_main_activity) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}