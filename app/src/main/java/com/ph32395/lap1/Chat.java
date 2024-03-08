package com.ph32395.lap1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ph32395.lap1.databinding.ActivityChatBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class Chat extends AppCompatActivity {

    ActivityChatBinding binding;
    String receiverId;
    DatabaseReference senderReference, receiverReference;
    MessageAdapter messageAdapter;
    List<MessengerModel> messengerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Cuộn xuống dưới cùng
        binding.recyclerView.setLayoutManager(layoutManager);

        // Lấy ID của người nhận từ Intent
        receiverId = getIntent().getStringExtra("id");

        // Khởi tạo danh sách tin nhắn
        messengerList = new ArrayList<>();

        // Khởi tạo adapter và thiết lập RecyclerView
        messageAdapter = new MessageAdapter(this, messengerList);
        binding.recyclerView.setAdapter(messageAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Xây dựng đường dẫn tham chiếu cho người gửi và người nhận
        String senderId = FirebaseAuth.getInstance().getUid();
        String senderRoom = senderId + receiverId;
        String receiverRoom = receiverId + senderId;

        // Tham chiếu đến nơi lưu trữ tin nhắn của người gửi và người nhận
        senderReference = FirebaseDatabase.getInstance().getReference("chats").child(senderRoom);
        receiverReference = FirebaseDatabase.getInstance().getReference("chats").child(receiverRoom);

        senderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messengerList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessengerModel messengerModel = dataSnapshot.getValue(MessengerModel.class);
                    messengerList.add(messengerModel);
                }
                Collections.sort(messengerList, new Comparator<MessengerModel>() {
                    @Override
                    public int compare(MessengerModel o1, MessengerModel o2) {
                        return Long.compare(o1.getTimestamp(), o2.getTimestamp());
                    }
                });
                messageAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                binding.recyclerView.scrollToPosition(messengerList.size() - 1); // Cuộn xuống vị trí mới nhất
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi khi truy cập dữ liệu
            }
        });

        // Xử lý sự kiện khi nhấn nút gửi tin nhắn
        binding.txtsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.edmess.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    sendMessage(message);
                    binding.edmess.setText(""); // Xóa nội dung tin nhắn sau khi gửi
                }
            }
        });
        binding.txtback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void sendMessage(String message) {
        String messageId = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis(); // Lấy thời gian hiện tại
        MessengerModel messengerModel = new MessengerModel(messageId, FirebaseAuth.getInstance().getUid(), message,timestamp);

        // Gửi tin nhắn cho cả người gửi và người nhận
        senderReference.child(messageId).setValue(messengerModel);
        receiverReference.child(messageId).setValue(messengerModel);
    }
}
