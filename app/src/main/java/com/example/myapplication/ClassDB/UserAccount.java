package com.example.myapplication.ClassDB;

public class UserAccount {
    private static final String COL_BALANCE = "BALANCE";
    private static final String COL_CURRENCY = "CURRENCY";

    private double balance;
    private String currency;

    public UserAccount(double balance, String currency) {
        this.balance = balance;
        this.currency = currency;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public static String getColBalance() {
        return COL_BALANCE;
    }

    public static String getColCurrency() {
        return COL_CURRENCY;
    }
}
