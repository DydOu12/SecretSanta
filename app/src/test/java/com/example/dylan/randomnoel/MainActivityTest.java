package com.example.dylan.randomnoel;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import static com.example.dylan.randomnoel.MainActivity.PERMISSIONS_REQUEST_READ_CONTACTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.dylan.randomnoel.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

@RunWith(MockitoJUnitRunner.class)
public class MainActivityTest {

    @Mock
    private View mockView;

    @Mock
    private DrawerLayout mockDrawerLayout;

    @Mock
    private ListView mockListView;

    @Mock
    private EditText mockEditText;

    @Mock
    private Spinner mockSpinner;

    @Mock
    private MenuItem mockMenuItem;

    @Mock
    private Cursor mockCursor;

    @Mock
    private KeyEvent mockKeyEvent;

    private MockedStatic<Toast> mockedToast;

    private MockedStatic<Looper> mockedLooper;

    private MockedConstruction<Intent> mockedIntent;

    private MainActivity mainActivitySpy;

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

            mainActivitySpy = spy(MainActivity.class);

            // Set up mocked fields
            mainActivitySpy.listNoms = mockListView;

            mainActivitySpy.drawerLayout = mockDrawerLayout;

            // Initialiser la liste noms
            mainActivitySpy.noms = new ArrayList<>();
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

        doReturn(mockListView).when(mainActivitySpy).findViewById(R.id.listeNoms);

        doReturn(false).when(mainActivitySpy).shouldCallSuperOnCreate();

        ActivityMainBinding mockBinding = mock(ActivityMainBinding.class);
        NavigationView mockNavView = mock(NavigationView.class);
        doReturn(mockBinding).when(mainActivitySpy).getBinding();
        doNothing().when(mainActivitySpy).setContentView(any());
        doNothing().when(mainActivitySpy).setSupportActionBar(any());

        doReturn(mockDrawerLayout).when(mainActivitySpy).getDrawerLayout(any());
        doReturn(mockNavView).when(mainActivitySpy).getNavView(any());
        doNothing().when(mockNavView).setNavigationItemSelectedListener(any());

        ActionBarDrawerToggle mockDrawerToggle = mock(ActionBarDrawerToggle.class);
        doReturn(mockDrawerToggle).when(mainActivitySpy).getActionBarDrawerToggle(any());
        doNothing().when(mockDrawerToggle).syncState();

        doReturn(mockEditText).when(mainActivitySpy).getGroupeEditText(any());
        doReturn(mock(Button.class)).when(mainActivitySpy).getAjouterButton(any());

        doNothing().when(mainActivitySpy).accordPermission();

        doReturn(mock(Button.class)).when(mainActivitySpy).findViewById(R.id.random);

        mainActivitySpy.onCreate(mockBundle);

