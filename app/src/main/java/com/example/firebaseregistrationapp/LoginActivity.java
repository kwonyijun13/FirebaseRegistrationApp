package com.example.firebaseregistrationapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// https://firebase.google.com/docs/auth/android/start#java
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        TextInputLayout emailText = findViewById(R.id.emailTextField);
        TextInputLayout passwordText = findViewById(R.id.passwordTextField);
        MaterialButton signInButton = findViewById(R.id.loginButton);
        TextInputEditText recoverButton = findViewById(R.id.forgotPasswordButton);
        TextInputEditText registerButton = findViewById(R.id.registerButton);

        // FIREBASE SIGN IN
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signInWithEmailAndPassword(emailText.getEditText().getText().toString().trim(), passwordText.getEditText().getText().toString().trim())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (firebaseUser != null) {
                                    boolean isEmailVerified = firebaseUser.isEmailVerified();
                                    // CHECK IF EMAIL HAS BEEN VERIFIED
                                    if (isEmailVerified) {
                                        // IF LOGIN IS SUCCESSFUL
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Email not verified （￣︶￣）↗", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
            }
        });

        // SHOW PASSWORD
        passwordText.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPasswordVisible = passwordText.getEditText().getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
                if (isPasswordVisible) {
                    passwordText.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    passwordText.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        // PASSWORD RECOVERY
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            //reload();
        }
    }
}

/* onCreate() vs onStart()
onCreate():
1. called when activity is first created
2. used for initialization tasks that should only be performed ONCE
3. UI, binding of data to views and other setup operations should be done here
4. called only ONCE

onStart():
1. called when activity becomes visible to user, but not yet in foreground
2. followed by the onResume(), which is called when activity comes to the foreground and becomes interactive
3. Components that I want to start and resume, e.g. registering broadcast receivers or initiating network requests should be done here
4. called everytime the activity becomes visible
 */
