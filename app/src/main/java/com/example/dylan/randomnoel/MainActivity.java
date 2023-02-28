package com.example.dylan.randomnoel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
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
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private ArrayList<Nom> noms = new ArrayList<>();

    private AdapterView monAdapter = new AdapterView(noms);

    private ListView listNoms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Définissez votre vue, rien de plus. Tout sera pris en charge par le fragment qui affiche les données
        setContentView(R.layout.activity_main_fragment);

        accordPermission();

        if(savedInstanceState != null)
        {
            noms = (ArrayList<Nom>) savedInstanceState.getSerializable("listeNoms");
            monAdapter = new AdapterView(noms);
        }

        listNoms = findViewById(R.id.listeNoms);
        listNoms.setAdapter(monAdapter);

        Button ok = findViewById(R.id.random);
        ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent versResult = new Intent(MainActivity.this, ResultRandom.class);
        versResult.putExtra("listeNoms", noms);

        // Modifier les noms si nécessaires
        for(int i = 0; i < listNoms.getChildCount(); ++i) {
            View v = listNoms.getChildAt(i);
            EditText eT = v.findViewById(R.id.nomContactInList);
            String overrideName = eT.getText().toString();

            noms.get(i).setNom(overrideName);
        }

        // Permet de savoir si les associations sont possibles
        HashMap<Integer, Integer> map = new HashMap<>();

        for (Nom n : noms) {
            Integer item = n.getGroupe();
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
            Toast.makeText(MainActivity.this, "Entrez au moins deux personnes !", Toast.LENGTH_SHORT).show();
        else if(!possible)
            Toast.makeText(MainActivity.this, "Association impossible !", Toast.LENGTH_SHORT).show();
        else
            startActivity(versResult);
    }

    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);

        saveInstanceState.putSerializable("listeNoms", noms);
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

    // pour savoir si un élément est dejà présent dans la liste
    private boolean estPresent(String nom, Integer integ){

        for (Nom n : noms) {
            Integer i = n.getGroupe();
            if (n.getNom().compareToIgnoreCase(nom) == 0 && i.equals(integ))
                return true;
        }
        return false;
    }

    /**
     *
     * @param groupe le groupe du nom
     * @return la couleur associee au groupe ou null s'il n'exite pas
     */
     private Integer groupePresent(Integer groupe) {
        for(Nom n : noms) {
            if(groupe.equals(n.getGroupe())) {
                return n.getCouleur();
            }
        }

        return null;
    }

    public void ajouter(View v){

        Spinner listeContacts = findViewById(R.id.nomContact);
        String fullContact = listeContacts.getSelectedItem().toString();

        String nom = fullContact.substring(0, fullContact.indexOf(':'));
        String numero = fullContact.substring(fullContact.indexOf(':')+1);
        EditText groupe = findViewById(R.id.groupe);

        Nom n = new Nom(nom, numero);
        if(!nom.isEmpty() && !groupe.getText().toString().isEmpty()) {
            String val = groupe.getText() + "";
            int i = Integer.parseInt(val);
            if(!estPresent(n.getNom(), i)) {

                Integer couleur = groupePresent(i);

                // pour changer la couleur
                if(couleur == null){
                    Random r = new Random();
                    couleur = Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256));
                }

                n.setCouleur(couleur);
                n.setGroupe(i);

                // pour trier la liste des noms par groupe (qu'elles soient dans l'ordre croissant)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    noms.sort(Comparator.comparing(Nom::getGroupe));
                }

            }
            else if(estPresent(n.getNom(), i)){
                Toast.makeText(MainActivity.this, "Déjà présent", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            if(groupe.getText().toString().isEmpty())
                Toast.makeText(MainActivity.this, "groupe vide", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this, "Nom vide", Toast.LENGTH_SHORT).show();
        }
        groupe.getText().clear();
        listeContacts.requestFocus();

        listNoms.setAdapter(monAdapter);
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
