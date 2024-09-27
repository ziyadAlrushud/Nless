package com.example.myapplication.SplashScreen;

import android.content.Intent; // Import Intent
import android.os.Bundle;
import android.view.View; // Import View
import android.widget.ImageButton; // Import ImageButton

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.ClassActivity.LoginActivity;
import com.example.myapplication.R;

public class OnboardingActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding3);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize ImageButton and set click listener
        ImageButton buttonNext = findViewById(R.id.imageButton3); // Ensure this ID matches your layout
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the next activity
                Intent intent = new Intent(OnboardingActivity3.this, LoginActivity.class); // Replace NextActivity with your target activity
                startActivity(intent);
            }
        });
    }
}
