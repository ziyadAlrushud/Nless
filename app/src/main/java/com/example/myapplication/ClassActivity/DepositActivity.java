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

import com.example.myapplication.Database.DatabaseHelper;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;

public class DepositActivity extends AppCompatActivity {

    private TextView balanceTextView;
    private EditText depositAmountEditText;
    private Button depositButton;
    private DatabaseHelper databaseHelper;
    private FirebaseAuth auth;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        balanceTextView = findViewById(R.id.balanceTextView);
        depositAmountEditText = findViewById(R.id.depositAmountEditText);
        depositButton = findViewById(R.id.depositButton);

        databaseHelper = new DatabaseHelper(this);
        auth = FirebaseAuth.getInstance();

        email = auth.getCurrentUser().getEmail();
        if (email != null) {
            updateUI();
        }

        // Set a TextWatcher to change the text color based on input
        depositAmountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if input is not empty and update text color
                if (!s.toString().isEmpty()) {
                    depositAmountEditText.setTextColor(getResources().getColor(android.R.color.black));
                } else {
                    depositAmountEditText.setTextColor(getResources().getColor(android.R.color.white));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });

        depositButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDeposit();
            }
        });
    }

    private void updateUI() {
        double balance = databaseHelper.getBalance(email);
        balanceTextView.setText("Current Balance: $" + balance);
    }

    private void handleDeposit() {
        String amountStr = depositAmountEditText.getText().toString();
        if (!amountStr.isEmpty()) {
            double depositAmount = Double.parseDouble(amountStr);
            double currentBalance = databaseHelper.getBalance(email);

            double newBalance = currentBalance + depositAmount;
            if (databaseHelper.updateBalance(email, newBalance)) {
                databaseHelper.logTransaction(email, "Deposit", depositAmount); // Log deposit
                Toast.makeText(this, "Deposit Successful: $" + depositAmount, Toast.LENGTH_SHORT).show();
                updateUI();
                depositAmountEditText.setText("");
                returnToAccountActivity();
            } else {
                Toast.makeText(this, "Deposit Failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter an amount.", Toast.LENGTH_SHORT).show();
        }
    }

    private void returnToAccountActivity() {
        Intent intent = new Intent(this, AccountActivity.class);
        intent.putExtra("updatedBalance", databaseHelper.getBalance(email));
        startActivity(intent);
        finish();
    }
}
