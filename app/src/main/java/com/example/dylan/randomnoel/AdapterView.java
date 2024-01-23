package com.example.dylan.randomnoel;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**

 * Created by Dylan on 08/04/2017.
 */
public class AdapterView extends BaseAdapter
{
    private final List<Nom> itemNom;

    public AdapterView(List<Nom> items) {
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
        // pour les noms avec leur groupe
        final Nom key = itemNom.get(position);

        // pour les groupes associés avec leur couleur
        final Integer coulValue = key.getCouleur();

        View row;

        // instantiation d’un View correspondant a notre fichier de layout
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        row=inflater.inflate(R.layout.vue_liste, parent, false);

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
        TextView idL = row.findViewById(R.id.idLi);
        idL.setText(String.valueOf(position+1));
        idL.setTextColor(textColor);

        EditText nom = row.findViewById(R.id.nomContactInList);
        nom.setText(key.getNom());
        nom.setTextColor(textColor);

        nom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing to do
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing to do
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // On modifie le nom avec celui modifié
                key.setNom(editable.toString());
            }
        });

        TextView numGroup = row.findViewById(R.id.numGroupe);
        numGroup.setText(key.getNumero() + " - " + key.getGroupe());
        numGroup.setTextColor(textColor);

        ImageView deletePicture = row.findViewById(R.id.suppr);
        deletePicture.setOnClickListener(v -> {
                itemNom.remove(key);
                notifyDataSetChanged();
        });

        return(row);
    }

}
