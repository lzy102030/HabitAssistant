package com.example.habitassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_register;
    private EditText et_userName;
    private EditText et_psw;
    private EditText et_validatePsw;
    private TextView tv_warning;
    private String inputUsername;
    private String inputPassword;
    private String inputAffirm;
    private String url = "http://123.57.135.185:8000/auth/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_register = findViewById(R.id.btn_register);
        et_userName = findViewById(R.id.et_userName);
        et_psw = findViewById(R.id.et_psw);
        et_validatePsw = findViewById(R.id.et_valiatePsw);
        tv_warning = findViewById(R.id.tv_warning);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_register) {
            inputUsername = et_userName.getText().toString();
            inputPassword = et_psw.getText().toString();
            inputAffirm = et_validatePsw.getText().toString();

            if (inputUsername.isEmpty() || inputPassword.isEmpty() || inputAffirm.isEmpty()) {
                tv_warning.setText("请全部填写信息");
                return;
            }
            if (validateFormat() && inputPassword.equals(inputAffirm)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.i("username", inputUsername);
                            Log.i("password", inputPassword);
                            OkHttpClient client = new OkHttpClient();
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("username", inputUsername)
                                    .add("password", inputPassword)
                                    .build();
                            Request request = new Request.Builder()
                                    .url(url)
                                    .post(requestBody)
                                    .build();
                            Response response = client.newCall(request).execute();

                            Log.i("注册", response.body().string());
                            if (response.code() == 200) {
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString("userName", inputUsername);
                                bundle.putString("password", inputPassword);
                                intent.putExtras(bundle);
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterActivity.this, "注册失败！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                //Toast.makeText(RegisterActivity.this, "注册失败！", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
                tv_warning.setText("前后密码不一致");
            }

        }
    }

    public boolean validateFormat() {
        //TODO 验证格式
        return true;
    }
}
