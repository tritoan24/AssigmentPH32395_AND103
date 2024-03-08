package com.ph32395.lap1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private Context context;
    private List<MessengerModel> messengerModelsList;


    public MessageAdapter(Context context, List<MessengerModel> messengerList) {
        this.context = context;
        this.messengerModelsList = messengerList;
    }

    public void add(MessengerModel userModel) {
        messengerModelsList.add(userModel);
        notifyDataSetChanged();
    }

    public void clear() {
        messengerModelsList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messagerow, parent, false);
        return new MyViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MessengerModel message = messengerModelsList.get(position);
        holder.msg.setText(message.getMessage());
        // Định dạng ngày giờ từ milliseconds thành định dạng giờ:phút
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String ngayGio = sdf.format(new Date(message.getTimestamp()));
        holder.ngay.setText(ngayGio);

        // Kiểm tra xem người gửi có phải là người đang đăng nhập hay không
        if (message.getSenderId().equals(FirebaseAuth.getInstance().getUid())) {
            // Tin nhắn từ người đang đăng nhập, sử dụng giao diện bên phải
            holder.main.setBackgroundResource(R.drawable.message_right); // Sử dụng hình nền bên phải
        } else {
            // Tin nhắn từ người khác, sử dụng giao diện bên trái
            holder.main.setBackgroundResource(R.drawable.message_left); // Sử dụng hình nền bên trái
        }

    }

    @Override
    public int getItemCount() {
        return messengerModelsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView msg,ngay;
        private LinearLayout main;

        public MyViewHolder(Context context, @NonNull View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.txtmessage);
            main = itemView.findViewById(R.id.mainmessageLayout);
            ngay = itemView.findViewById(R.id.txtngay);
        }
    }
}

