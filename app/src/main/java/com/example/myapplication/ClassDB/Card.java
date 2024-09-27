package com.example.myapplication.ClassDB;

public class Card {
    private String cardNumber;
    private String currency;
    private String cardHolderName;
    private String cvv;
    private String expiryDate;

    public Card(String cardNumber, String currency, String cardHolderName, String cvv, String expiryDate) {
        this.cardNumber = cardNumber;
        this.currency = currency;
        this.cardHolderName = cardHolderName;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public String getCvv() {
        return cvv;
    }

    public String getExpiryDate() {
        return expiryDate;
    }
}
