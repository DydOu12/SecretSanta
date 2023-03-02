package com.example.dylan.randomnoel;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dylan on 13/04/2017.
 */
public class MonAdapterRandom extends BaseAdapter
{
    private final HashMap<Nom,Nom> itemNom;

    public MonAdapterRandom(HashMap<Nom,Nom> items) {
        itemNom = items;
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

    @SuppressLint({"ViewHolder", "SetTextI18n"})
    public View getView(int position, final View convertView, ViewGroup parent)
    {
        List<Nom> indexes = new ArrayList<>(itemNom.keySet());
        Nom key = indexes.get(position);
        Nom value = itemNom.get(key);

        Integer coulValue = key.getCouleur();

        View row;

        // instantiation d’un View correspondant a notre fichier de layout
        row=LayoutInflater.from(parent.getContext()).inflate(R.layout.vue_liste_random, parent, false);

        int textColor = Color.BLACK;

        if(coulValue != null) {
            row.setBackgroundColor(coulValue);

            int brightness = (int) Math.round(
                    Color.red(coulValue) * 0.299 +
                            Color.green(coulValue) * 0.587 +
                            Color.blue(coulValue) * 0.114);

            if(brightness <= 125)
                textColor = Color.WHITE;
        }

        TextView nom = row.findViewById(R.id.assocNoms);
        assert value != null;
        nom.setText(key.getNom() + ", tu auras : " + value.getNom());
        nom.setTextColor(textColor);

        return(row);
    }
}
