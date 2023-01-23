package com.example.dylan.randomnoel;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dylan on 13/04/2017.
 */
public class MonAdapterRandom extends BaseAdapter
{
    private HashMap<Nom,Nom> itemNom;
    private HashMap<Nom,Integer> couleurs;

    public MonAdapterRandom(HashMap<Nom,Nom> items, HashMap<Nom,Integer> c) {
        itemNom = items;
        couleurs = c;
    }

    @Override
    public int getCount() {
        return itemNom.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public View getView(int position, final View convertView, ViewGroup parent)
    {
        List<Nom> indexes = new ArrayList<>(itemNom.keySet());
        Nom key = indexes.get(position);
        Nom value = itemNom.get(key);

        Integer coulValue = couleurs.get(key);

        View row;

        // instantiation d’un View correspondant a notre fichier de layout
        row=LayoutInflater.from(parent.getContext()).inflate(R.layout.vue_liste_random, parent, false);
        row.setBackgroundColor(coulValue);

        TextView nom = (TextView)row.findViewById(R.id.assocNoms);
        nom.setText(key.getNom() + ", tu auras : " + value.getNom());

        return(row);
    }
}
