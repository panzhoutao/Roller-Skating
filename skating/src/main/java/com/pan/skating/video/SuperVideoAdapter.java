package com.pan.skating.video;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pan.skating.R;
import com.pan.skating.bean.VideoBean;
import com.pan.skating.utils.ImageLoaderUtils;
import com.superplayer.library.utils.SuperPlayerUtils;

import java.util.List;

import butterknife.BindView;

/**
 *  VideoAdapter
 */

public class SuperVideoAdapter extends RecyclerView.Adapter<SuperVideoAdapter.VideoViewHolder> {
    private final Context mContext;

    private List<VideoBean> dataList;
    private VideoFrameImageLoader mVideoFrameImageLoader;
    private List<VideoBean> videoUrls;
    private String mImageUrl;
    private ImageLoader loader;
    private DisplayImageOptions options;

    public SuperVideoAdapter(Context context, List<VideoBean> dataList,VideoFrameImageLoader vfi) {
        this.mContext = context;
        this.dataList = dataList;
        this.mVideoFrameImageLoader = vfi;
        this.videoUrls = vfi.getVideoUrls();
    }

    @Override
    public SuperVideoAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.video_listview_layout, null);
        VideoViewHolder holder = new VideoViewHolder(view);
        view.setTag(holder);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SuperVideoAdapter.VideoViewHolder holder, int position) {
        holder.update(position);
        mImageUrl = videoUrls.get(position).getVideo().getUrl();
        //设置视频的第一帧图片
        Bitmap bitmap=mVideoFrameImageLoader.showCacheBitmap(VideoFrameImageLoader.formatVideoUrl(mImageUrl));
        holder.pic.setImageBitmap(bitmap);
        loader = ImageLoaderUtils.getInstance(mContext);
        options = ImageLoaderUtils.getOpt();
        loader.displayImage(dataList.get(position).getAuthor().getHead()
                .getFileUrl(mContext),holder.head, options);
        holder.user.setText(dataList.get(position).getAuthor().getUsername());
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout rlayPlayerControl;
        private RelativeLayout rlayPlayer;
        private ImageView pic,head;
        private TextView user;

        public VideoViewHolder(View itemView) {
            super(itemView);
            user = (TextView) itemView.findViewById(R.id.video_item_user);
            head = (ImageView) itemView.findViewById(R.id.imageView);
            rlayPlayerControl = (RelativeLayout) itemView.findViewById(R.id.adapter_player_control);
            rlayPlayer = (RelativeLayout) itemView.findViewById(R.id.adapter_super_video_layout);
            pic = (ImageView)itemView.findViewById(R.id.adapter_super_video_iv_cover);
            if (rlayPlayer!=null){
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rlayPlayer.getLayoutParams();
                layoutParams.height = (int) (SuperPlayerUtils.getScreenWidth((Activity) mContext) * 0.5652f);//这值是网上抄来的，我设置了这个之后就没有全屏回来拉伸的效果，具体为什么我也不太清楚
                rlayPlayer.setLayoutParams(layoutParams);
            }
        }

        public void update(final int position) {
            //点击回调 播放视频
            rlayPlayerControl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playclick != null)
                        playclick.onPlayclick(position, rlayPlayerControl);
                }
            });
        }
    }

    private onPlayClick playclick;

    public void setPlayClick(onPlayClick playclick) {
        this.playclick = playclick;
    }

    public interface onPlayClick {
        void onPlayclick(int position, RelativeLayout image);
    }

}
