package com.example.myapplication.ClassActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Database.DatabaseHelper;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailLogin, passwordLogin;
    private Button loginButton;
    private TextView signupText;
    private DatabaseHelper databaseHelper;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLogin = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        loginButton = findViewById(R.id.loginButton);
        signupText = findViewById(R.id.signupText);
        databaseHelper = new DatabaseHelper(this);
        auth = FirebaseAuth.getInstance();

        // Set text color to white for both EditTexts
        emailLogin.setTextColor(Color.WHITE);
        passwordLogin.setTextColor(Color.WHITE); // Set text color for password field

        // Apply blue color to "Sign up" text
        setSignupTextColor();

        // Set up text watchers to change font style when typing
        emailLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Apply custom font and change color when user types in the email field
                if (s.length() > 0) {
                    emailLogin.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                    emailLogin.setTextColor(Color.WHITE); // Ensure color remains white
                } else {
                    emailLogin.setTypeface(Typeface.DEFAULT); // Reset to default if no text
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        passwordLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Apply custom font and change color when user types in the password field
                if (s.length() > 0) {
                    passwordLogin.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                    passwordLogin.setTextColor(Color.WHITE); // Ensure color remains white
                } else {
                    passwordLogin.setTypeface(Typeface.DEFAULT); // Reset to default if no text
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loginButton.setOnClickListener(v -> loginUser());
        signupText.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
    }

    private void loginUser() {
        String email = emailLogin.getText().toString().trim();
        String password = passwordLogin.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase authentication
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login success
                        String cardNumber = databaseHelper.getCardNumber(email); // Get card number from database
                        if (cardNumber != null) {
                            Intent intent = new Intent(LoginActivity.this, AccountActivity.class);
                            intent.putExtra("EMAIL", email);
                            intent.putExtra("CARD_NUMBER", cardNumber); // Pass card number to AccountActivity
                            startActivity(intent);
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Card number not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Login failed
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(this, "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to change color of the "Sign up" part of the signupText TextView
    private void setSignupTextColor() {
        SpannableString spannable = new SpannableString("Don't have an account? Sign up");
        ForegroundColorSpan blueSpan = new ForegroundColorSpan(Color.parseColor("#0066FF")); // Blue color
        spannable.setSpan(blueSpan, 23, 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Apply to "Sign up"
        signupText.setText(spannable);
    }
}
