package com.ph32395.lap1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class SignupActivity extends AppCompatActivity {
    private TextInputEditText edemail, edpassword, edrppassword, fullname;
    private ImageView imgSigup;
    private Button btnsignup;
    private TextView txtLogin;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 3;
    private Uri selectedImageUri;
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edemail = findViewById(R.id.edemail);
        edpassword = findViewById(R.id.edpassword);
        edrppassword = findViewById(R.id.edrppassword);
        btnsignup = findViewById(R.id.btnsignup);
        txtLogin = findViewById(R.id.txtLogin);
        imgSigup = findViewById(R.id.imgsignup);
        fullname = findViewById(R.id.edhoTen);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("user");

        imgSigup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageOptions();
            }
        });

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = fullname.getText().toString();
                String email = edemail.getText().toString();
                String password = edpassword.getText().toString();
                String rppassword = edrppassword.getText().toString();
                if (email.equals("") || password.equals("") || rppassword.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Vui lòng nhập đầy đủ!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(rppassword)) {
                    Toast.makeText(SignupActivity.this, "Mật khẩu không khớp nhau!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isValidEmail(email)) {
                    Toast.makeText(SignupActivity.this, "Địa chỉ email không hợp lệ!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6 || !Character.isUpperCase(password.charAt(0))) {
                    Toast.makeText(SignupActivity.this, "Mật khẩu phải có ít nhất 6 kí tự và viết hoa chữ cái đầu tiên!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        Intent in = new Intent(SignupActivity.this,LoginActivity.class);
                                        in.putExtra("email",email);
                                        in.putExtra("password",password);
                                        startActivity(in);
                                        Toast.makeText(SignupActivity.this, "Đăng Kí Thành Công!",
                                                Toast.LENGTH_SHORT).show();

                                        String userId = user.getUid(); // Lấy userId
                                        // Tải ảnh lên Firebase Storage và truyền userId, name và email
                                        uploadImageToFirebaseStorage(selectedImageUri, userId, name, email);
                                    }
                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignupActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });



        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(in);
            }
        });
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // Hiển thị ảnh trên ImageView bằng Glide và bo tròn
            Glide.with(this)
                    .load(imageUri)
                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(100, 0)))
                    .into(imgSigup);
            selectedImageUri = data.getData();


        }


        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Tạo Uri tạm thời cho bitmap
            Uri tempUri = getImageUri(imageBitmap);

            // Hiển thị ảnh trên ImageView và tải lên Firebase Storage
            imgSigup.setImageBitmap(imageBitmap);
            selectedImageUri = tempUri;
        }
    }

    private void showImageOptions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            // Tiếp tục với việc hiển thị hộp thoại chọn ảnh từ camera hoặc bộ nhớ
            showDialogForImageSelection();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền truy cập vào camera đã được cấp, tiếp tục với việc hiển thị hộp thoại chọn ảnh từ camera hoặc bộ nhớ
                showDialogForImageSelection();
            } else {
                Toast.makeText(this, "Quyền truy cập vào camera bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDialogForImageSelection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn ảnh từ");
        builder.setItems(new CharSequence[]{"Bộ nhớ", "Camera"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        chooseImageFromGallery();
                        break;
                    case 1:
                        takePhotoFromCamera();
                        break;
                }
            }
        });
        builder.show();
    }

    private void chooseImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
    }

    private void takePhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    private Uri getImageUri(Bitmap bitmap) {
        // Tạo Uri tạm thời cho bitmap
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "TempImage", null);
        return Uri.parse(path);
    }


    private void uploadImageToFirebaseStorage(Uri imageUri, String userId, String name, String email) {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imagesRef = storageRef.child("images/" + UUID.randomUUID().toString());

            imagesRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(SignupActivity.this, "tải ảnh thành công", Toast.LENGTH_SHORT).show();
                            imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();

                                    // Tạo một thể hiện của lớp UserProfile với thông tin người dùng và URL của ảnh
                                    UserProfile userProfile = new UserProfile(name, email, imageUrl);

                                    // Lưu thông tin vào Realtime Database
                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("TaiKhoan").child(userId);
                                    userRef.setValue(userProfile)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Xử lý khi lưu thành công, ví dụ hiển thị thông báo hoặc chuyển sang màn hình khác
                                                    Toast.makeText(SignupActivity.this, "Đăng ký thành công và lưu hình ảnh", Toast.LENGTH_SHORT).show();
                                                    Log.e("thanh cong", "onSuccess: oke");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Xử lý khi lưu thất bại
                                                    Toast.makeText(SignupActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Xử lý khi có lỗi xảy ra trong quá trình tải lên ảnh
                            Toast.makeText(SignupActivity.this, "Lỗi: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}
