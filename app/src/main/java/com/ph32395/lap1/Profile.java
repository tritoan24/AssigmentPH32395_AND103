package com.ph32395.lap1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class Profile extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView txtUserName;
    private TextView txtUserEmail;
    private Button btnLogout;
    private ImageView imgAvatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        txtUserName = findViewById(R.id.tvName);
        txtUserEmail = findViewById(R.id.tvEmail);
        btnLogout = findViewById(R.id.btnLogout);
        imgAvatar = findViewById(R.id.imgAvatar);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(Profile.this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Profile.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Kiểm tra xem người dùng đã đăng nhập chưa
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();


            // Thực hiện truy vấn bảng "TaiKhoan" trong Realtime Database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("TaiKhoan").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Bản ghi với ID của người dùng đã được tìm thấy
                        UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                        if (userProfile != null) {
                            // Lấy thông tin tên và ảnh từ bản ghi
                            String userName = userProfile.getName();
                            String imageUrl = userProfile.getImageUrl();


                            // Hiển thị thông tin tên và ảnh lên giao diện
                            txtUserName.setText("Tên: " + userName);
                            txtUserEmail.setText("Email: " + currentUser.getEmail());

                            // Load ảnh từ URL và hiển thị nó lên ImageView
                            Glide.with(Profile.this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.image1) // Ảnh placeholder trong khi tải ảnh thực tế
                                    .error(R.drawable.image) // Ảnh hiển thị khi có lỗi xảy ra
                                    .apply(RequestOptions.circleCropTransform()) // Bo tròn ảnh
                                    .into(imgAvatar);
                        }
                    } else {
                        // Không tìm thấy bản ghi với ID của người dùng
                        Toast.makeText(Profile.this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý khi có lỗi xảy ra trong quá trình truy vấn
                    Toast.makeText(Profile.this, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            // Người dùng chưa đăng nhập, thực hiện các hành động khác (nếu cần)
            // Ví dụ: Chuyển hướng người dùng đến màn hình đăng nhập hoặc hiển thị thông báo
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            // Chuyển hướng người dùng đến màn hình đăng nhập
            Intent intent = new Intent(Profile.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Đóng màn hình Profile để người dùng không thể quay lại bằng nút Back
        }
    }
}