package com.ph32395.lap1;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity2 extends AppCompatActivity {

    RecyclerView recyclerView;

    List<SinhvienModel> list;

    SinhvienAdapter sinhvienAdapter;
    EditText seacrchname;
    TextView tangdan, giamdan;
    private boolean isLoading = false;
    private int currentPage = 1;
    private int totalPage = 1;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        seacrchname = findViewById(R.id.edsearch);
        recyclerView = findViewById(R.id.rcClerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        // Khởi tạo tham chiếu đến nút "gio_hang" trên FirebaseDatabase
        DatabaseReference gioHangRef = FirebaseDatabase.getInstance().getReference().child("gio_hang");



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<SinhvienModel>> call = apiService.getSinhviens();

        call.enqueue(new Callback<List<SinhvienModel>>() {
            @Override
            public void onResponse(Call<List<SinhvienModel>> call, Response<List<SinhvienModel>> response) {
                if (response.isSuccessful()) {
                    list = response.body();
                    sinhvienAdapter = new SinhvienAdapter(MainActivity2.this, list);
                    recyclerView.setAdapter(sinhvienAdapter);

                    String currentPageHeader = response.headers().get("currentPage");
                    String totalPageHeader = response.headers().get("totalPage");

                    if (currentPageHeader != null && totalPageHeader != null) {
                        currentPage = Integer.parseInt(currentPageHeader);
                        totalPage = Integer.parseInt(totalPageHeader);
                    } else {
                        // Xử lý khi không nhận được giá trị currentPage hoặc totalPage từ header
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SinhvienModel>> call, Throwable t) {
                Toast.makeText(MainActivity2.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton add = findViewById(R.id.fabCustom);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity2.this,AddSV.class);
                startActivity(i);
            }
        });





        ImageView imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainActivity2.this, Profile.class);
                startActivity(in);
            }
        });
        seacrchname = findViewById(R.id.edsearch); // Kết nối EditText để người dùng nhập tên cần tìm kiếm


        seacrchname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String nameToSearch = charSequence.toString().trim(); // Lấy tên cần tìm kiếm từ EditText
                searchSinhvienByName(nameToSearch); // Gọi hàm để thực hiện tìm kiếm sinh viên
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        TextView tvSoluongSp = findViewById(R.id.tvSoluongSp);
        tvSoluongSp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity2.this, CartActivity.class);
                startActivity(i);
            }
        });

        // Đếm số lượng sản phẩm trong bảng "gio_hang"
        gioHangRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Lấy số lượng sản phẩm từ dataSnapshot
                long soLuongSp = dataSnapshot.getChildrenCount();

                // Hiển thị số lượng sản phẩm trên TextView tvSoluongSp
                TextView tvSoluongSp = findViewById(R.id.tvSoluongSp);
                tvSoluongSp.setText(String.valueOf(soLuongSp));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra trong quá trình đếm số lượng sản phẩm
                Toast.makeText(MainActivity2.this, "Lỗi khi đếm số lượng sản phẩm: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        tangdan = findViewById(R.id.tangdan);
        giamdan = findViewById(R.id.giamdan);

        tangdan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                Call<List<SinhvienModel>> call = apiService.sortSinhvien(); // Gửi yêu cầu sắp xếp sinh viên theo tên

                call.enqueue(new Callback<List<SinhvienModel>>() {
                    @Override
                    public void onResponse(Call<List<SinhvienModel>> call, Response<List<SinhvienModel>> response) {
                        if (response.isSuccessful()) {
                            list = response.body();
                            sinhvienAdapter = new SinhvienAdapter(MainActivity2.this, list);
                            recyclerView.setAdapter(sinhvienAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SinhvienModel>> call, Throwable t) {
                        // Xử lý khi gặp lỗi trong quá trình sắp xếp
                        Toast.makeText(MainActivity2.this, "Lỗi khi sắp xếp sinh viên", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        giamdan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                Call<List<SinhvienModel>> call = apiService.sortSinhvienAsc(); // Gửi yêu cầu sắp xếp sinh viên theo tên

                call.enqueue(new Callback<List<SinhvienModel>>() {
                    @Override
                    public void onResponse(Call<List<SinhvienModel>> call, Response<List<SinhvienModel>> response) {
                        if (response.isSuccessful()) {
                            list = response.body();
                            sinhvienAdapter = new SinhvienAdapter(MainActivity2.this, list);
                            recyclerView.setAdapter(sinhvienAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SinhvienModel>> call, Throwable t) {
                        // Xử lý khi gặp lỗi trong quá trình sắp xếp
                        Toast.makeText(MainActivity2.this, "Lỗi khi sắp xếp sinh viên", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



    }
    // Hàm thực hiện tìm kiếm sinh viên theo tên
    private void searchSinhvienByName(String name) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<SinhvienModel>> call = apiService.searchSinhvien(name); // Gửi yêu cầu tìm kiếm sinh viên

        call.enqueue(new Callback<List<SinhvienModel>>() {
            @Override
            public void onResponse(Call<List<SinhvienModel>> call, Response<List<SinhvienModel>> response) {
                if (response.isSuccessful()) {
                    list = response.body();
                    sinhvienAdapter = new SinhvienAdapter(MainActivity2.this, list);
                    recyclerView.setAdapter(sinhvienAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<SinhvienModel>> call, Throwable t) {
                // Xử lý khi gặp lỗi trong quá trình tìm kiếm
                Toast.makeText(MainActivity2.this, "Looxi ", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
