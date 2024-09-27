package com.example.myapplication.ClassDB;

public class CardDetails {
    private String cardNumber;
    private String currency;

    public CardDetails(String cardNumber, String currency) {
        this.cardNumber = cardNumber;
        this.currency = currency;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
