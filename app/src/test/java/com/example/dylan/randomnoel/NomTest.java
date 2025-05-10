package com.example.dylan.randomnoel;

import android.os.Parcel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NomTest {

    @Mock
    private Parcel mockedParcel;

    private Nom nom;
    private static final String NAME = "Test Name";
    private static final String NUMERO = "123456";
    private static final Integer GROUPE = 1;
    private static final Integer COULEUR = 2;

    @Before
    public void setUp() {
        nom = new Nom(NAME, NUMERO, GROUPE, COULEUR);
    }

    @Test
    public void testConstructorAllArgs() {
        // Assert
        assertEquals(NAME, nom.getName());
        assertEquals(NUMERO, nom.getNumero());
        assertEquals(GROUPE, nom.getGroupe());
        assertEquals(COULEUR, nom.getCouleur());
    }

    @Test
    public void testConstructorTwoArgs() {
        // Arrange & Act
        Nom simplNom = new Nom(NAME, NUMERO);

        // Assert
        assertEquals(NAME, simplNom.getName());
        assertEquals(NUMERO, simplNom.getNumero());
        assertNull(simplNom.getGroupe());
        assertNull(simplNom.getCouleur());
    }

    @Test
    public void testParcelableConstructor() {
        // Arrange
        when(mockedParcel.readString()).thenReturn(NAME).thenReturn(NUMERO);
        when(mockedParcel.readInt()).thenReturn(COULEUR).thenReturn(GROUPE);

        // Act
        Nom parcelNom = new Nom(mockedParcel);

        // Assert
        assertEquals(NAME, parcelNom.getName());
        assertEquals(NUMERO, parcelNom.getNumero());
        assertEquals(COULEUR, parcelNom.getCouleur());
        assertEquals(GROUPE, parcelNom.getGroupe());

        // Verify
        verify(mockedParcel, times(2)).readString();
        verify(mockedParcel, times(2)).readInt();
    }

    @Test
    public void testCreatorCreateFromParcel() {
        // Arrange
        when(mockedParcel.readString()).thenReturn(NAME).thenReturn(NUMERO);
        when(mockedParcel.readInt()).thenReturn(COULEUR).thenReturn(GROUPE);

        // Act
        Nom createdNom = Nom.CREATOR.createFromParcel(mockedParcel);

        // Assert
        assertNotNull(createdNom);
        assertEquals(NAME, createdNom.getName());
        assertEquals(NUMERO, createdNom.getNumero());
        assertEquals(COULEUR, createdNom.getCouleur());
        assertEquals(GROUPE, createdNom.getGroupe());
    }

    @Test
    public void testCreatorNewArray() {
        // Arrange & Act
        Nom[] nomArray = Nom.CREATOR.newArray(3);

        // Assert
        assertNotNull(nomArray);
        assertEquals(3, nomArray.length);
    }

    @Test
    public void testDescribeContents() {
        // Act
        int contentsValue = nom.describeContents();

        // Assert
        assertEquals(0, contentsValue);
    }

    @Test
    public void testWriteToParcel() {
        // Act
        nom.writeToParcel(mockedParcel, 0);

        // Assert
        verify(mockedParcel).writeString(NAME);
        verify(mockedParcel).writeString(NUMERO);
        verify(mockedParcel).writeInt(COULEUR);
        verify(mockedParcel).writeInt(GROUPE);
    }

    @Test
    public void testEquals_SameObject() {
        // Act & Assert
        assertEquals(nom, nom);
    }

    @Test
    public void testEquals_NullObject() {
        // Act & Assert
        assertNotEquals(null, nom);
    }

    @Test
    public void testEquals_DifferentClass() {
        // Arrange
        String differentClassObject = "Not a Nom object";

        // Act & Assert
        assertNotEquals(differentClassObject, nom);
    }

    @Test
    public void testEquals_SameNumero() {
        // Arrange
        Nom otherNom = new Nom("Different name", NUMERO, 3, 4);

        // Act & Assert
        assertEquals(nom, otherNom);
    }

    @Test
    public void testEquals_DifferentNumero() {
        // Arrange
        Nom otherNom = new Nom(NAME, "654321", GROUPE, COULEUR);

        // Act & Assert
        assertNotEquals(nom, otherNom);
    }

    @Test
    public void testHashCode() {
        // Arrange
        Nom sameNom = new Nom(NAME, NUMERO, GROUPE, COULEUR);
        Nom differentNom = new Nom("Other", "654321", 3, 4);

        // Act & Assert
        assertEquals(nom.hashCode(), sameNom.hashCode());
        assertNotEquals(nom.hashCode(), differentNom.hashCode());
    }

    @Test
    public void testToString() {
        // Act
        String result = nom.toString();

        // Assert
        assertEquals(NAME, result);
    }

    @Test
    public void testSetters() {
        // Arrange
        String newName = "New Name";
        String newNumero = "987654";
        Integer newGroupe = 5;
        Integer newCouleur = 6;

        // Act
        nom.setName(newName);
        nom.setNumero(newNumero);
        nom.setGroupe(newGroupe);
        nom.setCouleur(newCouleur);

        // Assert
        assertEquals(newName, nom.getName());
        assertEquals(newNumero, nom.getNumero());
        assertEquals(newGroupe, nom.getGroupe());
        assertEquals(newCouleur, nom.getCouleur());
    }
}