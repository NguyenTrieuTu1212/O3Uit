package com.example.o3uit.Service;



import com.example.o3uit.Chart.DataPoint;
import com.example.o3uit.FindUser.NearbyUsers;
import com.example.o3uit.Map.MapModel;
import com.example.o3uit.ModelLogin.Asset;
import com.example.o3uit.WeatherData.DataWeather;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/master/map")
    Call<MapModel> getMapModel();

    @GET("api/master/asset/{assetID}")
    Call<DataWeather> getDataWeather(@Path("assetID") String assetID, @Header("Authorization") String auth);


    @GET("api/master/asset/{assetID}")
    Call<NearbyUsers> getUsers(@Path("assetID") String assetID, @Header("Authorization") String auth);

    @POST("/api/master/asset/datapoint/{assetId}/attribute/{attributeName}")
    Call<List<DataPoint>> getDataPoints(
            @Header("accept") String accept,
            @Header("Authorization") String auth,
            @Header("Content-Type") String contentType,
            @Path("assetId") String assetId,
            @Path("attributeName") String attributeName,
            @Body RequestBody body
    );

    @FormUrlEncoded
    @POST("/auth/realms/master/protocol/openid-connect/token")
    Call<Asset> authenticate(
            @Field("client_id") String clientId,
            @Field("username") String username,
            @Field("password") String password,
            @Field("grant_type") String grantType,
            @Field("email") String email
    );
}
