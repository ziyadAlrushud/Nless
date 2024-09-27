package com.example.myapplication.ClassActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;  // Added ImageButton import
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Database.DatabaseHelper;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;

public class TransferActivity extends AppCompatActivity {

    private TextView balanceTextView;
    private EditText recipientCardNumberEditText, transferAmountEditText;
    private Button transferButton;
    private DatabaseHelper databaseHelper;
    private FirebaseAuth auth;
    private String email;
    private ImageButton backButton; // Declare the ImageButton

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        balanceTextView = findViewById(R.id.balanceTextView);
        recipientCardNumberEditText = findViewById(R.id.recipientCardNumberEditText);
        transferAmountEditText = findViewById(R.id.transferAmountEditText);
        transferButton = findViewById(R.id.transferButton);
        backButton = findViewById(R.id.imageButton6);  // Initialize the back button

        databaseHelper = new DatabaseHelper(this);
        auth = FirebaseAuth.getInstance();

        email = auth.getCurrentUser().getEmail();
        if (email != null) {
            updateUI();
        }

        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTransfer();
            }
        });

        // Back button functionality
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Handle back navigation
            }
        });
    }

    private void updateUI() {
        double balance = databaseHelper.getBalance(email);
        balanceTextView.setText("Current Balance: $" + balance);
    }

    private void handleTransfer() {
        String recipientCardNumber = recipientCardNumberEditText.getText().toString();
        String amountStr = transferAmountEditText.getText().toString();

        if (!recipientCardNumber.isEmpty() && !amountStr.isEmpty()) {
            double transferAmount = Double.parseDouble(amountStr);
            double currentBalance = databaseHelper.getBalance(email);

            if (transferAmount > currentBalance) {
                Toast.makeText(this, "Insufficient Balance.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!databaseHelper.isCardNumberValid(recipientCardNumber)) {
                Toast.makeText(this, "Invalid recipient card number.", Toast.LENGTH_SHORT).show();
                return;
            }

            double newBalance = currentBalance - transferAmount;
            if (databaseHelper.updateBalance(email, newBalance)) {
                double recipientBalance = databaseHelper.getBalanceByCardNumber(recipientCardNumber);
                databaseHelper.updateBalanceByCardNumber(recipientCardNumber, recipientBalance + transferAmount);
                databaseHelper.logTransaction(email, "Transfer to " + recipientCardNumber, transferAmount);
                Toast.makeText(this, "Transfer Successful: $" + transferAmount, Toast.LENGTH_SHORT).show();
                updateUI();
                recipientCardNumberEditText.setText("");
                transferAmountEditText.setText("");
                returnToAccountActivity();
            } else {
                Toast.makeText(this, "Transfer Failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter card number and amount.", Toast.LENGTH_SHORT).show();
        }
    }

    private void returnToAccountActivity() {
        Intent intent = new Intent(this, AccountActivity.class);
        intent.putExtra("updatedBalance", databaseHelper.getBalance(email));
        startActivity(intent);
        finish();
    }
}
