package com.pan.skating.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.pan.skating.R;
import com.pan.skating.base.BaseApplication;
import com.pan.skating.bean.User;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;

public class Login extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.act_login_cancel)
    ImageView cancel;
    @BindView(R.id.act_login_register)
    TextView register;
    @BindView(R.id.act_login_phone)
    EditText phone;
    @BindView(R.id.act_login_password)
    EditText password;
    @BindView(R.id.act_login_login)
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        cancel.setOnClickListener(this);
        register.setOnClickListener(this);
        login.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        final String number=phone.getText().toString().trim();
        String pwd=password.getText().toString().trim();

        switch (v.getId()){
            //点击取消
            case R.id.act_login_cancel:
                startActivity(new Intent(Login.this,Welcome.class));
                Login.this.finish();
                break;
            //点击注册
            case R.id.act_login_register:
                startActivity(new Intent(Login.this,Register.class));
                Login.this.finish();
                break;
            //点击登陆
            case R.id.act_login_login:

                BmobUser.loginByAccount(Login.this, number, pwd, new LogInListener<User>() {
                    @Override
                    public void done(final User user, BmobException e) {
                        if(user!=null){
                            //获取用户列表
                            BmobQuery<User> query = new BmobQuery<User>();
                            query.addWhereEqualTo("mobilePhoneNumber", number);
                            query.findObjects(Login.this, new FindListener<User>() {
                                @Override
                                public void onSuccess(List<User> object) {
                                    //Toast.makeText(Login.this,"查询用户成功",Toast.LENGTH_SHORT).show();
                                    BaseApplication.app.setUserdatas(user);
                                }
                                @Override
                                public void onError(int code, String msg) {
                                    // TODO Auto-generated method stub
                                    //toast("查询用户失败："+msg);
                                }
                            });

                            Toast.makeText(Login.this,"登录成功",Toast.LENGTH_SHORT).show();
                            //登录成功后跳转主界面
                            startActivity(new Intent(Login.this,MainActivity.class));
                            Login.this.finish();
                            (new Welcome()).finish();

                        }else{
                            Toast.makeText(Login.this,"登录失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                break;

        }
    }
}
