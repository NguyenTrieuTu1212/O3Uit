package com.example.o3uit.Map;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CoordinateBounds;

import java.util.ArrayList;

public class MapModel {
    public static boolean isReady = false;

    public static MapModel MapObj = null;

    public static MapModel getMapObj() {
        return MapObj;
    }

    public static void setMapObj(MapModel mapObj) {
        MapObj = mapObj;
        isReady = true;
    }

    @SerializedName("options")
    public JsonObject options;
    @SerializedName("version")
    public int version;
    @SerializedName("sources")
    public JsonObject sources;
    @SerializedName("sprite")
    public String sprite;
    @SerializedName("glyphs")
    public String glyphs;
    @SerializedName("layers")
    public ArrayList<JsonObject> layers;

    public Point getCenter() {
        float lat = options.get("default").getAsJsonObject().get("center").getAsJsonArray().get(1).getAsFloat();
        float lng = options.get("default").getAsJsonObject().get("center").getAsJsonArray().get(0).getAsFloat();
        return Point.fromLngLat(lng, lat);
    }

    public double getZoom() {
        return options.get("default").getAsJsonObject().get("zoom").getAsInt();
    }

    public double getMinZoom() {
        return options.get("default").getAsJsonObject().get("minZoom").getAsInt();
    }

    public double getMaxZoom() {
        return options.get("default").getAsJsonObject().get("maxZoom").getAsInt();
    }

    public CoordinateBounds getBounds() {
        ArrayList<Float> bounds = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            bounds.add(options.get("default").getAsJsonObject().get("bounds").getAsJsonArray().get(i).getAsFloat());
        }

        return new CoordinateBounds(
                Point.fromLngLat(bounds.get(0),bounds.get(1)),
                Point.fromLngLat(bounds.get(2),bounds.get(3))
        );
    }
}
