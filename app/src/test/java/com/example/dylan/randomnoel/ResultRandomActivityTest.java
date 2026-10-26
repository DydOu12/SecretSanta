package com.example.dylan.randomnoel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.dylan.randomnoel.databinding.ActivityRandomBinding;
import com.google.android.material.navigation.NavigationView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;

@RunWith(MockitoJUnitRunner.class)
public class ResultRandomActivityTest {

    @Mock
    private DrawerLayout mockDrawerLayout;
    @Mock
    private Button mockButton;
    @Mock
    private ListView mockListView;
    @Mock
    private MenuItem mockMenuItem;
    @Mock
    private KeyEvent mockKeyEvent;
    @Mock
    private DatabaseHelper mockDatabaseHelper;

    private MockedStatic<Toast> mockedToast;

    private MockedStatic<Looper> mockedLooper;

    private MockedConstruction<Intent> mockedIntent;

    private ResultRandom resultRandomActivitySpy;

    private final ArrayList<Nom> listeNomsAssocies = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        try(AutoCloseable ignored = MockitoAnnotations.openMocks(this)) {

            mockedToast = mockStatic(Toast.class);
            mockedToast.when(() -> Toast.makeText(any(Context.class), any(CharSequence.class), anyInt())).thenReturn(mock(Toast.class));

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

            resultRandomActivitySpy = spy(ResultRandom.class);

            resultRandomActivitySpy.drawerLayout = mockDrawerLayout;

            Nom nom1 = new Nom("Nom1", "1");
            nom1.setGroupe(1);
            Nom nom2 = new Nom("Nom2", "2");
            nom2.setGroupe(2);
            listeNomsAssocies.add(nom1);
            listeNomsAssocies.add(nom2);

            resultRandomActivitySpy.association = new HashMap<>();
            resultRandomActivitySpy.association.put(nom1, nom2);
            resultRandomActivitySpy.association.put(nom2, nom1);

            resultRandomActivitySpy.databaseHelper = mockDatabaseHelper;
        }
    }

    @After
    public void tearDown() {
        if(mockedToast != null) {
            mockedToast.close();
        }
        if(mockedLooper != null) {
            mockedLooper.close();
        }
        if(mockedIntent != null) {
            mockedIntent.close();
        }

        ArchTaskExecutor.getInstance().setDelegate(null);
    }

    @Test
    public void testOnCreate() {
        Bundle mockBundle = mock(Bundle.class);
        doReturn(new SaveAssociation(resultRandomActivitySpy.association)).when(mockBundle).getParcelable(anyString());
        paramOnCreate();

        resultRandomActivitySpy.onCreate(mockBundle);

        doReturn(null).when(mockBundle).getParcelable(anyString());
        resultRandomActivitySpy.onCreate(mockBundle);

        verify(mockListView, times(2)).setAdapter(any());
    }

    @Test
    public void testOnCreateWithAtLeastTiramisuVersionAndNotNullBundle() {
        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(Utils::isAtLeastUpsideDownCakeVersion).thenReturn(true);
            mockedUtils.when(Utils::isAtLeastTiramisuVersion).thenReturn(true);

            Bundle mockBundle = mock(Bundle.class);
            paramOnCreate();
            resultRandomActivitySpy.onCreate(mockBundle);
            verify(mockButton).setOnClickListener(any());
        }

    }

    @Test
    public void testOnCreateWithAtLeastTiramisuVersionAndNullBundle() {
        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(Utils::isAtLeastUpsideDownCakeVersion).thenReturn(true);
            mockedUtils.when(Utils::isAtLeastTiramisuVersion).thenReturn(true);

            paramOnCreate();
            paramOnCreateWithNullBundle();
            doReturn(new ArrayList<Nom>()).when(resultRandomActivitySpy).choixPossible(anyList(), any(Nom.class));
            doNothing().when(resultRandomActivitySpy).finish();

            resultRandomActivitySpy.onCreate(null);
            verify(mockButton).setOnClickListener(any());
        }

    }

    @Test
    public void testOnCreateWithNullBundle() {
        paramOnCreate();
        paramOnCreateWithNullBundle();
        doReturn(new ArrayList<Nom>()).when(resultRandomActivitySpy).choixPossible(anyList(), any(Nom.class));
        doNothing().when(resultRandomActivitySpy).finish();

        resultRandomActivitySpy.onCreate(null);

        verify(mockButton).setOnClickListener(any());
    }

    private void paramOnCreateWithNullBundle() {
        Intent mockIntent = mock(Intent.class);
        doReturn(mockIntent).when(resultRandomActivitySpy).getIntent();
        when(mockIntent.getParcelableArrayListExtra("listeNoms", Nom.class)).thenReturn(listeNomsAssocies);
        when(mockIntent.getParcelableArrayListExtra("listeNoms")).thenReturn((ArrayList) listeNomsAssocies);
    }

    private void paramOnCreate() {
        doReturn(mockListView).when(resultRandomActivitySpy).findViewById(R.id.listeNomsAssocies);
        doReturn(mockButton).when(resultRandomActivitySpy).findViewById(R.id.sms);

        doReturn(false).when(resultRandomActivitySpy).shouldCallSuperOnCreate();

        ActivityRandomBinding mockBinding = mock(ActivityRandomBinding.class);
        NavigationView mockNavView = mock(NavigationView.class);
        doReturn(mockBinding).when(resultRandomActivitySpy).getBinding();
        doNothing().when(resultRandomActivitySpy).setContentView(any());
        doNothing().when(resultRandomActivitySpy).setSupportActionBar(any());

        doReturn(mockDrawerLayout).when(resultRandomActivitySpy).getDrawerLayout(any());
        doReturn(mockNavView).when(resultRandomActivitySpy).getNavView(any());
        doNothing().when(mockNavView).setNavigationItemSelectedListener(any());

        ActionBarDrawerToggle mockDrawerToggle = mock(ActionBarDrawerToggle.class);
        doReturn(mockDrawerToggle).when(resultRandomActivitySpy).getActionBarDrawerToggle(any());
        doNothing().when(mockDrawerToggle).syncState();
    }

    @Test
    public void testShouldCallSuperOnCreate() {
        assertTrue(resultRandomActivitySpy.shouldCallSuperOnCreate());
    }

    @Test
    public void testOnNavigationItemSelected_History() {
        // Setup test
        when(mockMenuItem.getItemId()).thenReturn(R.id.nav_history);

        paramOnNavigationItemSelected();
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
        when(mockMenuItem.getItemId()).thenReturn(0);

        paramOnNavigationItemSelected();

        verify(resultRandomActivitySpy, never()).startActivity(any(Intent.class));
    }

    private void paramOnNavigationItemSelected() {
        doNothing().when(resultRandomActivitySpy).startActivity(any(Intent.class));

        // Execute test
        boolean result = resultRandomActivitySpy.onNavigationItemSelected(mockMenuItem);

        // Verify results
        assertTrue(result);
        verify(mockDrawerLayout).closeDrawer(any(Integer.class));
    }

    @Test
    public void testOnKeyDown_BackNotPressed() {
        // Execute test
        boolean result = resultRandomActivitySpy.onKeyDown(KeyEvent.KEYCODE_0, mockKeyEvent);

        // Verify results
        assertFalse(result);
    }

    @Test
    public void testOnKeyDown_BackPressed_DrawerOpen() {
        // Setup test
        when(mockDrawerLayout.isDrawerOpen(anyInt())).thenReturn(true);

        // Execute test
        boolean result = resultRandomActivitySpy.onKeyDown(KeyEvent.KEYCODE_BACK, mockKeyEvent);

        // Verify results
        assertFalse(result);
        verify(mockDrawerLayout).closeDrawer(anyInt());
    }

    @Test
    public void testOnClick_SmsFailed() {
        try(MockedStatic<ContextCompat> mockedContextCompat = mockStatic(ContextCompat.class);
            MockedStatic<Log> mockedLog = mockStatic(Log.class)) {

            mockedContextCompat.when(() -> ContextCompat.checkSelfPermission(any(Context.class), anyString())).thenReturn(0);
            mockedLog.when(() -> Log.e(anyString(), anyString(), any(Throwable.class))).thenReturn(0);
            doReturn(resultRandomActivitySpy).when(resultRandomActivitySpy).getApplicationContext();
            resultRandomActivitySpy.onClick(mockButton);
            verify(mockDatabaseHelper, times(2)).insertAssociation(anyString(), anyString());
            mockedToast.verify(() -> Toast.makeText(eq(resultRandomActivitySpy), eq("Your sms has failed... Method getSystemService in android.content.Context not mocked. See https://developer.android.com/r/studio-ui/build/not-mocked for details."), anyInt()));
        }
    }

    @Test
    public void testOnClick_withoutPermissions() {
        try(MockedStatic<ContextCompat> mockedContextCompat = mockStatic(ContextCompat.class)) {

            mockedContextCompat.when(() -> ContextCompat.checkSelfPermission(any(Context.class), anyString())).thenReturn(1);
            doNothing().when(resultRandomActivitySpy).requestPermissions(any(String[].class), anyInt());
            resultRandomActivitySpy.onClick(mockButton);
            verify(resultRandomActivitySpy).requestPermissions(any(String[].class), anyInt());
        }
    }

    @Test
    public void testOnClickSms_Success() {
        try(MockedStatic<ContextCompat> mockedContextCompat = mockStatic(ContextCompat.class);
            MockedStatic<Log> mockedLog = mockStatic(Log.class)) {

            mockedContextCompat.when(() -> ContextCompat.checkSelfPermission(any(Context.class), anyString())).thenReturn(0);
            mockedLog.when(() -> Log.e(anyString(), anyString(), any(Throwable.class))).thenReturn(0);
            doReturn(resultRandomActivitySpy).when(resultRandomActivitySpy).getApplicationContext();
            doReturn(mock(SmsManager.class)).when(resultRandomActivitySpy).getSystemService(SmsManager.class);

            resultRandomActivitySpy.onClick(mockButton);
            verify(mockDatabaseHelper, times(2)).insertAssociation(anyString(), anyString());
            mockedToast.verify(() -> Toast.makeText(eq(resultRandomActivitySpy), eq("Your sms has successfully sent!"), anyInt()), times(2));

        }
    }

    @Test
    public void testOnRequestPermissionResult_RequestNoSMS() {
        doReturn(false).when(resultRandomActivitySpy).shouldCallSuperOnRequestPermissionsResult();
        resultRandomActivitySpy.onRequestPermissionsResult(1, new String[0], new int[0]);
        verify(resultRandomActivitySpy, never()).sendSMSBySmsManager();
    }

    @Test
    public void testOnRequestPermissionResult_NotGranted() {
        try(MockedStatic<Log> mockedLog = mockStatic(Log.class)) {
            mockedLog.when(() -> Log.i(anyString(), anyString())).thenReturn(0);
            doReturn(false).when(resultRandomActivitySpy).shouldCallSuperOnRequestPermissionsResult();
            resultRandomActivitySpy.onRequestPermissionsResult(100, new String[0], new int[0]);
            verify(resultRandomActivitySpy, never()).sendSMSBySmsManager();
            mockedToast.verify(() -> Toast.makeText(eq(resultRandomActivitySpy), eq("Permission denied!"), anyInt()));
        }
    }

    @Test
    public void testOnRequestPermissionResult_Granted() {
        try(MockedStatic<Log> mockedLog = mockStatic(Log.class)) {
            mockedLog.when(() -> Log.i(anyString(), anyString())).thenReturn(0);
            doReturn(resultRandomActivitySpy).when(resultRandomActivitySpy).getApplicationContext();            doReturn(false).when(resultRandomActivitySpy).shouldCallSuperOnRequestPermissionsResult();
            resultRandomActivitySpy.onRequestPermissionsResult(100, new String[0], new int[]{0});
            verify(resultRandomActivitySpy).sendSMSBySmsManager();
            mockedToast.verify(() -> Toast.makeText(eq(resultRandomActivitySpy), eq("Permission granted!"), anyInt()));
        }
    }

    @Test
    public void testOnActivityResult_doNothing() {
        doReturn(false).when(resultRandomActivitySpy).shoudCallSuperOnActivityResult();
        resultRandomActivitySpy.onActivityResult(1, 0, null);
        mockedToast.verify(() -> Toast.makeText(any(), anyString(), anyInt()), never());
    }

    @Test
    public void testOnActivityResult_actionOK() {
        doReturn(false).when(resultRandomActivitySpy).shoudCallSuperOnActivityResult();
        resultRandomActivitySpy.onActivityResult(100, -1, null);
        mockedToast.verify(() -> Toast.makeText(eq(resultRandomActivitySpy), eq("Action OK"), anyInt()));
    }

    @Test
    public void testOnActivityResult_actionCanceled() {
        doReturn(false).when(resultRandomActivitySpy).shoudCallSuperOnActivityResult();
        resultRandomActivitySpy.onActivityResult(100, 0, null);
        mockedToast.verify(() -> Toast.makeText(eq(resultRandomActivitySpy), eq("Action canceled"), anyInt()));
    }

    @Test
    public void testOnActivityResult_actionFailed() {
        doReturn(false).when(resultRandomActivitySpy).shoudCallSuperOnActivityResult();
        resultRandomActivitySpy.onActivityResult(100, 1, null);
        mockedToast.verify(() -> Toast.makeText(eq(resultRandomActivitySpy), eq("Action Failed"), anyInt()));
    }
}