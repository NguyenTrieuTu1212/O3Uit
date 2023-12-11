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


import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapFragment extends Fragment {

    MapModel mapData;
    NearbyUsers nearbyUsers1 = null;
    NearbyUsers nearbyUsers2 = null;
    MapView mapView;
    static MapboxMap mapboxMap;
    ApiService apiServiceMapData;
    ApiService apiServiceFindUserNearBy;

    AnnotationConfig annoConfig;
    AnnotationPlugin annoPlugin;
    PointAnnotationManager pointAnnoManager;

    Point pointUser1;
    Point pointUser2;







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
        GetUserNearBy("5zI6XqkQVSfdgOrZ1MyWEf",Token.getToken());
        GetUserNearBy("6iWtSbgqMQsVq8RPkJJ9vo",Token.getToken());
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


    private void GetUserNearBy(String assetId, String token){

        apiServiceFindUserNearBy = RetrofitClient.getClient().create(ApiService.class);

        Call<NearbyUsers> call = apiServiceFindUserNearBy.getUsers(assetId, "Bearer "+ token);
        call.enqueue(new Callback<NearbyUsers>() {
            @Override
            public void onResponse(Call<NearbyUsers> call, Response<NearbyUsers> response) {
                NearbyUsers.setNearbyUsers(response.body());
            }

            @Override
            public void onFailure(Call<NearbyUsers> call, Throwable t) {
                Log.d("API CALL", t.getMessage().toString());

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
        nearbyUsers1 =NearbyUsers.getNearbyUsers();
        mapboxMap = mapView.getMapboxMap();
        /*Point point = Point.fromLngLat(106.80280655508835, 10.869778736885038);*/
        Point point1 = Point.fromLngLat(106.80345028525176, 10.869905172970164);
        if (mapboxMap != null) {
            mapboxMap.loadStyleJson(Objects.requireNonNull(new Gson().toJson(mapData)), style -> {
                style.removeStyleLayer("poi-level-1");
                style.removeStyleLayer("highway-name-major");

                annoPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
                annoConfig = new AnnotationConfig("map_annotation");
                pointAnnoManager = (PointAnnotationManager) annoPlugin.createAnnotationManager(AnnotationType.PointAnnotation, annoConfig);
                pointAnnoManager.addClickListener(pointAnnotation -> {
                    String id = Objects.requireNonNull(pointAnnotation.getData()).getAsJsonObject().get("id").getAsString();
                    showDialog();
                    return true;
                });

                // Create point annotations
                createPointAnnotation(nearbyUsers1.geLocation(), "5zI6XqkQVSfdgOrZ1MyWEf", R.drawable.baseline_location_on_24);
                createPointAnnotation(point1, "6iWtSbgqMQsVq8RPkJJ9vo", R.drawable.baseline_location_on_24);

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


    private void showDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        LinearLayout editLayout = dialog.findViewById(R.id.layoutEdit);
        LinearLayout shareLayout = dialog.findViewById(R.id.layoutShare);
        LinearLayout uploadLayout = dialog.findViewById(R.id.layoutUpload);
        LinearLayout printLayout = dialog.findViewById(R.id.layoutPrint);



        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    /*private void setMapView() {
        mapData = MapModel.getMapObj();
        ScaleBarPlugin scaleBarPlugin = mapView.getPlugin(Plugin.MAPBOX_SCALEBAR_PLUGIN_ID);
        assert scaleBarPlugin != null;
        scaleBarPlugin.setEnabled(false);
        LogoPlugin logoPlugin = mapView.getPlugin(Plugin.MAPBOX_LOGO_PLUGIN_ID);
        assert logoPlugin != null;
        logoPlugin.setEnabled(false);
        AttributionPlugin attributionPlugin = mapView.getPlugin(Plugin.MAPBOX_ATTRIBUTION_PLUGIN_ID);
        assert attributionPlugin != null;
        attributionPlugin.setEnabled(false);

        mapboxMap = mapView.getMapboxMap();
        mapboxMap.loadStyleJson(Objects.requireNonNull(new Gson().toJson(mapData)), style -> {

            style.removeStyleLayer("poi-level-1");
            style.removeStyleLayer("highway-name-major");
            AnnotationPlugin annoPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
            AnnotationConfig annoConfig = new AnnotationConfig("map_annotation");
            PointAnnotationManager pointAnnoManager = (PointAnnotationManager) annoPlugin.createAnnotationManager(AnnotationType.PointAnnotation, annoConfig);

            pointAnnoManager.addClickListener(pointAnnotation -> {
                String id = Objects.requireNonNull(pointAnnotation.getData()).getAsJsonObject().get("id").getAsString();
                return true;
            });

            ArrayList<PointAnnotationOptions> markerList = new ArrayList<>();
            pointAnnoManager.create(markerList);
        });
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

        CameraAnimationsPlugin cameraAnimationsPlugin = mapView.getPlugin(Plugin.MAPBOX_CAMERA_PLUGIN_ID);
        assert cameraAnimationsPlugin != null;
        mapView.setVisibility(View.VISIBLE);
    }*/





}