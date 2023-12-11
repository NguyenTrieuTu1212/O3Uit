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



    public String getEmail(){
        return String.valueOf(attributesUserNearby
                .getAsJsonObject("email")
                .get("value").getAsString());
    }


    public String getColourTemperature(){
        return String.valueOf(attributesUserNearby
                .getAsJsonObject("colourTemperature")
                .get("value").getAsInt());
    }

    public String getBrightness(){
        return String.valueOf(attributesUserNearby
                .getAsJsonObject("brightness")
                .get("value").getAsInt());
    }

    public String getOnOff(){
        return String.valueOf(attributesUserNearby
                .getAsJsonObject("onOff")
                .get("value").getAsBoolean());
    }


    public String getTemperature(){
        return String.valueOf(attributesUserNearby
                .getAsJsonObject("temperature")
                .get("value").getAsFloat());
    }

    public String getHumidity(){
        return String.valueOf(attributesUserNearby
                .getAsJsonObject("humidity")
                .get("value").getAsInt());
    }

    public String getPlace(){
        return String.valueOf(attributesUserNearby
                .getAsJsonObject("place")
                .get("value").getAsString());
    }

    public String getManufacturer(){
        return String.valueOf(attributesUserNearby
                .getAsJsonObject("manufacturer")
                .get("value").getAsString());
    }

    public String getWindDirection(){
        return String.valueOf(attributesUserNearby
                .getAsJsonObject("windDirection")
                .get("value").getAsInt());
    }

    public String getWindSpeed(){
        return String.valueOf(attributesUserNearby
                .getAsJsonObject("windSpeed")
                .get("value").getAsFloat());
    }

    public String getRainFall(){
        return String.valueOf(attributesUserNearby
                .getAsJsonObject("rainfall")
                .get("value").getAsFloat());
    }

}
