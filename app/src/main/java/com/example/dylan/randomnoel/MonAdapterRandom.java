package com.example.dylan.randomnoel;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Dylan on 13/04/2017.
 */
public class MonAdapterRandom extends BaseAdapter
{
    private final Map<Nom,Nom> itemNom;

    public MonAdapterRandom(Map<Nom,Nom> items) {
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

        textColor = Utils.getTextColor(coulValue, row, textColor);

        TextView nom = row.findViewById(R.id.assocNoms);
        assert value != null;
        nom.setText(key.getName() + ", tu auras : " + value.getName());
        nom.setTextColor(textColor);

        return(row);
    }
}
