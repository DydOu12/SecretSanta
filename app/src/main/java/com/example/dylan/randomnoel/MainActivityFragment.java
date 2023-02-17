package com.example.dylan.randomnoel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class MainActivityFragment extends AppCompatActivity implements View.OnClickListener {

    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private HashMap<Nom,Integer> noms = new LinkedHashMap<>();
    private HashMap<Integer,Integer> couleursGroupe = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Définissez votre vue, rien de plus. Tout sera pris en charge par le fragment qui affiche les données
        setContentView(R.layout.activity_main_fragment);

        accordPermission();

        if(savedInstanceState != null)
        {
            noms = (HashMap<Nom,Integer>)savedInstanceState.getSerializable("listeNoms");
            couleursGroupe = (HashMap<Integer,Integer>)savedInstanceState.getSerializable("listeCouleurs");
        }

        AdapterView monAdapter = new AdapterView(noms,couleursGroupe, getApplicationContext());

        ListView lv = (ListView) findViewById(R.id.listeNoms);
        lv.setAdapter(monAdapter);

        Button ok = (Button) findViewById(R.id.random);
        ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent versResult = new Intent(MainActivityFragment.this, ResultRandom.class);
        versResult.putExtra("listeNoms", noms);
        versResult.putExtra("couleurs", couleursGroupe);

        // Permet de savoir si les associations sont possibles
        HashMap<Integer, Integer> map = new HashMap<>();

        for (Map.Entry<Nom, Integer> mapentry : noms.entrySet()) {
            Integer item = mapentry.getValue();
            Integer nombre = map.get(item);
            if (nombre == null) {
                nombre = 0;
            }
            nombre++;
            map.put(item, nombre);
        }

        boolean possible = true;
        for (Integer key : map.keySet()) {
            Integer nombre = map.get(key);
            if(nombre != null && nombre > sommeReste(map, key, nombre))
                possible = false;
        }

        if (noms.size() < 2 )
            Toast.makeText(MainActivityFragment.this, "Entrez au moins deux personnes !", Toast.LENGTH_SHORT).show();
        else if(!possible)
            Toast.makeText(MainActivityFragment.this, "Association impossible !", Toast.LENGTH_SHORT).show();
        else
            startActivity(versResult);
    }

    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);

        saveInstanceState.putSerializable("listeNoms", noms);
        saveInstanceState.putSerializable("listeCouleurs", couleursGroupe);
    }

    public void accordPermission() {
        if (android.os.Build.VERSION.SDK_INT >=  android.os.Build.VERSION_CODES.M) { // 23

            // Check if we have send SMS permission
            int readContactPermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CONTACTS);

            if (readContactPermission != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{Manifest.permission.READ_CONTACTS},
                        PERMISSIONS_REQUEST_READ_CONTACTS
                );
                return;
            }
        }

        addContact();
    }

    public void addContact() {
        List<String> contacts = new ArrayList<>();

        Cursor c = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                null, null, null);

        while (c.moveToNext()) {

            @SuppressLint("Range") String contactName = c
                    .getString(c
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            @SuppressLint("Range") String phNumber = c
                    .getString(c
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            contacts.add(contactName + ":" + phNumber);

        }
        c.close();

        Spinner s = (Spinner) findViewById(R.id.nomContact);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, contacts);
        s.setAdapter(adapter);
    }

    private int sommeReste(HashMap<Integer,Integer> m, Integer cle, Integer valeur){
        int somme = 0;

        for (Integer key : m.keySet()) {
            Integer nombre = m.get(key);
            if(!(key.equals(cle) && nombre != null && nombre.equals(valeur)))
                somme += nombre;
        }

        return somme;
    }

    // pour savoir si un élément est dejà présent dans le hashmap
    private boolean estPresent(String nom, Integer integ, HashMap<Nom,Integer> ln){

        for (Map.Entry<Nom, Integer> mapentry : ln.entrySet()) {
            Nom n = mapentry.getKey();
            Integer i = mapentry.getValue();
            if (n.getNom().compareToIgnoreCase(nom) == 0 && i.equals(integ))
                return true;
        }
        return false;
    }

    public void ajouter(View v){

        Spinner listeContacts = (Spinner) findViewById(R.id.nomContact);
        String fullContact = listeContacts.getSelectedItem().toString();

        String nom = fullContact.substring(0, fullContact.indexOf(':'));
        String numero = fullContact.substring(fullContact.indexOf(':')+1);
        EditText groupe = (EditText) findViewById(R.id.groupe);

        Nom n = new Nom(nom, numero);
        if(!nom.isEmpty() && !groupe.getText().toString().isEmpty()) {
            String val = groupe.getText() + "";
            int i = Integer.parseInt(val);
            if(!estPresent(n.getNom(), i, noms)) {
                // pour changer la couleur
                if(!noms.containsValue(i)){
                    Random r = new Random();
                    int couleur = Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256));
                    couleursGroupe.put(i,couleur);
                }

                noms.put(n, i);
                // pour trier le hashmap selon les valeurs (qu'elles soient dans l'ordre croissant)
                Comparator<Integer> valueComparator = Integer::compareTo;

                MapValueComparator<Nom, Integer> mapComparator = new MapValueComparator<>(noms, valueComparator);
                Map<Nom, Integer> sortedOnValuesMap = new TreeMap<>(mapComparator);
                sortedOnValuesMap.putAll(noms);
                // on les remet dans le hashmap de base
                noms.clear();
                noms.putAll(sortedOnValuesMap);
            }
            else if(estPresent(n.getNom(), i, noms)){
                Toast.makeText(MainActivityFragment.this, "Déjà présent", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            if(groupe.getText().toString().isEmpty())
                Toast.makeText(MainActivityFragment.this, "groupe vide", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivityFragment.this, "Nom vide", Toast.LENGTH_SHORT).show();
        }
        groupe.getText().clear();
        listeContacts.requestFocus();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addContact();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
