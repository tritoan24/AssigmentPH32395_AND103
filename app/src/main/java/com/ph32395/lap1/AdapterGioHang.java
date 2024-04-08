package com.ph32395.lap1;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdapterGioHang extends ArrayAdapter<GioHangModel> {

    private Context context;
    private List<GioHangModel> gioHangList;

    public AdapterGioHang(Context context, List<GioHangModel> gioHangList) {
        super(context, R.layout.item_gio_hang, gioHangList);
        this.context = context;
        this.gioHangList = gioHangList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.item_gio_hang, null);
            holder = new ViewHolder();
            holder.tenspTextView = view.findViewById(R.id.tvName);
            holder.giatienTextView = view.findViewById(R.id.tvTuoi);
            holder.hangTextView = view.findViewById(R.id.tvMssv);
            holder.imgAvatar = view.findViewById(R.id.imgAvatar);
            holder.imgDelete = view.findViewById(R.id.imgDelete);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        GioHangModel gioHang = gioHangList.get(position);
        holder.tenspTextView.setText(gioHang.getTensp());
        Locale vietnamLocale = new Locale("vi", "VN");
        NumberFormat vietnamFormat = NumberFormat.getCurrencyInstance(vietnamLocale);
        String priceFormatted = vietnamFormat.format(gioHang.getGiatien());
        holder.giatienTextView.setText(priceFormatted);
        holder.hangTextView.setText(gioHang.getHang());
       //dung Picaso hien anh
        if (gioHang.getImage() != null) {
            Picasso.get().load(gioHang.getImage()).into(holder.imgAvatar);
        }
        //dung Glide hien anh
//        holder.quantityTextView.setText(String.valueOf(gioHang.getQuantity()));


        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị hộp thoại xác nhận
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xác nhận xóa sản phẩm");
                builder.setMessage("Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng không?");

                // Nút đồng ý
                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xóa sản phẩm khỏi danh sách giỏ hàng
                        gioHangList.remove(gioHang);

                        // Cập nhật ListView
                        notifyDataSetChanged();

                        // Xóa sản phẩm khỏi Firebase Database
                        FirebaseDatabase.getInstance().getReference().child("gio_hang").child(gioHang.getId()).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Lỗi khi xóa sản phẩm khỏi giỏ hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                // Nút hủy bỏ
                builder.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Đóng hộp thoại
                    }
                });

                // Hiển thị hộp thoại
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        return view;
    }

    static class ViewHolder {
        TextView tenspTextView;
        TextView giatienTextView;
        TextView hangTextView;
        TextView quantityTextView;
        ImageView imgAvatar,imgDelete;
    }
}

