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

import com.bughunters.bugmed.Email.DaanJavaMailApi;
import com.bughunters.bugmed.Model.DaanUser;
import com.bughunters.bugmed.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DaanUserAdapter extends RecyclerView.Adapter<DaanUserAdapter.ViewHolder>{

    private Context context;
    private List<DaanUser> daanUserList;

    public DaanUserAdapter(Context context, List<DaanUser> daanUserList) {
        this.context = context;
        this.daanUserList = daanUserList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.daan_user_displayed_layout, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         final DaanUser daanUser = daanUserList.get(position);

         holder.type.setText(daanUser.getType());

         if (daanUser.getType().equals("donor")){
            holder.emailNow.setVisibility(View.VISIBLE);
         }

         holder.userEmail.setText(daanUser.getEmail());
         holder.phoneNumber.setText(daanUser.getPhonenumber());
         holder.userName.setText(daanUser.getName());
         holder.bloodGroup.setText(daanUser.getBloodgroup());

        Glide.with(context).load(daanUser.getProfilepictureurl()).into(holder.userProfileImage);

        final String nameOfTheReceiver = daanUser.getName();
        final String idOfTheReceiver = daanUser.getId();

        //sending the email

        holder.emailNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("SEND EMAIL")
                        .setMessage("Send email to " + daanUser.getName() + "?")
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
                                        String  blood = snapshot.child("bloodgroup").getValue().toString();

                                        String mEmail = daanUser.getEmail();
                                        String mSubject = "BLOOD DONATION";
                                        String mMessage = "Hello "+ nameOfTheReceiver+", "+nameOfSender+
                                                " would like blood donation from you. Here's his/her details:\n"
                                                +"Name: "+nameOfSender+ "\n"+
                                                "Phone Number: "+phone+ "\n"+
                                                "Email: " +email+"\n"+
                                                "Blood Group: "+blood+ "\n"+
                                                "Kindly Reach out to him/her. Thank you!\n"
                                                +"BLOOD DONATION APP - DONATE BLOOD, SAVE LIVES!";

                                        DaanJavaMailApi daanJavaMailApi = new DaanJavaMailApi(context, mEmail, mSubject, mMessage);
                                        daanJavaMailApi.execute();

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
        return daanUserList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView userProfileImage;
        public TextView type, userName, userEmail, phoneNumber, bloodGroup;
        public Button emailNow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userProfileImage  = itemView.findViewById(R.id.userProfileImage);
            type = itemView.findViewById(R.id.type);
            userName   = itemView.findViewById(R.id.userName);
            userEmail = itemView.findViewById(R.id.userEmail);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
            bloodGroup = itemView.findViewById(R.id.bloodGroup);
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
