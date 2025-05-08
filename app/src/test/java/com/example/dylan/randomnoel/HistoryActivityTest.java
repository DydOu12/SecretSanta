package com.example.dylan.randomnoel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dylan.randomnoel.databinding.ActivityHistoryBinding;
import com.google.android.material.navigation.NavigationView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class HistoryActivityTest {

    @Mock
    private DrawerLayout mockDrawerLayout;

    @Mock
    private MenuItem mockMenuItem;

    @Mock
    private KeyEvent mockKeyEvent;

    @Mock
    private Spinner mockSpinner;

    @Mock
    private ArrayAdapter<String> adapter;

    @Mock
    private DatabaseHelper mockDatabaseHelper;

    private MockedStatic<Looper> mockedLooper;

    private MockedConstruction<Intent> mockedIntent;

    private HistoryActivity historyActivity;


    List<String> dates = Arrays.asList("2023-10-26", "2023-10-27", "2023-10-28");

    @Before
    public void setUp() throws Exception {
        try(AutoCloseable ignored = MockitoAnnotations.openMocks(this)) {

            mockedLooper = mockStatic(Looper.class);
            mockedLooper.when(Looper::myLooper).thenReturn(mock(Looper.class));
            mockedLooper.when(Looper::getMainLooper).thenReturn(mock(Looper.class));

            mockedIntent = mockConstruction(Intent.class, (mock, context) -> when(mock.putParcelableArrayListExtra(anyString(), ArgumentMatchers.any())).thenReturn(mock));

            ArchTaskExecutor.getInstance().setDelegate(new TaskExecutor() {
                @Override
                public void executeOnDiskIO(@NonNull Runnable runnable) {
                    runnable.run();
                }

                @Override
                public void postToMainThread(@NonNull Runnable runnable) {
                    runnable.run();
                }

                @Override
                public boolean isMainThread() {
                    return true;
                }
            });

            historyActivity = spy(HistoryActivity.class);

            historyActivity.drawerLayout = mockDrawerLayout;

            historyActivity.dateSpinner = mockSpinner;
        }
    }

    @After
    public void tearDown() {
        if(mockedLooper != null) {
            mockedLooper.close();
        }
        if(mockedIntent != null) {
            mockedIntent.close();
        }

        ArchTaskExecutor.getInstance().setDelegate(null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testOnCreate() {
        Bundle mockBundle = mock(Bundle.class);

        doReturn(false).when(historyActivity).shouldCallSuperOnCreate();

        ActivityHistoryBinding mockBinding = mock(ActivityHistoryBinding.class);
        NavigationView mockNavView = mock(NavigationView.class);
        doReturn(mockBinding).when(historyActivity).getBinding();
        doNothing().when(historyActivity).setContentView(any());
        doNothing().when(historyActivity).setSupportActionBar(any());

        doReturn(mockDrawerLayout).when(historyActivity).getDrawerLayout(any());
        doReturn(mockNavView).when(historyActivity).getNavView(any());
        doNothing().when(mockNavView).setNavigationItemSelectedListener(any());

        ActionBarDrawerToggle mockDrawerToggle = mock(ActionBarDrawerToggle.class);
        doReturn(mockDrawerToggle).when(historyActivity).getActionBarDrawerToggle(any());
        doNothing().when(mockDrawerToggle).syncState();

        RecyclerView mockRecyclerView = mock(RecyclerView.class);
        doReturn(mockRecyclerView).when(historyActivity).getRecylerViewHistory(any());

        doReturn(mockDatabaseHelper).when(historyActivity).getDatabaseHelper();
        doReturn(dates).when(mockDatabaseHelper).getAllDates();

        doReturn(mockSpinner).when(historyActivity).getDateSpinner(any());

        // Mock ArrayAdapter
        doReturn(adapter).when(historyActivity).getArrayAdapter(dates);
        when(adapter.getCount()).thenReturn(dates.size());

        historyActivity.onCreate(mockBundle);

        verify(historyActivity.databaseHelper).getAllDates();
        verify(mockRecyclerView).setAdapter(any());

        ArgumentCaptor<ArrayAdapter<String>> captor = ArgumentCaptor.forClass(ArrayAdapter.class);
        verify(mockSpinner).setAdapter(captor.capture());
        ArrayAdapter<String> adapter = captor.getValue();
        assertNotNull(adapter);
        assertEquals(dates.size(), adapter.getCount());

        verify(mockSpinner).setOnItemSelectedListener(any());
    }

    @Test
    public void testShouldCallSuperOnCreate() {
        assertTrue(historyActivity.shouldCallSuperOnCreate());
    }

    @Test
    public void testOnNavigationItemSelected_MainActivity() {
        // Setup test
        when(mockMenuItem.getItemId()).thenReturn(R.id.nav_main_activity);

        paramOnNavigationItemSelected();
    }

    @Test
    public void testOnNavigationItemSelected_Stay() {
        // Setup test
        when(mockMenuItem.getItemId()).thenReturn(R.id.nav_history);

        paramOnNavigationItemSelected();

        verify(historyActivity, never()).startActivity(any(Intent.class));
    }

    private void paramOnNavigationItemSelected() {
        doNothing().when(historyActivity).startActivity(any(Intent.class));

        // Execute test
        boolean result = historyActivity.onNavigationItemSelected(mockMenuItem);

        // Verify results
        assertTrue(result);
        verify(mockDrawerLayout).closeDrawer(any(Integer.class));
    }

    @Test
    public void testOnKeyDown_BackNotPressed() {
        // Execute test
        boolean result = historyActivity.onKeyDown(KeyEvent.KEYCODE_0, mockKeyEvent);

        // Verify results
        assertFalse(result);
    }

    @Test
    public void testOnKeyDown_BackPressed_DrawerOpen() {
        // Setup test
        when(mockDrawerLayout.isDrawerOpen(anyInt())).thenReturn(true);

        // Execute test
        boolean result = historyActivity.onKeyDown(KeyEvent.KEYCODE_BACK, mockKeyEvent);

        // Verify results
        assertFalse(result);
        verify(mockDrawerLayout).closeDrawer(anyInt());
    }

    @Test
    public void initializeSpinner_shouldSetListenerAndAdapter() {
        // Given
        historyActivity.databaseHelper = mockDatabaseHelper;
        doReturn(dates).when(mockDatabaseHelper).getAllDates();
        doReturn(adapter).when(historyActivity).getArrayAdapter(dates);

        // When
        historyActivity.initializeSpinner();

        // Then
        verify(mockSpinner).setAdapter(adapter);
        verify(mockSpinner).setOnItemSelectedListener(any(AdapterView.OnItemSelectedListener.class));
    }

    @Test
    public void spinnerListener_onItemSelected_shouldCallLoadHistoryByDate() {
        // Given
        historyActivity.historyAdapter = mock(HistoryAdapter.class);
        historyActivity.databaseHelper = mockDatabaseHelper;
        doReturn(dates).when(mockDatabaseHelper).getAllDates();
        doReturn(adapter).when(historyActivity).getArrayAdapter(dates);

        ArgumentCaptor<AdapterView.OnItemSelectedListener> captor = ArgumentCaptor.forClass(AdapterView.OnItemSelectedListener.class);

        // When
        historyActivity.initializeSpinner();

        // Then
        verify(mockSpinner).setOnItemSelectedListener(captor.capture());
        AdapterView.OnItemSelectedListener listener = captor.getValue();

        // Simule la sélection
        AdapterView<?> parent = mock(AdapterView.class);
        when(parent.getItemAtPosition(1)).thenReturn("2023-10-27");

        listener.onItemSelected(parent, null, 1, 0);

        verify(historyActivity).loadHistoryByDate("2023-10-27");
    }
}