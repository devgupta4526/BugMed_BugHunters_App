package com.bughunters.bugmed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.bughunters.bugmed.Adapter.AppointmentNotificationAdapter;
import com.bughunters.bugmed.Model.AppointmentNotification;

import java.util.ArrayList;
import java.util.List;

public class AppointmentNotificationsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    List<AppointmentNotification> appointmentNotificationList;
    AppointmentNotificationAdapter appointmentNotificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_notifications);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        appointmentNotificationList = new ArrayList<>();
        appointmentNotificationAdapter = new AppointmentNotificationAdapter(AppointmentNotificationsActivity.this, appointmentNotificationList);
        recyclerView.setAdapter(appointmentNotificationAdapter);

        readNotifications();
    }

    private void readNotifications() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              appointmentNotificationList.clear();
              for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                  AppointmentNotification appointmentNotification = dataSnapshot.getValue(AppointmentNotification.class);
                  appointmentNotificationList.add(appointmentNotification);
              }
              appointmentNotificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}