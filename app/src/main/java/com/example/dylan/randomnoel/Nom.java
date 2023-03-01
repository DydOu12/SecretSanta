package com.example.dylan.randomnoel;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Dylan on 08/04/2017.
 */
public class Nom implements Parcelable, Serializable {
    private String nom;
    private final String numero;
    private Integer groupe;
    private Integer couleur;

    public Nom(String n, String numero, Integer groupe, Integer couleur) { this.nom = n; this.numero = numero; this.groupe = groupe; this.couleur = couleur; }

    public Nom(String n, String numero) { this.nom = n; this.numero = numero; }

    public String getNom() {
        return nom;
    }

    public void setGroupe(Integer groupe) {
        this.groupe = groupe;
    }

    public void setCouleur(Integer couleur) {
        this.couleur = couleur;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getGroupe() {
        return groupe;
    }

    public Integer getCouleur() {
        return couleur;
    }

    public String getNumero() {
        return numero;
    }

    @NonNull
    @Override
    public String toString() {
        return "Nom{" +
                "nom='" + nom + '\'' +
                ", numero='" + numero + '\'' +
                ", groupe=" + groupe +
                ", couleur=" + couleur +
                '}';
    }

    protected Nom(Parcel in) {
        nom = in.readString();
        numero = in.readString();
        couleur = in.readInt();
        groupe = in.readInt();
    }

    public static final Parcelable.Creator<Nom> CREATOR = new Parcelable.Creator<Nom>() {
        @Override
        public Nom createFromParcel(Parcel in) {
            return new Nom(in);
        }

        @Override
        public Nom[] newArray(int size) {
            return new Nom[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nom);
        parcel.writeString(numero);
        parcel.writeInt(couleur);
        parcel.writeInt(groupe);
    }
}
