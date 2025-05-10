package com.example.dylan.randomnoel;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MonAdapterRandomTest {

    @Mock
    private ViewGroup mockParent;

    @Mock
    private View mockRowView;

    @Mock
    private TextView mockAssoNom;

    @Mock
    private LayoutInflater mockLayoutInflater;

    @Mock
    private Nom mockNom;

    private MonAdapterRandom adapter;
    private Map<Nom,Nom> nomMap;

    @Before
    public void setUp() {
        when(mockNom.getName()).thenReturn("Test1");
        when(mockNom.getCouleur()).thenReturn(0xFFFF0000);

        nomMap = new HashMap<>();
        nomMap.put(mockNom,mockNom);

        adapter = spy(new MonAdapterRandom(nomMap));

        // Configuration des mocks pour les vues
        when(mockParent.getContext()).thenReturn(mock(Context.class));
        when(mockRowView.findViewById(R.id.assocNoms)).thenReturn(mockAssoNom);
    }

    @Test
    public void testGetCount() {
        // Act
        int count = adapter.getCount();

        // Assert
        assertEquals(1, count);
    }

    @Test
    public void testGetItem() {
        // Act
        Object item = adapter.getItem(0);

        // Assert
        assertNull(item);
    }

    @Test
    public void testGetItemId() {
        // Act
        long id = adapter.getItemId(5);

        // Assert
        assertEquals(5, id);
    }

    @Test
    public void testGetView_populatesViewsCorrectly() {
        try(MockedStatic<LayoutInflater> mockSLayoutInflater = mockStatic(LayoutInflater.class);
            MockedStatic<Color> mockColor = mockStatic(Color.class)) {

            mockSLayoutInflater.when(() -> LayoutInflater.from(any())).thenReturn(mockLayoutInflater);
            when(mockLayoutInflater.inflate(anyInt(), any(), anyBoolean())).thenReturn(mockRowView);

            mockColor.when(() -> Color.red(anyInt())).thenReturn(10);
            mockColor.when(() -> Color.green(anyInt())).thenReturn(100);
            mockColor.when(() -> Color.blue(anyInt())).thenReturn(1000);

            View result = adapter.getView(0, null, mockParent);

            // Vérifie que le LayoutInflater a été appelé correctement
            verify(mockLayoutInflater).inflate(R.layout.vue_liste_random, mockParent, false);

            // Vérifie que les bons setters sont appelés avec les valeurs attendues
            assertNotNull(nomMap.get(mockNom));
            verify(mockAssoNom).setText("Test1, tu auras : Test1");

            // Vérifie que les couleurs ont été définies (même si ici c’est la couleur par défaut)
            verify(mockAssoNom).setTextColor(anyInt());

            // Vérifie que le bon résultat est retourné
            assertEquals(mockRowView, result);
        }
    }
}