package com.pan.skating.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import com.pan.skating.R;
import com.pan.skating.base.BaseApplication;
import com.pan.skating.interfaces.BaseInterface;
import com.pan.skating.bean.User;
import com.pan.skating.utils.ConnectionNetUtils;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.GetListener;

public class Welcome extends AppCompatActivity implements BaseInterface{
    private LinearLayout ly;
    private Animation anim;
    private Button login,register;
    private boolean isHaveNet=false;
    private AlertDialog.Builder builder;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏状态栏
        //定义全屏参数
        int flag=WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window=Welcome.this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        setContentView(R.layout.activity_welcome);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        initViews();
        initDatas();
        initOpers();
    }

    @Override
    public void initViews() {
        ly= (LinearLayout) findViewById(R.id.act_welcome);
        login= (Button) findViewById(R.id.act_login);
        register= (Button) findViewById(R.id.act_register);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void initOpers() {

        //动画来源、监听
        anim=AnimationUtils.loadAnimation(this,R.anim.anim);
        ly.setAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            BmobUser bmobUser = BmobUser.getCurrentUser(Welcome.this);
            @Override
            public void onAnimationStart(Animation animation) {
                //判断网络状态
                isHaveNet= ConnectionNetUtils.isConnectionNet(Welcome.this);

                //将登陆用户数据放到Application
                String id= (String) bmobUser.getObjectByKey(Welcome.this,"objectId");
                BmobQuery<User> query = new BmobQuery<User>();
                query.getObject(Welcome.this, id, new GetListener<User>() {

                    @Override
                    public void onSuccess(User user) {
                        BaseApplication.app.setUserdatas(user);
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(isHaveNet){
                    if(bmobUser != null){
                        // 允许用户使用应用
                        startActivity(new Intent(Welcome.this,MainActivity.class));
                        finish();
                    }else{
                        //缓存用户对象为空时， 可打开用户注册界面…
                        //登录注册按钮显示
                        login.setVisibility(View.VISIBLE);
                        register.setVisibility(View.VISIBLE);
                    }
                }else{
                    popDialog();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //登录按钮
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Welcome.this, Login.class));
                //startActivity(new Intent(Welcome.this, MainActivity.class));
            }
        });
        //注册按钮
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Welcome.this, Register.class));
            }
        });
    }

    //设置网络警告框
    private void popDialog(){
        builder=new AlertDialog.Builder(Welcome.this);
        builder.setTitle("设置网络")
                .setMessage("当前没有网络，请设置")
                .setCancelable(false)//设置对话框是否可以取消
                .setPositiveButton("设置网络", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //跳转到设置网路的Activity
                        Intent intent=new Intent();
                        intent.setAction(Settings.ACTION_WIFI_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }


    /**
     * 利用生命周期判断联网状态
     */
    @Override
    protected void onStart() {
        super.onStart();

        //表示刚设置网络
        if(builder!=null){
            progressDialog=new ProgressDialog(this);//实例化进度条对话框
            AsyncTask<Void,String,Void> task=new AsyncTask<Void, String, Void>() {
                //前期准备工作
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog.show();
                    progressDialog.setMessage("获取网络\n\r.");
                }

                //子线程，耗时操作
                @Override
                protected Void doInBackground(Void... params) {

                    for(int i=1;i<20;i++) {
                        //持续判断网络
                        isHaveNet = ConnectionNetUtils.isConnectionNet(Welcome.this);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (isHaveNet) {//有网
                            i=20;
                        }
                        switch (i%6){
                            case 1:
                                publishProgress("获取网络\n\r.");
                                break;
                            case 2:
                                publishProgress("获取网络\n\r..");
                                break;
                            case 3:
                                publishProgress("获取网络\n\r...");
                                break;
                            case 4:
                                publishProgress("获取网络\n\r....");
                                break;
                            case 5:
                                publishProgress("获取网络\n\r.....");
                                break;
                        }
                    }

                    return null;
                }
                //实时更新
                @Override
                protected void onProgressUpdate(String... values) {
                    super.onProgressUpdate(values);
                    progressDialog.setMessage(values[0]);
                }

                //退出时
                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if(isHaveNet){
                        //登录注册按钮显示
                        login.setVisibility(View.VISIBLE);
                        register.setVisibility(View.VISIBLE);
                    }else{
                        popDialog();
                    }
                    progressDialog.dismiss();
                }
            };
            task.execute();
        }
    }
}
