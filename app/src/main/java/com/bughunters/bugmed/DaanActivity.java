package com.bughunters.bugmed;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bughunters.bugmed.Adapter.DaanUserAdapter;
import com.bughunters.bugmed.Model.DaanUser;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DaanActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView nav_view;

    private CircleImageView nav_profile_image;
    private TextView nav_fullname, nav_email, nav_bloodgroup, nav_type;

    private DatabaseReference userRef;

    private RecyclerView recyclerView;
    private ProgressBar progressbar;

    private List<DaanUser> daanUserList;
    private DaanUserAdapter userAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daan_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Blood Donation App");

        drawerLayout = findViewById(R.id.drawerLayout);
        nav_view = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(DaanActivity.this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        nav_view.setNavigationItemSelectedListener(this);

        progressbar = findViewById(R.id.progressbar);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        daanUserList = new ArrayList<>();
        userAdapter = new DaanUserAdapter(DaanActivity.this, daanUserList);

        recyclerView.setAdapter(userAdapter);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              String type = Objects.requireNonNull(snapshot.child("type").getValue()).toString();
              if (type.equals("donor")){
                  readRecipients();
              }else {
                  readDonors();
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        nav_profile_image = nav_view.getHeaderView(0).findViewById(R.id.nav_user_image);
        nav_fullname = nav_view.getHeaderView(0).findViewById(R.id.nav_user_fullname);
        nav_email = nav_view.getHeaderView(0).findViewById(R.id.nav_user_email);
        nav_bloodgroup = nav_view.getHeaderView(0).findViewById(R.id.nav_user_bloodgroup);
        nav_type = nav_view.getHeaderView(0).findViewById(R.id.nav_user_type);

        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()
        );

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.exists()){

                   String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                   nav_fullname.setText(name);

                   String email = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                   nav_email.setText(email);

                   String bloodgroup = Objects.requireNonNull(snapshot.child("bloodgroup").getValue()).toString();
                   nav_bloodgroup.setText(bloodgroup);

                   String type = Objects.requireNonNull(snapshot.child("type").getValue()).toString();
                   nav_type.setText(type);


                   if (snapshot.hasChild("profilepictureurl")){
                       String imageUrl = Objects.requireNonNull(snapshot.child("profilepictureurl").getValue()).toString();
                       Glide.with(getApplicationContext()).load(imageUrl).into(nav_profile_image);
                   }else {
                       nav_profile_image.setImageResource(R.drawable.profile_image);
                   }

                   Menu nav_menu = nav_view.getMenu();

                   if (type.equals("donor")){
                       nav_menu.findItem(R.id.sentEmail).setTitle("Received Emails");
                       nav_menu.findItem(R.id.notifications).setVisible(true);
                   }

               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void readDonors() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("users");
        Query query = reference.orderByChild("type").equalTo("donor");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                daanUserList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    DaanUser daanUser = dataSnapshot.getValue(DaanUser.class);
                    daanUserList.add(daanUser);
                }
                userAdapter.notifyDataSetChanged();
                progressbar.setVisibility(View.GONE);

                if (daanUserList.isEmpty()){
                    Toast.makeText(DaanActivity.this, "No recipients", Toast.LENGTH_SHORT).show();
                    progressbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readRecipients() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("users");
        Query query = reference.orderByChild("type").equalTo("recipient");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              daanUserList.clear();
              for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                  DaanUser daanUser = dataSnapshot.getValue(DaanUser.class);
                  daanUserList.add(daanUser);
              }
              userAdapter.notifyDataSetChanged();
              progressbar.setVisibility(View.GONE);

              if (daanUserList.isEmpty()){
                  Toast.makeText(DaanActivity.this, "No recipients", Toast.LENGTH_SHORT).show();
                  progressbar.setVisibility(View.GONE);
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.surg:
                Intent intent3 = new Intent(DaanActivity.this, com.bughunters.bugmed.DaanCategorySelectedActivity.class);
                intent3.putExtra("group", "A+");
                startActivity(intent3);
                break;

            case R.id.aller:
                Intent intent4 = new Intent(DaanActivity.this, com.bughunters.bugmed.DaanCategorySelectedActivity.class);
                intent4.putExtra("group", "A-");
                startActivity(intent4);
                break;
            case R.id.cardio:
                Intent intent5 = new Intent(DaanActivity.this, com.bughunters.bugmed.DaanCategorySelectedActivity.class);
                intent5.putExtra("group", "B+");
                startActivity(intent5);
                break;

            case R.id.derma:
                Intent intent6 = new Intent(DaanActivity.this, com.bughunters.bugmed.DaanCategorySelectedActivity.class);
                intent6.putExtra("group", "B-");
                startActivity(intent6);
                break;
            case R.id.gast:
                Intent intent7 = new Intent(DaanActivity.this, com.bughunters.bugmed.DaanCategorySelectedActivity.class);
                intent7.putExtra("group", "AB+");
                startActivity(intent7);
                break;
            case R.id.hema:
                Intent intent8 = new Intent(DaanActivity.this, com.bughunters.bugmed.DaanCategorySelectedActivity.class);
                intent8.putExtra("group", "AB-");
                startActivity(intent8);
                break;
            case R.id.neuro:
                Intent intent9 = new Intent(DaanActivity.this, com.bughunters.bugmed.DaanCategorySelectedActivity.class);
                intent9.putExtra("group", "O+");
                startActivity(intent9);
                break;
            case R.id.onco:
                Intent intent10 = new Intent(DaanActivity.this, com.bughunters.bugmed.DaanCategorySelectedActivity.class);
                intent10.putExtra("group", "O-");
                startActivity(intent10);
                break;

            case R.id.compatible:
                Intent intent11 = new Intent(DaanActivity.this, com.bughunters.bugmed.DaanCategorySelectedActivity.class);
                intent11.putExtra("group", "Compatible with me");
                startActivity(intent11);
                break;

            case R.id.notifications:
                Intent intent13 = new Intent(DaanActivity.this, DaanNotificationsActivity.class);
                startActivity(intent13);
                break;

            case R.id.sentEmail:
                Intent intent12 = new Intent(DaanActivity.this, com.bughunters.bugmed.DaanSentEmailActivity.class);
                startActivity(intent12);
                break;


            case R.id.profile:
                Intent intent = new Intent(DaanActivity.this, com.bughunters.bugmed.DaanProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent2 = new Intent(DaanActivity.this, com.bughunters.bugmed.DaanLoginActivity.class);
                startActivity(intent2);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}