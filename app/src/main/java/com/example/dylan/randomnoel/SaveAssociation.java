package com.example.dylan.randomnoel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class SaveAssociation implements Parcelable {

    private final Map<Nom, Nom> association;

    public SaveAssociation(Map<Nom, Nom> association) {
        this.association = association;
    }

    protected SaveAssociation(Parcel in) {
        int size = in.readInt();
        association = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            Nom key;
            Nom value;

            if (Utils.isAtLeastUpsideDownCakeVersion()) {
                key = in.readParcelable(Nom.class.getClassLoader(), Nom.class);
                value = in.readParcelable(Nom.class.getClassLoader(), Nom.class);
            } else {
                key = in.readParcelable(Nom.class.getClassLoader());
                value = in.readParcelable(Nom.class.getClassLoader());
            }
            association.put(key, value);
        }
    }

    public static final Creator<SaveAssociation> CREATOR = new Creator<>() {
        @Override
        public SaveAssociation createFromParcel(Parcel in) {
            return new SaveAssociation(in);
        }

        @Override
        public SaveAssociation[] newArray(int size) {
            return new SaveAssociation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(association.size());
        for (Map.Entry<Nom, Nom> entry : association.entrySet()) {
            dest.writeParcelable(entry.getKey(), flags);
            dest.writeParcelable(entry.getValue(), flags);
        }
    }
}