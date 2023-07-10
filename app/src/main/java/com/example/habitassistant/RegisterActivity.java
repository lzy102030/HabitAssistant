package com.example.habitassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_register;
    private EditText et_userName;
    private EditText et_psw;
    private EditText et_validatePsw;
    private TextView tv_warning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_register = (Button) findViewById(R.id.btn_register);
        et_userName = (EditText) findViewById(R.id.et_userName);
        et_psw = (EditText) findViewById(R.id.et_psw);
        et_validatePsw = (EditText) findViewById(R.id.et_valiatePsw);
        tv_warning = (TextView) findViewById(R.id.tv_warning);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btn_register){
            String inputUsername = et_userName.getText().toString();
            String inputPassword = et_psw.getText().toString();
            String inputAffirm = et_validatePsw.getText().toString();

            if(inputUsername==null||inputPassword==null||inputAffirm==null){
                tv_warning.setText("请全部填写信息");
            }
            if(validateFormat()&&inputPassword.equals(inputAffirm)){
                Intent intent=new Intent();
                Bundle bundle=new Bundle();
                bundle.putString("userName",inputUsername);
                bundle.putString("password",inputPassword);
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }else{
                tv_warning.setText("前后密码不一致");
            }

        }
    }

    public boolean validateFormat(){
        //TODO 验证格式
        return true;
    }
}
