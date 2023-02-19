package com.bughunters.bugmed.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bughunters.bugmed.Model.DaanNotification;
import com.bughunters.bugmed.Model.DaanUser;
import com.bughunters.bugmed.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DaanNotificationAdapter extends  RecyclerView.Adapter<DaanNotificationAdapter.ViewHolder>{

    private Context context;
    private List<DaanNotification> daanNotificationList;

    public DaanNotificationAdapter(Context context, List<DaanNotification> daanNotificationList) {
        this.context = context;
        this.daanNotificationList = daanNotificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.daan_notification_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final DaanNotification daanNotification = daanNotificationList.get(position);

        holder.notification_text.setText(daanNotification.getText());
        holder.notification_date.setText(daanNotification.getDate());

        getUserInfo(holder.notification_profile_image, holder.notification_name, daanNotification.getSenderId());

    }



    @Override
    public int getItemCount() {
        return daanNotificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView notification_profile_image;
        public TextView notification_name, notification_text, notification_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            notification_profile_image = itemView.findViewById(R.id.notification_profile_image);
            notification_name = itemView.findViewById(R.id.notification_name);
            notification_text = itemView.findViewById(R.id.notification_text);
            notification_date = itemView.findViewById(R.id.notification_date);

        }
    }

    private void getUserInfo(final CircleImageView circleImageView, final TextView nameTextView, final String senderId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(senderId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DaanUser daanUser = snapshot.getValue(DaanUser.class);
                nameTextView.setText(daanUser.getName());
                Glide.with(context).load(daanUser.getProfilepictureurl()).into(circleImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
