package com.example.o3uit.ModelLogin;




import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.LayoutInflater;

import com.example.o3uit.R;


public class LoadingAlert {

    Activity activity;
    Dialog dialog;

    public LoadingAlert(Activity myActivity){
        activity=myActivity;
    }


    public void StartAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.custom_dialog,null));
        builder.setCancelable(true);
        dialog =builder.create();
        dialog.show();
    }

    public void CloseAlertDialog(){
        dialog.dismiss();
    }

}
