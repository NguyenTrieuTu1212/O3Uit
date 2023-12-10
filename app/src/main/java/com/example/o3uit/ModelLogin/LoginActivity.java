package com.example.o3uit.ModelLogin;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.o3uit.MainActivity;
import com.example.o3uit.R;
import com.example.o3uit.Service.ApiService;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    ApiService apiServiceLogin;

    private EditText edtUsername,edtPass;

    private TextView txtWelcome,txtTitle,txtForgetPass,txtSignIn,txtNotAMember,txtSignUpNow;
    private CheckBox chekboxRemember;
    private LinearLayout linearLayoutSignIn,linearLayoutNotAccount;

    private ImageView imgIcon;


    private boolean isChanged=false;

    private URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoadElement();

        imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeLanguage();
            }
        });
        linearLayoutSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });


    }
    private void SignIn(){
        String username = edtUsername.getText().toString();
        String password = edtPass.getText().toString();

        if(isEmptyInput()) return;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url.GetURLMain())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiServiceLogin = retrofit.create(ApiService.class);

        // Thực hiện POST request
        Call<Asset> call = apiServiceLogin.authenticate("openremote", username, password, "password", "cakho12345@gmail.com");
        call.enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(Call<Asset> call, Response<Asset> response) {
                if (response.isSuccessful()) {
                    Asset result = response.body();
                    String token = result.getAccessToken();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("token",token);
                    startActivity(intent);
                    finishAffinity();
                } else {
                    Toast.makeText(LoginActivity.this, "Tên tài khoản hoặc mật khẩu không đúng !!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Asset> call, Throwable t) {
                Log.e("RetrofitError", t.getMessage());
                Toast.makeText(LoginActivity.this, "Lỗi kết nối.", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private boolean isEmptyInput(){
        if(edtUsername.getText().toString().isEmpty()){
            ShowError(edtUsername,"Bạn chưa nhập thông tin");
            return true;
        }
        else if(edtPass.getText().toString().isEmpty()){
            ShowError(edtPass,"Bạn chưa nhập thông tin");
            return true;
        }
        return false;
    }


    private void LoadElement(){
        url= new URL();

        txtWelcome = findViewById(R.id.txtWelcomeback);
        txtTitle = findViewById(R.id.txtSignintoacce);
        txtForgetPass = findViewById(R.id.txtForgetpassword);
        txtSignIn = findViewById(R.id.txtSignIn);
        txtNotAMember = findViewById(R.id.txtNotMember);
        txtSignUpNow = findViewById(R.id.txtSignUpNow);
        chekboxRemember =findViewById(R.id.checkBoxRememberme);
        edtUsername= findViewById(R.id.edtUsername);
        edtPass = findViewById(R.id.edtPassword);
        linearLayoutSignIn = findViewById(R.id.linearSignIn);
        linearLayoutNotAccount = findViewById(R.id.linerNotAcount);
        imgIcon = findViewById(R.id.imgChanged);
    }

    private void ShowError(EditText edtInput,String s){
        edtInput.setError(s);
        edtInput.requestFocus();
    }

    private void ChangeLanguage(){
        isChanged = !isChanged;
        if(isChanged){
            imgIcon.setImageResource(R.drawable.vietnam_icon);
            edtUsername.setHint("Tên đăng nhập");
            edtPass.setHint("Mật khẩu");
            txtWelcome.setText("Chào mừng trở lại");
            txtTitle.setText("Chúc bạn một ngày tốt lành");
            txtSignUpNow.setText("Đăng kí ngay !!!");
            txtNotAMember.setText("Bạn chưa có tài khoản? ");
            chekboxRemember.setText("Nhớ tài khoản này");
            txtSignIn.setText("Đăng nhập");
            txtForgetPass.setText("Quên mật khẩu");
        }else{
            imgIcon.setImageResource(R.drawable.usa_icon);
            edtUsername.setHint("Username");
            edtPass.setHint("Password");
            txtWelcome.setText("Welcome back");
            txtTitle.setText(R.string.msg_sign_in_to_acce);
            txtSignUpNow.setText("Sign up now");
            txtNotAMember.setText("Not a member?");
            chekboxRemember.setText("Remember me");
            txtSignIn.setText("Sign In");
            txtForgetPass.setText("Forget password");
        }
    }
}