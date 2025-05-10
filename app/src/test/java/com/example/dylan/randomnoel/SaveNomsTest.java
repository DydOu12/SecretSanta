package com.example.dylan.randomnoel;

import android.os.Parcel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SaveNomsTest {

    @Mock
    private Parcel mockedParcel;

    @Mock
    private Nom mockedNom;

    private ArrayList<Nom> nomsList;

    @Before
    public void setUp() {
        nomsList = new ArrayList<>();
        nomsList.add(mockedNom);
    }

    @Test
    public void testConstructor() {
        // Arrange & Act
        SaveNoms saveNoms = new SaveNoms(nomsList);

        // Assert
        assertEquals(nomsList, saveNoms.getNoms());
    }

    @Test
    public void testParcelableConstructor() {
        // Arrange
        ArrayList<Nom> expectedNoms = new ArrayList<>();
        expectedNoms.add(mockedNom);

        when(mockedParcel.createTypedArrayList(Nom.CREATOR)).thenReturn(expectedNoms);

        // Act
        SaveNoms saveNoms = new SaveNoms(mockedParcel);

        // Assert
        assertEquals(expectedNoms, saveNoms.getNoms());
        verify(mockedParcel).createTypedArrayList(Nom.CREATOR);
    }

    @Test
    public void testCreatorCreateFromParcel() {
        // Arrange
        when(mockedParcel.createTypedArrayList(Nom.CREATOR)).thenReturn(nomsList);

        // Act
        SaveNoms saveNoms = SaveNoms.CREATOR.createFromParcel(mockedParcel);

        // Assert
        assertNotNull(saveNoms);
        assertEquals(nomsList, saveNoms.getNoms());
        verify(mockedParcel).createTypedArrayList(Nom.CREATOR);
    }

    @Test
    public void testCreatorNewArray() {
        // Arrange & Act
        SaveNoms[] saveNomsArray = SaveNoms.CREATOR.newArray(3);

        // Assert
        assertNotNull(saveNomsArray);
        assertEquals(3, saveNomsArray.length);
    }

    @Test
    public void testDescribeContents() {
        // Arrange
        SaveNoms saveNoms = new SaveNoms(nomsList);

        // Act
        int contentsValue = saveNoms.describeContents();

        // Assert
        assertEquals(0, contentsValue);
    }

    @Test
    public void testWriteToParcel() {
        // Arrange
        SaveNoms saveNoms = new SaveNoms(nomsList);

        // Act
        saveNoms.writeToParcel(mockedParcel, 0);

        // Assert
        verify(mockedParcel).writeTypedList(nomsList);
    }
}