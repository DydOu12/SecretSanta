package com.example.dylan.randomnoel;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dylan.randomnoel.databinding.ItemHistoryBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import lombok.Getter;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<String> history;

    public HistoryAdapter(List<String> history) {
        this.history = history;
    }

    @Getter
    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewHistoryItem;

        public HistoryViewHolder(ItemHistoryBinding binding) {
            super(binding.getRoot());
            textViewHistoryItem = getTextViewHistoryItem(binding);
        }

        protected TextView getTextViewHistoryItem(ItemHistoryBinding binding) {
            return binding.textViewHistoryItem;
        }
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemHistoryBinding binding = ItemHistoryBinding.inflate(inflater, parent, false);
        return new HistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        String historyItem = history.get(position);
        holder.getTextViewHistoryItem().setText(historyItem);
    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateHistory(List<String> newHistory) {
        history = newHistory;
        notifyDataSetChanged();
    }
}