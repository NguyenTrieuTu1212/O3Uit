package com.example.o3uit.ModelLogin;

import com.google.gson.annotations.SerializedName;

public class Asset {

    @SerializedName("access_token")
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }
}
