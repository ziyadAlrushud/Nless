package com.example.myapplication.ClassActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Database.DatabaseHelper;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {

    private EditText emailSignup, passwordSignup, verifyPasswordSignup, nameSignup, phoneSignup;
    private Button signupButton;
    private ImageButton backButton; // ImageButton for back navigation
    private DatabaseHelper databaseHelper;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private static final String TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initializeViews();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        signupButton.setOnClickListener(v -> createUser());

        // Set click listener for the back button
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void initializeViews() {
        emailSignup = findViewById(R.id.emailSignup);
        passwordSignup = findViewById(R.id.passwordSignup);
        verifyPasswordSignup = findViewById(R.id.verifyPasswordSignup);
        nameSignup = findViewById(R.id.nameSignup);
        phoneSignup = findViewById(R.id.phoneSignup);
        signupButton = findViewById(R.id.signupButton);
        backButton = findViewById(R.id.imageButton4); // Initialize the back button
        databaseHelper = new DatabaseHelper(this);
    }

    private void createUser() {
        String email = emailSignup.getText().toString().trim();
        String password = passwordSignup.getText().toString().trim();
        String verifyPassword = verifyPasswordSignup.getText().toString().trim();
        String name = nameSignup.getText().toString().trim();
        String phone = phoneSignup.getText().toString().trim();

        if (validateInputs(email, password, verifyPassword, name, phone)) {
            checkEmailExistsInFirebase(email, password, name, phone);
        }
    }

    private void checkEmailExistsInFirebase(String email, String password, String name, String phone) {
        auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean emailExists = !task.getResult().getSignInMethods().isEmpty();
                        if (emailExists) {
                            showToast("This email is already registered. Please use a different email.");
                        } else {
                            registerUser(email, password, name, phone);
                        }
                    } else {
                        String errorMessage = "Failed to check email existence: " + task.getException().getMessage();
                        showToast(errorMessage);
                        Log.e(TAG, errorMessage);
                    }
                });
    }

    private boolean validateInputs(String email, String password, String verifyPassword, String name, String phone) {
        if (email.isEmpty() || password.isEmpty() || verifyPassword.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            showToast("Please fill all fields");
            return false;
        }
        if (!password.equals(verifyPassword)) {
            showToast("Passwords do not match");
            return false;
        }
        return true;
    }

    private void registerUser(String email, String password, String name, String phone) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        double initialBalance = 30000;

                        if (databaseHelper.insertOrUpdateUser(email, password, name, phone, initialBalance)) {
                            saveUserToFirestore(email, password, name, phone, initialBalance);
                        } else {
                            String errorMessage = "Failed to save user data in SQLite.";
                            showToast(errorMessage);
                            Log.e(TAG, errorMessage);

                            auth.getCurrentUser().delete().addOnCompleteListener(deleteTask -> {
                                if (deleteTask.isSuccessful()) {
                                    Log.e(TAG, "User deleted from Firebase due to SQLite failure.");
                                } else {
                                    Log.e(TAG, "Failed to delete user from Firebase: " + deleteTask.getException().getMessage());
                                }
                            });
                        }
                    } else {
                        handleRegistrationError(task.getException());
                    }
                });
    }

    private void saveUserToFirestore(String email, String password, String name, String phone, double balance) {
        DatabaseHelper.User user = new DatabaseHelper.User(email, password, name, phone, balance);

        firestore.collection("users")
                .document(email)
                .set(user)
                .addOnSuccessListener(aVoid -> showToast("User data saved to Firestore"))
                .addOnFailureListener(e -> {
                    String successMessage = "User data saved to Firestore.";
                    Log.d(TAG, successMessage); // Log the success message
                    showToast(successMessage);
                    navigateToLogin();
                });
    }

    private void navigateToLogin() {
        Intent loginIntent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish(); // Optionally close SignupActivity
    }

    private void handleRegistrationError(Exception exception) {
        if (exception instanceof FirebaseAuthUserCollisionException) {
            showToast("This email is already registered. Please use a different email.");
        } else {
            String errorMessage = exception.getMessage();
            if (errorMessage != null) {
                if (errorMessage.contains("The email address is already in use")) {
                    showToast("This email is already registered. Please use a different email.");
                } else if (errorMessage.contains("The email address is badly formatted")) {
                    showToast("The email address is badly formatted.");
                } else if (errorMessage.contains("Weak password")) {
                    showToast("The password is weak. It should be at least 6 characters long.");
                } else {
                    showToast("Registration failed: " + errorMessage);
                }
                Log.e(TAG, "Registration failed: " + errorMessage);
            } else {
                showToast("Registration failed: Unknown error");
                Log.e(TAG, "Registration failed: Unknown error");
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // Optionally close SignupActivity
    }
}
