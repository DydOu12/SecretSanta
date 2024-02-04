package com.example.dylan.randomnoel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    protected static List<Nom> noms = new ArrayList<>();

    private AdapterView monAdapter = new AdapterView(noms);

    private ListView listNoms;

    private int remainingClickEasterEgg = 5;

    private SecureRandom r = new SecureRandom();

    private static final String LISTE_NOMS = "listeNoms";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Définissez votre vue, rien de plus. Tout sera pris en charge par le fragment qui affiche les données
        setContentView(R.layout.activity_main_fragment);

        accordPermission();

        if(savedInstanceState != null)
        {
            noms = (ArrayList<Nom>) savedInstanceState.getSerializable(LISTE_NOMS);
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
        versResult.putParcelableArrayListExtra(LISTE_NOMS, (ArrayList<? extends Parcelable>) noms);

        // Modifier les noms si nécessaires
        for(int i = 0; i < listNoms.getChildCount(); ++i) {
            View v = listNoms.getChildAt(i);
            EditText eT = v.findViewById(R.id.nomContactInList);
            String overrideName = eT.getText().toString();

            noms.get(i).setName(overrideName);
        }

        // permet d'associer le nombre de personnes par groupe et de savoir si l'association est
        // possible par la suite
        boolean possible = associationPossible(getNumberOfPersonByGroupe());

        if (noms.size() < 2 )
            Toast.makeText(MainActivity.this, "Entrez au moins deux personnes !", Toast.LENGTH_SHORT).show();
        else if(!possible)
            Toast.makeText(MainActivity.this, "Association impossible !", Toast.LENGTH_SHORT).show();
        else
            startActivity(versResult);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);

        saveInstanceState.putParcelableArrayList(LISTE_NOMS, (ArrayList<? extends Parcelable>) noms);
    }

    public void accordPermission() {

        // Check if we have send SMS permission
        int readContactPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS);

        if (readContactPermission != PackageManager.PERMISSION_GRANTED) {
            // If don't have permission so prompt the user.
            this.requestPermissions(
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS
            );
            return;
        }

        addContact();
    }

    public void addContact() {
        List<String> contacts = new ArrayList<>();

        Cursor c = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                null, null, null);

        if(c != null) {
            while (c.moveToNext()) {

                @SuppressLint("Range") String contactName = c
                        .getString(c
                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                @SuppressLint("Range") String phNumber = c
                        .getString(c
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                contacts.add(contactName + ":" + phNumber);

            }
            c.close();
        }

        Spinner s = findViewById(R.id.nomContact);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, contacts);
        s.setAdapter(adapter);
    }

    /**
     *
     * @return l'association du nombre de personne par groupe
     *
     */
    public static Map<Integer, Integer> getNumberOfPersonByGroupe() {
        Map<Integer, Integer> map = new HashMap<>();

        noms.forEach(nom -> map.merge(nom.getGroupe(), 1, Integer::sum));

        return map;
    }

    /**
     *
     * @param assoPersonByGroup la map associant le nombre de personne par groupe
     * @return vrai si l'association entre les personnes est possible
     */
    public static boolean associationPossible(Map<Integer,Integer> assoPersonByGroup){

        // pour que l'association soit possible, il faut que le nombre de personnes pour
        // un couple (groupNumber, numberOfPerson) soit <= a la somme des personnes des autres groupes
        return assoPersonByGroup
                .entrySet()
                .stream()
                .map(entry
                        -> {
                            int sommeReste = assoPersonByGroup
                                    .entrySet()
                                    .stream()
                                    .filter(asso -> !asso.getKey().equals(entry.getKey()))
                                    .map(Map.Entry::getValue)
                                    .reduce(0, Integer::sum);

                            // pour chaque asso entry (groupeNumber, numberOfPerson)
                            // on regarde si numberOfPerson (entry.getValue) est <=
                            // a la somme des personnes des autres groupes
                            return entry.getValue() <= sommeReste;
                        }
                )
                .reduce(true, Boolean::logicalAnd);
    }

    // pour savoir si un élément est dejà présent dans la liste
    public static boolean estPresent(Nom nom){

            return noms
                    .contains(nom);
    }

    /**
     *
     * @param groupe le groupe du nom
     * @return la couleur associee au groupe ou null s'il n'exite pas
     */
     public static Integer getCouleurIfGroupePresent(Integer groupe) {

         return noms
            .stream()
            .filter(nom ->
                nom.getGroupe().equals(groupe))
            .map(Nom::getCouleur)
            .findFirst()
            .orElse(null);
    }

    public void egg(View v) {
         --remainingClickEasterEgg;

         if(remainingClickEasterEgg < 3 && remainingClickEasterEgg > 0)
            Toast.makeText(MainActivity.this, remainingClickEasterEgg + " clics avant easter egg :)", Toast.LENGTH_SHORT).show();

         if(remainingClickEasterEgg == 0) {
             Nom helene = new Nom("Hélène", "0677693045", 1, Color.BLUE);
             Nom laurent = new Nom("Laurent", "0631352000", 1, Color.BLUE);
             Nom manon = new Nom("Manon", "0782384810", 1, Color.BLUE);
             Nom elie = new Nom("Elie", "0768497874", 1, Color.BLUE);

             Nom alain = new Nom("Alain", "0788590358", 2, Color.GREEN);
             Nom bea = new Nom("Béa", "0683089227", 2, Color.GREEN);
             Nom matthieu = new Nom("Matthieu", "0647746240", 2, Color.GREEN);
             Nom francois = new Nom("François", "0635467242", 2, Color.GREEN);
             Nom aurelie = new Nom("Aurelie", "0614283241", 2, Color.GREEN);
             Nom mariel = new Nom("Mariel", "0687586357", 2, Color.GREEN);

             Nom babeth = new Nom("Babeth", "0621224549", 3, Color.RED);
             Nom jacques = new Nom("Jacques", "0676999624", 3, Color.RED);
             Nom sarah = new Nom("Sarah", "0616579051", 3, Color.RED);
             Nom samuel = new Nom("Samuel", "0644236802", 3, Color.RED);
             Nom paul = new Nom("Paul", "0687258509", 3, Color.RED);

             Nom dylan = new Nom("Dylan", "0643495070", 4, Color.YELLOW);
             Nom oceane = new Nom("Oceane", "0686705621", 4, Color.YELLOW);
             Nom jp = new Nom("Jean-Pierre", "0632895613", 4, Color.YELLOW);

             noms.add(helene);
             noms.add(laurent);
             noms.add(manon);
             noms.add(elie);
             noms.add(bea);
             noms.add(alain);
             noms.add(matthieu);
             noms.add(francois);
             noms.add(aurelie);
             noms.add(mariel);
             noms.add(babeth);
             noms.add(jacques);
             noms.add(samuel);
             noms.add(sarah);
             noms.add(paul);
             noms.add(oceane);
             noms.add(dylan);
             noms.add(jp);

             listNoms.setAdapter(monAdapter);
         }
    }

    public void ajouter(View v){

        Spinner listeContacts = findViewById(R.id.nomContact);
        String fullContact = listeContacts.getSelectedItem().toString();

        String nom = fullContact.substring(0, fullContact.indexOf(':'));
        String numero = fullContact.substring(fullContact.indexOf(':')+1);
        EditText groupe = findViewById(R.id.groupe);

        Nom n = new Nom(nom, numero);
        if(!nom.isEmpty() && !groupe.getText().toString().isEmpty()) {
            String val = String.valueOf(groupe.getText());
            int i = Integer.parseInt(val);
            if(!estPresent(n)) {

                Integer couleur = getCouleurIfGroupePresent(i);

                // pour changer la couleur
                if(couleur == null){
                    couleur = Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256));
                }

                n.setCouleur(couleur);
                n.setGroupe(i);

                noms.add(n);

                // pour trier la liste des noms par groupe (qu'elles soient dans l'ordre croissant)
                noms.sort(Comparator.comparing(Nom::getGroupe));
            }
            else {
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
