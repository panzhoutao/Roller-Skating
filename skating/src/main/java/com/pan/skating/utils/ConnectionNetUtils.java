package com.pan.skating.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 判断联网状态工具类
 *
 * Created by Mr.潘 on 2016/6/15.
 */
public class ConnectionNetUtils {
    //判断手机是否有网
    public static boolean isConnectionNet(Context context){
        ConnectivityManager manager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
       //获取网络状态对象
        NetworkInfo netInfo= manager.getActiveNetworkInfo();
       //判断当前网络是否联网（netInfo有可能为空）
        if(netInfo==null){
            return  false;
        }
        return netInfo.isConnected();
    }
}
