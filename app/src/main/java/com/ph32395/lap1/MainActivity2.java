package com.ph32395.lap1;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity2 extends AppCompatActivity {

    ListView listView;

    List<SinhvienModel> list;

    SinhvienAdapter sinhvienAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        listView = findViewById(R.id.lvCustomListView);
        FloatingActionButton add = findViewById(R.id.fabCustom);

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


                    listView.setAdapter(sinhvienAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<SinhvienModel>> call, Throwable t) {

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity2.this,AddSV.class);
                startActivity(i);
            }
        });

        Button logout = findViewById(R.id.btnLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent in = new Intent(MainActivity2.this, ChoiceLoginActivity.class);
                startActivity(in);
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
    }



}