        verify(mockListView).setAdapter(any());
    }

    @Test
    public void testShouldCallSuperOnCreate() {
        assertTrue(mainActivitySpy.shouldCallSuperOnCreate());
    }

    @Test
    public void testOnNavigationItemSelected_History() {
        // Setup test
        when(mockMenuItem.getItemId()).thenReturn(R.id.nav_history);

        paramOnNavigationItemSelected();
    }

    @Test
    public void testOnNavigationItemSelected_Stay() {
        // Setup test
        when(mockMenuItem.getItemId()).thenReturn(R.id.nav_main_activity);

        paramOnNavigationItemSelected();

        verify(mainActivitySpy, never()).startActivity(any(Intent.class));
    }

    private void paramOnNavigationItemSelected() {
        doNothing().when(mainActivitySpy).startActivity(any(Intent.class));

        // Execute test
        boolean result = mainActivitySpy.onNavigationItemSelected(mockMenuItem);

        // Verify results
        assertTrue(result);
        verify(mockDrawerLayout).closeDrawer(any(Integer.class));
    }

    @Test
    public void testOnKeyDown_BackNotPressed() {
        // Execute test
        boolean result = mainActivitySpy.onKeyDown(KeyEvent.KEYCODE_0, mockKeyEvent);

        // Verify results
        assertFalse(result);
    }

    @Test
    public void testOnKeyDown_BackPressed_DrawerOpen() {
        // Setup test
        when(mockDrawerLayout.isDrawerOpen(anyInt())).thenReturn(true);

        // Execute test
        boolean result = mainActivitySpy.onKeyDown(KeyEvent.KEYCODE_BACK, mockKeyEvent);

        // Verify results
        assertFalse(result);
        verify(mockDrawerLayout).closeDrawer(anyInt());
    }

    @Test
    public void testOnClick_NotEnoughPeople() {
        // Setup test
        mainActivitySpy.noms = new ArrayList<>(); // Empty list = not enough people

        mainActivitySpy.onClick(mockView);

        mockedToast.verify(() -> Toast.makeText(eq(mainActivitySpy), eq("Entrez au moins deux personnes !"), anyInt()));
    }

    @Test
    public void testOnClick_ImpossibleAssociation() {

        // Setup noms with impossible association
        List<Nom> testNoms = new ArrayList<>();
        testNoms.add(new Nom("Test1", "123", 1, Color.RED));
        testNoms.add(new Nom("Test2", "456", 1, Color.RED));
        testNoms.add(new Nom("Test3", "789", 1, Color.RED));
        mainActivitySpy.noms = testNoms;

        // Mock getNumberOfPersonByGroupe to return a map
        Map<Integer, Integer> groupMap = new HashMap<>();
        groupMap.put(1, 3); // All 3 in the same group - impossible association
        doReturn(groupMap).when(mainActivitySpy).getNumberOfPersonByGroupe();

        // Execute test
        mainActivitySpy.onClick(mockView);

        mockedToast.verify(() -> Toast.makeText(eq(mainActivitySpy), eq("Association impossible !"), anyInt()));
    }

    @Test
    public void testOnClick_ModifyNames() {
        List<Nom> noms = new ArrayList<>();
        noms.add(new Nom("Test1", "123", 1, Color.RED));
        noms.add(new Nom("Test2", "456", 1, Color.RED));
        mainActivitySpy.noms = noms;

        View child1 = mock(View.class);
        View child2 = mock(View.class);
        EditText editText1 = mock(EditText.class);
        EditText editText2 = mock(EditText.class);

        when(mockListView.getChildCount()).thenReturn(2);
        when(mockListView.getChildAt(0)).thenReturn(child1);
        when(mockListView.getChildAt(1)).thenReturn(child2);
        when(child1.findViewById(R.id.nomContactInList)).thenReturn(editText1);
        when(child2.findViewById(R.id.nomContactInList)).thenReturn(editText2);

        when(editText1.getText()).thenReturn(mock(Editable.class));
        when(editText2.getText()).thenReturn(mock(Editable.class));

        when(editText1.getText().toString()).thenReturn("Modified1");
        when(editText2.getText().toString()).thenReturn("Modified2");

        mainActivitySpy.listNoms = mockListView;

        mainActivitySpy.onClick(mockView);

        assertEquals("Modified1", noms.get(0).getName());
        assertEquals("Modified2", noms.get(1).getName());
    }

    @Test
    public void testOnClick_StartActivity() {

        // Setup noms with impossible association
        List<Nom> testNoms = new ArrayList<>();
        testNoms.add(new Nom("Test1", "123", 1, Color.RED));
        testNoms.add(new Nom("Test2", "456", 2, Color.GREEN));
        mainActivitySpy.noms = testNoms;

        // Mock associationPossible to return true
        doReturn(true).when(mainActivitySpy).associationPossible(anyMap());

        doNothing().when(mainActivitySpy).startActivity(any(Intent.class));

        // Execute test
        mainActivitySpy.onClick(mockView);

        verify(mainActivitySpy).startActivity(any(Intent.class));
    }

    @Test
    public void testGetNoms() {
        // Setup test data
        List<Nom> testNoms = new ArrayList<>();
        testNoms.add(new Nom("Test1", "123"));
        testNoms.add(new Nom("Test2", "456"));
        mainActivitySpy.noms = testNoms;

        // Execute test
        List<Nom> result = MainActivity.getNoms(mainActivitySpy);

        // Verify results
        assertEquals(testNoms, result);
        assertEquals(2, result.size());
        assertEquals("Test1", result.get(0).getName());
        assertEquals("Test2", result.get(1).getName());
    }

    @Test
    public void testAccordPermission_PersmissionGranted() {
        try(MockedStatic<ContextCompat> mock = mockStatic(ContextCompat.class)) {
            mock.when(() ->
                    ContextCompat.checkSelfPermission(mainActivitySpy, Manifest.permission.READ_CONTACTS)
            ).thenReturn(PackageManager.PERMISSION_GRANTED);

            doNothing().when(mainActivitySpy).addContact();

            mainActivitySpy.accordPermission();

            verify(mainActivitySpy).addContact();
        }
    }

    @Test
    public void testAccordPermission_PersmissionDenied() {
        try(MockedStatic<ContextCompat> mock = mockStatic(ContextCompat.class)) {
            mock.when(() ->
                    ContextCompat.checkSelfPermission(mainActivitySpy, Manifest.permission.READ_CONTACTS)
            ).thenReturn(PackageManager.PERMISSION_DENIED);

            doNothing().when(mainActivitySpy).requestPermissions(any(String[].class), anyInt());

            mainActivitySpy.accordPermission();

            verify(mainActivitySpy).requestPermissions(
                new String[]{Manifest.permission.READ_CONTACTS},
                PERMISSIONS_REQUEST_READ_CONTACTS
            );
        }
    }

    @Test
    public void testAddContact_ProcessesCursorData() {
        // Setup mock cursor behavior
        ContentResolver mockContentResolver = mock(ContentResolver.class);

        doReturn(mockContentResolver).when(mainActivitySpy).getContentResolver();
        when(mockContentResolver.query(any(), any(), any(), any(), any())).thenReturn(mockCursor);

        Spinner mockSpinner = mock(Spinner.class);
        doReturn(mockSpinner).when(mainActivitySpy).findViewById(R.id.nomContact);

        when(mockCursor.moveToNext()).thenReturn(true, true, false); // Return true twice, then false
        when(mockCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)).thenReturn(0);
        when(mockCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).thenReturn(1);
        when(mockCursor.getString(0)).thenReturn("John Doe", "Jane Smith");
        when(mockCursor.getString(1)).thenReturn("1234567890", "0987654321");

        mainActivitySpy.addContact();

        assertEquals(2, mainActivitySpy.contacts.size());
        assertEquals("John Doe", mainActivitySpy.contacts.get(0).getName());
        assertEquals("1234567890", mainActivitySpy.contacts.get(0).getNumero());
        assertEquals("Jane Smith", mainActivitySpy.contacts.get(1).getName());
        assertEquals("0987654321", mainActivitySpy.contacts.get(1).getNumero());

        verify(mockCursor).close();
    }

    @Test
    public void testGetNumberOfPersonByGroupe() {
        // Setup test data
        List<Nom> testNoms = new ArrayList<>();
        testNoms.add(new Nom("Test1", "123", 1, Color.RED));
        testNoms.add(new Nom("Test2", "456", 1, Color.RED));
        testNoms.add(new Nom("Test3", "789", 2, Color.BLUE));
        testNoms.add(new Nom("Test4", "012", 3, Color.GREEN));
        testNoms.add(new Nom("Test5", "345", 3, Color.GREEN));
        mainActivitySpy.noms = testNoms;

        // Execute test
        Map<Integer, Integer> result = mainActivitySpy.getNumberOfPersonByGroupe();

        // Verify results
        assertEquals(3, result.size());
        assertEquals(Integer.valueOf(2), result.get(1));
        assertEquals(Integer.valueOf(1), result.get(2));
        assertEquals(Integer.valueOf(2), result.get(3));
    }

    @Test
    public void testAssociationPossible_True() {
        // Setup test data for a possible association
        Map<Integer, Integer> testData = new HashMap<>();
        testData.put(1, 2); // 2 people in group 1
        testData.put(2, 1); // 1 person in group 2
        testData.put(3, 2); // 2 people in group 3

        // Execute test
        boolean result = mainActivitySpy.associationPossible(testData);

        // Verify results
        assertTrue(result);
    }

    @Test
    public void testAssociationPossible_False() {
        // Setup test data for an impossible association
        Map<Integer, Integer> testData = new HashMap<>();
        testData.put(1, 7); // 7 people in group 1
        testData.put(2, 1); // 1 person in group 2
        testData.put(3, 2); // 2 people in group 3

        // Execute test
        boolean result = mainActivitySpy.associationPossible(testData);

        // Verify results
        assertFalse(result);
    }

    @Test
    public void testGetCouleurIfGroupePresent_GroupExists() {
        // Setup test data
        List<Nom> testNoms = new ArrayList<>();
        testNoms.add(new Nom("Test1", "123", 1, Color.RED));
        testNoms.add(new Nom("Test2", "456", 2, Color.BLUE));
        mainActivitySpy.noms = testNoms;

        // Execute test
        Integer result = mainActivitySpy.getCouleurIfGroupePresent(1);

        // Verify results
        assertNotNull(result);
        assertEquals(Integer.valueOf(Color.RED), result);
    }

    @Test
    public void testGetCouleurIfGroupePresent_GroupDoesNotExist() {
        // Setup test data
        List<Nom> testNoms = new ArrayList<>();
        testNoms.add(new Nom("Test1", "123", 1, Color.RED));
        testNoms.add(new Nom("Test2", "456", 2, Color.BLUE));
        mainActivitySpy.noms = testNoms;

        // Execute test
        Integer result = mainActivitySpy.getCouleurIfGroupePresent(3);

        // Verify results
        assertNull(result);
    }

    @Test
    public void testEgg() {
        // Cas 1 : remainingClickEasterEgg > 3
        mainActivitySpy.remainingClickEasterEgg = 4;
        mainActivitySpy.egg();
        assertEquals(3, mainActivitySpy.remainingClickEasterEgg);
        mockedToast.verify(() -> Toast.makeText(any(), any(), anyInt()), never());

        // Cas 2 : remainingClickEasterEgg = 3
        mainActivitySpy.remainingClickEasterEgg = 3;
        mainActivitySpy.egg();
        assertEquals(2, mainActivitySpy.remainingClickEasterEgg);
        mockedToast.verify(() -> Toast.makeText(eq(mainActivitySpy), eq("2 clics avant easter egg :)"), anyInt()));

        // Cas 3 : remainingClickEasterEgg = 2
        mainActivitySpy.remainingClickEasterEgg = 2;
        mainActivitySpy.egg();
        assertEquals(1, mainActivitySpy.remainingClickEasterEgg);
        mockedToast.verify(() -> Toast.makeText(eq(mainActivitySpy), eq("1 clics avant easter egg :)"), anyInt()));

        // Cas 4 : remainingClickEasterEgg = 1
        mainActivitySpy.remainingClickEasterEgg = 1;
        mainActivitySpy.egg();
        assertEquals(0, mainActivitySpy.remainingClickEasterEgg);
        assertFalse(mainActivitySpy.noms.isEmpty());
        verify(mockListView).setAdapter(any());
    }

    @Test
    public void testAjouter_Success() {
        // Setup mocks
        doReturn(mockSpinner).when(mainActivitySpy).findViewById(R.id.nomContact);
        doReturn(mockEditText).when(mainActivitySpy).findViewById(R.id.groupe);

        // Mock spinner selected item
        Nom mockSelectedNom = new Nom("Test Contact", "123456789");
        when(mockSpinner.getSelectedItem()).thenReturn(mockSelectedNom);

        // Mock EditText to return a group number
        when(mockEditText.getText()).thenReturn(mock(android.text.Editable.class));
        when(mockEditText.getText().toString()).thenReturn("1");

        // Mock the list
        mainActivitySpy.noms = new ArrayList<>();

        // Mock color
        doReturn(null).when(mainActivitySpy).getCouleurIfGroupePresent(anyInt());
        try(MockedStatic<Color> mockColor = mockStatic(Color.class)) {
            mockColor.when(() -> Color.rgb(anyInt(), anyInt(), anyInt())).thenReturn(Color.RED);

            // Execute test
            mainActivitySpy.ajouter();

            // Verify that a new Nom was added to the list
            assertEquals(1, mainActivitySpy.noms.size());
            assertEquals("Test Contact", mainActivitySpy.noms.get(0).getName());
            assertEquals(Integer.valueOf(1), mainActivitySpy.noms.get(0).getGroupe());
        }
    }

    @Test
    public void testAjouter_EmptyGroup() {
        // Mock spinner selected item
        Nom mockSelectedNom = new Nom("Test Contact", "123456789");
        when(mockSpinner.getSelectedItem()).thenReturn(mockSelectedNom);

        // Mock EditText to return an empty group
        when(mockEditText.getText()).thenReturn(mock(android.text.Editable.class));
        when(mockEditText.getText().toString()).thenReturn("");
        verifyAjouter();
    }

    @Test
    public void testAjouter_EmptyNom() {
        // Mock spinner selected item
        Nom mockSelectedNom = new Nom("Test Contact", "123456789");
        mockSelectedNom.setName("");
        when(mockSpinner.getSelectedItem()).thenReturn(mockSelectedNom);

        // Mock EditText to return an empty group
        when(mockEditText.getText()).thenReturn(mock(android.text.Editable.class));
        when(mockEditText.getText().toString()).thenReturn("1");

        verifyAjouter();
    }

    private void verifyAjouter() {
        // Setup mocks
        doReturn(mockSpinner).when(mainActivitySpy).findViewById(R.id.nomContact);
        doReturn(mockEditText).when(mainActivitySpy).findViewById(R.id.groupe);

        // Execute test
        mainActivitySpy.ajouter();

        // Verify that no Nom was added (since group is empty)
        assertEquals(0, mainActivitySpy.noms.size());
    }

    @Test
    public void testAjouter_AlreadyPresent() {
        // Setup mocks
        doReturn(mockSpinner).when(mainActivitySpy).findViewById(R.id.nomContact);
        doReturn(mockEditText).when(mainActivitySpy).findViewById(R.id.groupe);

        // Mock spinner selected item
        Nom mockSelectedNom = new Nom("Test Contact", "123456789");
        when(mockSpinner.getSelectedItem()).thenReturn(mockSelectedNom);

        // Mock EditText to return a group number
        when(mockEditText.getText()).thenReturn(mock(android.text.Editable.class));
        when(mockEditText.getText().toString()).thenReturn("1");

        // Mock the list with the contact already present
        List<Nom> testNoms = new ArrayList<>();
        testNoms.add(new Nom("Test Contact", "123456789"));
        mainActivitySpy.noms = testNoms;

        // Execute test
        mainActivitySpy.ajouter();

        // Verify that no new Nom was added
        assertEquals(1, mainActivitySpy.noms.size());
    }

    @Test
    public void testAjouter_WithExistingGroupColor() {
        // Setup mocks
        doReturn(mockSpinner).when(mainActivitySpy).findViewById(R.id.nomContact);
        doReturn(mockEditText).when(mainActivitySpy).findViewById(R.id.groupe);

        // Mock spinner selected item
        Nom mockSelectedNom = new Nom("Test Contact", "123456789");
        when(mockSpinner.getSelectedItem()).thenReturn(mockSelectedNom);

        // Mock EditText to return a group number
        when(mockEditText.getText()).thenReturn(mock(android.text.Editable.class));
        when(mockEditText.getText().toString()).thenReturn("1");

        // Mock the list
        mainActivitySpy.noms = new ArrayList<>();

        // Mock existing color for group
        doReturn(Color.RED).when(mainActivitySpy).getCouleurIfGroupePresent(1);

        // Execute test
        mainActivitySpy.ajouter();

        // Verify that the new Nom has the existing color
        assertEquals(1, mainActivitySpy.noms.size());
        assertEquals(Integer.valueOf(Color.RED), mainActivitySpy.noms.get(0).getCouleur());
    }
}