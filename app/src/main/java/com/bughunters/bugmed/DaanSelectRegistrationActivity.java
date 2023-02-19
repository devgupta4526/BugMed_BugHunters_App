package com.bughunters.bugmed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DaanSelectRegistrationActivity extends AppCompatActivity {

    private Button donorButton, recipientButton;
    private TextView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daan_select_registration);

        donorButton = findViewById(R.id.donorButton);
        recipientButton = findViewById(R.id.recipientButton);
        backButton = findViewById(R.id.backButton);

        donorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(
                        DaanSelectRegistrationActivity.this, com.bughunters.bugmed.DaanDonorRegistrationActivity.class);
                startActivity(intent);
            }
        });

        recipientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(
                        DaanSelectRegistrationActivity.this, DaanRecipientRegistrationActivity.class);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(
                        DaanSelectRegistrationActivity.this, DaanLoginActivity.class);
                startActivity(intent);
            }
        });
    }
}