package com.example.dylan.randomnoel;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dylan.randomnoel.databinding.ActivityRandomBinding;
import com.google.android.material.navigation.NavigationView;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

/**
 * Created by Dylan on 09/04/2017.
 */
public class ResultRandom extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener
{
    private Map<Nom,Nom> association;

    private static final int MY_PERMISSION_REQUEST_CODE_SEND_SMS = 100;

    private final SecureRandom rand = new SecureRandom();

    private static final String LISTE_NOMS = "listeNoms";

    private DrawerLayout drawerLayout;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        com.example.dylan.randomnoel.databinding.ActivityRandomBinding binding = ActivityRandomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        MonAdapterRandom monAdapter;
        ListView lv;
        if(savedInstanceState != null){
            SaveAssociation saveAssociation = savedInstanceState.getParcelable(LISTE_NOMS, SaveAssociation.class);
            if(saveAssociation != null) {
                association = saveAssociation.getAssociation();
            }
            monAdapter = new MonAdapterRandom(association);
            lv = findViewById(R.id.listeNomsAssocies);
            lv.setAdapter(monAdapter);
        }
        else {
            // contient la liste des noms associés aux groupe
            List<Nom> listeNoms = getIntent().getParcelableArrayListExtra(LISTE_NOMS, Nom.class);

            // on les trie par groupe (car ils ne le sont pas)
            assert listeNoms != null;
            listeNoms.sort(Comparator.comparing(Nom::getGroupe));

            // on ajoute aussi les noms ici pour savoir quels sont les noms qu'ils restent à associer
            List<Nom> listeNomsAAssocies = new ArrayList<>(listeNoms);

            // association des noms
            association = new LinkedHashMap<>();

            // instanciation de la vue correspondant à la liste des associations des prénoms
            lv = findViewById(R.id.listeNomsAssocies);
            monAdapter = new MonAdapterRandom(association);
            lv.setAdapter(monAdapter);

            try {
                // on itère sur chacune des personnes pour les associer à quelqu'un
                for(Nom n : listeNoms) {
                    Integer i = n.getGroupe();

                    List<Nom> nomsPossibles = choixPossible(listeNomsAAssocies, i);

                    Nom nomAAssocie;

                    // on prend un nombre aléatoire entre 0 et la taille des possibilités - 1
                    int valeur = rand.nextInt(nomsPossibles.size());
                    nomAAssocie = nomsPossibles.get(valeur);

                    association.put(n, nomAAssocie);

                    // on supprime le prenom des choix disponibles
                    listeNomsAAssocies.remove(nomAAssocie);
                }
            } catch (IllegalArgumentException e) {
                Toast.makeText(ResultRandom.this, "Erreur association, recommencer !", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        Button sendMessage = findViewById(R.id.sms);
        sendMessage.setOnClickListener(this);

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_main_activity) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public static List<Nom> choixPossible(List<Nom> noms, Integer groupe){

        return noms
                .stream()
                .filter(n -> !n.getGroupe().equals(groupe))
                .toList();
    }

    private void sendMessage() {

        // Check if we have send SMS permission
        int sendSmsPermisson = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);

        if (sendSmsPermisson != PackageManager.PERMISSION_GRANTED) {
            // If don't have permission so prompt the user.
            this.requestPermissions(
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSION_REQUEST_CODE_SEND_SMS
            );
            return;
        }

        // Add association to database
        association.entrySet()
                .iterator()
                .forEachRemaining(it ->
                        databaseHelper.insertAssociation(
                                it.getKey().getName(),
                                it.getValue().getName()));

        // Send SMS
        this.sendSMSBySmsManager();

    }

    private void sendSMSBySmsManager()  {

        Iterator<Map.Entry<Nom, Nom>> it = association.entrySet().iterator();
        String phoneNumber;
        Map.Entry<Nom, Nom> entry;
        Nom from;
        Nom to;
        String toName;

        try {
            // Get the default instance of the SmsManager
            SmsManager smsManager = this.getSystemService(SmsManager.class);

            // Send Message
            while(it.hasNext()) {
                entry = it.next();
                from = entry.getKey();
                to = entry.getValue();
                phoneNumber = from.getNumero();
                toName = to.getName();

                smsManager.sendTextMessage(phoneNumber,
                        null,
                        "Coucou, pour le Père Noël Secret, tu as : " + toName,
                        null,
                        null);

                Log.i("SMS", "Your sms has successfully sent!");
                Toast.makeText(getApplicationContext(), "Your sms has successfully sent!",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Log.e( "SMS","Your sms has failed...", ex);
            Toast.makeText(getApplicationContext(),"Your sms has failed... " + ex.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);

        SaveAssociation saveAssociation = new SaveAssociation(association);
        saveInstanceState.putParcelable(LISTE_NOMS, saveAssociation);
    }

    // When you have the request results
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSION_REQUEST_CODE_SEND_SMS) {// Note: If request is cancelled, the result arrays are empty.
            // Permissions granted (SEND_SMS).
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.i("SMS", "Permission granted!");
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();

                this.sendSMSBySmsManager();
            }
            // Cancelled or denied.
            else {
                Log.i("SMS", "Permission denied!");
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
            }
        }
    }

    // When results returned
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_PERMISSION_REQUEST_CODE_SEND_SMS) {
            if (resultCode == RESULT_OK) {
                // Do something with data (Result returned).
                Toast.makeText(this, "Action OK", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Action canceled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Action Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        sendMessage();
    }
}
