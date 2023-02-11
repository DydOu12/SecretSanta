package com.example.dylan.randomnoel;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivityFragment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Définissez votre vue, rien de plus. Tout sera pris en charge par le fragment qui affiche les données
        setContentView(R.layout.activity_main_fragment);
    }

}
