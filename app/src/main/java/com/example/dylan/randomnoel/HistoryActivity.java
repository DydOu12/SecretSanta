package com.example.dylan.randomnoel;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dylan.randomnoel.databinding.ActivityHistoryBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected DatabaseHelper databaseHelper;
    protected HistoryAdapter historyAdapter;
    protected DrawerLayout drawerLayout;
    protected Spinner dateSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(shouldCallSuperOnCreate())
            super.onCreate(savedInstanceState);

        ActivityHistoryBinding binding = getBinding();
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        drawerLayout = getDrawerLayout(binding);
        NavigationView navigationView = getNavView(binding);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = getActionBarDrawerToggle(binding);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Initialize the database helper
        databaseHelper = getDatabaseHelper();

        // Initialize RecyclerView
        RecyclerView recyclerViewHistory = getRecylerViewHistory(binding);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new HistoryAdapter(new ArrayList<>());
        recyclerViewHistory.setAdapter(historyAdapter);

        //Initialize Spinner
        dateSpinner = getDateSpinner(binding);
        initializeSpinner();
    }

    protected boolean shouldCallSuperOnCreate() {
        return true;
    }

    protected ActivityHistoryBinding getBinding() {
        return ActivityHistoryBinding.inflate(getLayoutInflater());
    }

    protected DrawerLayout getDrawerLayout(ActivityHistoryBinding binding) {
        return binding.drawerLayout;
    }

    protected NavigationView getNavView(ActivityHistoryBinding binding) {
        return binding.navView;
    }

    protected ActionBarDrawerToggle getActionBarDrawerToggle(ActivityHistoryBinding binding) {
        return new ActionBarDrawerToggle(
                this, drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    }

    protected RecyclerView getRecylerViewHistory(ActivityHistoryBinding binding) {
        return binding.recyclerViewHistory;
    }

    protected DatabaseHelper getDatabaseHelper() {
        return new DatabaseHelper(this);
    }

    protected Spinner getDateSpinner(ActivityHistoryBinding binding) {
        return binding.dateSpinner;
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

    protected void initializeSpinner() {
        // Load all distinct dates from the database
        List<String> allDates = databaseHelper.getAllDates();

        // Use the app's high-contrast spinner layouts.
        ArrayAdapter<String> spinnerAdapter = getArrayAdapter(allDates);

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

    protected ArrayAdapter<String> getArrayAdapter(List<String> allDates) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_selected_item, allDates);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        return adapter;
    }

    protected void loadHistoryByDate(String date) {
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