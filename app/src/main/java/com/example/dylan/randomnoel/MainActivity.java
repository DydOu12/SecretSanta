package com.example.dylan.randomnoel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class MainActivity extends Activity implements View.OnClickListener
{
    private EditText nom;
    private EditText groupe;
    private EditText numero;
    private HashMap<Nom,Integer> noms = new LinkedHashMap<>();
    private HashMap<Integer,Integer> couleursGroupe = new LinkedHashMap<>();

    private MonAdapter monAdapter = new MonAdapter(noms, couleursGroupe);

    // pour savoir si un élément est dejà présent dans le hashmap
    public boolean estPresent(String nom, Integer integ, HashMap<Nom,Integer> ln){
        Iterator<Map.Entry<Nom, Integer>> it = ln.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<Nom, Integer> mapentry = it.next();
            Nom n = mapentry.getKey();
            Integer i = mapentry.getValue();
            if (n.getNom().compareToIgnoreCase(nom) == 0 && i.equals(integ))
                return true;
        }
        return false;
    }

    public int sommeReste(HashMap<Integer,Integer> m, Integer cle, Integer valeur){
        int somme = 0;

        for (Integer key : m.keySet()) {
            Integer nombre = m.get(key);
            if(!(key.equals(cle) && nombre != null && nombre.equals(valeur)))
                somme += nombre;
        }

        return somme;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null)
        {
            noms = (HashMap<Nom,Integer>)savedInstanceState.getSerializable("listeNoms");
            couleursGroupe = (HashMap<Integer,Integer>)savedInstanceState.getSerializable("listeCouleurs");
            monAdapter = new MonAdapter(noms,couleursGroupe);
        }

        ListView lv = (ListView) findViewById(R.id.listeNoms);
        lv.setAdapter(monAdapter);

        nom = (EditText) findViewById(R.id.nom);
        groupe = (EditText) findViewById(R.id.groupe);
        numero = (EditText) findViewById(R.id.numero);

        Button ok = (Button) findViewById(R.id.random);
        ok.setOnClickListener(this);
    }

    public void ajouter(View v){
        Nom n = new Nom(nom.getText().toString(), numero.getText().toString());
        if(!nom.getText().toString().isEmpty() && !groupe.getText().toString().isEmpty()) {
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
                // pour trier le hashmap selon les valeurs (qu'elles soient dans l'ordre croissant
                Comparator<Integer> valueComparator = (s1, s2) -> {
                    if(s1 >= s2) return 1;
                    else return -1;
                };
                MapValueComparator<Nom, Integer> mapComparator = new MapValueComparator<>(noms, valueComparator);
                Map<Nom, Integer> sortedOnValuesMap = new TreeMap<>(mapComparator);
                sortedOnValuesMap.putAll(noms);
                // on les remet dans le hashmap de base
                noms.clear();
                noms.putAll(sortedOnValuesMap);
            }
            else if(estPresent(n.getNom(), i, noms)){
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
        nom.getText().clear();
        groupe.getText().clear();
        numero.getText().clear();
        nom.requestFocus();
    }

    @Override
    public void onClick(View view) {
        Intent versResult = new Intent(MainActivity.this, ResultRandom.class);
        versResult.putExtra("listeNoms", noms);
        versResult.putExtra("couleurs", couleursGroupe);

        // Permet de savoir si les associations sont possibles
        HashMap<Integer, Integer> map = new HashMap<>();
        Iterator<Map.Entry<Nom, Integer>> it = noms.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<Nom, Integer> mapentry = it.next();
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
            Toast.makeText(MainActivity.this, "Entrez au moins deux personnes !", Toast.LENGTH_SHORT).show();
        else if(!possible)
            Toast.makeText(MainActivity.this, "Association impossible !", Toast.LENGTH_SHORT).show();
        else
            startActivity(versResult);
    }

    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);

        saveInstanceState.putSerializable("listeNoms", noms);
        saveInstanceState.putSerializable("listeCouleurs", couleursGroupe);
    }
}
