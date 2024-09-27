package com.example.myapplication.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log; // Import Log for error logging
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 5; // Updated version for transactions
    private static final String TABLE_USERS = "users";
    private static final String TABLE_TRANSACTIONS = "transactions"; // Transactions table

    // Column Names for users
    private static final String COL_1 = "ID";
    private static final String COL_2 = "EMAIL";
    private static final String COL_3 = "PASSWORD";
    private static final String COL_4 = "NAME";
    private static final String COL_5 = "PHONE";
    private static final String COL_6 = "BALANCE";
    private static final String COL_7 = "CARD_NUMBER";

    // Column Names for transactions
    private static final String COL_TRANS_ID = "TRANS_ID";
    private static final String COL_TRANS_EMAIL = "TRANS_EMAIL"; // Email of the user who made the transaction
    private static final String COL_TRANS_TYPE = "TRANS_TYPE"; // Type of transaction (Deposit, Withdraw, Transfer)
    private static final String COL_TRANS_AMOUNT = "TRANS_AMOUNT"; // Amount of money involved
    private static final String COL_TRANS_TIMESTAMP = "TRANS_TIMESTAMP"; // When the transaction happened

    private FirebaseFirestore firestore;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " TEXT UNIQUE, " +
                COL_3 + " TEXT, " +
                COL_4 + " TEXT, " +
                COL_5 + " TEXT, " +
                COL_6 + " REAL DEFAULT 30000, " +
                COL_7 + " TEXT UNIQUE)";
        db.execSQL(createUsersTable);

        // Create transactions table
        String createTransactionsTable = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                COL_TRANS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TRANS_EMAIL + " TEXT, " +
                COL_TRANS_TYPE + " TEXT, " +
                COL_TRANS_AMOUNT + " REAL, " +
                COL_TRANS_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(createTransactionsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS); // Drop transactions table on upgrade
        onCreate(db);
    }

    // Insert or update a user's information in the database
    public boolean insertOrUpdateUser(String email, String password, String name, String phone, double initialBalance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, email);
        contentValues.put(COL_3, password);
        contentValues.put(COL_4, name);
        contentValues.put(COL_5, phone);
        contentValues.put(COL_6, initialBalance);
        contentValues.put(COL_7, generateUniqueCardNumber());

        long result = db.insertWithOnConflict(TABLE_USERS, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        if (result == -1) {
            // User already exists, update the existing user data
            int updatedRows = db.update(TABLE_USERS, contentValues, COL_2 + " = ?", new String[]{email});
            return updatedRows > 0;
        }
        return true;
    }

    // Generate a unique card number for the user
    private String generateUniqueCardNumber() {
        Set<String> existingCardNumbers = new HashSet<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_7 + " FROM " + TABLE_USERS, null);
        while (cursor.moveToNext()) {
            existingCardNumbers.add(cursor.getString(0));
        }
        cursor.close();

        Random random = new Random();
        String cardNumber;
        do {
            cardNumber = String.format("%06d", random.nextInt(1000000));
        } while (existingCardNumbers.contains(cardNumber));

        return cardNumber;
    }

    // Check if a user exists based on their email and password
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_2 + " = ? AND " + COL_3 + " = ?", new String[]{email, password});
        boolean userExists = cursor.getCount() > 0;
        cursor.close();
        return userExists;
    }

    // Check if a given card number is valid (exists in the database)
    public boolean isCardNumberValid(String cardNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_7 + " = ?", new String[]{cardNumber});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean insertOrUpdateAccount(String email, String username, String cardNumber, double initialBalance, String currency) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, email); // Email
        contentValues.put("USERNAME", username); // Username
        contentValues.put(COL_7, cardNumber); // Card number
        contentValues.put("BALANCE", initialBalance); // Add balance column
        contentValues.put("CURRENCY", currency); // Add currency column

        long result = db.insertWithOnConflict(TABLE_USERS, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        return result != -1; // Returns true if the insertion was successful
    }


    // Get the balance of a user by their email
    public double getBalance(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_6 + " FROM " + TABLE_USERS + " WHERE " + COL_2 + " = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            double balance = cursor.getDouble(0);
            cursor.close();
            return balance;
        }
        cursor.close();
        return 0;
    }

    // Get the balance of a user by their card number
    public double getBalanceByCardNumber(String cardNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_6 + " FROM " + TABLE_USERS + " WHERE " + COL_7 + " = ?", new String[]{cardNumber});
        if (cursor.moveToFirst()) {
            double balance = cursor.getDouble(0);
            cursor.close();
            return balance;
        }
        cursor.close();
        return 0;
    }

    // Update the balance of a user by their email
    public boolean updateBalance(String email, double newBalance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_6, newBalance);
        int result = db.update(TABLE_USERS, contentValues, COL_2 + " = ?", new String[]{email});
        return result > 0; // Return true if at least one row was updated
    }

    // Update the balance of a user by their card number
    public boolean updateBalanceByCardNumber(String cardNumber, double newBalance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_6, newBalance);
        int result = db.update(TABLE_USERS, contentValues, COL_7 + " = ?", new String[]{cardNumber});
        return result > 0; // Return true if at least one row was updated
    }

    // Log a transaction in the database
    public void logTransaction(String email, String transactionType, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TRANS_EMAIL, email);
        contentValues.put(COL_TRANS_TYPE, transactionType);
        contentValues.put(COL_TRANS_AMOUNT, amount);
        db.insert(TABLE_TRANSACTIONS, null, contentValues);
    }

    // Get all transaction history for a user
// Get transaction history for a specific user by their email
    public ArrayList<String> getTransactionHistoryForUser(String email) {
        ArrayList<String> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Execute the query to get all transaction history for the specified email
            cursor = db.rawQuery("SELECT * FROM " + TABLE_TRANSACTIONS + " WHERE " + COL_TRANS_EMAIL + " = ? ORDER BY " + COL_TRANS_TIMESTAMP + " DESC", new String[]{email});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int typeIndex = cursor.getColumnIndex(COL_TRANS_TYPE);
                    int amountIndex = cursor.getColumnIndex(COL_TRANS_AMOUNT);
                    int timestampIndex = cursor.getColumnIndex(COL_TRANS_TIMESTAMP);

                    if (typeIndex != -1 && amountIndex != -1 && timestampIndex != -1) {
                        String type = cursor.getString(typeIndex);
                        double amount = cursor.getDouble(amountIndex);
                        String timestamp = cursor.getString(timestampIndex);

                        // Format and add the transaction to the list
                        String transaction = type + ": $" + amount + " at " + timestamp;
                        transactions.add(transaction);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving transaction history for user: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return transactions;
    }


    // Get the card number of a user by their email
    public String getCardNumber(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_7 + " FROM " + TABLE_USERS + " WHERE " + COL_2 + " = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            String cardNumber = cursor.getString(0);
            cursor.close();
            return cardNumber;
        }
        cursor.close();
        return null;
    }

    // Clear the transaction history for a user (optional)
    public void clearTransactionHistory(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTIONS, COL_TRANS_EMAIL + " = ?", new String[]{email});
    }

    // User class to represent user data
    public static class User {
        public String email;
        public String password;
        public String name;
        public String phone;
        public double balance;

        public User() {}

        public User(String email, String password, String name, String phone, double balance) {
            this.email = email;
            this.password = password;
            this.name = name;
            this.phone = phone;
            this.balance = balance;
        }
    }
}


