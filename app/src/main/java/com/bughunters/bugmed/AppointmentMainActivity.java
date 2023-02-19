package com.bughunters.bugmed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bughunters.bugmed.Model.AppointmentUser;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
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
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppointmentMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView nav_view;

    private CircleImageView nav_profile_image;
    private TextView nav_fullname, nav_email, nav_group, nav_type;

    private DatabaseReference userRef;

    private RecyclerView recyclerView;
    private ProgressBar progressbar;

    private List<AppointmentUser> appointmentUserList;
    private AppointmentUserAdapter appointmentUserAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Doctor Finder");

        drawerLayout = findViewById(R.id.drawerLayout);
        nav_view = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(AppointmentMainActivity.this, drawerLayout,
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

        appointmentUserList = new ArrayList<>();
        appointmentUserAdapter = new AppointmentUserAdapter(AppointmentMainActivity.this, appointmentUserList);

        recyclerView.setAdapter(appointmentUserAdapter);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              String type = Objects.requireNonNull(snapshot.child("type").getValue()).toString();
              if (type.equals("doctor")){
                  readPatients();
              }else {
                  readDoctors();
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        nav_profile_image = nav_view.getHeaderView(0).findViewById(R.id.nav_user_image);
        nav_fullname = nav_view.getHeaderView(0).findViewById(R.id.nav_user_fullname);
        nav_email = nav_view.getHeaderView(0).findViewById(R.id.nav_user_email);
        nav_group = nav_view.getHeaderView(0).findViewById(R.id.nav_user_group);
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

                   String group = Objects.requireNonNull(snapshot.child("group").getValue()).toString();
                   nav_group.setText(group);

                   String type = Objects.requireNonNull(snapshot.child("type").getValue()).toString();
                   nav_type.setText(type);


                   if (snapshot.hasChild("profilepictureurl")){
                       String imageUrl = Objects.requireNonNull(snapshot.child("profilepictureurl").getValue()).toString();
                       Glide.with(getApplicationContext()).load(imageUrl).into(nav_profile_image);
                   }else {
                       nav_profile_image.setImageResource(R.drawable.profile_image);
                   }

                   Menu nav_menu = nav_view.getMenu();

                   if (type.equals("doctor")){
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

    private void readDoctors() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("users");
        Query query = reference.orderByChild("type").equalTo("doctor");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentUserList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    AppointmentUser appointmentUser = dataSnapshot.getValue(AppointmentUser.class);
                    appointmentUserList.add(appointmentUser);
                }
                appointmentUserAdapter.notifyDataSetChanged();
                progressbar.setVisibility(View.GONE);

                if (appointmentUserList.isEmpty()){
                    Toast.makeText(AppointmentMainActivity.this, "No patients", Toast.LENGTH_SHORT).show();
                    progressbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readPatients() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("users");
        Query query = reference.orderByChild("type").equalTo("patients");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              appointmentUserList.clear();
              for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                  AppointmentUser appointmentUser = dataSnapshot.getValue(AppointmentUser.class);
                  appointmentUserList.add(appointmentUser);
              }
              appointmentUserAdapter.notifyDataSetChanged();
              progressbar.setVisibility(View.GONE);

              if (appointmentUserList.isEmpty()){
                  Toast.makeText(AppointmentMainActivity.this, "No patients", Toast.LENGTH_SHORT).show();
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
                Intent intent3 = new Intent(AppointmentMainActivity.this, AppointmentCategorySelectedActivity.class);
                intent3.putExtra("group", "surgeon");
                startActivity(intent3);
                break;

            case R.id.aller:
                Intent intent4 = new Intent(AppointmentMainActivity.this, AppointmentCategorySelectedActivity.class);
                intent4.putExtra("group", "Allergists/Immunologists");
                startActivity(intent4);
                break;
            case R.id.cardio:
                Intent intent5 = new Intent(AppointmentMainActivity.this, AppointmentCategorySelectedActivity.class);
                intent5.putExtra("group", "Cardiologists");
                startActivity(intent5);
                break;

            case R.id.derma:
                Intent intent6 = new Intent(AppointmentMainActivity.this, AppointmentCategorySelectedActivity.class);
                intent6.putExtra("group", "Dermatologists");
                startActivity(intent6);
                break;
            case R.id.gast:
                Intent intent7 = new Intent(AppointmentMainActivity.this, AppointmentCategorySelectedActivity.class);
                intent7.putExtra("group", "Gastroenterologists");
                startActivity(intent7);
                break;
            case R.id.hema:
                Intent intent8 = new Intent(AppointmentMainActivity.this, AppointmentCategorySelectedActivity.class);
                intent8.putExtra("group", "Hematologists");
                startActivity(intent8);
                break;
            case R.id.neuro:
                Intent intent9 = new Intent(AppointmentMainActivity.this, AppointmentCategorySelectedActivity.class);
                intent9.putExtra("group", "Neurologists");
                startActivity(intent9);
                break;
            case R.id.onco:
                Intent intent10 = new Intent(AppointmentMainActivity.this, AppointmentCategorySelectedActivity.class);
                intent10.putExtra("group", "Oncologists");
                startActivity(intent10);
                break;

            case R.id.compatible:
                Intent intent11 = new Intent(AppointmentMainActivity.this, AppointmentCategorySelectedActivity.class);
                intent11.putExtra("group", "Best suited for me");
                startActivity(intent11);
                break;

            case R.id.notifications:
                Intent intent13 = new Intent(AppointmentMainActivity.this, AppointmentNotificationsActivity.class);
                startActivity(intent13);
                break;

            case R.id.sentEmail:
                Intent intent12 = new Intent(AppointmentMainActivity.this, AppointmentSentEmailActivity.class);
                startActivity(intent12);
                break;


            case R.id.profile:
                Intent intent = new Intent(AppointmentMainActivity.this, AppointmentProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent2 = new Intent(AppointmentMainActivity.this, AppointmentLoginActivity.class);
                startActivity(intent2);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}