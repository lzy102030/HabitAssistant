package com.example.habitassistant;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_username;
    private EditText et_password;
    private Button btn_login;
    private TextView tv_register;
    private ActivityResultLauncher<Intent> register;
    private String url = "http://192.168.105.225:8000/auth/login";
    private String username;
    private String password;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences sp = getSharedPreferences("loginSera",MODE_PRIVATE);
        String isLogin = sp.getString("isLogin","");
        //登录检测，已登录不需要重新登录
        if(isLogin.equals("1")){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
        et_username = findViewById(R.id.et_userName);
        et_password = findViewById(R.id.et_psw);
        btn_login = findViewById(R.id.btn_login);
        tv_register = findViewById(R.id.tv_register);

        btn_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);

        register = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result != null) {
                    Intent intent = result.getData();
                    if (intent != null && result.getResultCode() == Activity.RESULT_OK) {
                        Bundle bundle = intent.getExtras();
                        username = bundle.getString(LoginActivity.this.getString(R.string.userName_L));
                        password = bundle.getString(LoginActivity.this.getString(R.string.password_L));
                        et_username.setText(username);
                        et_password.setText(password);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_login) {
            //TODO 账号密码比对
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        username = et_username.getText().toString();
                        password = et_password.getText().toString();
                        Log.i("username", username);
                        Log.i("password", password);
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("username", username)
                                .add("password", password)
                                .build();
                        Request request = new Request.Builder()
                                .url(url)
                                .post(requestBody)
                                .build();
                        Response response = client.newCall(request).execute();
                        if (response.code() == 200) {
                            //保存token以及token_type
                            String data = response.body().string();
                            JSONObject jsonObject = new JSONObject(data);
                            String token = jsonObject.getString("access_token");
                            Log.i("登录返回", token);
                            Log.i("登录返回", data);
                            SharedPreferences.Editor editor = getSharedPreferences("loginSera", MODE_PRIVATE).edit();
                            editor.putString("token", token);
                            editor.putString("isLogin","1");
                            editor.commit();
                            intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "账号或密码错误，请重新登录", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } else if (id == R.id.tv_register) {
            intent = new Intent(LoginActivity.this, RegisterActivity.class);
            register.launch(intent);
        }
    }
}
