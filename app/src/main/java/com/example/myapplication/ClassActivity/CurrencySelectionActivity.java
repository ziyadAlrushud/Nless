package com.example.myapplication.ClassActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.Database.FirebaseRealtimeDatabaseHelper;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Random;

public class CurrencySelectionActivity extends AppCompatActivity {
    private Spinner currencySpinner;
    private Button saveButton;
    private ImageButton backButton;
    private EditText cardHolderNameEditText;
    private FirebaseRealtimeDatabaseHelper firebaseDatabaseHelper;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_selection);

        currencySpinner = findViewById(R.id.currencySpinner);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.imageButton5);
        cardHolderNameEditText = findViewById(R.id.cardHolderNameEditText);

        firebaseDatabaseHelper = new FirebaseRealtimeDatabaseHelper();
        auth = FirebaseAuth.getInstance();

        // تعبئة القائمة المنسدلة بالخيارات
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(adapter);

        saveButton.setOnClickListener(v -> saveCardInformation());
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void saveCardInformation() {
        String cardNumber = generateRandomCardNumber();
        String currency = currencySpinner.getSelectedItem() != null ? currencySpinner.getSelectedItem().toString() : null;
        String cardHolderName = cardHolderNameEditText.getText().toString().trim();
        String cvv = generateRandomCVV();
        String expiryDate = generateRandomExpiryDate();

        if (currency == null) {
            Toast.makeText(this, "Please select a currency", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            String formattedEmail = formatEmailForFirebase(email);
            firebaseDatabaseHelper.addCard(formattedEmail, cardNumber, currency, cardHolderName, cvv, expiryDate);

            // تمرير البيانات إلى CardDetailsActivity
            Intent intent = new Intent(CurrencySelectionActivity.this, CardDetailsActivity.class);
            intent.putExtra("CARD_NUMBER", cardNumber);
            intent.putExtra("CURRENCY", currency);
            intent.putExtra("CARD_HOLDER_NAME", cardHolderName);
            intent.putExtra("CVV", cvv);
            intent.putExtra("EXPIRY_DATE", expiryDate);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateRandomCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder("4"); // Visa
        for (int i = 1; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }

    private String generateRandomCVV() {
        Random random = new Random();
        StringBuilder cvv = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            cvv.append(random.nextInt(10));
        }
        return cvv.toString();
    }

    private String generateRandomExpiryDate() {
        Random random = new Random();
        int month = random.nextInt(12) + 1;
        int year = random.nextInt(5) + 23;
        return String.format("%02d/%d", month, year);
    }

    private String formatEmailForFirebase(String email) {
        return email.replace(".", "_");
    }
}
