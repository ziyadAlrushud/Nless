package com.example.myapplication.ClassActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ClassDB.Card;
import com.example.myapplication.R;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private ArrayList<Card> cardList;

    public CardAdapter(ArrayList<Card> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = cardList.get(position);
        holder.cardNumberTextView.setText("Card Number: " + card.getCardNumber());
        holder.currencyTextView.setText("Currency: " + card.getCurrency());
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardNumberTextView;
        TextView currencyTextView;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardNumberTextView = itemView.findViewById(R.id.cardNumberTextView);
            currencyTextView = itemView.findViewById(R.id.currencyTextView);
        }
    }
}
