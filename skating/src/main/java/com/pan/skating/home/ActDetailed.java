package com.pan.skating.home;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pan.skating.R;
import com.pan.skating.base.BaseApplication;
import com.pan.skating.bean.ActBean;
import com.pan.skating.interfaces.BaseInterface;
import com.pan.skating.utils.ImageLoaderUtils;
import com.pan.skating.view.SlideShowView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;

public class ActDetailed extends AppCompatActivity implements BaseInterface,View.OnClickListener{
    @ViewInject(R.id.title_back)
    private ImageView back;
    @ViewInject(R.id.title_center)
    private TextView title;
    @ViewInject(R.id.title_right)
    private TextView share;
    @ViewInject(R.id.act_detailed_name)
    private TextView name;
    @ViewInject(R.id.act_detailed_head)
    private ImageView head;
    @ViewInject(R.id.act_detailed_publishuser)
    private TextView publishuser;
    @ViewInject(R.id.act_detailed_introduce)
    private TextView introduce;
    @ViewInject(R.id.act_detailed_time)
    private TextView time;
    @ViewInject(R.id.act_detailed_location)
    private TextView location;
    @ViewInject(R.id.act_detailed_contact)
    private TextView contact;
    @ViewInject(R.id.act_detailed_xuzhi)
    private TextView xuzhi;
    @ViewInject(R.id.act_detailed_locationmap)
    private RelativeLayout locationmap;
    @ViewInject(R.id.act_detailed_slide)
    private SlideShowView slide;
    @ViewInject(R.id.act_detailed_count)
    private TextView timeCount;
    @ViewInject(R.id.act_detailed_PV)
    private TextView pv;

    private ActBean data;
    private ImageLoader loader;
    private DisplayImageOptions options;
    private ArrayList<String> imageURLlist = new ArrayList<String>();
    private int timec=0;     //时间差
    private Handler mHandler = new Handler();// 全局handler
    private ProgressDialog progressDialog;
    private boolean is=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_detailed);
        setbarColor();
        ViewUtils.inject(this);
        getimagelist();
        initViews();
        initDatas();
        initOpers();
    }
    //设置沉浸式
    @TargetApi(19)
    void setbarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setStatusBarColor(this.getResources().getColor(R.color.title));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    private void getimagelist() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<BaseApplication.app.getAct().getPic().size();i++){
                    imageURLlist.add(BaseApplication.app.getAct().getPic().get(i).getUrl());
                }
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (imageURLlist.size() == 0) {
                    imageURLlist.add("");
                }
                slide.setlistimage(imageURLlist, ActDetailed.this);
            }
        }
    };

    @Override
    public void initViews() {
        title.setText("活动详情");
        share.setText("分享");
        back.setOnClickListener(this);
        locationmap.setOnClickListener(this);
        share.setOnClickListener(this);
    }

    @Override
    public void initDatas() {
        data=BaseApplication.app.getAct();
        //浏览量自增
        ActBean act=new ActBean();
        act.increment("PV");
        act.update(this, data.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

        progressDialog=new ProgressDialog(ActDetailed.this);
        progressDialog.setMessage("正在加载数据");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        BmobQuery<ActBean> query=new BmobQuery<ActBean>();
        query.addQueryKeys("PV");
        query.getObject(this, data.getObjectId(), new GetListener<ActBean>() {
            @Override
            public void onSuccess(ActBean actBean) {
                pv.setText(actBean.getPV().toString());
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
        progressDialog.dismiss();

    }

    @Override
    public void initOpers() {
        name.setText(data.getName());
        if(data.getAuthor().getHead()!=null){
            loader = ImageLoaderUtils.getInstance(this);
            options = ImageLoaderUtils.getOpt();
            loader.displayImage(data.getAuthor().getHead().getFileUrl(this),head, options);
        }
        publishuser.setText(data.getAuthor().getUsername());
        introduce.setText(data.getIntroduce());
        time.setText(data.getStarttime()+" - "+data.getEndtime());
        location.setText(data.getLocation());
        contact.setText(data.getContact());
        xuzhi.setText(data.getXuzhi());

        timec = getTimeInterval(data.getStarttime());       // 获取时间差

        new Thread(new TimeCount()).start();// 开启线程
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.act_detailed_locationmap:
                Intent intent=new Intent(this,ActMap.class);
                startActivity(intent);
                break;
            case R.id.title_right:
                showShare(this, null, false);
                break;
        }
    }

    /**
     * 获取两个日期的时间差
     */
    public static int getTimeInterval(String data)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        int interval = 0;
        try
        {
            Date currentTime = new Date();// 获取现在的时间
            Date beginTime = dateFormat.parse(data);
            interval = (int) ((beginTime.getTime() - currentTime.getTime()) / (1000));// 时间差 单位秒
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return interval;
    }

    class TimeCount implements Runnable
    {
        @Override
        public void run()
        {
            while (timec > 0)// 整个倒计时执行的循环
            {
                timec--;
                mHandler.post(new Runnable() // 通过它在UI主线程中修改显示的剩余时间
                {
                    public void run()
                    {
                        timeCount.setText(getInterval(timec));// 显示剩余时间
                    }
                });
                try
                {
                    Thread.sleep(1000); // 线程休眠一秒钟 这个就是倒计时的间隔时间
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            // 下面是倒计时结束逻辑
            mHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    timeCount.setText("已停止报名");
                }
            });
        }
    }


    /**
     * 设定显示文字
     */
    public static String getInterval(int time)
    {
        String txt = null;
        if (time >= 0)
        {
            long day = time / (24 * 3600);// 天
            long hour = time % (24 * 3600) / 3600;// 小时
            long minute = time % 3600 / 60;// 分钟
            long second = time % 60;// 秒

            txt ="截止参与时间："+day + "天" + hour + "小时" + minute + "分" + second + "秒";
        }
        else
        {
            txt="已停止报名";
        }
        return txt;
    }


    /**
     * 调用ShareSDK执行分享
     *
     * @param context
     * @param platformToShare  指定直接分享平台名称（一旦设置了平台名称，则九宫格将不会显示）
     * @param showContentEdit  是否显示编辑页
     */
    public void showShare(Context context, String platformToShare, boolean showContentEdit) {
        OnekeyShare oks = new OnekeyShare();
        oks.setSilent(!showContentEdit);
        if (platformToShare != null) {
            oks.setPlatform(platformToShare);
        }
        //ShareSDK快捷分享提供两个界面第一个是九宫格 CLASSIC  第二个是SKYBLUE
        oks.setTheme(OnekeyShareTheme.CLASSIC);
        // 令编辑页面显示为Dialog模式
        oks.setDialogMode();
        // 在自动授权时可以禁用SSO方式
        oks.disableSSOWhenAuthorize();
        oks.setTitle(data.getName());
        oks.setTitleUrl("http://mob.com");
        oks.setText("请下载轮滑社查看最新活动");
        oks.setImageUrl(data.getPic().get(0).getUrl());
        oks.setUrl("http://www.mob.com"); //微信不绕过审核分享链接
        //oks.setFilePath("/sdcard/test-pic.jpg");  //filePath是待分享应用程序的本地路劲，仅在微信（易信）好友和Dropbox中使用，否则可以不提供
        oks.setComment("分享"); //我对这条分享的评论，仅在人人网和QQ空间使用，否则可以不提供
        oks.setSite("ShareSDK");  //QZone分享完之后返回应用时提示框上显示的名称
        oks.setSiteUrl("http://mob.com");//QZone分享参数
        oks.setVenueName("ShareSDK");
        oks.setVenueDescription("This is a beautiful place!");
        oks.show(context);
    }
}
