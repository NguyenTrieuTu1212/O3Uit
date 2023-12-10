package com.example.o3uit.ModelLogin;

public class URL {

    private String mainURL= "https://uiot.ixxc.dev";
    private String urlFormSignUp= "https://uiot.ixxc.dev/auth/realms/master/protocol/openid-connect/registrations?client_id=openremote&redirect_uri=https%3A%2F%2Fuiot.ixxc.dev%2Fmanager%2F&response_mode=fragment&response_type=code&scope=openid";



    public String GetURLMain(){
        return mainURL;
    }
    public String GetURLFormSignUp(){
        return urlFormSignUp;
    }
}
