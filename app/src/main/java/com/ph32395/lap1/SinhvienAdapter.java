package com.ph32395.lap1;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SinhvienAdapter extends RecyclerView.Adapter<SinhvienAdapter.ViewHolder> {

    private Context context;
    private List<SinhvienModel> mList;

    // Constructor
    public SinhvienAdapter(Context context, List<SinhvienModel> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sv_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SinhvienModel sinhvien = mList.get(position);

        // Hiển thị ảnh đầu tiên từ mảng images (nếu có)
        if (sinhvien.getImage() != null && sinhvien.getImage().size() > 0) {
            Picasso.get().load(sinhvien.getImage().get(0)).into(holder.imgAvatar);
        }

        holder.tvName.setText(sinhvien.getName());
        Locale vietnamLocale = new Locale("vi", "VN");
        NumberFormat vietnamFormat = NumberFormat.getCurrencyInstance(vietnamLocale);
        String priceFormatted = vietnamFormat.format(sinhvien.getAge());
        holder.tvTuoi.setText(priceFormatted);
        holder.tvMssv.setText(sinhvien.getMsv());

        String statusText = sinhvien.getStatusText();
        holder.tvStatus.setText(statusText);

        if (sinhvien.isStatus()) {
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
        } else {
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        }

        holder.imgxoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy vị trí của mục được nhấp vào
                int position = holder.getAdapterPosition();
                showDeleteConfirmationDialog(position);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditSinhvienActivity.class);
                intent.putExtra("sinhvien", sinhvien);
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(context, Chitietsv.class);
                intent.putExtra("sinhvien", sinhvien);
                context.startActivity(intent);
                return false;
            }
        });

        holder.addToCartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SinhvienModel sinhvien = mList.get(position);

                String firstImage = "";
                if (sinhvien.getImage() != null && sinhvien.getImage().size() > 0) {
                    firstImage = sinhvien.getImage().get(0); // Lấy ảnh đầu tiên từ mảng ảnh
                }

                // Tạo một đối tượng Map chứa thông tin của sinh viên và chỉ lưu ảnh đầu tiên
                Map<String, Object> gioHangItem = new HashMap<>();
                gioHangItem.put("id", sinhvien.get_id());
                gioHangItem.put("tensp", sinhvien.getName());
                gioHangItem.put("giatien", sinhvien.getAge());
                gioHangItem.put("hang", sinhvien.getMsv());
                gioHangItem.put("image", firstImage); // Chỉ lưu ảnh đầu tiên
                gioHangItem.put("status", sinhvien.getStatusText());
                gioHangItem.put("quantity", 1);

                // Lưu thông tin sinh viên vào Realtime Database
                FirebaseDatabase.getInstance().getReference().child("gio_hang").push().setValue(gioHangItem)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Lỗi khi thêm vào giỏ hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar, imgxoa,addToCartIcon;
        TextView tvName, tvTuoi, tvMssv, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvTuoi = itemView.findViewById(R.id.tvTuoi);
            tvMssv = itemView.findViewById(R.id.tvMssv);
            tvStatus = itemView.findViewById(R.id.tvstatus);
            imgxoa = itemView.findViewById(R.id.imgDelete);
            addToCartIcon = itemView.findViewById(R.id.addToCartIcon);
        }
    }

    private void showDeleteConfirmationDialog(final int position) {
        Activity activity = (Activity) context;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa?");
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SinhvienModel sinhvien = mList.get(position); // Lấy đối tượng SinhvienModel từ danh sách
                String id = sinhvien.get_id();
                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                Call<Void> call = apiService.deleteSinhvien(id);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            mList.remove(position); // Xóa mục khỏi danh sách
                            notifyDataSetChanged(); // Cập nhật giao diện
                            Toast.makeText(activity, "Xóa sinh viên thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "Xóa sinh viên thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(activity, "Đã xảy ra lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

