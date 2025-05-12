package com.example.dylan.randomnoel;

import static java.util.Objects.requireNonNull;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cadeaux_noel.db";
    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_ASSOCIATIONS = "associations";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_PERSONNE = "personne";
    public static final String COLUMN_DESTINATAIRE = "destinataire";

    // Format pour stockage en base de données (ISO standard)
    private final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    // Format pour affichage à l'utilisateur
    private final SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());


    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_ASSOCIATIONS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DATE + " TEXT NOT NULL, " +
            COLUMN_PERSONNE + " TEXT NOT NULL, " +
            COLUMN_DESTINATAIRE + " TEXT NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrade
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSOCIATIONS);
            onCreate(db);
        }
    }

    // Méthode pour insérer une association
    public void insertAssociation(String personne, String destinataire) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, getCurrentDate());
        values.put(COLUMN_PERSONNE, personne);
        values.put(COLUMN_DESTINATAIRE, destinataire);
        db.insert(TABLE_ASSOCIATIONS, null, values);
        db.close();
    }

    // Méthode pour récupérer les associations d'une année donnée
    public List<String> getAssociationsByDate(String displayDate) {

        List<String> associations = new ArrayList<>();

        try (SQLiteDatabase db = this.getReadableDatabase()) {
            // Convertir la date d'affichage en date DB
            String dbDate = formatDate(displayDate, DISPLAY_DATE_FORMAT, DB_DATE_FORMAT);

            Cursor cursor = db.query(TABLE_ASSOCIATIONS,
                    new String[]{COLUMN_PERSONNE, COLUMN_DESTINATAIRE},
                    COLUMN_DATE + "=?",
                    new String[]{dbDate},
                    null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    String result = cursor.getString(0) + " -> " + cursor.getString(1);
                    associations.add(result);
                } while (cursor.moveToNext());
            }
            cursor.close();

        }

        return associations;
    }

    // Get all distinct dates from the database
    public List<String> getAllDates() {
        Set<String> displayDates  = new HashSet<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_ASSOCIATIONS,
                new String[]{COLUMN_DATE},
                null, null, null, null,
                COLUMN_DATE + " DESC", // Tri chronologique direct
                null);

        if (cursor.moveToFirst()) {
            do {
                String dbDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                // Convertir en format d'affichage
                displayDates.add(formatDate(dbDate, DB_DATE_FORMAT, DISPLAY_DATE_FORMAT));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return new ArrayList<>(displayDates);
    }

    // Méthode pour obtenir la date actuelle au format yyyy-MM-dd HH:mm:ss pour la BD (ISO)
    protected String getCurrentDate() {
        return DB_DATE_FORMAT.format(new Date());
    }

    /**
     * Convertit une date du format DB vers le format d'affichage et vice versa
     */
    public String formatDate(String dbDate, SimpleDateFormat source, SimpleDateFormat target) {
        try {
            Date date = source.parse(dbDate);
            return target.format(requireNonNull(date));
        } catch (ParseException e) {
            return dbDate; // Retourne la chaîne originale en cas d'erreur
        }
    }

    /**
     * Récupère le dernier destinataire associé à une personne donnée
     */
    public String getLastAssociation(String personne) {
        SQLiteDatabase db = this.getReadableDatabase();
        String destinataire = null;

        // Maintenant, on peut trier directement par date puisqu'elle est au format ISO
        Cursor cursor = db.query(
                TABLE_ASSOCIATIONS,
                new String[]{COLUMN_DESTINATAIRE},
                COLUMN_PERSONNE + "=?",
                new String[]{personne},
                null,
                null,
                COLUMN_DATE + " DESC", // Tri direct possible avec format ISO
                "1"); // Limite à 1 seul résultat

        if (cursor.moveToFirst()) {
            destinataire = cursor.getString(0);
        }

        cursor.close();
        db.close();

        return destinataire;
    }
}