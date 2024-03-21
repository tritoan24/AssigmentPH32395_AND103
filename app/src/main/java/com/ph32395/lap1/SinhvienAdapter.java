package com.ph32395.lap1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SinhvienAdapter extends BaseAdapter {


    Context context;
    List<SinhvienModel> mList = new ArrayList<>();


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

        Picasso.get().load(sinhvien.getImage()).into(imgAvatar);
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
                showDialogSua(position);
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
                            Toast.makeText(context, "Xóa sinh viên thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Xóa sinh viên thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Đã xảy ra lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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




    //dialog sửa
    private void showDialogSua(final int position) {
        // Lấy đối tượng SinhvienModel từ danh sách
        final SinhvienModel sinhvien = mList.get(position);

        // Tạo một AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_sua, null); // layout của dialog sửa

        // Khởi tạo các trường nhập liệu trong dialog
        EditText edtName = view.findViewById(R.id.etName);
        EditText edtTuoi = view.findViewById(R.id.etAge);
        EditText edtMssv = view.findViewById(R.id.etMsv);
        EditText edtImageUrl = view.findViewById(R.id.etImageUrl);
        CheckBox checkStatus = view.findViewById(R.id.checkStatus);

        // Gán giá trị hiện tại của SinhvienModel cho các trường nhập liệu
        edtName.setText(sinhvien.getName());
        edtTuoi.setText(String.valueOf(sinhvien.getAge()));
        edtMssv.setText(sinhvien.getMsv());
        edtImageUrl.setText(sinhvien.getImage());
        checkStatus.setChecked(sinhvien.isStatus());

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



        // Thiết lập nút Lưu trong dialog
        builder.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = edtName.getText().toString().trim();
                String ageStr = edtTuoi.getText().toString().trim();
                String newMssv = edtMssv.getText().toString().trim();
                String newImageUrl = edtImageUrl.getText().toString().trim();
                boolean newStatus = checkStatus.isChecked();

                // Kiểm tra xem tất cả các trường đều đã được nhập liệu
                if (newName.isEmpty() || ageStr.isEmpty() || newMssv.isEmpty() || newImageUrl.isEmpty()) {
                    Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int newAge = 0;
                try {
                    newAge = Integer.parseInt(ageStr);
                    if (newAge < 0 || newAge > 120) {
                        Toast.makeText(context, "Tuổi phải nằm trong khoảng từ 0 đến 120!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(context, "Vui lòng nhập tuổi là một số nguyên!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Cập nhật thông tin cho đối tượng SinhvienModel
                sinhvien.setName(newName);
                sinhvien.setAge(newAge);
                sinhvien.setMsv(newMssv);
                sinhvien.setImage(newImageUrl);
                sinhvien.setStatus(newStatus);
                // Gọi phương thức cập nhật dữ liệu trên giao diện
                notifyDataSetChanged();

                // Gọi phương thức cập nhật dữ liệu lên server
                updateSinhvienOnServer(sinhvien);

                // Dismiss dialog
                dialog.dismiss();
            }
        });

        // Thiết lập nút Hủy trong dialog
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Thiết lập layout cho AlertDialog
        builder.setView(view);

        // Tạo AlertDialog từ AlertDialog.Builder
        AlertDialog dialog = builder.create();

        // Hiển thị AlertDialog
        dialog.show();
    }
    // Phương thức cập nhật dữ liệu lên server
    private void updateSinhvienOnServer(SinhvienModel sinhvien) {
        // Gọi API cập nhật thông tin sinh viên lên server
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<SinhvienModel> call = apiService.updateSinhvien(sinhvien.get_id(), sinhvien);
        call.enqueue(new Callback<SinhvienModel>() {
            @Override
            public void onResponse(Call<SinhvienModel> call, Response<SinhvienModel> response) {
                if (response.isSuccessful()) {
                    // Thông báo cập nhật thành công
                    Toast.makeText(context, "Cập nhật sinh viên thành công", Toast.LENGTH_SHORT).show();
                } else {
                    // Thông báo cập nhật thất bại
                    Toast.makeText(context, "Cập nhật sinh viên thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SinhvienModel> call, Throwable t) {
                // Thông báo lỗi khi gọi API
                Toast.makeText(context, "Đã xảy ra lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}

