package com.example.o3uit.FindUser;

import com.example.o3uit.Map.MapModel;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.mapbox.geojson.Point;

public class NearbyUsers {

    public static NearbyUsers nearbyUsers = null;

    public static NearbyUsers getNearbyUsers() {
        return nearbyUsers;
    }

    public static void setNearbyUsers(NearbyUsers nearbyUsersRef) {
        nearbyUsers = nearbyUsersRef;
    }

    @SerializedName("id")
    public String idUserNearby;
    @SerializedName("version")
    public String versionUserNearby;
    @SerializedName("createdOn")
    public String createdOnUserNearby;
    @SerializedName("name")
    public String nameUserNearby;
    @SerializedName("accessPublicRead")
    public String accessPublicReadUserNearby;
    @SerializedName("parentID")
    public String parentIDUserNearby;
    @SerializedName("realm")
    public String realmUserNearby;
    @SerializedName("type")
    public String typeUserNearby;
    @SerializedName("path")
    public String pathUserNearby[];
    @SerializedName("attributes")
    public JsonObject attributesUserNearby;


    public String getIdUserNearby() {
        return idUserNearby;
    }

    public Point geLocation() {
        float lng = attributesUserNearby
                .getAsJsonObject("location")
                .getAsJsonObject("value")
                .getAsJsonArray("coordinates").
                get(0).getAsFloat();
        float lat = attributesUserNearby
                .getAsJsonObject("location")
                .getAsJsonObject("value")
                .getAsJsonArray("coordinates").
                get(1).getAsFloat();
        return Point.fromLngLat(lng,lat);
    }






}
