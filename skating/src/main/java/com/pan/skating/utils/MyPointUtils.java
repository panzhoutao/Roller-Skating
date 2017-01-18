package com.pan.skating.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.pan.skating.base.BaseApplication;
import com.pan.skating.bean.PointBean;

/**
 * Created by 潘洲涛 on 2016/9/19.
 */
public class MyPointUtils extends Activity{

    private static String PERMISSIONS_CONTACT=Manifest.permission.ACCESS_COARSE_LOCATION;
    private static Context contet;
    private static View v;
    private static LocationClient mLocClient;
    public static MyLocationListenner myListener = new MyLocationListenner();
    static boolean isFirstLoc = true;//是否首次定位
    private static LatLng ll;
    private static PointBean dis=new PointBean();
    private static int REQUEST_CONTACTS=0;

    public static PointBean getPoint(Context context,View view){
        v=view;
        contet=context;
        if (Build.VERSION.SDK_INT>=23){
            showContacts(context);
        }else{
            init(context);
        }
        return null;
    }

    private static void init(Context context){
        //定位
        mLocClient = new LocationClient(context);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /**
     * 定位SDK监听函数
     */
    public static class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            if (isFirstLoc) {
                isFirstLoc = false;
                ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                dis.setLat(ll.latitude);
                dis.setLon(ll.longitude);
                BaseApplication.app.setPoint(dis);
            }
        }
    }


    public static void showContacts(Context context) {
        Log.i("aa", "Show contacts button pressed. Checking permissions.");

        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.
            Log.i("aa", "Contact permissions has NOT been granted. Requesting permissions.");
            requestContactsPermissions();

        } else {

            // Contact permissions have been granted. Show the contacts fragment.
            Log.i("aa", "Contact permissions have already been granted. Displaying contact details.");
            init(context);
        }
    }

    private static void requestContactsPermissions() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) contet,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale((Activity) contet,
                Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale((Activity) contet,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale((Activity) contet,
                Manifest.permission.READ_PHONE_STATE)
                ) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i("aa", "Displaying contacts permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(v, "permission_contacts_rationale",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions((Activity)contet, new String[]{PERMISSIONS_CONTACT},
                                            REQUEST_CONTACTS);
                        }
                    })
                    .show();
        } else {
            // Contact permissions have not been granted yet. Request them directly.

            ActivityCompat.requestPermissions((Activity) contet, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CONTACTS);


        }
        // END_INCLUDE(contacts_permission_request)

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults) {
        if (requestCode==REQUEST_CONTACTS){
            if (PermissionUtil.verifyPermissions(grantResults)) {

                init(contet);
            } else {

            }

        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Utility class that wraps access to the runtime permissions API in M and provides basic helper
     * methods.
     */
    public abstract static class PermissionUtil {

        /**
         * Check that all given permissions have been granted by verifying that each entry in the
         * given array is of the value {@link PackageManager#PERMISSION_GRANTED}.
         *
         * @see Activity#onRequestPermissionsResult(int, String[], int[])
         */
        public static boolean verifyPermissions(int[] grantResults) {
            // At least one result must be checked.
            if(grantResults.length < 1){
                return false;
            }

            // Verify that each required permission has been granted, otherwise return false.
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }

    }
}
