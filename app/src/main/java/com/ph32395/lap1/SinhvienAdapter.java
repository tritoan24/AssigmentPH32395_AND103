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

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SinhvienAdapter extends BaseAdapter {


    Context context;
    List<SinhvienModel> mList = new ArrayList<>();
    private SinhvienModel sinhvien;
    private LinearLayout imageContainer;
    private  ImageView choiceImage;
    private ArrayList<String> imageUrls = new ArrayList<>(); // Mảng để lưu các đường dẫn URL của ảnh





    // Constructor
    public SinhvienAdapter(Activity activity, List<SinhvienModel> mList) {
        this.context = activity;
        this.mList = mList;

    }



    @Override
    public int getCount() {
        return mList.size();
    }


    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int i) {
        return mList.size();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View rowView = inflater.inflate(R.layout.sv_list_item, parent, false);

        ImageView imgAvatar = rowView.findViewById(R.id.imgAvatar);
        TextView tvName = rowView.findViewById(R.id.tvName);
        TextView tvTuoi = rowView.findViewById(R.id.tvTuoi);
        TextView tvMssv = rowView.findViewById(R.id.tvMssv);
        TextView tvStatus = rowView.findViewById(R.id.tvstatus);
        ImageView imgxoa = rowView.findViewById(R.id.imgDelete);
        ImageView imgsua = rowView.findViewById(R.id.imgEdit);


        final SinhvienModel sinhvien = mList.get(position);
        // Hiển thị ảnh đầu tiên từ mảng images (nếu có)
        if (sinhvien.getImage() != null && sinhvien.getImage().size() > 0) {
            Picasso.get().load(sinhvien.getImage().get(0)).into(imgAvatar);
        }

        tvName.setText(sinhvien.getName());
        tvTuoi.setText(String.valueOf(sinhvien.getAge()));
        tvMssv.setText(sinhvien.getMsv());

        String statusText = sinhvien.getStatusText();
        tvStatus.setText(statusText);

        if (sinhvien.isStatus()) {
            tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
        } else {
            tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        }

        imgxoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy vị trí của mục được nhấp vào
                int position = (int) view.getTag();
                showDeleteConfirmationDialog(position);
            }
        });
        imgxoa.setTag(position);



        imgsua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditSinhvienActivity.class);
                intent.putExtra("sinhvien", sinhvien);
                context.startActivity(intent);
            }
        });


        return rowView;
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

