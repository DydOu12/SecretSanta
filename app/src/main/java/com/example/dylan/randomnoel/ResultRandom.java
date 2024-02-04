package com.example.dylan.randomnoel;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * Created by Dylan on 09/04/2017.
 */
public class ResultRandom extends AppCompatActivity implements View.OnClickListener
{
    private HashMap<Nom,Nom> association;

    private static final int MY_PERMISSION_REQUEST_CODE_SEND_SMS = 100;

    private final SecureRandom rand = new SecureRandom();

    private static final String LISTE_NOMS = "listeNoms";

    public static List<Nom> choixPossible(List<Nom> noms, Integer groupe){

        return noms
                .stream()
                .filter(n -> !n.getGroupe().equals(groupe))
                .collect(Collectors.toList());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);

        TextView titre = findViewById(R.id.titre);
        titre.setTextSize(50);

        MonAdapterRandom monAdapter;
        ListView lv;
        if(savedInstanceState != null){
            association = (HashMap<Nom,Nom>)savedInstanceState.getSerializable(LISTE_NOMS);
            monAdapter = new MonAdapterRandom(association);
            lv = findViewById(R.id.listeNomsAssocies);
            lv.setAdapter(monAdapter);
        }
        else {
            // contient la liste des noms associés aux groupe
            List<Nom> listeNoms = (List<Nom>) getIntent().getSerializableExtra(LISTE_NOMS);

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
            SmsManager smsManager = SmsManager.getDefault();

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

        saveInstanceState.putSerializable(LISTE_NOMS, association);
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
