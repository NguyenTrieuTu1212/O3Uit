package com.example.o3uit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.o3uit.FindUser.NearbyUsers;
import com.example.o3uit.FindUser.NearbyUsersCallback;
import com.example.o3uit.Map.MapModel;
import com.example.o3uit.Service.ApiService;
import com.example.o3uit.Service.RetrofitClient;
import com.example.o3uit.Token.Token;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraBoundsOptions;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.animation.CameraAnimationsPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.AnnotationType;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.attribution.AttributionPlugin;
import com.mapbox.maps.plugin.logo.LogoPlugin;
import com.mapbox.maps.plugin.scalebar.ScaleBarPlugin;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapFragment extends Fragment {

    MapModel mapData;
    NearbyUsers nearbyUsers1 = null;
    NearbyUsers nearbyUsers2 = null;

    String assetIdDefautWeather= "5zI6XqkQVSfdgOrZ1MyWEf";
    String assetIdTest="6iWtSbgqMQsVq8RPkJJ9vo";
    MapView mapView;
    static MapboxMap mapboxMap;
    ApiService apiServiceMapData;
    ApiService apiServiceFindUserNearBy;

    AnnotationConfig annoConfig;
    AnnotationPlugin annoPlugin;
    PointAnnotationManager pointAnnoManager;

    Point pointUser1;
    Point pointUser2;


    TextView txtEmailInfor,txtBrightness,txtcolourTemperature,txtonOff;



    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                Toast.makeText(getActivity(), "Permission granted!", Toast.LENGTH_SHORT).show();
            }
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.mapView);

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }


        GetDataMap();
        GetUserNearbyLocation();
        DrawMap();
        return view;
    }



    private void GetDataMap(){
        apiServiceMapData = RetrofitClient.getClient().create(ApiService.class);
        Call<MapModel> call = apiServiceMapData.getMapModel();
        call.enqueue(new Callback<MapModel>() {
            @Override
            public void onResponse(Call<MapModel> call, Response<MapModel> response) {
                if (response.isSuccessful()) {
                    MapModel.setMapObj(response.body());
                } else {
                    // Xử lý khi có lỗi từ server
                }
            }

            @Override
            public void onFailure(Call<MapModel> call, Throwable t) {

            }
        });
    }

    private void GetUserNearbyLocation(){
        GetUserNearBy(assetIdDefautWeather, Token.getToken(), new NearbyUsersCallback() {
            @Override
            public void onDataFetchComplete() {
                pointUser1 = nearbyUsers1.geLocation();
            }
        });
        GetUserNearBy(assetIdTest, Token.getToken(), new NearbyUsersCallback() {
            @Override
            public void onDataFetchComplete() {
                pointUser2 = nearbyUsers2.geLocation();
            }
        });
    }


    private void DrawMap() {
        mapView.setVisibility(View.INVISIBLE);

        new Thread(() -> {
            while (!MapModel.isReady) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            requireActivity().runOnUiThread(() -> setMapView());
        }).start();
    }


    private void setMapView() {

        mapData = MapModel.getMapObj();
        mapboxMap = mapView.getMapboxMap();
        /*Point point = Point.fromLngLat(106.80280655508835, 10.869778736885038);*/
        /*Point point1 = Point.fromLngLat(106.80345028525176, 10.869905172970164);*/
        if (mapboxMap != null) {
            mapboxMap.loadStyleJson(Objects.requireNonNull(new Gson().toJson(mapData)), style -> {
                style.removeStyleLayer("poi-level-1");
                style.removeStyleLayer("highway-name-major");

                annoPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
                annoConfig = new AnnotationConfig("map_annotation");
                pointAnnoManager = (PointAnnotationManager) annoPlugin.createAnnotationManager(AnnotationType.PointAnnotation, annoConfig);
                pointAnnoManager.addClickListener(pointAnnotation -> {
                    String id = Objects.requireNonNull(pointAnnotation.getData()).getAsJsonObject().get("id").getAsString();
                    GetUserNearBy(id, Token.getToken(), new NearbyUsersCallback() {
                        @Override
                        public void onDataFetchComplete() {
                        }
                    });
                    showDialog(id);
                    return true;
                });

                // Create point annotations
                createPointAnnotation(pointUser1, assetIdDefautWeather, R.drawable.baseline_location_on_24);
                createPointAnnotation(pointUser2, assetIdTest, R.drawable.baseline_location_on_24);

                // Set camera values
                setCameraValues();

                CameraAnimationsPlugin cameraAnimationsPlugin = mapView.getPlugin(Plugin.MAPBOX_CAMERA_PLUGIN_ID);
                if (cameraAnimationsPlugin != null) {
                    mapView.setVisibility(View.VISIBLE);
                }
            });
        }
    }


    private void createPointAnnotation(Point point, String id, int iconResource) {
        ArrayList<PointAnnotationOptions> markerList = new ArrayList<>();
        Drawable iconDrawable = getResources().getDrawable(iconResource);
        Bitmap iconBitmap = drawableToBitmap(iconDrawable);
        JsonObject idDeviceTemperature = new JsonObject();
        idDeviceTemperature.addProperty("id", id);
        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(iconBitmap)
                .withData(idDeviceTemperature);
        markerList.add(pointAnnotationOptions);
        pointAnnoManager.create(markerList);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    private void setCameraValues() {
        if (mapboxMap != null) {
            mapboxMap.setCamera(
                    new CameraOptions.Builder()
                            .center(mapData.getCenter())
                            .zoom(mapData.getZoom())
                            .build()
            );

            mapboxMap.setBounds(
                    new CameraBoundsOptions.Builder()
                            .minZoom(mapData.getMinZoom())
                            .maxZoom(mapData.getMaxZoom())
                            .bounds(mapData.getBounds())
                            .build()
            );
        }
    }



    private void GetUserNearBy(String assetId, String token, NearbyUsersCallback listener){
        apiServiceFindUserNearBy = RetrofitClient.getClient().create(ApiService.class);
        Call<NearbyUsers> call = apiServiceFindUserNearBy.getUsers(assetId, "Bearer "+ token);
        call.enqueue(new Callback<NearbyUsers>() {
            @Override
            public void onResponse(Call<NearbyUsers> call, Response<NearbyUsers> response) {
                if (assetId.equals("5zI6XqkQVSfdgOrZ1MyWEf")) {
                    nearbyUsers1 = response.body();
                } else if (assetId.equals("6iWtSbgqMQsVq8RPkJJ9vo")) {
                    nearbyUsers2 = response.body();
                }
                listener.onDataFetchComplete();
            }

            @Override
            public void onFailure(Call<NearbyUsers> call, Throwable t) {
                Log.d("API CALL", t.getMessage().toString());
            }
        });
    }






    private void showDialog(String idUser) {

        final Dialog dialog = new Dialog(getActivity());

        if(idUser.equals(assetIdTest)){
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.bottomsheetlayout);
            txtEmailInfor = dialog.findViewById(R.id.txtInforEmail);
            txtBrightness = dialog.findViewById(R.id.txtInforBrightness);
            txtcolourTemperature = dialog.findViewById(R.id.txtCoulourTemp);
            txtonOff = dialog.findViewById(R.id.txtOnOff);

            txtEmailInfor.setText(nearbyUsers2.getEmail());
            txtBrightness.setText(nearbyUsers2.getBrightness());
            txtcolourTemperature.setText(nearbyUsers2.getColourTemperature());
            txtonOff.setText(nearbyUsers2.getOnOff());
            dialog.show();
        }

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }







}