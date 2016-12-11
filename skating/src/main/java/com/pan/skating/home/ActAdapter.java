package com.pan.skating.home;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pan.skating.R;
import com.pan.skating.base.BaseApplication;
import com.pan.skating.bean.ActBean;
import com.pan.skating.utils.ImageLoaderUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.datatype.BmobGeoPoint;


/**
 * Created by 潘洲涛 on 2016/9/20.
 */
public class ActAdapter extends BaseAdapter {
    private Context context;
    private List<ActBean> data;
    private ImageLoader loader;
    private DisplayImageOptions options;
    private int time=0;     //时间差
    private TextView timeCount;

    public ActAdapter(Context context, List<ActBean> data) {
        this.context=context;
        this.data=data;

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.act_item,null);
            timeCount= (TextView) view.findViewById(R.id.act_item_count);
            viewHolder=new ViewHolder(view);
            view.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) view.getTag();
        }

        Double dis=data.get(i).getPoint()
                .distanceInKilometersTo(new BmobGeoPoint(BaseApplication.app.getPoint().getLon()
                        ,BaseApplication.app.getPoint().getLat()));
        int ii = (int) (dis*100);
        float dis1 = ((float)ii)/100;

        viewHolder.title.setText(data.get(i).getName());
        viewHolder.time.setText(data.get(i).getStarttime());
        viewHolder.address.setText(data.get(i).getLocation());
        viewHolder.distance.setText(dis1+"km");

        //设置背景图片
        loader = ImageLoaderUtils.getInstance(context);
        options = ImageLoaderUtils.getOpt();
        loader.displayImage(data.get(i).getPic().get(0)
                .getFileUrl(context),viewHolder.pic, options);

        time = getTimeInterval(data.get(i).getStarttime());       // 获取开始活动时间差
        if(time>0){
            timeCount.setText("可以参加");
        }else{
            timeCount.setText("停止报名");
        }

        return view;
    }

    class ViewHolder{
        @ViewInject(R.id.act_item_title)
                private TextView title;
        @ViewInject(R.id.act_item_time)
                private TextView time;
        @ViewInject(R.id.act_item_address)
                private TextView address;
        @ViewInject(R.id.act_item_distance)
                private TextView distance;
        @ViewInject(R.id.act_item_pic)
                private ImageView pic;

        ViewHolder(View view) {
            ViewUtils.inject(this,view);
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
//
//    class TimeCount implements Runnable
//    {
//        @Override
//        public void run()
//        {
//            while (time > 0)// 整个倒计时执行的循环
//            {
//                time--;
//                mHandler.post(new Runnable() // 通过它在UI主线程中修改显示的剩余时间
//                {
//                    public void run()
//                    {
//                        timeCount.setText(getInterval(time));// 显示剩余时间
//                    }
//                });
//                try
//                {
//                    Thread.sleep(1000); // 线程休眠一秒钟 这个就是倒计时的间隔时间
//                } catch (InterruptedException e)
//                {
//                    e.printStackTrace();
//                }
//            }
//            // 下面是倒计时结束逻辑
//            mHandler.post(new Runnable()
//            {
//                @Override
//                public void run()
//                {
//                    timeCount.setText("已停止报名");
//                }
//            });
//        }
//    }
//
//
//    /**
//     * 设定显示文字
//     */
//    public static String getInterval(int time)
//    {
//        String txt = null;
//        if (time >= 0)
//        {
//            long day = time / (24 * 3600);// 天
//            long hour = time % (24 * 3600) / 3600;// 小时
//            long minute = time % 3600 / 60;// 分钟
//            long second = time % 60;// 秒
//
//            txt =day + "天" + hour + "小时" + minute + "分" + second + "秒";
//        }
//        else
//        {
//            txt="已停止报名";
//        }
//        return txt;
//    }
}
