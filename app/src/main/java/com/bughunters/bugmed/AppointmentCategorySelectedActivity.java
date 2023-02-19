package com.bughunters.bugmed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.bughunters.bugmed.Model.AppointmentUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.bughunters.bugmed.Adapter.AppointmentUserAdapter;

import java.util.ArrayList;
import java.util.List;

public class AppointmentCategorySelectedActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private List<AppointmentUser> appointmentUserList;
    private AppointmentUserAdapter appointmentUserAdapter;

    private String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_category_selected);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        appointmentUserList = new ArrayList<>();
        appointmentUserAdapter = new AppointmentUserAdapter(AppointmentCategorySelectedActivity.this, appointmentUserList);
        recyclerView.setAdapter(appointmentUserAdapter);

        if (getIntent().getExtras() !=null){
            title = getIntent().getStringExtra("group");

            getSupportActionBar().setTitle("Group "+ title);

            if (title.equals("Compatible with me")){
                getCompatibleUsers();
                getSupportActionBar().setTitle("Compatible with me");
            }
            else {
                readUsers();
            }

        }
    }

    private void getCompatibleUsers() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String result;
                String type = snapshot.child("type").getValue().toString();
                if (type.equals("doctor")){
                    result = "patient";
                }else {
                    result = "doctor";
                }

                String group = snapshot.child("group").getValue().toString();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child("users");
                Query query = reference.orderByChild("search").equalTo(result+group);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        appointmentUserList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            AppointmentUser appointmentUser = dataSnapshot.getValue(AppointmentUser.class);
                            appointmentUserList.add(appointmentUser);
                        }
                        appointmentUserAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readUsers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String result;
                String type = snapshot.child("type").getValue().toString();
                if (type.equals("doctor")){
                    result = "patient";
                }else {
                    result = "doctor";
                }

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child("users");
                Query query = reference.orderByChild("search").equalTo(result+title);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       appointmentUserList.clear();
                       for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                           AppointmentUser appointmentUser = dataSnapshot.getValue(AppointmentUser.class);
                           appointmentUserList.add(appointmentUser);
                       }
                       appointmentUserAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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