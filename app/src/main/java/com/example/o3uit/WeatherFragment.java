package com.example.o3uit;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.o3uit.ModelLogin.Username;
import com.example.o3uit.Service.ApiService;
import com.example.o3uit.Service.RetrofitClient;
import com.example.o3uit.Token.Token;
import com.example.o3uit.WeatherData.DataWeather;

import java.time.DayOfWeek;
import java.time.LocalDate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherFragment extends Fragment {

    private ApiService apiServiceWeatherData;


    //String token="eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJoREkwZ2hyVlJvaE5zVy1wSXpZeDBpT2lHMzNlWjJxV21sRk4wWGE1dWkwIn0.eyJleHAiOjE3MDIwMDM1NTksImlhdCI6MTcwMTkxNzE2MCwiYXV0aF90aW1lIjoxNzAxOTE3MTU5LCJqdGkiOiJiZGVlMjY0MC1hODNlLTQzMTQtOGRkMS0wNmFjODVhMzllZDAiLCJpc3MiOiJodHRwczovL3Vpb3QuaXh4Yy5kZXYvYXV0aC9yZWFsbXMvbWFzdGVyIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjRlM2E0NDk2LTJmMTktNDgxMy1iZjAwLTA5NDA3ZDFlZThjYiIsInR5cCI6IkJlYXJlciIsImF6cCI6Im9wZW5yZW1vdGUiLCJzZXNzaW9uX3N0YXRlIjoiY2U5YThmNGYtYWU5NS00YWU3LWFiYjYtNjFjMjBlNmMyMzQ2IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL3Vpb3QuaXh4Yy5kZXYiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImRlZmF1bHQtcm9sZXMtbWFzdGVyIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7Im9wZW5yZW1vdGUiOnsicm9sZXMiOlsicmVhZDptYXAiLCJyZWFkOnJ1bGVzIiwicmVhZDppbnNpZ2h0cyIsInJlYWQ6YXNzZXRzIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiJjZTlhOGY0Zi1hZTk1LTRhZTctYWJiNi02MWMyMGU2YzIzNDYiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJGaXJzdCBOYW1lIExhc3QgbmFtZSIsInByZWZlcnJlZF91c2VybmFtZSI6InVzZXIiLCJnaXZlbl9uYW1lIjoiRmlyc3QgTmFtZSIsImZhbWlseV9uYW1lIjoiTGFzdCBuYW1lIiwiZW1haWwiOiJ1c2VyQGl4eGMuZGV2In0.XjdvBvFIBlrHI0EIY-_BZldpupCQd092gau7LFbR8g7lAi1wc_36fl7KokHKe0wsa_Ma7Am_K7HI3idXxjaMLwuU3kFt94RaOfcIm3IpyoSHgUwTUM72Hj0OsyboiwM-GDy426te7tzaB3ZS6KnjOOYWC8aOUlGr5GZJAslSL3GaALGb7_XWMIddOzFk-a6YC2_qPdrH-jzrzPx7twY37mjMrMPGPjreZZ8AGNzsLGxI9ioZUXRVWH3bjho4t21kEtYjvSCxxLAv-yHU4IUPairDfU_hYH2j8T2R7YMqByJwUgeZj_QaWGCBlS9jXCfOW_Db8KLdYjhz17J_NWR1Yg";


    private TextView txtTempeture,txtHumidity,txtWindSpeed,txtAmoutOfRain,txtSunrise,txtSunset,txtStatusWeather,txtTime,txtUsername;

    private ImageView imgStatusWeather;

    private Handler handler;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        txtTempeture = view.findViewById(R.id.txtTemperature);
        txtHumidity = view.findViewById(R.id.txtHumidity);
        txtWindSpeed = view.findViewById(R.id.txtWindSpeed);
        txtAmoutOfRain = view.findViewById(R.id.txtAmountOfRain);
        txtSunrise = view.findViewById(R.id.txtSunrise);
        txtSunset = view.findViewById(R.id.txtSunset);
        txtStatusWeather = view.findViewById(R.id.txtStatusWeather);
        txtTime = view.findViewById(R.id.txtTime);
        txtUsername = view.findViewById(R.id.txtUsername);
        imgStatusWeather = view.findViewById(R.id.imgStatusWeather);


        txtUsername.setText("Hello, "+ Username.getName());
        handler = new Handler();
        GetDataWeather(Token.getToken());
        handler.postDelayed(runnableGetDataWeather, 3600000);
        return view;
    }


    private Runnable runnableGetDataWeather = new Runnable() {
        @Override
        public void run() {
            GetDataWeather(Token.getToken());
            Toast.makeText(getActivity(), "Hàm Get Weather được gọi", Toast.LENGTH_SHORT).show();
            handler.postDelayed(this, 3600000);
        }
    };
    private void GetDataWeather(String token){

        apiServiceWeatherData = RetrofitClient.getClient().create(ApiService.class);
        // 3xih2945Hes4yxpB15ZyKb
        // 4EqQeQ0L4YNWNNTzvTOqjy
        Call<DataWeather> call = apiServiceWeatherData.getDataWeather("4EqQeQ0L4YNWNNTzvTOqjy", "Bearer "+ token);
        call.enqueue(new Callback<DataWeather>() {
            @Override
            public void onResponse(Call<DataWeather> call, Response<DataWeather> response) {
                DataWeather dataWeather = response.body();

                txtTempeture.setText(dataWeather.getTemperature());
                txtHumidity.setText(dataWeather.getHumidity() +" %");
                txtWindSpeed.setText(dataWeather.getWindSpeed()+" Km/h");
                txtSunrise.setText(dataWeather.getSunrise());
                txtSunset.setText(dataWeather.getSunset());
                txtStatusWeather.setText(dataWeather.getStatusWeather());
                txtTime.setText(String.valueOf(getCurrentDayOfWeek()));
                SetIcon(dataWeather.getIconWeather());
                if (!dataWeather.attributes.getAsJsonObject("data").getAsJsonObject("value").has("rain")) {
                    txtAmoutOfRain.setText("0 mm");
                } else {
                    txtAmoutOfRain.setText(dataWeather.getRainFall());
                }

            }
            @Override
            public void onFailure(Call<DataWeather> call, Throwable t) {
                Log.d("API CALL", t.getMessage().toString());

            }
        });

    }


    public static DayOfWeek getCurrentDayOfWeek() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.getDayOfWeek();
    }



    private void SetIcon(String iconCode){
        if(iconCode.equals("01d")) imgStatusWeather.setImageResource(R.drawable.clear_sky);
        if(iconCode.equals("01n")) imgStatusWeather.setImageResource(R.drawable.clear_sky_night);

        if(iconCode.equals("02d")) imgStatusWeather.setImageResource(R.drawable.sun);
        if(iconCode.equals("02n")) imgStatusWeather.setImageResource(R.drawable.moon);

        if(iconCode.equals("09d")) imgStatusWeather.setImageResource(R.drawable.sun_shower);
        if(iconCode.equals("09n")) imgStatusWeather.setImageResource(R.drawable.night_shower);

        if(iconCode.equals("03d") || iconCode.equals("03n")) imgStatusWeather.setImageResource(R.drawable.scattered_clouds);
        if(iconCode.equals("04d") || iconCode.equals("04n")) imgStatusWeather.setImageResource(R.drawable.scattered_clouds);

        if(iconCode.equals("10d") || iconCode.equals("10n")) imgStatusWeather.setImageResource(R.drawable.rain);
        if(iconCode.equals("11d") || iconCode.equals("11n")) imgStatusWeather.setImageResource(R.drawable.storm);
    }


}