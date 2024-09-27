package com.example.myapplication.ClassActivity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.Database.FirebaseRealtimeDatabaseHelper;
import com.example.myapplication.R;

public class CardDetailsActivity extends AppCompatActivity {

    private TextView cardNumberTextView;
    private TextView currencyTextView;
    private TextView cardHolderNameTextView;
    private TextView cvvTextView;
    private TextView expiryDateTextView;
    private FirebaseRealtimeDatabaseHelper firebaseDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);

        cardNumberTextView = findViewById(R.id.cardNumberTextView);
        currencyTextView = findViewById(R.id.currencyTextView);
        cardHolderNameTextView = findViewById(R.id.cardHolderNameTextView);
        cvvTextView = findViewById(R.id.cvvTextView);
        expiryDateTextView = findViewById(R.id.expiryDateTextView);

        firebaseDatabaseHelper = new FirebaseRealtimeDatabaseHelper();

        // استلام بيانات البطاقة من النية (Intent)
        String cardNumber = getIntent().getStringExtra("CARD_NUMBER");
        String currency = getIntent().getStringExtra("CURRENCY");
        String cardHolderName = getIntent().getStringExtra("CARD_HOLDER_NAME");
        String cvv = getIntent().getStringExtra("CVV");
        String expiryDate = getIntent().getStringExtra("EXPIRY_DATE");

        // عرض البيانات في TextViews
        cardNumberTextView.setText(cardNumber);
        currencyTextView.setText(currency);
        cardHolderNameTextView.setText(cardHolderName);
        cvvTextView.setText(cvv);
        expiryDateTextView.setText(expiryDate);
    }
}
