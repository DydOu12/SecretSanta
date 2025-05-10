package com.example.dylan.randomnoel;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dylan.randomnoel.databinding.ItemHistoryBinding;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HistoryAdapterTest {

    @Mock
    private ItemHistoryBinding mockItemHistoryBinding;

    @Mock
    private LayoutInflater mockLayoutInflater;

    @Mock
    private ViewGroup mockViewGroup;

    @Mock
    private TextView mockTextView;

    @Mock
    private LinearLayout mockLinearLayout;

    private HistoryAdapter adapter;

    @Before
    public void setUp() {
        List<String> initialHistory = Arrays.asList("2023: Alice → Bob", "2024: Bob → Alice");
        adapter = spy(new HistoryAdapter(initialHistory));
    }

    @Test
    public void testGetItemCount() {
        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void testUpdateHistory() {
        List<String> newHistory = Collections.singletonList("2025: Charlie → Dana");
        doNothing().when(adapter).notifyDataSetChanged();
        adapter.updateHistory(newHistory);
        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void testOnCreateViewHolder() {
        try(MockedStatic<LayoutInflater> mockSLayoutInflater = mockStatic(LayoutInflater.class);
            MockedStatic<ItemHistoryBinding> mockSItemHistoryBinding = mockStatic(ItemHistoryBinding.class)) {

            mockSLayoutInflater.when(() ->
                        LayoutInflater.from(any()))
                        .thenReturn(mockLayoutInflater);
            mockSItemHistoryBinding.when(() ->
                         ItemHistoryBinding.inflate(any(LayoutInflater.class), any(ViewGroup.class), anyBoolean()))
                        .thenReturn(mockItemHistoryBinding);

            doReturn(mockLinearLayout).when(mockItemHistoryBinding).getRoot();

            HistoryAdapter.HistoryViewHolder viewHolder = adapter.onCreateViewHolder(mockViewGroup, 0);
            assertNotNull(viewHolder);
        }
    }

    @Test
    public void testOnBindViewHolder() {
        // Création d'un ViewHolder mocké avec TextView simulé
        doReturn(mockLinearLayout).when(mockItemHistoryBinding).getRoot();

        HistoryAdapter.HistoryViewHolder mockViewHolder = spy(new HistoryAdapter.HistoryViewHolder(mockItemHistoryBinding));
        doReturn(mockTextView).when(mockViewHolder).getTextViewHistoryItem();

        adapter.onBindViewHolder(mockViewHolder, 1);
        verify(mockTextView).setText("2024: Bob → Alice");
    }
}
