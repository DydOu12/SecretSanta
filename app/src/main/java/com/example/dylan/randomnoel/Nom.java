package com.example.dylan.randomnoel;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Objects;

import androidx.annotation.NonNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Dylan on 08/04/2017.
 */
@Getter
@Setter
@AllArgsConstructor
public class Nom implements Parcelable, Serializable {
    private String name;
    private String numero;
    private Integer groupe;
    private Integer couleur;

    public Nom(String n, String numero) { this.name = n; this.numero = numero; }

    protected Nom(Parcel in) {
        name = in.readString();
        numero = in.readString();
        couleur = in.readInt();
        groupe = in.readInt();
    }

    public static final Parcelable.Creator<Nom> CREATOR = new Creator<>() {
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
        parcel.writeString(name);
        parcel.writeString(numero);
        parcel.writeInt(couleur);
        parcel.writeInt(groupe);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Nom nom1)) return false;
        return Objects.equals(numero, nom1.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, numero, groupe, couleur);
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
