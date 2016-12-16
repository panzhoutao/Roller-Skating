package com.pan.skating.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.pan.skating.R;
import com.pan.skating.pager.Find;
import com.pan.skating.pager.Home;
import com.pan.skating.pager.My;

import com.pan.skating.pager.Video2;
import com.pan.skating.publish.PublishAct;
import com.pan.skating.recorder.FFmpegRecorderActivity;
import com.pan.skating.utils.NetUtil;
import com.pan.skating.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements View.OnClickListener{

    public List<Fragment> ls;
    private FragmentManager manager;
    private ViewPager vp;
    private ImageView imgs[] = new ImageView[4],add;
    private TextView[] tvs = new TextView[4];
    private LinearLayout[] lins = new LinearLayout[4];
    private int linIds[] = { R.id.lin1, R.id.lin2, R.id.lin3, R.id.lin4 };
    private int tvIds[] = { R.id.lin1_tv, R.id.lin2_tv, R.id.lin3_tv,
            R.id.lin4_tv };
    private int imgIds[] = { R.id.lin1_img, R.id.lin2_img, R.id.lin3_img,
            R.id.lin4_img };
    private Animation rotate_anticlockwise, rotate_clockwise, scale_max,
            scale_min, alpha_button;
    private boolean clicked = false;// 记录加号按钮的点击状态，默认为没有点击
    private Button act,video,tiezi;
    private RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(this.getResources().getColor(R.color.title));
        }
        setContentView(R.layout.activity_main);
        initData();
        //初始化组件
        initView();
        //将页面添加到viewpager
        ls= new ArrayList<Fragment>();
        ls.add(new Home());
        //ls.add(new Video());
        ls.add(new Find());
        ls.add(new My());
        vp.setOffscreenPageLimit(0);
        manager=getSupportFragmentManager();
        vp.setAdapter(new FmAdapter(manager));

    }

    private void initData() {
        rotate_anticlockwise = AnimationUtils.loadAnimation(this,
                R.anim.rotate_anticlockwise);
        rotate_clockwise = AnimationUtils.loadAnimation(this,
                R.anim.rotate_clockwise);
        scale_max = AnimationUtils.loadAnimation(this, R.anim.scale_max);
        scale_min = AnimationUtils.loadAnimation(this, R.anim.scale_min);
        alpha_button = AnimationUtils.loadAnimation(this, R.anim.alpha_button);
    }

    private void initView() {
        for (int i = 0; i < 4; i++) {
            lins[i] = (LinearLayout) this.findViewById(linIds[i]);
            tvs[i] = (TextView) this.findViewById(tvIds[i]);
            imgs[i] = (ImageView) this.findViewById(imgIds[i]);
            lins[i].setOnClickListener(this);
        }
        //Viewpager监听
        MyViewPager mvp = new MyViewPager(this);

        vp= (ViewPager) findViewById(R.id.act_main_vp);
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }
            @Override
            public void onPageSelected(int i) {
                //changeView(i);
            }
            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        add= (ImageView) findViewById(R.id.lin_img);
        act= (Button) findViewById(R.id.act_mian_activity);
        act.setOnClickListener(this);
        video= (Button) findViewById(R.id.act_mian_video);
        video.setOnClickListener(this);
        tiezi= (Button) findViewById(R.id.act_main_tiezi);
        tiezi.setOnClickListener(this);
        rl= (RelativeLayout) findViewById(R.id.act_main_rl);
        rl.setOnClickListener(this);
    }

    //点击选项后的颜色和图片变化
    int resImgOff[] = { R.drawable.main_home,
            R.drawable.main_video, R.drawable.main_bar,
            R.drawable.main_me};
    int resImgOn[] = { R.drawable.main_home_fill,
            R.drawable.main_video_fill, R.drawable.main_bar_fill,
            R.drawable.main_me_fill};
    private void changeView(int index) {
        for (int i = 0; i < 4; i++) {
            if (i == index) {
                tvs[i].setTextColor(Color.parseColor("#45C01A"));
                imgs[i].setImageResource(resImgOn[i]);
            } else {
                tvs[i].setTextColor(Color.parseColor("#000000"));
                imgs[i].setImageResource(resImgOff[i]);
            }
        }
    }

    //点击选项响应事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin1:
                changeView(0);
                vp.setCurrentItem(0);
                if(clicked==true){
                    clicka();
                }
                break;
            case R.id.lin2:
                //changeView(1);
                //vp.setCurrentItem(1);
                startActivity(new Intent(this,Video2.class));
                if(clicked==true){
                    clicka();
                }
                break;
            case R.id.lin3:
                changeView(2);
                vp.setCurrentItem(1);
                if(clicked==true){
                    clicka();
                }
                break;
            case R.id.lin4:
                changeView(3);
                vp.setCurrentItem(2);
                if(clicked==true){
                    clicka();
                }
                break;
            //发布
            case R.id.lin:
                clicka();
                break;
            //点击空白处发布界面消失
            case R.id.act_main_rl:
                clicka();
                break;
            //发布活动
            case R.id.act_mian_activity:
                if(clicked==true){
                    clicka();
                }
                threads(PublishAct.class);
                break;
            //发布视频
            case R.id.act_mian_video:
                if(clicked==true){
                    clicka();
                }
                threads(FFmpegRecorderActivity.class);
                break;

        }
    }

    //开启新线程，等待加号动画执行完毕再跳转
    private void threads(final Class<?> cls) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    startActivity(new Intent(MainActivity.this,cls));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //点击加号事件
    private void clicka() {
        clicked = !clicked;
        // 两个按钮的显示隐藏
        act.setVisibility(clicked ? View.VISIBLE : View.GONE);
        video.setVisibility(clicked ? View.VISIBLE : View.GONE);
        tiezi.setVisibility(clicked ? View.VISIBLE : View.GONE);
        rl.setVisibility(clicked ? View.VISIBLE : View.GONE);
        // 加号旋转
        add.startAnimation(clicked ? rotate_anticlockwise
                : rotate_clockwise);
        // 按钮显示隐藏效果
        act.startAnimation(clicked ? scale_max : scale_min);
        video.startAnimation(clicked ? scale_max : scale_min);
        tiezi.startAnimation(clicked ? scale_max : scale_min);
        // 背景色的改变
        rl.setBackgroundColor(clicked ? Color
                .parseColor("#ddffffff") : Color.TRANSPARENT);
        // 背景是否可点击，用于控制Framelayout层下面的视图是否可点击
        rl.setClickable(true);
    }


    class FmAdapter extends FragmentStatePagerAdapter {
        public FmAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int i) {
            return ls.get(i);
        }
        @Override
        public int getCount() {
            return ls.size();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (clicked==true && keyCode == KeyEvent.KEYCODE_BACK) {
            //add.performClick();
            clicka();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(NetUtil.getNetworkState(this)== NetUtil.NETWORN_NONE){
            ToastUtil.showShort(this,"网络已断开");
        }
    }
}


