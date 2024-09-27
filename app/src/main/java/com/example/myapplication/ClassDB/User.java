package com.example.myapplication.ClassDB;

public class User {
    private String email;
    private String name;
    private String phone;
    private double balance;
    private String cardNumber;
    private String currency;

    public User(String email, String name, String phone, double balance, String cardNumber, String currency) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.balance = balance;
        this.cardNumber = cardNumber;
        this.currency = currency;
    }

    public User() { // Required for Firestore
    }

    // Getters and Setters (if necessary)
}
