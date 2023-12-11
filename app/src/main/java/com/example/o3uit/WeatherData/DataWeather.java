package com.example.o3uit.WeatherData;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DataWeather {
    @SerializedName("id")
    public String id;
    @SerializedName("version")
    public String version;
    @SerializedName("createdOn")
    public String createdOn;
    @SerializedName("name")
    public String name;
    @SerializedName("accessPublicRead")
    public String accessPublicRead;
    @SerializedName("parentID")
    public String parentID;
    @SerializedName("realm")
    public String realm;
    @SerializedName("type")
    public String type;
    @SerializedName("path")
    public String path[];
    @SerializedName("attributes")
    public JsonObject attributes;


    private String formatDataWeather(String nameMember){
        return String.valueOf(attributes.getAsJsonObject("data")
                .getAsJsonObject("value")
                .getAsJsonObject("main")
                .get(nameMember));
    }


    public String test(){
        return formatDataWeather("type");
    }

    public String getBaseURL(){
        String baseUrl = attributes.getAsJsonObject("baseURL").get("value").getAsString();
        return baseUrl;
    }

    public String getTemperature(){
        String stemp = formatDataWeather("temp");
        float ftemp = Float.valueOf(stemp);
        int roundedTemperature = Math.round(ftemp);
        return String.valueOf(roundedTemperature);
        /*return formatDataWeather("temp");*/
    }

    public String getMaxTemperature(){
        return formatDataWeather("temp_max");
    }

    public String getMinTemperature(){
        return formatDataWeather("temp_min");
    }

    public String getFeelLikeTemperature(){
        return formatDataWeather("feels_like");
    }

    public String getHumidity(){
        return formatDataWeather("humidity");
    }

    public String getPressure(){
        return formatDataWeather("feels_like");
    }


    public String getWindSpeed(){
        return String.valueOf(attributes.getAsJsonObject("data")
                .getAsJsonObject("value")
                .getAsJsonObject("wind")
                .get("speed"));
    }


    public String getSunrise(){
        return convertUnixTimestamp(String.valueOf(attributes.getAsJsonObject("data")
                .getAsJsonObject("value")
                .getAsJsonObject("sys")
                .get("sunrise")));
    }


    public String getSunset(){

        return convertUnixTimestamp(String.valueOf(attributes.getAsJsonObject("data")
                .getAsJsonObject("value")
                .getAsJsonObject("sys")
                .get("sunset")));


    }


    public String getIconWeather(){
        return String.valueOf(attributes.getAsJsonObject("data")
                .getAsJsonObject("value")
                .getAsJsonArray("weather")
                .get(0)
                .getAsJsonObject()
                .get("icon").getAsString());
    }



    public String getStatusWeather(){
        String stateWeather = String.valueOf(attributes.getAsJsonObject("data")
                .getAsJsonObject("value")
                .getAsJsonArray("weather")
                .get(0)
                .getAsJsonObject()
                .get("description").getAsString());
        return capitalizeFirstLetter(stateWeather);
    }

    public String getRainFall(){
        return String.valueOf(attributes.getAsJsonObject("data")
                .getAsJsonObject("value")
                .getAsJsonObject("rain")
                .get("1h"));
    }


    public static String convertUnixTimestamp(String unixTimestamp) {
        Long unixTime = Long.valueOf(unixTimestamp);
        Instant instant = Instant.ofEpochSecond(unixTime);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return dateTime.format(formatter);
    }



    public String capitalizeFirstLetter(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        } else {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }
}
