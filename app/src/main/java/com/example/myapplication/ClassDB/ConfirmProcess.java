package com.example.myapplication.ClassDB;

import com.example.myapplication.Database.DatabaseHelper;

public class ConfirmProcess {
    private DatabaseHelper databaseHelper;

    public ConfirmProcess(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public boolean transferFunds(String senderCardNumber, String receiverCardNumber, double amount) {
        double senderBalance = databaseHelper.getBalanceByCardNumber(senderCardNumber);
        double receiverBalance = databaseHelper.getBalanceByCardNumber(receiverCardNumber);

        if (senderBalance >= amount) {
            databaseHelper.updateBalanceByCardNumber(senderCardNumber, senderBalance - amount);
            databaseHelper.updateBalanceByCardNumber(receiverCardNumber, receiverBalance + amount);
            saveTransferHistory(senderCardNumber, receiverCardNumber, amount); // Log the transaction
            return true;
        } else {
            return false;
        }
    }

    private void saveTransferHistory(String senderCardNumber, String receiverCardNumber, double amount) {
        // Save transfer details to a history table (see next section)
    }
}
