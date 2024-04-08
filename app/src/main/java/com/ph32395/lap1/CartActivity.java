package com.ph32395.lap1;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {
    private List<GioHangModel> gioHangList;
    private AdapterGioHang adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        gioHangList = new ArrayList<>();
        adapter = new AdapterGioHang(this, gioHangList);

        ListView listView = findViewById(R.id.lvCart);
        listView.setAdapter(adapter);
        loadGioHangFromFirebase();



    }

    private void loadGioHangFromFirebase() {
        FirebaseDatabase.getInstance().getReference().child("gio_hang")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        gioHangList.clear();
                        double total = 0; // Khởi tạo tổng tiền

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            GioHangModel gioHang = snapshot.getValue(GioHangModel.class);
                            gioHangList.add(gioHang);

                            // Tính tổng tiền từ giá tiền của các sản phẩm trong giỏ hàng
                            total += gioHang.getGiatien() * gioHang.getQuantity();
                        }
                        adapter.notifyDataSetChanged();

                        // Hiển thị tổng tiền lên giao diện
                        TextView tvTotal = findViewById(R.id.tvTotal);
                        Locale vietnamLocale = new Locale("vi", "VN");
                        NumberFormat vietnamFormat = NumberFormat.getCurrencyInstance(vietnamLocale);
                        String priceFormatted = vietnamFormat.format(total);
                        tvTotal.setText(priceFormatted);
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
                    }
                });
    }
}