package com.pan.skating.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import com.pan.skating.base.BaseApplication;
import com.pan.skating.bean.ActBean;
import com.pan.skating.bean.PointBean;
import com.pan.skating.utils.MyPointUtils;
import com.pan.skating.utils.NetUtil;
import com.pan.skating.utils.ToastUtil;
import java.util.List;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by 潘洲涛 on 2016/9/19.
 */
public class HomeTask extends AsyncTask<String,Void,ActBean>{
    private Context context;
    private ProgressDialog progressDialog;
    private PointBean point=new PointBean();
    protected ActInterface actInterface;
    private View view;

    public HomeTask(Context context, ActInterface actInterface, View view) {
        this.context = context;
        this.actInterface=actInterface;
        this.view=view;
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
            //获取到我现在的位置
            MyPointUtils.getPoint(context,view);
        }
    }

    @Override
    protected ActBean doInBackground(String... strings) {
        for(int i=0;i<5;i++){
            point=BaseApplication.app.getPoint();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(point!=null){
                i=5;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(ActBean data1) {
        super.onPostExecute(data1);
        //获取活动列表
        BmobQuery<ActBean> query = new BmobQuery<ActBean>("ActBean");
        query.order("-createdAt");
        query.include("author");
        query.findObjects(context, new FindListener<ActBean>() {
            @Override
            public void onSuccess(final List<ActBean> list) {

                if(actInterface!=null){
                    actInterface.callBackAct(list);
                }
                progressDialog.dismiss();
            }
            @Override
            public void onError(int i, String s) {
            }
        });
    }
}
