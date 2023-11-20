package com.example.firebaseregistrationapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;

import android.os.Build;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TOOLTIP
//        TooltipCompat.setTooltipText(emailText, "Invalid email address");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            emailText.setTooltipText(null);
//        }
    }
}