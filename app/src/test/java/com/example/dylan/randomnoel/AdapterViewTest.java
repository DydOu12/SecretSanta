package com.example.dylan.randomnoel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class AdapterViewTest {

    @Mock
    private ViewGroup mockParent;

    @Mock
    private View mockRowView;

    @Mock
    private TextView mockIdL;

    @Mock
    private EditText mockNomEditText;

    @Mock
    private TextView mockNumGroup;

    @Mock
    private LayoutInflater mockLayoutInflater;

    @Mock
    private Editable mockEditable;

    @Mock
    private Nom mockNom;

    @Mock
    private ImageView mockImageView;

    @Captor
    private ArgumentCaptor<TextWatcher> watcherCaptor;

    @Captor
    private ArgumentCaptor<View.OnClickListener> clickCaptor;

    private AdapterView adapter;
    private List<Nom> nomList;

    @Before
    public void setUp() {
        when(mockNom.getName()).thenReturn("Test1");
        when(mockNom.getNumero()).thenReturn("123");
        when(mockNom.getGroupe()).thenReturn(1);
        when(mockNom.getCouleur()).thenReturn(0xFFFF0000);

        nomList = new ArrayList<>();
        nomList.add(mockNom);

        adapter = spy(new AdapterView(nomList));

        // Configuration des mocks pour les vues
        when(mockParent.getContext()).thenReturn(mock(Context.class));
        when(mockRowView.findViewById(R.id.idLi)).thenReturn(mockIdL);
        when(mockRowView.findViewById(R.id.nomContactInList)).thenReturn(mockNomEditText);
        when(mockRowView.findViewById(R.id.numGroupe)).thenReturn(mockNumGroup);
        when(mockRowView.findViewById(R.id.suppr)).thenReturn(mockImageView);
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
            verify(mockLayoutInflater).inflate(R.layout.vue_liste, mockParent, false);

            // Vérifie que les bons setters sont appelés avec les valeurs attendues
            verify(mockIdL).setText("1");
            verify(mockNomEditText).setText("Test1");
            verify(mockNumGroup).setText("123 - 1");

            // Vérifie que les couleurs ont été définies (même si ici c’est la couleur par défaut)
            verify(mockIdL).setTextColor(anyInt());
            verify(mockNomEditText).setTextColor(anyInt());
            verify(mockNumGroup).setTextColor(anyInt());

            // Vérifie qu’un listener est bien ajouté
            verify(mockNomEditText).addTextChangedListener(any());

            // Vérifie que le bon résultat est retourné
            assertEquals(mockRowView, result);
        }
    }

    @Test
    public void testTextWatcher_setsNameOnNom() {
        try(MockedStatic<LayoutInflater> mockSLayoutInflater = mockStatic(LayoutInflater.class)) {

            mockSLayoutInflater.when(() -> LayoutInflater.from(any())).thenReturn(mockLayoutInflater);
            when(mockLayoutInflater.inflate(anyInt(), any(), anyBoolean())).thenReturn(mockRowView);

            when(mockNom.getCouleur()).thenReturn(null);

            adapter.getView(0, null, mockParent);

            // Capture le TextWatcher
            verify(mockNomEditText).addTextChangedListener(watcherCaptor.capture());
            TextWatcher watcher = watcherCaptor.getValue();

            // Simule un Editable avec un texte donné
            when(mockEditable.toString()).thenReturn("NouveauNom");

            // Appelle manuellement onTextChanged, afterTextChanged() et beforeTextChanged()
            watcher.beforeTextChanged("AncienNom", 0, 0, 0);
            watcher.onTextChanged("NouveauNom", 0, 0, 0);
            watcher.afterTextChanged(mockEditable);

            // Vérifie que Nom.setName("NouveauNom") a bien été appelé
            verify(mockNom).setName("NouveauNom");
        }
    }

    @Test
    public void testDeleteClick_removesItemAndNotifies() {

        try(MockedStatic<LayoutInflater> mockSLayoutInflater = mockStatic(LayoutInflater.class)) {

            mockSLayoutInflater.when(() -> LayoutInflater.from(any())).thenReturn(mockLayoutInflater);
            when(mockLayoutInflater.inflate(anyInt(), any(), anyBoolean())).thenReturn(mockRowView);

            when(mockNom.getCouleur()).thenReturn(null);

            doNothing().when(adapter).notifyDataSetChanged();

            // Création d'un mock TextWatcher qui sera retourné par getTag()
            TextWatcher oldWatcher = mock(TextWatcher.class);
            when(mockNomEditText.getTag()).thenReturn(oldWatcher);

            // Appel de getView
            adapter.getView(0, null, mockParent);

            // Vérifie que removeTextChangedListener a été appelé avec l'ancien watcher
            verify(mockNomEditText).removeTextChangedListener(oldWatcher);

            // Capture du listener du bouton
            verify(mockImageView).setOnClickListener(clickCaptor.capture());
            View.OnClickListener capturedListener = clickCaptor.getValue();

            // Simule un clic
            capturedListener.onClick(mockRowView);

            // Vérifie que l'élément a été supprimé
            assertEquals(0, nomList.size());

            // Vérifie que notifyDataSetChanged a été appelé
            verify(adapter).notifyDataSetChanged();
        }
    }
}