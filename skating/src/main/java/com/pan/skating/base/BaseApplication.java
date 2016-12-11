package com.pan.skating.base;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.pan.skating.bean.ActBean;
import com.pan.skating.bean.PointBean;
import com.pan.skating.bean.User;

import cn.bmob.v3.Bmob;
import cn.sharesdk.framework.ShareSDK;
import cn.smssdk.SMSSDK;

/**
 * Created by Mr.潘 on 2016/8/7.
 */
public class BaseApplication extends Application {
    public static BaseApplication app;
    private User userdatas;     //用户数据
    private PointBean point;    //经纬度
    private ActBean act;        //活动信息

    public ActBean getAct() {
        return act;
    }

    public void setAct(ActBean act) {
        this.act = act;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, "4d97166cbd99fd6933adc319e3ac4a72");
        SMSSDK.initSDK(this, "13cce17d466fb", "61788f3c4b020ceaf6003a378d300286");
        ShareSDK.initSDK(this,"1792353a03520");
        SDKInitializer.initialize(getApplicationContext());
        app=this;
    }

    public User getUserdatas() {
        return userdatas;
    }

    public void setUserdatas(User userdatas) {
        this.userdatas = userdatas;
    }

    public PointBean getPoint() {
        return point;
    }

    public void setPoint(PointBean point) {
        this.point = point;
    }
}
