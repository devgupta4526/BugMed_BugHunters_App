package com.bughunters.bugmed.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bughunters.bugmed.Model.AppointmentUser;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.bughunters.bugmed.Email.AppointmentJavaMailApi;
import com.bughunters.bugmed.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppointmentUserAdapter extends RecyclerView.Adapter<AppointmentUserAdapter.ViewHolder>{

    private Context context;
    private List<AppointmentUser> appointmentUserList;

    public AppointmentUserAdapter(Context context, List<AppointmentUser> appointmentUserList) {
        this.context = context;
        this.appointmentUserList = appointmentUserList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.appointment_user_displayed_layout, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         final AppointmentUser appointmentUser = appointmentUserList.get(position);

         holder.type.setText(appointmentUser.getType());

         if (appointmentUser.getType().equals("doctor")){
            holder.emailNow.setVisibility(View.VISIBLE);
         }

         holder.userEmail.setText(appointmentUser.getEmail());
         holder.phoneNumber.setText(appointmentUser.getPhonenumber());
         holder.userName.setText(appointmentUser.getName());
         holder.Group.setText(appointmentUser.getBloodgroup());

        Glide.with(context).load(appointmentUser.getProfilepictureurl()).into(holder.userProfileImage);

        final String nameOfTheReceiver = appointmentUser.getName();
        final String idOfTheReceiver = appointmentUser.getId();

        //sending the email

        holder.emailNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("SEND EMAIL")
                        .setMessage("Send email to " + appointmentUser.getName() + "?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String nameOfSender = snapshot.child("name").getValue().toString();
                                        String email = snapshot.child("email").getValue().toString();
                                        String phone = snapshot.child("phonenumber").getValue().toString();
                                        String  group = snapshot.child("group").getValue().toString();

                                        String mEmail = appointmentUser.getEmail();
                                        String mSubject = "Need Help";
                                        String mMessage = "Hello "+ nameOfTheReceiver+", "+nameOfSender+
                                                " would like to get consutance from you. Here's his/her details:\n"
                                                +"Name: "+nameOfSender+ "\n"+
                                                "Phone Number: "+phone+ "\n"+
                                                "Email: " +email+"\n"+
                                                " Group: "+group+ "\n"+
                                                "Kindly Reach out to him/her. Thank you!\n"
                                                +" SAVE LIVES!";

                                        AppointmentJavaMailApi appointmentJavaMailApi = new AppointmentJavaMailApi(context, mEmail, mSubject, mMessage);
                                        appointmentJavaMailApi.execute();

                                        DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference("emails")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        senderRef.child(idOfTheReceiver).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful()){
                                                   DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference("emails")
                                                           .child(idOfTheReceiver);
                                                   receiverRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

                                                   addNotifications(idOfTheReceiver, FirebaseAuth.getInstance().getCurrentUser().getUid());
                                               }
                                            }
                                        });


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return appointmentUserList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView userProfileImage;
        public TextView type, userName, userEmail, phoneNumber, Group;
        public Button emailNow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userProfileImage  = itemView.findViewById(R.id.userProfileImage);
            type = itemView.findViewById(R.id.type);
            userName   = itemView.findViewById(R.id.userName);
            userEmail = itemView.findViewById(R.id.userEmail);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
            Group = itemView.findViewById(R.id.Group);
            emailNow = itemView.findViewById(R.id.emailNow);

        }
    }

    private void addNotifications(String receiverId, String senderId){
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("notifications").child(receiverId);
        String date = DateFormat.getDateInstance().format(new Date());
        HashMap<String, Object>  hashMap = new HashMap<>();
        hashMap.put("receiverId", receiverId);
        hashMap.put("senderId", senderId);
        hashMap.put("text", "Sent you an email, kindly check it out!");
        hashMap.put("date", date);

        reference.push().setValue(hashMap);
    }
}
