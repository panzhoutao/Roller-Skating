package com.pan.skating.utils;


import android.content.Context;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.pan.skating.R;

import java.io.File;

public class ImageLoaderUtils {
    private static ImageLoader loader;
    private static ImageLoaderConfiguration.Builder confbuilder;
    private static ImageLoaderConfiguration conf;
    private static DisplayImageOptions.Builder optbuilder;
    private static DisplayImageOptions opt;

    // 返回注册号的imageloader对象
    /**
     * 双缓存机制
     *
     * @param context
     * @return
     */
    public static ImageLoader getInstance(Context context) {

        loader = ImageLoader.getInstance();
        confbuilder = new ImageLoaderConfiguration.Builder(context);
        File file = new File("/mnt/sdcard/userhead/imageloader");
        // 制定sdcard缓存的路径
        confbuilder.discCache(new UnlimitedDiscCache(file));
        // 缓存的图片个数
        confbuilder.discCacheFileCount(100);
        // 缓存的最大容量
        confbuilder.discCacheSize(1024 * 1024 * 20 * 10);
        conf = confbuilder.build();
        loader.init(conf);
        return loader;
    }

    // 返回显示图片使得opt(listview 里面的活动的返回图片)
    public static DisplayImageOptions getOpt() {
        optbuilder = new DisplayImageOptions.Builder();
        // uri 加载默认图片
        optbuilder.showImageForEmptyUri(R.drawable.myhand);
        // 图片获取失败 加载的默认图片
        optbuilder.showImageOnFail(R.drawable.myhand);
        optbuilder.cacheInMemory(true);// 做内存缓存
        optbuilder.cacheOnDisc(true);// 做硬盘缓存
        opt = optbuilder.build();
        return opt;
    }

    // 返回显示图片使得opt ListView中 用户的头像（活动详情里面的用户头像）
    public static DisplayImageOptions getOpt2() {
        optbuilder = new DisplayImageOptions.Builder();
        // uri 加载默认图片
        optbuilder.showImageForEmptyUri(R.drawable.myhand);
        // 图片获取失败 加载的默认图片
        optbuilder.showImageOnFail(R.drawable.myhand);
        optbuilder.cacheInMemory(true);// 做内存缓存
        optbuilder.cacheOnDisc(true);// 做硬盘缓存
        opt = optbuilder.build();
        return opt;
    }
}
