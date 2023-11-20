package com.example.firebaseregistrationapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.snackbar.Snackbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private PopupWindow popupWindowEmail, popupWindowPassword, popupWindowConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        TextInputLayout emailText = findViewById(R.id.emailTextField);
        TextInputLayout passwordText = findViewById(R.id.passwordTextField);
        TextInputLayout confirmPasswordText = findViewById(R.id.confirmPasswordTextField);
        MaterialButton registerButton = findViewById(R.id.registerButton);
        TextInputEditText signInButton = findViewById(R.id.signInButton);
        ProgressBar progressSpinner = findViewById(R.id.progressSpinner);

        popupWindowEmail = new PopupWindow();
        popupWindowPassword = new PopupWindow();
        popupWindowConfirmPassword = new PopupWindow();

        progressSpinner.setVisibility(View.INVISIBLE);

        // EMAIL VALIDATION
        emailText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString().trim();
                if(isValidEmail(email)) {
                    showSnackbar(emailText, null);

                    // HIDE POPUP WINDOW
                    if (popupWindowEmail != null && popupWindowEmail.isShowing()) {
                        popupWindowEmail.dismiss();
                    }
                } else {
                    showSnackbar(emailText, "Invalid Email Address (。﹏。)");

                    // SHOW POPUP WINDOW
                    if (popupWindowEmail == null || !popupWindowEmail.isShowing()) {
                        showPopupWindow(popupWindowEmail, emailText, "Invalid email address");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = s.toString();
                if (email.isEmpty()) {
                    // HIDE POPUP WINDOW
                    if (popupWindowEmail != null && popupWindowEmail.isShowing()) {
                        popupWindowEmail.dismiss();
                    }
                }
            }
        });

        // PASSWORD VALIDATION
        passwordText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int passwordLength = s.toString().trim().length();
                if(passwordLength >= 6) {
                    showSnackbar(passwordText, null);

                    // HIDE POPUP WINDOW
                    if (popupWindowPassword != null && popupWindowPassword.isShowing()) {
                        popupWindowPassword.dismiss();
                    }
                } else {
                    showSnackbar(passwordText, "Invalid Password (≧ ﹏ ≦)");

                    // SHOW POPUP WINDOW
                    if (popupWindowPassword == null || !popupWindowPassword.isShowing()) {
                        showPopupWindow(popupWindowPassword, passwordText, "Password requires at least 6 characters");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString();
                if (password.isEmpty()) {
                    // HIDE POPUP WINDOW
                    if (popupWindowPassword != null && popupWindowPassword.isShowing()) {
                        popupWindowPassword.dismiss();
                    }
                }
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

        // CONFIRM PASSWORD VALIDATION
        confirmPasswordText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String confirmPassword = s.toString().trim();
                if (!confirmPassword.equals(passwordText.getEditText().getText().toString().trim())) {
                    // SHOW POPUP WINDOW
                    if (popupWindowConfirmPassword == null || !popupWindowConfirmPassword.isShowing()) {
                        showPopupWindow(popupWindowConfirmPassword, confirmPasswordText, "Passwords do not match");
                    }
                } else {
                    // HIDE POPUP WINDOW
                    if (popupWindowConfirmPassword != null && popupWindowConfirmPassword.isShowing()) {
                        popupWindowConfirmPassword.dismiss();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String confirmPassword = s.toString().trim();
                if (confirmPassword == passwordText.getEditText().getText().toString().trim() || confirmPassword.isEmpty()) {
                    // HIDE POPUP WINDOW
                    if (popupWindowConfirmPassword != null && popupWindowConfirmPassword.isShowing()) {
                        popupWindowConfirmPassword.dismiss();
                    }
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PROGRESSBAR
                progressSpinner.setVisibility(View.VISIBLE);

                // REMOVE WHITE SPACES AT BOTH ENDS
                String email = emailText.getEditText().getText().toString().trim();
                String password = passwordText.getEditText().getText().toString().trim();
                String confirmPassword = confirmPasswordText.getEditText().getText().toString().trim();

                // CHECK FOR EMPTY FIELDS
                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    progressSpinner.setVisibility(View.INVISIBLE);
                    Toast.makeText(RegisterActivity.this, "Please fill in all the fields (╬▔皿▔)╯", Toast.LENGTH_LONG).show();
                    return;
                }

                // CHECK FOR SAME PASSWORDS
                if (!password.equals(confirmPassword)) {
                    progressSpinner.setVisibility(View.INVISIBLE);
                    Toast.makeText(RegisterActivity.this, "Passwords do not match (╬▔皿▔)╯", Toast.LENGTH_LONG).show();
                    return;
                }

                // CREATE NEW USER
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressSpinner.setVisibility(View.INVISIBLE);
                                    sendEmailVerification();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                } else {
                                    progressSpinner.setVisibility(View.INVISIBLE);
                                    Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private boolean isValidEmail(String email) {
        // EMAIL PATTERN USING REGULAR EXPRESSIONS (REGEX)
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        // CHECK IF PATTERN AND EMAIL INPUT MATCHES AND RETURNS A BOOL
        return email.matches(emailPattern);
    }

    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    private void showPopupWindow(PopupWindow popupWindow, View anchorView, String errorMessage) {
        // Inflate the custom layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        // Create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = false; // Allows taps outside the popup to dismiss it
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        popupWindow.setFocusable(focusable);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Set transparent background

        // Find views inside the popup layout
        TextView popupTextView = popupView.findViewById(R.id.popupTextView);
        popupTextView.setText(errorMessage);

        // Calculate the desired x and y coordinates for the popup window
        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);
        int x = location[0] + anchorView.getWidth(); // Right edge of anchor view
        int y = location[1] - popupWindow.getHeight(); // Top edge of anchor view minus popup window height

        // SET THE POPUP TOP RIGHT
        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y);
    }

    private void sendEmailVerification() {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
/* MISTAKES / REMINDERS
When checking if strings equals each other, use .equals() and not "=="
 */

/* CUSTOM POPUP
Also includes popup_layout.xml and popup_background.xml
Many imports above (import android.widget.PopupWindow;)

popupWindowEmail = new PopupWindow(); ...
- creating a new PopupWindow() for each validation instead of creating a new one each time in the showPopupWindow()
 */

/* SHOW PASSWORD EXPLANATION
COMBINE 2 INPUT TYPE FLAGS:
TYPE_CLASS_TEXT: input type is plain text, can enter any character, TYPE_TEXT_VARIATION_PASSWORD: ~ password
By combining these two flags using the bitwise OR operator (|),
we set the input type of the EditText to accept plain text while visually treating it as a password field.
This allows us to toggle between hiding and showing the entered password characters.
ADDITIONAL CODE: Move the cursor to the end of the text
    passwordEditText.setSelection(passwordEditText.getText().length());
 */

/* ADDITIONAL OPTIONS
Register with 3rd party (Facebook, Google etc...)
 */

/* FACEBOOK LOGIN
https://developers.facebook.com/docs/facebook-login/android


 */

/* GMAIL API (NOT USED)
https://console.cloud.google.com/
https://developers.google.com/gmail/api/guides/sending

ENABLE GMAIL API
1. Go to the API Library (APIs & Services -> Library) and search for "Gmail API".
2. Select the Gmail API from the results.
3. Click the "Enable" button.

Set up OAuth 2.0 credentials
1. In the Google Cloud Console, go to APIs & Services -> Credentials.
2. Click the "Create Credentials" button and select "OAuth client ID".
3. Configure the OAuth consent screen with the necessary details.
4. Select "Android" as the application type.
5. Provide the package name of your Android application.
6. Enter the SHA-1 fingerprint of your signing key.
7. Click the "Create" button.
8. Once the credentials are created, download the JSON file containing the client ID and client secret.

Obtain SHA-1 Fingerprint (Secure Hash Algorithm 1)
1. Open 'Gradle' on the right
2. Click the first button under the word 'Gradle'
3. Search 'signingReport' and hit enter
4. Now it appears in console
5. Remember to remove after

Add in build.gradle
implementation 'com.google.api-client:google-api-client-android:1.31.5'
implementation 'com.google.apis:google-api-services-gmail:v1-rev20210630-1.31.5'
 */