package com.pan.skating.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.pan.skating.R;
import com.pan.skating.interfaces.BaseInterface;
import com.pan.skating.bean.User;

import cn.bmob.v3.listener.SaveListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class Register extends AppCompatActivity implements BaseInterface,View.OnClickListener{
    private ImageView back;
    private Button getcode,register;
    private EditText phone,code,password,username;
    private EventHandler eh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_register);

        eh=new EventHandler(){

            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        // 注册回调监听接口
        SMSSDK.registerEventHandler(eh);

        initViews();
        initDatas();
        initOpers();
    }

    @Override
    public void initViews() {
        back= (ImageView) findViewById(R.id.act_register_back);
        back.setOnClickListener(this);
        getcode= (Button) this.findViewById(R.id.act_register_getcode);
        getcode.setOnClickListener(this);
        register= (Button) this.findViewById(R.id.act_register_register);
        register.setOnClickListener(this);
        phone= (EditText) this.findViewById(R.id.act_register_phone);
        code= (EditText) this.findViewById(R.id.act_register_code);
        password= (EditText) this.findViewById(R.id.act_register_password);
        username= (EditText) this.findViewById(R.id.act_register_username);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void initOpers() {

    }

    @Override
    public void onClick(View view) {
        final String pwd=password.getText().toString().trim();
        final String code1=code.getText().toString().trim();
        final String number=phone.getText().toString().trim();

        switch (view.getId()){
            //点击取消
            case R.id.act_register_back:
                startActivity(new Intent(Register.this,Welcome.class));
                Register.this.finish();
                break;
            //获取验证码
            case R.id.act_register_getcode:
                if(!TextUtils.isEmpty(number)){
                    SMSSDK.getVerificationCode("86",number);
                }else{
                    Toast.makeText(Register.this,"请输入号码：",Toast.LENGTH_SHORT).show();
                }
                break;
            //点击注册
            case R.id.act_register_register:
                if(pwd.equals("")||number.equals("")){
                    Toast.makeText(Register.this,"请输入手机号和密码", Toast.LENGTH_SHORT).show();
                }else{
                    if(!TextUtils.isEmpty(phone.getText().toString().trim())&&!TextUtils.isEmpty(code1)){
                        SMSSDK.submitVerificationCode("86", number, code1);
                    }else{
                        Toast.makeText(Register.this,"请输入手机号和验证码", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }


    class MyCount extends AsyncTask<Integer,Integer,Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(values[0]!=0){
                getcode.setText("剩余"+values[0]+"秒");
            }else{
                getcode.setText("重新获取");
            }
        }

        //倒计时
        @Override
        protected Integer doInBackground(Integer... params) {
            for (int i=60;i>=0;i--){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(i);
            }
            return null;
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    Toast.makeText(Register.this,"短信验证成功",Toast.LENGTH_SHORT).show();
                    //将用户信息存到bmob
                    //将手机号密码存到bomb
                    User user= new User();
                    user.setUsername(username.getText().toString().trim());
                    user.setMobilePhoneNumber(phone.getText().toString().trim());
                    user.setPassword(password.getText().toString().trim());
                    //注意：不能用save方法进行注册
                    user.signUp(Register.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(Register.this,"注册成功", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Register.this,Login.class));
                            Register.this.finish();
                            //通过BmobUser.getCurrentUser(context)方法获取登录成功后的本地用户信息
                        }
                        @Override
                        public void onFailure(int code, String msg) {
                            // TODO Auto-generated method stub
                            Toast.makeText(Register.this,"注册失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    //获取验证码成功
                    Toast.makeText(Register.this,"获取验证码成功",Toast.LENGTH_SHORT).show();
                    new MyCount().execute();
                }
            }else{
                ((Throwable)data).printStackTrace();
            }
        }
    };
}
