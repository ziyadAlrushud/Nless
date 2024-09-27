package com.example.myapplication.ClassActivity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.Database.DatabaseHelper;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private LinearLayout transactionContainer;
    private DatabaseHelper databaseHelper;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        transactionContainer = findViewById(R.id.transactionContainer);
        databaseHelper = new DatabaseHelper(this);
        auth = FirebaseAuth.getInstance();

        // Fetch all transaction history
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        List<String> transactionHistory = databaseHelper.getTransactionHistoryForUser(userEmail);

        // Display the transaction history
        if (transactionHistory.isEmpty()) {
            TextView noTransactionTextView = new TextView(this);
            noTransactionTextView.setText("No transactions found.");
            noTransactionTextView.setTextColor(getResources().getColor(R.color.white));
            transactionContainer.addView(noTransactionTextView);
        } else {
            for (String transaction : transactionHistory) {
                addTransactionToView(transaction);
            }
        }
    }

    private void addTransactionToView(String transaction) {
        // Create TextView for the transaction
        TextView transactionTextView = new TextView(this);
        transactionTextView.setText(transaction);
        transactionTextView.setTextColor(getResources().getColor(R.color.white));
        transactionTextView.setTextSize(16);
        transactionTextView.setPadding(8, 8, 8, 8);

        // Add the transaction TextView to the container
        transactionContainer.addView(transactionTextView);

        // Create a divider line
        View divider = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
        );
        params.setMargins(0, 8, 0, 8); // Add margin around the divider
        divider.setLayoutParams(params);
        divider.setBackgroundColor(getResources().getColor(R.color.white));

        // Add the divider line below each transaction
        transactionContainer.addView(divider);
    }
}
