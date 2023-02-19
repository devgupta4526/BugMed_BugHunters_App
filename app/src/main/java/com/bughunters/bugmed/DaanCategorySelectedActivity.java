package com.bughunters.bugmed;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bughunters.bugmed.Adapter.DaanUserAdapter;
import com.bughunters.bugmed.Model.DaanUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DaanCategorySelectedActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private List<DaanUser> daanUserList;
    private DaanUserAdapter userAdapter;

    private String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daan_category_selected);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        daanUserList = new ArrayList<>();
        userAdapter = new DaanUserAdapter(DaanCategorySelectedActivity.this, daanUserList);
        recyclerView.setAdapter(userAdapter);

        if (getIntent().getExtras() !=null){
            title = getIntent().getStringExtra("group");

            getSupportActionBar().setTitle("Blood group "+ title);

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
                if (type.equals("donor")){
                    result = "recipient";
                }else {
                    result = "donor";
                }

                String blooodgroup = snapshot.child("bloodgroup").getValue().toString();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child("users");
                Query query = reference.orderByChild("search").equalTo(result+blooodgroup);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        daanUserList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            DaanUser daanUser = dataSnapshot.getValue(DaanUser.class);
                            daanUserList.add(daanUser);
                        }
                        userAdapter.notifyDataSetChanged();
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
                if (type.equals("donor")){
                    result = "recipient";
                }else {
                    result = "donor";
                }

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child("users");
                Query query = reference.orderByChild("search").equalTo(result+title);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       daanUserList.clear();
                       for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                           DaanUser daanUser = dataSnapshot.getValue(DaanUser.class);
                           daanUserList.add(daanUser);
                       }
                       userAdapter.notifyDataSetChanged();
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