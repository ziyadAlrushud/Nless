package com.example.myapplication.SplashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase App
        FirebaseApp.initializeApp(this);

        // Initialize App Check
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());

        // Set your splash layout and delay before transitioning to OnboardingActivity1
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, OnboardingActivity1.class); // Start OnboardingActivity1
            startActivity(intent);
            finish(); // Close MainActivity after starting the new activity
        }, 2000); // Delay in milliseconds (2 seconds)
    }
}
