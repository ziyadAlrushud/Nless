package com.example.myapplication.Database;

import com.example.myapplication.ClassDB.Card;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseRealtimeDatabaseHelper {
    private DatabaseReference cardsRef;

    public FirebaseRealtimeDatabaseHelper() {
        cardsRef = FirebaseDatabase.getInstance().getReference("cards");
    }

    // Method to add a card to the database
    public void addCard(String email, String cardNumber, String currency, String cardHolderName, String cvv, String expiryDate) {
        Card card = new Card(cardNumber, currency, cardHolderName, cvv, expiryDate);
        cardsRef.child(email.replace(".", ",")).child("user_cards").push().setValue(card);
    }

    // Method to retrieve user's cards from the database
    public void getUserCards(String email, DataCallback callback) {
        cardsRef.child(email.replace(".", ",")).child("user_cards").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Card> cards = new ArrayList<>();
                for (DataSnapshot cardSnapshot : snapshot.getChildren()) {
                    Card card = cardSnapshot.getValue(Card.class);
                    if (card != null) {
                        cards.add(card);
                    }
                }
                callback.onSuccess(cards);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    // Callback interface for data retrieval
    public interface DataCallback {
        void onSuccess(List<Card> cards);
        void onFailure(Exception e);
    }
}
