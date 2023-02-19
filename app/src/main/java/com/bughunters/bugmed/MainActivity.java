package com.bughunters.bugmed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button DaanButton;
    private Button FindButton;
    private Button AppointmentButton;
    private Button VirtualAssistantButton;
    private Button KnowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        DaanButton=findViewById(R.id.DaanButton);
        FindButton=findViewById(R.id.FindButton);
        AppointmentButton=findViewById(R.id.AppointmentButton);
        VirtualAssistantButton=findViewById(R.id.VirtualAssistantButton);
        KnowButton =findViewById(R.id.KnowButton);

        FindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MainActivity.this,
                        com.bughunters.bugmed.FinderActivity.class);
                startActivity(intent);

            }
        });

        DaanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MainActivity.this, com.bughunters.bugmed.DaanLoginActivity.class);
                startActivity(intent);

            }

        });

        AppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MainActivity.this, AppointmentLoginActivity.class);
                startActivity(intent);

            }

        });

        VirtualAssistantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MainActivity.this, VirtualAssistantActivity.class);
                startActivity(intent);

            }

        });

        KnowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MainActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });


    }
}