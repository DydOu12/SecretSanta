package com.example.dylan.randomnoel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Dylan on 08/04/2017.
 */
public class Nom implements Parcelable {
    private String nom;
    private String numero;

    public Nom(String n, String numero) { this.nom = n; this.numero = numero; }

    public String getNom() {
        return nom;
    }

    public void setNom(String n) { this.nom = n; }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String n) { this.numero = n; }

    @Override
    public String toString() {
        return "Nom{" +
                "nom='" + nom + '\'' +
                "numero='" + numero + '\'' +
                '}';
    }

    protected Nom(Parcel in) {
        nom = in.readString();
        numero = in.readString();
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
    }
}
