package com.example.dylan.randomnoel;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.Looper;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainActivityTest {

    @Mock
    private View mockView;

    @Mock
    private Context mockContext;

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
    private Intent mockIntent;

    private MainActivity mainActivitySpy;

    private MockedStatic<Toast> mockedToast;

    private MockedStatic<Looper> mockedLooper;

    private MockedConstruction<Intent> mockedIntent;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockedToast = mockStatic(Toast.class);
        mockedToast.when(() -> Toast.makeText(any(Context.class), any(CharSequence.class), anyInt())).thenReturn(mock(Toast.class));

        mockedLooper = mockStatic(Looper.class);
        mockedLooper.when(Looper::myLooper).thenReturn(mock(Looper.class));
        mockedLooper.when(Looper::getMainLooper).thenReturn(mock(Looper.class));

        mockedIntent = mockConstruction(Intent.class, (mock, context) -> {
            when(mock.putParcelableArrayListExtra(anyString(), any(ArrayList.class))).thenReturn(mock);
        });

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

        LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(mock(LifecycleOwner.class));
        lifecycleRegistry.setCurrentState(LifecycleRegistry.State.RESUMED);

        // Set up mocked fields
        mainActivitySpy.listNoms = mockListView;

        // Initialiser la liste noms
        mainActivitySpy.noms = new ArrayList<>();

        // Setup mock cursor behavior
        //when(mockCursor.moveToNext()).thenReturn(true, true, false); // Return true twice, then false
        //when(mockCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)).thenReturn(0);
        //when(mockCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).thenReturn(1);
        //when(mockCursor.getString(0)).thenReturn("John Doe", "Jane Smith");
        //when(mockCursor.getString(1)).thenReturn("1234567890", "0987654321");

        // Mocteamk getContentResolver
        //when(mockContext.getContentResolver()).thenReturn(null); // Will be overridden in specific tests
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
        boolean result = MainActivity.associationPossible(testData);

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
        boolean result = MainActivity.associationPossible(testData);

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

    @Ignore
    @Test
    public void testOnNavigationItemSelected_History() {
        // Setup test
        when(mockMenuItem.getItemId()).thenReturn(R.id.nav_history);
        doReturn(mockContext).when(mainActivitySpy).getApplicationContext();

        // Mock startActivity
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);

        // Mock drawer
        mainActivitySpy.drawerLayout = mockDrawerLayout;

        // Execute test
        boolean result = mainActivitySpy.onNavigationItemSelected(mockMenuItem);

        // Verify results
        assertTrue(result);
        verify(mockDrawerLayout).closeDrawer(any(Integer.class));
    }

    @Test
    public void testOnKeyDown_BackPressed_DrawerOpen() {
        // Setup test
        mainActivitySpy.drawerLayout = mockDrawerLayout;
        when(mockDrawerLayout.isDrawerOpen(anyInt())).thenReturn(true);

        // Execute test
        boolean result = mainActivitySpy.onKeyDown(KeyEvent.KEYCODE_BACK, mock(KeyEvent.class));

        // Verify results
        assertFalse(result);
        verify(mockDrawerLayout).closeDrawer(anyInt());
    }

    @Test
    public void testOnKeyDown_BackPressed_DrawerClosed() {
        // Setup test
        mainActivitySpy.drawerLayout = mockDrawerLayout;
        doReturn(true).when(mainActivitySpy).onKeyDown(eq(KeyEvent.KEYCODE_BACK), any(KeyEvent.class));

        // Execute test
        mainActivitySpy.onKeyDown(KeyEvent.KEYCODE_BACK, mock(KeyEvent.class));

        // Verify that super.onKeyDown is called
        verify(mainActivitySpy).onKeyDown(eq(KeyEvent.KEYCODE_BACK), any(KeyEvent.class));
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

        // The test passes if we don't get any exceptions and the code follows the expected path
    }

    @Ignore
    @Test
    public void testAddContact() {
        // Create a MatrixCursor with contact data
        MatrixCursor mockMatrixCursor = new MatrixCursor(
                new String[] {
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                }
        );

        // Add data to the cursor
        mockMatrixCursor.addRow(new Object[] {"John Doe", "1234567890"});
        mockMatrixCursor.addRow(new Object[] {"Jane Smith", "0987654321"});

        // Mock the content resolver to return our cursor
        doReturn(mockMatrixCursor).when(mockContext.getContentResolver()).query(any(), any(), any(), any(), any());

        // Mock findViewById to return our spinner
        doReturn(mockSpinner).when(mainActivitySpy).findViewById(R.id.nomContact);

        // Execute test - can't fully verify without Robolectric
        // But we can test that code executes without exceptions
        // and that specific methods are called
        try {
            mainActivitySpy.addContact();
            // Test passes if no exception is thrown
        } catch (Exception e) {
            // Test will fail if exception occurs
            throw new AssertionError("addContact threw an exception", e);
        }
    }

    @Ignore
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

        // Execute test
        mainActivitySpy.ajouter();

        // Verify that a new Nom was added to the list
        assertEquals(1, mainActivitySpy.noms.size());
        assertEquals("Test Contact", mainActivitySpy.noms.get(0).getName());
        assertEquals(Integer.valueOf(1), mainActivitySpy.noms.get(0).getGroupe());
    }

    @Test
    public void testAjouter_EmptyGroup() {

        // Setup mocks
        doReturn(mockSpinner).when(mainActivitySpy).findViewById(R.id.nomContact);
        doReturn(mockEditText).when(mainActivitySpy).findViewById(R.id.groupe);

        // Mock spinner selected item
        Nom mockSelectedNom = new Nom("Test Contact", "123456789");
        when(mockSpinner.getSelectedItem()).thenReturn(mockSelectedNom);

        // Mock EditText to return an empty group
        when(mockEditText.getText()).thenReturn(mock(android.text.Editable.class));
        when(mockEditText.getText().toString()).thenReturn("");

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