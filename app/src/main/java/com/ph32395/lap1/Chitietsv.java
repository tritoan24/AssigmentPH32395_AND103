package com.ph32395.lap1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

public class Chitietsv extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chitietsv);
        Intent intent = getIntent();
        if (intent != null) {
            SinhvienModel sinhvien = (SinhvienModel) intent.getSerializableExtra("sinhvien");
            if (sinhvien != null) {
                // Hiển thị thông tin chi tiết của sinh viên trong layout
                TextView tvName = findViewById(R.id.name);
                TextView tvAge = findViewById(R.id.tuoi);
                LinearLayout imageContainer = findViewById(R.id.imageContainerif);
//
                tvName.setText(sinhvien.getName());
                tvAge.setText(String.valueOf(sinhvien.getAge()));

                // Hiển thị hình ảnh trong imageContainer (nếu có)
                Button hy = findViewById(R.id.cancle);
                hy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                imageContainer.removeAllViews();
                for (String imageUrl : sinhvien.getImage()) {
                    ImageView imageView = new ImageView(this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    imageView.setLayoutParams(layoutParams);

                    // Sử dụng Glide để tải và hiển thị hình ảnh từ URL
                    Glide.with(this)
                            .load(imageUrl)
                            .apply(new RequestOptions()
                                    .override(100, 150) // Kích thước ảnh mới (chiều rộng, chiều cao)
                                    .transform(new RoundedCorners(10)) // Bo góc ảnh (nếu cần)
                            )
                            .into(imageView);


                    // Thêm ImageView vào imageContainer
                    imageContainer.addView(imageView);
                // Thêm các dòng code tương ứng để hiển thị các thông tin khác của sinh viên
            }
        }}}
    }
