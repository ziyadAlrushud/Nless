package com.example.myapplication.ClassDB;

public class Account {
    public int accountId;
    public String email;
    public String username;
    public double balance;
    public String currency;

    public Account(int accountId, String email, String username, double balance, String currency) {
        this.accountId = accountId;
        this.email = email;
        this.username = username;
        this.balance = balance;
        this.currency = currency;
    }
}
