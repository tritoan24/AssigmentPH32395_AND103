package com.ph32395.lap1;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface ApiService {
        String BASE_URL = "http://192.168.0.104:3000";

        @GET("/api/")
        Call<List<SinhvienModel>> getSinhviens();

        @POST("api/post-sv")
        Call<SinhvienModel> addSinhvien(@Body SinhvienModel sinhvien);

        @PUT("api/update-sv/{id}")
        Call<SinhvienModel> updateSinhvien(@Path("id") String id, @Body SinhvienModel sinhvien);

        @DELETE("api/delete-sv/{id}")
        Call<Void> deleteSinhvien(@Path("id") String id);

        @GET("api/search-sv")
        Call<List<SinhvienModel>> searchSinhvien(@Query("name") String name);

        @GET("api/sort-sv")
        Call<List<SinhvienModel>> sortSinhvien();
        @GET("api/sort-sv-desc")
        Call<List<SinhvienModel>> sortSinhvienAsc();
        @GET("api/load-more")
        Call<List<SinhvienModel>> loadMore(@Query("page") int page);
        //them vao gio hang
        @POST("api/add-cart")
        Call<SinhvienModel> addTocart(@Body SinhvienModel sinhvien);



}
