package com.pan.skating.video;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.pan.skating.bean.VideoBean;
import com.pan.skating.utils.NetUtil;
import com.pan.skating.utils.ToastUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by 潘洲涛 on 2016/10/13.
 */

public class VideoTask extends AsyncTask {

    private Context context;
    private VideoInterface videoInterface;
    private ProgressDialog progressDialog;

    public VideoTask(Context context, VideoInterface videoInterface) {
        this.context = context;
        this.videoInterface=videoInterface;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(NetUtil.getNetworkState(context)== NetUtil.NETWORN_NONE){
            ToastUtil.showShort(context,"网络已断开");
            cancel(true);
        }else{
            progressDialog=new ProgressDialog(context);
            progressDialog.setMessage("正在加载数据");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    @Override
    protected Object doInBackground(Object[] params) {

        for(int i=0;i<3;i++){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        //获取活动列表
        BmobQuery<VideoBean> query = new BmobQuery<VideoBean>("ActBean");
        query.order("-createdAt");
        query.include("author");
        query.findObjects(context, new FindListener<VideoBean>() {
            @Override
            public void onSuccess(final List<VideoBean> list) {
                if(videoInterface!=null){
                    videoInterface.callBackVideo(list);
                }
                progressDialog.dismiss();
            }
            @Override
            public void onError(int i, String s) {
            }
        });
    }
}
