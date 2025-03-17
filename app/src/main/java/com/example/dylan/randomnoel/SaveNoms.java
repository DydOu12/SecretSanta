package com.example.dylan.randomnoel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import lombok.Getter;

@Getter
public class SaveNoms implements Parcelable {
    private final List<Nom> noms;

    public SaveNoms(List<Nom> noms) {
        this.noms = noms;
    }

    protected SaveNoms(Parcel in) {
        noms = in.createTypedArrayList(Nom.CREATOR);
    }

    public static final Creator<SaveNoms> CREATOR = new Creator<>() {
        @Override
        public SaveNoms createFromParcel(Parcel in) {
            return new SaveNoms(in);
        }

        @Override
        public SaveNoms[] newArray(int size) {
            return new SaveNoms[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(noms);
    }
}