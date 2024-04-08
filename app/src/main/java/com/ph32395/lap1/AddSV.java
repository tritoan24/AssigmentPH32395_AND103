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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSV extends AppCompatActivity {

    private EditText etName, etAge, etMsv ;
    private CheckBox checkStatus;
    private Button btnAdd;
    private LinearLayout imageContainer;
    private  ImageView choiceImage;
    private ArrayList<String> imageUrls = new ArrayList<>(); // Mảng để lưu các đường dẫn URL của ảnh
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sv);


        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etMsv = findViewById(R.id.etMsv);
        checkStatus = findViewById(R.id.checkStatus);
        btnAdd = findViewById(R.id.btnAdd);
        imageContainer = findViewById(R.id.imageContainer);
        choiceImage = findViewById(R.id.choiImage);



        Button btncancle = findViewById(R.id.btncancle);

        checkStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkStatus.setText("Status: Hết Hàng");
                } else {
                    checkStatus.setText("Status: Còn Hàng");
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String ageStr = etAge.getText().toString().trim();
                String msv = etMsv.getText().toString().trim();
                boolean status = checkStatus.isChecked();

                // Kiểm tra xem tuổi có phải là số nguyên hay không
                // Kiểm tra xem tất cả các trường đều đã được nhập liệu
                if (name.isEmpty() || ageStr.isEmpty() || msv.isEmpty() ){
                    Toast.makeText(AddSV.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int age = 0;
                try {
                    age = Integer.parseInt(ageStr);
                    if (age < 0) {
                        Toast.makeText(AddSV.this, "Tiền Phải Lớn hơn 0", Toast.LENGTH_SHORT).show();
                        return;
                    }                } catch (NumberFormatException e) {
                    Toast.makeText(AddSV.this, "Vui lòng nhập tuổi là một số nguyên!", Toast.LENGTH_SHORT).show();
                    return; // Thoát khỏi phương thức nếu có lỗi
                }

                SinhvienModel sinhvien = new SinhvienModel(
                        name,
                        age,
                        msv,
                        imageUrls, // Sử dụng mảng imageUrls chứa các URL của ảnh
                        status);
                addSinhvien(sinhvien);

                Intent i = new Intent(AddSV.this,MainActivity2.class);
                startActivity(i);
            }
        });


        btncancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    private void addSinhvien(SinhvienModel sinhvien) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<SinhvienModel> call = apiService.addSinhvien(sinhvien);

        call.enqueue(new Callback<SinhvienModel>() {
            @Override
            public void onResponse(Call<SinhvienModel> call, Response<SinhvienModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AddSV.this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    // Xử lý sau khi thêm thành công
                } else {
                    Toast.makeText(AddSV.this, "Thêm sản phẩm thất bại!", Toast.LENGTH_SHORT).show();
                    // Xử lý khi thêm thất bại
                    Log.e("AddSV", "Lỗi khi thêm sinh viên: ");
                }
            }

            @Override
            public void onFailure(Call<SinhvienModel> call, Throwable t) {
                Log.e("AddSV", "Lỗi khi thêm sinh viên: " + t.getMessage());
                Toast.makeText(AddSV.this, "Lỗi khi thêm sinh viên, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
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
            // Tạo một đường dẫn mới trong Storage cho mỗi ảnh
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("imageSP/" + UUID.randomUUID().toString());
            storageRef.putFile(imageUris[i])
                    .addOnSuccessListener(taskSnapshot -> {
                        // Nếu tải lên thành công, lấy URL của ảnh
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            imageUrls.add(uri.toString());
                            Log.d("UploadImage", "Image URL: " + uri.toString());
                            // Tăng số lượng ảnh đã tải lên thành công
                            int uploadedCount = uploadedImages.incrementAndGet();
                            // Kiểm tra xem đã tải lên thành công tất cả các ảnh chưa
                            if (uploadedCount == totalImages) {
                                // Nếu đã tải lên thành công tất cả các ảnh, hiển thị Toast
                                Toast.makeText(this, "Tải ảnh lên thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Xử lý khi có lỗi xảy ra trong quá trình tải lên
                        Log.e("UploadImage", "Error uploading image: " + e.getMessage());
                    });
        }
    }


}
