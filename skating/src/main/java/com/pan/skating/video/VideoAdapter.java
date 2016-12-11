package com.pan.skating.video;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
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
import com.pan.skating.bean.VideoBean;
import com.pan.skating.utils.ImageLoaderUtils;

import java.util.List;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;

import io.vov.vitamio.widget.VideoView;

/**
 * Created by 潘洲涛 on 2016/10/13.
 */

public class VideoAdapter extends BaseAdapter {

    private Activity context;
    List<VideoBean> data;
    private ImageLoader loader;
    private DisplayImageOptions options;
    private List<VideoBean> videoUrls;
    private VideoFrameImageLoader mVideoFrameImageLoader;


    public VideoAdapter(Activity context, List<VideoBean> data, VideoFrameImageLoader vfi) {
        this.context=context;
        this.data=data;
        this.mVideoFrameImageLoader=vfi;
        this.videoUrls=vfi.getVideoUrls();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        String mImageUrl = videoUrls.get(position).getVideo().getUrl();
        if(convertView==null){
            if (!LibsChecker.checkVitamioLibs(context))
                return null;
            convertView= LayoutInflater.from(context).inflate(R.layout.video_item,null);
            //初始化显示数据
            mVideoFrameImageLoader.initList();
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }

        //设置背景图片
        loader = ImageLoaderUtils.getInstance(context);
        options = ImageLoaderUtils.getOpt();
        loader.displayImage(data.get(position).getAuthor().getHead()
                .getFileUrl(context),viewHolder.head, options);
        viewHolder.user.setText(data.get(position).getAuthor().getUsername());

        //设置视频的第一帧图片
        Bitmap bitmap=mVideoFrameImageLoader.showCacheBitmap(VideoFrameImageLoader.formatVideoUrl(mImageUrl));
        viewHolder.pic.setImageBitmap(bitmap);

        viewHolder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.videoView.stopPlayback();
                viewHolder.play.setVisibility(View.GONE);
                viewHolder.pic.setVisibility(View.GONE);
                //视频播放
                viewHolder.videoView.setVideoPath(data.get(position).getVideo().getUrl());//播放地址
                viewHolder.videoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);//设置播放画质（高画质）
                viewHolder.videoView.requestFocus();//获取焦点
            }
        });

        //设置暂停后的事件
        pause(viewHolder);

        return convertView;
    }

    private void pause(final ViewHolder viewHolder) {
        viewHolder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                viewHolder.play.setVisibility(View.VISIBLE);
                viewHolder.videoView.stopPlayback();
            }
        });

    }

    class ViewHolder{
        @ViewInject(R.id.video_item_video)
        private VideoView videoView;
        @ViewInject(R.id.video_item_head)
        private ImageView head;
        @ViewInject(R.id.video_item_publishuser)
        private TextView user;
        @ViewInject(R.id.video_item_PV)
        private TextView pv;
        @ViewInject(R.id.video_item_play)
        private ImageView play;
        @ViewInject(R.id.video_item_pic)
        private ImageView pic;

        ViewHolder(View view) {
            ViewUtils.inject(this,view);
        }
    }

}
