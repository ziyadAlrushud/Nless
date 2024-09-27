package com.example.myapplication.ClassActivity;

import android.content.Intent;  // للاستيراد Intent
import android.os.Bundle;        // للاستيراد Bundle
import android.view.View;        // للاستيراد View
import android.widget.Button;     // للاستيراد Button
import android.widget.ImageButton; // للاستيراد ImageButton
import android.widget.TextView;   // للاستيراد TextView
import androidx.appcompat.app.AppCompatActivity; // للاستيراد AppCompatActivity

import com.example.myapplication.Database.DatabaseHelper; // للاستيراد DatabaseHelper
import com.example.myapplication.R; // للاستيراد R
import com.google.firebase.auth.FirebaseAuth; // للاستيراد FirebaseAuth


public class AccountActivity extends AppCompatActivity {

    private TextView balanceTextView, cardNumberTextView;
    private ImageButton depositButton, withdrawButton, transferButton, historyButton, currencyButton;
    private Button viewCardDetailsButton; // Declare the new button
    private DatabaseHelper databaseHelper;
    private FirebaseAuth auth;
    private static final int CURRENCY_SELECTION_REQUEST = 1; // Request code for CurrencySelectionActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        balanceTextView = findViewById(R.id.balanceTextView);
        cardNumberTextView = findViewById(R.id.cardNumberTextView);
        depositButton = findViewById(R.id.depositButton);
        withdrawButton = findViewById(R.id.withdrawButton);
        transferButton = findViewById(R.id.transferButton);
        historyButton = findViewById(R.id.historyButton);
        currencyButton = findViewById(R.id.currencyButton);
        viewCardDetailsButton = findViewById(R.id.viewCardDetailsButton); // Initialize the new button

        databaseHelper = new DatabaseHelper(this);
        auth = FirebaseAuth.getInstance();

        String email = auth.getCurrentUser().getEmail();
        if (email != null) {
            updateUI();
        }

        currencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCurrencySelectionActivity();
            }
        });

        depositButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToDepositActivity();
            }
        });

        withdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToWithdrawActivity();
            }
        });

        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTransferActivity();
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToHistoryActivity();
            }
        });

        // Set OnClickListener for the new button
        viewCardDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCardDetailsActivity();
            }
        });

        // Check if there's an updated balance from DepositActivity, WithdrawActivity, or TransferActivity
        if (getIntent().hasExtra("updatedBalance")) {
            double updatedBalance = getIntent().getDoubleExtra("updatedBalance", 0);
            balanceTextView.setText("Balance: $" + updatedBalance);
        }
    }

    private void updateUI() {
        double balance = databaseHelper.getBalance(auth.getCurrentUser().getEmail());
        String cardNumber = databaseHelper.getCardNumber(auth.getCurrentUser().getEmail());

        balanceTextView.setText("Balance: $" + balance);
        cardNumberTextView.setText("Card Number: " + cardNumber);
    }

    private void navigateToDepositActivity() {
        Intent intent = new Intent(this, DepositActivity.class);
        startActivity(intent);
    }

    private void navigateToWithdrawActivity() {
        Intent intent = new Intent(this, WithdrawActivity.class);
        startActivity(intent);
    }

    private void navigateToTransferActivity() {
        Intent intent = new Intent(this, TransferActivity.class);
        startActivity(intent);
    }

    private void navigateToHistoryActivity() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    private void navigateToCurrencySelectionActivity() {
        Intent intent = new Intent(this, CurrencySelectionActivity.class);
        startActivityForResult(intent, CURRENCY_SELECTION_REQUEST); // Start for result
    }

    // New method to navigate to CardDetailsActivity
    private void navigateToCardDetailsActivity() {
        Intent intent = new Intent(this, CardDetailsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CURRENCY_SELECTION_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                String updatedCardNumber = data.getStringExtra("updatedCardNumber");
                String updatedCurrency = data.getStringExtra("updatedCurrency");

                // Update the UI with the new card number
                cardNumberTextView.setText("Card Number: " + updatedCardNumber);
                // Optionally show currency if needed
                // cardNumberTextView.setText("Card Number: " + updatedCardNumber + " (" + updatedCurrency + ")");
            }
        }
    }
}

