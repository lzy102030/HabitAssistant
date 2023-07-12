package com.example.habitassistant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_userName;
    private EditText et_psw;
    private Button btn_login;
    private TextView tv_register;
    private ActivityResultLauncher<Intent> register;


    private String userName;
    private String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_userName = (EditText) findViewById(R.id.et_userName);
        et_psw = (EditText) findViewById(R.id.et_psw);
        btn_login = (Button) findViewById(R.id.btn_login);
        tv_register = (TextView) findViewById(R.id.tv_register);

        btn_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);

        register = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result!=null){
                    Intent intent=result.getData();
                    if(intent!=null && result.getResultCode()== Activity.RESULT_OK){
                        Bundle bundle = intent.getExtras();
                        userName = bundle.getString(LoginActivity.this.getString(R.string.userName_L) );
                        password = bundle.getString(LoginActivity.this.getString(R.string.password_L));
                        et_userName.setText(userName);
                        et_psw.setText(password);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        int id = view.getId();
        if (id == R.id.btn_login) {
            //TODO 账号密码比对
            intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.tv_register) {
            intent = new Intent(LoginActivity.this, RegisterActivity.class);
            register.launch(intent);
        }
    }
}
