package com.example.dylan.randomnoel;

import android.annotation.SuppressLint;
import android.content.Context;
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

 * Created by Dylan on 08/04/2017.
 */
public class AdapterView extends BaseAdapter
{
    private final HashMap<Nom,Integer> itemNom;
    private final HashMap<Integer,Integer> couleurs;

    private final Context context;

    public AdapterView(HashMap<Nom,Integer> items, HashMap<Integer,Integer> c, Context cont) {
        itemNom = items;
        couleurs = c;
        context = cont;
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

        TextView nom = (TextView)row.findViewById(R.id.nomDansListe);
        nom.setText(context.getString(R.string.contact_liste_groupe, position+1, key.getNom(), key.getNumero(), value));

        ImageView deletePicture = (ImageView) row.findViewById(R.id.suppr);
        deletePicture.setOnClickListener(v -> {
                itemNom.remove(key);
                notifyDataSetChanged();
        });

        return(row);
    }
}
