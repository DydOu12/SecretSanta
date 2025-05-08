package com.example.dylan.randomnoel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cadeaux_noel.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_ASSOCIATIONS = "associations";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_PERSONNE = "personne";
    public static final String COLUMN_DESTINATAIRE = "destinataire";

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
    public List<String> getAssociationsByDate(String date) {
        List<String> associations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ASSOCIATIONS,
                new String[]{COLUMN_PERSONNE, COLUMN_DESTINATAIRE},
                COLUMN_DATE + "=?",
                new String[]{String.valueOf(date)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String result = cursor.getString(0) + " -> " + cursor.getString(1);
                associations.add(result);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return associations;
    }

    // Get all distinct dates from the database
    public List<String> getAllDates() {
        Set<String> dates = new HashSet<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_ASSOCIATIONS, new String[]{COLUMN_DATE}, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                dates.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return new ArrayList<>(dates);
    }

    // Méthode pour obtenir la date actuelle au format dd/MM/yyyy-HH:mm:ss
    protected String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}