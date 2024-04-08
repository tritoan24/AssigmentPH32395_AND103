package com.ph32395.lap1;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.concurrent.atomic.AtomicInteger;

import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.UUID;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditSinhvienActivity extends AppCompatActivity {
    private Button btnUpdate, btnCancel;
    private EditText edtName, edtTuoi, edtMssv;
    private CheckBox checkStatus;
    private LinearLayout imageContainer;
    private ImageView choiceImage;
    private SinhvienModel sinhvien;
    private ArrayList<String> imageUrls = new ArrayList<>();
    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sinhvien);

        edtName = findViewById(R.id.etName);
        edtTuoi = findViewById(R.id.etAge);
        edtMssv = findViewById(R.id.etMsv);
        checkStatus = findViewById(R.id.checkStatus);
        imageContainer = findViewById(R.id.imageContainered);
        choiceImage = findViewById(R.id.choiImageed);

        btnCancel = findViewById(R.id.btncancleed);
        btnUpdate = findViewById(R.id.btnAdded);

        // Nhận đối tượng sinh viên từ intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("sinhvien")) {
            sinhvien = (SinhvienModel) intent.getSerializableExtra("sinhvien");
            displaySinhvienInfo();
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSinhvienInfo();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        choiceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
    }






    private void displaySinhvienInfo() {
        edtName.setText(sinhvien.getName());
        edtTuoi.setText(String.valueOf(sinhvien.getAge()));
        edtMssv.setText(sinhvien.getMsv());
        checkStatus.setChecked(sinhvien.isStatus());

        checkStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkStatus.setText("Status: Còn Hàng");
                } else {
                    checkStatus.setText("Status: Hết Hàng");
                }
            }
        });

        // Hiển thị hình ảnh trong imageContainer (nếu có)
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
        }
    }

    private void updateSinhvienInfo() {
        String newName = edtName.getText().toString().trim();
        String ageStr = edtTuoi.getText().toString().trim();
        String newMssv = edtMssv.getText().toString().trim();
        boolean newStatus = checkStatus.isChecked();

        // Kiểm tra xem tất cả các trường đều đã được nhập liệu
        if (newName.isEmpty() || ageStr.isEmpty() || newMssv.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        int newAge = 0;
        try {
            newAge = Integer.parseInt(ageStr);
            if (newAge < 0 ) {
                Toast.makeText(this, "Tuổi phải nằm trong khoảng từ 0 đến 120!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập tuổi là một số nguyên!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật thông tin cho đối tượng SinhvienModel
        sinhvien.setName(newName);
        sinhvien.setAge(newAge);
        sinhvien.setMsv(newMssv);
        sinhvien.setStatus(newStatus);
        sinhvien.setImage(imageUrls);

        // Gọi phương thức cập nhật dữ liệu trên giao diện
        // Kiểm tra xem người dùng có chọn hình ảnh mới hay không
        if (imageUrls.isEmpty()) {
            // Người dùng chưa chọn hình ảnh mới, không cần cập nhật lại danh sách imageUrls
        } else {
            // Người dùng đã chọn hình ảnh mới, cập nhật danh sách imageUrls
            sinhvien.setImage(imageUrls);
        }

        // Gọi phương thức cập nhật dữ liệu trên giao diện
        displaySinhvienInfo();


        // Gọi phương thức cập nhật dữ liệu lên server
        updateSinhvienOnServer(sinhvien);
    }

    private void updateSinhvienOnServer(SinhvienModel sinhvien) {
        // Gọi API cập nhật thông tin sinh viên lên server
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<SinhvienModel> call = apiService.updateSinhvien(sinhvien.get_id(), sinhvien);
        call.enqueue(new Callback<SinhvienModel>() {
            @Override
            public void onResponse(Call<SinhvienModel> call, Response<SinhvienModel> response) {
                if (response.isSuccessful()) {
                    // Thông báo cập nhật thành công
                    Toast.makeText(EditSinhvienActivity.this, "Cập nhật sinh viên thành công", Toast.LENGTH_SHORT).show();
                    Intent intent= new Intent(EditSinhvienActivity.this, MainActivity2.class);
                    startActivity(intent);
                } else {
                    // Thông báo cập nhật thất bại
                    Toast.makeText(EditSinhvienActivity.this, "Cập nhật sinh viên thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SinhvienModel> call, Throwable t) {
                // Thông báo lỗi khi gọi API
                Toast.makeText(EditSinhvienActivity.this, "Đã xảy ra lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageContainer.removeAllViews();
            if (data.getData() != null) {
                // Trường hợp chọn một ảnh
                Uri imageUri = data.getData();
                // Hiển thị ảnh được chọn lên ImageView bằng Glide
                Glide.with(this)
                        .load(imageUri)
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(100, 0)))
                        .into(choiceImage);
                uploadImagesToFirebaseStorage(new Uri[]{imageUri});
            } else if (data.getClipData() != null) {
                // Trường hợp chọn nhiều ảnh
                ClipData clipData = data.getClipData();
                int count = data.getClipData().getItemCount();
                int selectedImages = Math.min(count, 3); // Chỉ xử lý cho 3 hình ảnh đầu tiên
                for (int i = 0; i < selectedImages; i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    // Thực hiện xử lý hiển thị ảnh tại đây
                    // Ví dụ: thêm ảnh vào LinearLayout imageContainer
                    ImageView imageView = new ImageView(this);
                    // Thay đổi kích thước của ImageView
                    int imageSize = getResources().getDimensionPixelSize(R.dimen.image_size); // Định nghĩa kích thước trong resources
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageSize, imageSize);
                    imageView.setLayoutParams(layoutParams);
                    // Tải và hiển thị ảnh với Glide
                    Glide.with(this)
                            .load(imageUri)
                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(20, 0)))
                            .into(imageView);
                    // Thêm ImageView vào LinearLayout
                    imageContainer.addView(imageView);
                }
                Uri[] imageUris = new Uri[count];
                for (int i = 0; i < count; i++) {
                    imageUris[i] = data.getClipData().getItemAt(i).getUri();
                }
                uploadImagesToFirebaseStorage(imageUris);
            }
        }
    }

    private void uploadImagesToFirebaseStorage(Uri[] imageUris) {
        int totalImages = imageUris.length;
        AtomicInteger uploadedImages = new AtomicInteger(0);

        for (int i = 0; i < totalImages; i++) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("imageSP/" + UUID.randomUUID().toString());
            storageRef.putFile(imageUris[i])
                    .addOnSuccessListener(taskSnapshot -> {
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            imageUrls.add(uri.toString());
                            Log.d("UploadImage", "Image URL: " + uri.toString());
                            // Tăng số lượng ảnh đã tải lên thành công
                            int uploadedCount = uploadedImages.incrementAndGet();
                            // Kiểm tra xem đã tải lên thành công tất cả các ảnh chưa
                            if (uploadedCount == totalImages) {
                                // Nếu đã tải lên thành công tất cả các ảnh, hiển thị Toast
                                Toast.makeText(EditSinhvienActivity.this, "Tải ảnh lên thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("UploadImage", "Error uploading image: " + e.getMessage());
                    });
        }
    }


}