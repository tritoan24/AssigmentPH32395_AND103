package com.ph32395.lap1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSV extends AppCompatActivity {

    private EditText etName, etAge, etMsv, etImageUrl;
    private CheckBox checkStatus;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sv);

        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etMsv = findViewById(R.id.etMsv);
        etImageUrl = findViewById(R.id.etImageUrl);
        checkStatus = findViewById(R.id.checkStatus);
        btnAdd = findViewById(R.id.btnAdd);
        Button btncancle = findViewById(R.id.btncancle);

        checkStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkStatus.setText("Status: Đã ra trường");
                } else {
                    checkStatus.setText("Status: Chưa ra trường");
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String ageStr = etAge.getText().toString().trim();
                String msv = etMsv.getText().toString().trim();
                String image = etImageUrl.getText().toString().trim();
                boolean status = checkStatus.isChecked();

                // Kiểm tra xem tuổi có phải là số nguyên hay không
                // Kiểm tra xem tất cả các trường đều đã được nhập liệu
                if (name.isEmpty() || ageStr.isEmpty() || msv.isEmpty() || image.isEmpty()) {
                    Toast.makeText(AddSV.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int age = 0;
                try {
                    age = Integer.parseInt(ageStr);
                    if (age < 0 || age > 120) {
                        Toast.makeText(AddSV.this, "Tuổi phải nằm trong khoảng từ 0 đến 120!", Toast.LENGTH_SHORT).show();
                        return;
                    }                } catch (NumberFormatException e) {
                    Toast.makeText(AddSV.this, "Vui lòng nhập tuổi là một số nguyên!", Toast.LENGTH_SHORT).show();
                    return; // Thoát khỏi phương thức nếu có lỗi
                }

                SinhvienModel sinhvien = new SinhvienModel(
                        name,
                        age,
                        msv,
                        image,
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
    }

    private void addSinhvien(SinhvienModel sinhvien) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<SinhvienModel> call = apiService.addSinhvien(sinhvien);

        call.enqueue(new Callback<SinhvienModel>() {
            @Override
            public void onResponse(Call<SinhvienModel> call, Response<SinhvienModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AddSV.this, "Thêm sinh viên thành công!", Toast.LENGTH_SHORT).show();
                    // Xử lý sau khi thêm thành công
                } else {
                    Toast.makeText(AddSV.this, "Thêm sinh viên thất bại!", Toast.LENGTH_SHORT).show();
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
}
