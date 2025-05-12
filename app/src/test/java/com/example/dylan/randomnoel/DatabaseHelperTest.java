package com.example.dylan.randomnoel;

import static com.example.dylan.randomnoel.DatabaseHelper.COLUMN_DATE;
import static com.example.dylan.randomnoel.DatabaseHelper.COLUMN_PERSONNE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseHelperTest {

    @Mock
    private Context mockContext;

    @Mock
    private SQLiteDatabase mockDatabase;

    @Mock
    private Cursor mockCursor;

    private DatabaseHelper databaseHelper;

    @Before
    public void setUp() throws Exception {
        // Initialiser les mocks
        try(AutoCloseable ignored = MockitoAnnotations.openMocks(this)) {

            // Créer l'instance de DatabaseHelper avec le contexte mocké
            databaseHelper = spy(new DatabaseHelper(mockContext) {
                @Override
                public SQLiteDatabase getWritableDatabase() {
                    return mockDatabase;
                }

                @Override
                public SQLiteDatabase getReadableDatabase() {
                    return mockDatabase;
                }
            });
        }
    }

    @Test
    public void testOnCreate() {
        // Appeler la méthode à tester
        databaseHelper.onCreate(mockDatabase);

        // Vérifier que la commande SQL pour créer la table a été exécutée
        String expectedCreateTableSQL = "CREATE TABLE " + DatabaseHelper.TABLE_ASSOCIATIONS + " (" +
                DatabaseHelper.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_PERSONNE + " TEXT NOT NULL, " +
                DatabaseHelper.COLUMN_DESTINATAIRE + " TEXT NOT NULL);";

        // Vérifier que execSQL a été appelé avec la commande SQL attendue
        verify(mockDatabase).execSQL(expectedCreateTableSQL);
    }

    @Test
    public void testOnUpgrade() {
        // Tester la méthode onUpgrade avec une mise à jour de version 1 vers 2
        databaseHelper.onUpgrade(mockDatabase, 1, 2);

        // Vérifier que la table est supprimée
        verify(mockDatabase).execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_ASSOCIATIONS);

        // Vérifier que onCreate est appelée après la suppression de la table
        verify(mockDatabase, times(2)).execSQL(anyString());
    }

    @Test
    public void testOnUpgrade_newVersion() {
        // Tester la méthode onUpgrade avec une mise à jour de version 1 vers 2
        databaseHelper.onUpgrade(mockDatabase, 2, 3);

        // Vérifier que onCreate est appelée après la suppression de la table
        verify(mockDatabase, never()).execSQL(anyString());
    }


    @Test
    public void testGetAssociationsByDate() {
        // Préparer les données de test
        String testDate = "01/01/2025 12:00:00";
        String testDateDB = "2025-01-01 12:00:00";

        // Configurer le comportement du curseur pour simuler 2 entrées
        when(mockCursor.moveToFirst()).thenReturn(true);
        when(mockCursor.moveToNext()).thenReturn(true, false); // true pour la première itération, false ensuite
        when(mockCursor.getString(0)).thenReturn("Alice", "Charlie"); // Première colonne: personne
        when(mockCursor.getString(1)).thenReturn("Bob", "David"); // Deuxième colonne: destinataire

        // Configurer le comportement du mock de la base de données
        when(mockDatabase.query(
                eq(DatabaseHelper.TABLE_ASSOCIATIONS),
                any(String[].class),
                eq(COLUMN_DATE + "=?"),
                eq(new String[]{testDateDB}),
                isNull(),
                isNull(),
                isNull()
        )).thenReturn(mockCursor);

        // Exécuter la méthode à tester
        List<String> result = databaseHelper.getAssociationsByDate(testDate);

        // Vérifier les résultats
        assertEquals(2, result.size());
        assertEquals("Alice -> Bob", result.get(0));
        assertEquals("Charlie -> David", result.get(1));

        // Vérifier que les ressources sont libérées
        verify(mockCursor).close();
        verify(mockDatabase).close();
    }

    @Test
    public void testGetAssociationsByDate_whenNoResults_returnsEmptyList() {
        String testDate = "01/01/2025 12:00:00";
        String testDateDB = "2025-01-01 12:00:00";

        // Simuler un curseur vide
        when(mockDatabase.query(
                eq(DatabaseHelper.TABLE_ASSOCIATIONS),
                any(String[].class),
                eq(COLUMN_DATE + "=?"),
                eq(new String[]{testDateDB}),
                isNull(),
                isNull(),
                isNull()
        )).thenReturn(mockCursor);

        when(mockCursor.moveToFirst()).thenReturn(false); // Le if ne passe pas

        // Appel de la méthode
        List<String> result = databaseHelper.getAssociationsByDate(testDate);

        // Vérifications
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(mockCursor).close();
        verify(mockDatabase).close();
    }

    @Test
    public void testGetAllDates() {
        // Configurer le comportement du curseur pour simuler 3 entrées dont une dupliquée
        when(mockCursor.moveToFirst()).thenReturn(true);
        when(mockCursor.moveToNext()).thenReturn(true, true, false);
        when(mockCursor.getColumnIndexOrThrow(COLUMN_DATE)).thenReturn(0);
        when(mockCursor.getString(0)).thenReturn("2025-01-01 12:00:00", "2025-01-02 12:00:00", "2025-01-03 12:00:00");

        // Configurer le comportement du mock de la base de données
        when(mockDatabase.query(
                eq(true),
                eq(DatabaseHelper.TABLE_ASSOCIATIONS),
                any(String[].class),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(COLUMN_DATE + " DESC"),
                isNull()
        )).thenReturn(mockCursor);

        // Exécuter la méthode à tester
        List<String> result = databaseHelper.getAllDates();

        // Vérifier les résultats - doit contenir 2 dates uniques grâce au HashSet dans la méthode
        assertEquals(3, result.size());
        assertTrue(result.contains("01/01/2025 12:00:00"));
        assertTrue(result.contains("02/01/2025 12:00:00"));
        assertTrue(result.contains("03/01/2025 12:00:00"));

        // Vérifier que les ressources sont libérées
        verify(mockCursor).close();
        verify(mockDatabase).close();
    }


    @Test
    public void testGetAllDates_whenNoResults_returnsEmptyList() {
        // Simuler un curseur vide
        when(mockDatabase.query(
                     eq(true),
                     eq(DatabaseHelper.TABLE_ASSOCIATIONS),
                     eq(new String[]{COLUMN_DATE}),
                     isNull(),
                     isNull(),
                     isNull(),
                     isNull(),
                     eq(COLUMN_DATE + " DESC"),
                     isNull()
                     )).thenReturn(mockCursor);

        when(mockCursor.moveToFirst()).thenReturn(false); // Le if ne passe pas

        // Appel de la méthode
        List<String> result = databaseHelper.getAllDates();

        // Vérifications
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(mockCursor).close();
        verify(mockDatabase).close();
    }


    @Test
    public void testGetCurrentDateFormat() {

        String dateStr = databaseHelper.getCurrentDate();

        assertNotNull("La date ne doit pas être nulle", dateStr);

        // Vérifie que la date est bien au format attendu
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        format.setLenient(false);

        try {
            format.parse(dateStr); // Doit réussir sans exception
        } catch (ParseException e) {
            fail("La date retournée n'est pas au format attendu : " + dateStr);
        }
    }

    @Test
    public void testFormatDateWithValidInput() {
        // Arrange
        SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat target = new SimpleDateFormat("dd/MM/yyyy");
        String dbDate = "2023-01-15";

        // Act
        String result = databaseHelper.formatDate(dbDate, source, target);

        // Assert
        assertEquals("15/01/2023", result);
    }

    @Test
    public void testFormatDateWithInvalidFormat() {
        // Arrange
        SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat target = new SimpleDateFormat("dd/MM/yyyy");
        String dbDate = "15/01/2023"; // Format invalide par rapport à source

        // Act
        String result = databaseHelper.formatDate(dbDate, source, target);

        // Assert
        assertEquals("15/01/2023", result); // Devrait retourner la chaîne originale
    }

    @Test
    public void testGetLastAssociation() {
        String personne = "personne";

        when(mockCursor.moveToFirst()).thenReturn(true);
        when(mockCursor.getString(0)).thenReturn("quelqun");

        // Simuler un curseur vide
        when(mockDatabase.query(
                eq(DatabaseHelper.TABLE_ASSOCIATIONS),
                any(String[].class),
                eq(COLUMN_PERSONNE + "=?"),
                eq(new String[]{personne}),
                isNull(),
                isNull(),
                eq(COLUMN_DATE + " DESC"),
                eq("1")
        )).thenReturn(mockCursor);

        // Appel de la méthode
        String result = databaseHelper.getLastAssociation(personne);

        // Vérifications
        assertNotNull(result);
        assertEquals("quelqun", result);

        verify(mockCursor).close();
        verify(mockDatabase).close();
    }

    @Test
    public void testGetLastAssociation_whenNoResults_returnsNull() {
        String personne = "personne";

        when(mockCursor.moveToFirst()).thenReturn(false);

        // Simuler un curseur vide
        when(mockDatabase.query(
                eq(DatabaseHelper.TABLE_ASSOCIATIONS),
                any(String[].class),
                eq(COLUMN_PERSONNE + "=?"),
                eq(new String[]{personne}),
                isNull(),
                isNull(),
                eq(COLUMN_DATE + " DESC"),
                eq("1")
        )).thenReturn(mockCursor);

        // Appel de la méthode
        String result = databaseHelper.getLastAssociation(personne);

        // Vérifications
        assertNull(result);

        verify(mockCursor).close();
        verify(mockDatabase).close();
    }

}