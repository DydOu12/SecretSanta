package com.example.dylan.randomnoel;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**

 * Created by Dylan on 08/04/2017.
 */
public class AdapterView extends BaseAdapter
{
    private final HashMap<Nom,Integer> itemNom;
    private final HashMap<Integer,Integer> couleurs;

    public AdapterView(HashMap<Nom,Integer> items, HashMap<Integer,Integer> c) {
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

    @SuppressLint({"ViewHolder"})
    public View getView(int position, final View convertView, ViewGroup parent)
    {
        // pour les noms avec leur groupe
        List<Nom> indexes = new ArrayList<>(itemNom.keySet());
        final Nom key = indexes.get(position);
        final Integer value = itemNom.get(key);

        // pour les groupes associés avec leur couleur
        final Integer coulValue = couleurs.get(value);

        View row;

        // instantiation d’un View correspondant a notre fichier de layout
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        row=inflater.inflate(R.layout.vue_liste, parent, false);

        if(coulValue != null)
            row.setBackgroundColor(coulValue);

        TextView idL = row.findViewById(R.id.idLi);
        idL.setText(String.valueOf(position+1));

        EditText nom = row.findViewById(R.id.nomContactInList);
        nom.setText(key.getNom());

        TextView numGroup = row.findViewById(R.id.numGroupe);
        numGroup.setText(key.getNumero() + " - " + value);

        ImageView deletePicture = row.findViewById(R.id.suppr);
        deletePicture.setOnClickListener(v -> {
                itemNom.remove(key);
                notifyDataSetChanged();
        });

        return(row);
    }
}
