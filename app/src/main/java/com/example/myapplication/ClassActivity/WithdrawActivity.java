package com.example.myapplication.ClassActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.Database.DatabaseHelper;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;

public class WithdrawActivity extends AppCompatActivity {

    private TextView balanceTextView;
    private EditText withdrawAmountEditText;
    private EditText phoneNumberEditText; // New EditText for phone number
    private Button withdrawButton;
    private DatabaseHelper databaseHelper;
    private FirebaseAuth auth;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        balanceTextView = findViewById(R.id.balanceTextView);
        withdrawAmountEditText = findViewById(R.id.withdrawAmountEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText); // Initialize phone number EditText
        withdrawButton = findViewById(R.id.withdrawButton);

        databaseHelper = new DatabaseHelper(this);
        auth = FirebaseAuth.getInstance();

        email = auth.getCurrentUser().getEmail();
        if (email != null) {
            updateUI();
        }

        // Set a TextWatcher to change the text color based on input for phone number
        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if input is not empty and update text color
                if (!s.toString().isEmpty()) {
                    // Change text color to black when input is not empty
                    phoneNumberEditText.setTextColor(ContextCompat.getColor(WithdrawActivity.this, android.R.color.black));
                } else {
                    // Change text color to white when input is empty
                    phoneNumberEditText.setTextColor(ContextCompat.getColor(WithdrawActivity.this, android.R.color.white));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });

        withdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleWithdraw();
            }
        });
    }

    private void updateUI() {
        double balance = databaseHelper.getBalance(email);
        balanceTextView.setText("Current Balance: $" + balance);
    }

    private void handleWithdraw() {
        String amountStr = withdrawAmountEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString(); // Get the phone number

        if (!amountStr.isEmpty() && !phoneNumber.isEmpty()) { // Check if phone number is provided
            double withdrawAmount = Double.parseDouble(amountStr);
            double currentBalance = databaseHelper.getBalance(email);

            if (withdrawAmount > currentBalance) {
                Toast.makeText(this, "Insufficient Balance.", Toast.LENGTH_SHORT).show();
                return;
            }

            double newBalance = currentBalance - withdrawAmount;
            if (databaseHelper.updateBalance(email, newBalance)) {
                databaseHelper.logTransaction(email, "Withdraw", withdrawAmount); // Log withdrawal
                Toast.makeText(this, "Withdrawal Successful: $" + withdrawAmount, Toast.LENGTH_SHORT).show();
                updateUI();
                withdrawAmountEditText.setText("");
                phoneNumberEditText.setText(""); // Clear phone number
                returnToAccountActivity();
            } else {
                Toast.makeText(this, "Withdrawal Failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter an amount and phone number.", Toast.LENGTH_SHORT).show();
        }
    }

    private void returnToAccountActivity() {
        Intent intent = new Intent(this, AccountActivity.class);
        intent.putExtra("updatedBalance", databaseHelper.getBalance(email));
        startActivity(intent);
        finish();
    }
}
