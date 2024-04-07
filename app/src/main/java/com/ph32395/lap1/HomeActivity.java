package com.ph32395.lap1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ph32395.lap1.databinding.ActivityHomeBinding;


public class HomeActivity extends AppCompatActivity {

    private UserAdapter userAdapter;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userAdapter = new UserAdapter(this);
        binding.recyclerView.setAdapter(userAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseReference = FirebaseDatabase.getInstance().getReference("user");
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userAdapter.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    // Kiểm tra xem id của tài khoản có khớp với id của người dùng hiện tại không
                    if (!userModel.getUid().equals(currentUserId)) {
                        userAdapter.add(userModel);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
            }
        });
    binding.animationView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FirebaseAuth.getInstance().signOut();
            Intent in = new Intent(HomeActivity.this,LoginActivity.class);
            startActivity(in);
        }
    });

    }
}


