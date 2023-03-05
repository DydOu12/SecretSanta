package com.example.dylan.randomnoel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecretSantaTest {

    private static final Nom helene = new Nom("Hélène", "+33606060600", 1, 1);
    private static final Nom laurent = new Nom("Laurent", "+33606060601", 1, 1);
    private static final Nom manon = new Nom("Manon", "+33606060602", 1, 1);
    private static final Nom elie = new Nom("Elie", "+33606060603", 1, 1);

    private static final Nom alain = new Nom("Alain", "+33606060604", 2, 2);
    private static final Nom bea = new Nom("Béa", "+33606060605", 2, 2);
    private static final Nom matthieu = new Nom("Matthieu", "+33606060606", 2, 2);
    private static final Nom francois = new Nom("François", "+33606060607", 2, 2);
    private static final Nom aurelie = new Nom("Aurelie", "+33606060608", 2, 2);

    private static final Nom babeth = new Nom("Babeth", "+33606060609", 3, 3);
    private static final Nom jacques = new Nom("Jacques", "+33606060610", 3, 3);
    private static final Nom sarah = new Nom("Sarah", "+33606060611", 3, 3);
    private static final Nom samuel = new Nom("Samuel", "+33606060612", 3, 3);
    private static final Nom paul = new Nom("Paul", "+33606060613", 3, 3);

    private static final Nom dylan = new Nom("Dylan", "+33606060614", 4, 4);
    private static final Nom oceane = new Nom("Oceane", "+33606060615", 4, 4);
    private static final Nom jp = new Nom("Jean-Pierre", "+33606060616", 4, 4);

    @BeforeClass
    public static void setup() {
        MainActivity.noms = Arrays.asList(dylan, oceane, jp, paul, samuel, sarah, jacques, babeth,
                aurelie, francois, matthieu, bea, alain, elie, manon, laurent, helene);
    }

    @Test
    public void estPresent_isCorrect() {
        Nom nom = new Nom("Dydou", "+33606060610", 3, 3);
        assertTrue(MainActivity.estPresent(nom));
        nom.setNumero("+33606060617");
        assertFalse(MainActivity.estPresent(nom));
    }

    @Test
    public void getCouleurIfGroupePresent_isCorrect() {
        assertEquals(Integer.valueOf(4), MainActivity.getCouleurIfGroupePresent(4));
        assertNull(MainActivity.getCouleurIfGroupePresent(5));
        assertNotEquals(Integer.valueOf(3), MainActivity.getCouleurIfGroupePresent(2));
    }

    @Test
    public void getNumberOfPersonByGroupe_isCorrect() {
        Map<Integer, Integer> assoFamille = new HashMap<>();
        assoFamille.put(1,4);
        assoFamille.put(2,5);
        assoFamille.put(3,5);
        assoFamille.put(4,3);
        assertEquals(assoFamille, MainActivity.getNumberOfPersonByGroupe());
    }

    @Test
    public void associationPossible_isCorrect() {
        Map<Integer, Integer> coupleAssoImpossible = new HashMap<>();
        coupleAssoImpossible.put(1,1);
        coupleAssoImpossible.put(2,2);
        assertFalse(MainActivity.associationPossible(coupleAssoImpossible));

        coupleAssoImpossible.put(3,1);
        assertTrue(MainActivity.associationPossible(coupleAssoImpossible));
    }

    @Test
    public void choixPossible_isCorrect() {
        List<Nom> noms = new ArrayList<>();
        noms.add(dylan);
        noms.add(oceane);
        noms.add(jacques);

        List<Nom> choix = new ArrayList<>();
        choix.add(dylan);
        choix.add(oceane);
        assertEquals(choix, ResultRandom.choixPossible(noms, 3));

        choix.clear();
        choix.add(jacques);
        assertEquals(choix, ResultRandom.choixPossible(noms, 4));

        noms.clear();
        assertEquals(Collections.emptyList(), ResultRandom.choixPossible(noms, 1));
    }

}