package com.example.dylan.randomnoel;

import android.os.Parcel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SaveAssociationTest {

    @Mock
    private Nom mockKey1;
    @Mock
    private Nom mockValue1;
    @Mock
    private Parcel mockParcel;

    @Test
    public void testConstructorAndGetter() {
        Map<Nom, Nom> map = new HashMap<>();
        map.put(mockKey1, mockValue1);

        SaveAssociation saveAssociation = new SaveAssociation(map);
        assertEquals(1, saveAssociation.getAssociation().size());
        assertEquals(mockValue1, saveAssociation.getAssociation().get(mockKey1));
    }

    @Test
    public void testWriteToParcel() {
        Map<Nom, Nom> map = new HashMap<>();
        map.put(mockKey1, mockValue1);
        SaveAssociation saveAssociation = new SaveAssociation(map);

        saveAssociation.writeToParcel(mockParcel, 0);

        verify(mockParcel).writeInt(1);
        verify(mockParcel).writeParcelable(mockKey1, 0);
        verify(mockParcel).writeParcelable(mockValue1, 0);
    }

    @Test
    public void testCreateFromParcel_WithTiramisu() {
        // Simule la version Android Tiramisu+
        try (MockedStatic<Utils> utilsMock = mockStatic(Utils.class)) {
            utilsMock.when(Utils::isAtLeastTiramisuVersion).thenReturn(true);

            when(mockParcel.readInt()).thenReturn(1);
            when(mockParcel.readParcelable(Nom.class.getClassLoader(), Nom.class)).thenReturn(mockKey1, mockValue1);

            SaveAssociation result = SaveAssociation.CREATOR.createFromParcel(mockParcel);

            assertEquals(1, result.getAssociation().size());
            assertEquals(mockValue1, result.getAssociation().get(mockKey1));
        }
    }

    @Test
    public void testCreateFromParcel_PreTiramisu() {
        // Simule une version Android avant Tiramisu
        try (MockedStatic<Utils> utilsMock = mockStatic(Utils.class)) {
            utilsMock.when(Utils::isAtLeastTiramisuVersion).thenReturn(false);

            when(mockParcel.readInt()).thenReturn(1);
            when(mockParcel.readParcelable(Nom.class.getClassLoader())).thenReturn(mockKey1, mockValue1);

            SaveAssociation result = SaveAssociation.CREATOR.createFromParcel(mockParcel);

            assertEquals(1, result.getAssociation().size());
            assertEquals(mockValue1, result.getAssociation().get(mockKey1));
        }
    }

    @Test
    public void testDescribeContents() {
        Map<Nom, Nom> map = new HashMap<>();
        SaveAssociation saveAssociation = new SaveAssociation(map);
        assertEquals(0, saveAssociation.describeContents());
    }

    @Test
    public void testNewArray() {
        SaveAssociation[] array = SaveAssociation.CREATOR.newArray(3);
        assertEquals(3, array.length);
        for (SaveAssociation s : array) {
            assertNull(s); // Par défaut, éléments non initialisés
        }
    }
}
