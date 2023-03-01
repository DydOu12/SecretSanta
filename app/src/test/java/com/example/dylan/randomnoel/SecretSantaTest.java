package com.example.dylan.randomnoel;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

public class SecretSantaTest {



    private final Nom helene = new Nom("Hélène", "+33606060600", 1, 1);
    private final Nom laurent = new Nom("Laurent", "+33606060601", 1, 1);
    private final Nom manon = new Nom("Manon", "+33606060602", 1, 1);
    private final Nom elie = new Nom("Elie", "+33606060603", 1, 1);

    private final Nom alain = new Nom("Alain", "+33606060604", 2, 2);
    private final Nom bea = new Nom("Béa", "+33606060605", 2, 2);
    private final Nom matthieu = new Nom("Matthieu", "+33606060606", 2, 2);
    private final Nom francois = new Nom("François", "+33606060607", 2, 2);
    private final Nom aurelie = new Nom("Aurelie", "+33606060608", 2, 2);

    private final Nom babeth = new Nom("Babeth", "+33606060609", 3, 3);
    private final Nom jacques = new Nom("Jacques", "+33606060610", 3, 3);
    private final Nom sarah = new Nom("Sarah", "+33606060611", 3, 3);
    private final Nom samuel = new Nom("Samuel", "+33606060612", 3, 3);
    private final Nom paul = new Nom("Paul", "+33606060613", 3, 3);

    private final Nom dylan = new Nom("Jacques", "+33606060610", 4, 4);
    private final Nom oceane = new Nom("Sarah", "+33606060611", 4, 4);
    private final Nom jp = new Nom("Samuel", "+33606060612", 4, 4);

    private List<Nom> famille = Arrays.asList(dylan, oceane, jp, paul, samuel, sarah, jacques, babeth,
                                    aurelie, francois, matthieu, bea, alain, elie, manon, laurent, helene);

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        // getCouleurIfGroupePresent
        // estPresent
        // associationPossible
    }
}