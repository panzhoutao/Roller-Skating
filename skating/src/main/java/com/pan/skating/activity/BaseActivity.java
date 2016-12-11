package com.pan.skating.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;

import com.lidroid.xutils.BitmapUtils;
import com.pan.skating.R;
import java.io.File;

/**
 * Created by 潘洲涛 on 2016/9/27.
 */
public class BaseActivity extends FragmentActivity{
    static String SDCardRoot = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + File.separator;
    static String WLGJ = SDCardRoot + ".wlgj" + File.separator;
    public static String HZD = WLGJ + ".hzd" + File.separator;
    private BitmapUtils bitmapUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBitmapUtil();
    }

    private void initBitmapUtil() {
        bitmapUtils = new BitmapUtils(this, HZD);
        bitmapUtils.configDiskCacheEnabled(true);
        bitmapUtils.configDefaultLoadFailedImage(R.drawable.logo);
    }
}
