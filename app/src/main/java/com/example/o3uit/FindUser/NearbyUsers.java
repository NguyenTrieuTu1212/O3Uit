package com.example.o3uit.FindUser;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.mapbox.geojson.Point;

public class NearbyUsers {
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


    public float geLocation() {
        return  attributesUserNearby
                .getAsJsonObject("location")
                .getAsJsonObject("value")
                .getAsJsonArray("coordinates").
                get(0).getAsFloat();

    }

    public String getIdUserNearby() {
        return idUserNearby;
    }

    public String getNameUserNearby() {
        return nameUserNearby;
    }

    public String getTemp(){
        return String.valueOf(attributesUserNearby
                .getAsJsonObject("temperature")
                .get("value"));
    }
    public String getHuminity(){
        return String.valueOf(attributesUserNearby
                .getAsJsonObject("humidity")
                .get("value"));
    }

    public String getPlace(){
        return String.valueOf(attributesUserNearby
                .getAsJsonObject("place")
                .get("value"));
    }


}
