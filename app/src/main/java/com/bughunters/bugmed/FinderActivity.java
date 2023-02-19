package com.bughunters.bugmed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FinderActivity extends AppCompatActivity {
   private Button hospitalbutton,pharmacybutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finder);

        hospitalbutton=findViewById(R.id.hospitalbutton);
        pharmacybutton=findViewById(R.id.pharmacybutton);



        hospitalbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("http://maps.google.co.uk/maps?q=Hospitals&hl=en");
            }
        });

        pharmacybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("http://maps.google.co.uk/maps?q=Pharmacy&hl=en");
            }
        });


    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(s));
        intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
        startActivity(intent);
    }
}