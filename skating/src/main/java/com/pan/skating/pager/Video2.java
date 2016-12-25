package com.pan.skating.pager;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pan.skating.R;
import com.pan.skating.bean.VideoBean;
import com.pan.skating.utils.NetUtil;
import com.pan.skating.utils.ToastUtil;
import com.pan.skating.video.SuperVideoAdapter;
import com.pan.skating.video.VideoFrameImageLoader;
import com.pan.skating.video.VideoInterface;
import com.pan.skating.video.VideoTask;
import com.pan.skating.view.PullToRefreshView;
import com.superplayer.library.SuperPlayer;
import com.superplayer.library.SuperPlayerManage;
import com.superplayer.library.mediaplayer.IjkVideoView;
import com.superrecycleview.superlibrary.recycleview.SuperRecyclerView;

import java.util.List;

/**
 *
 * 类描述：视频列表播放页面
 *
 * @author Super南仔
 * @time 2016-9-19
 */
public class Video2 extends Activity implements VideoInterface,PullToRefreshView.OnHeaderRefreshListener,PullToRefreshView.OnFooterRefreshListener{

    private RecyclerView superRecyclerView;
    private SuperVideoAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private SuperPlayer player;
    private int postion = -1;
    private int lastPostion = -1;
    private RelativeLayout fullScreen;
    //---------------------
    private PullToRefreshView mPullToRefreshView;
    private boolean isFirst=true;
    private VideoTask task;
    List<VideoBean> data;
    private VideoFrameImageLoader mVideoFrameImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_recycleview_super_vido_layout);

        //上下拉刷新
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
        mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);

        getData();
        initPlayer();
        //setData();

        initView();

    }


    /**
     * 初始化播放器
     */
    private void initPlayer() {
        player = SuperPlayerManage.getSuperManage().initialize(this);
        player.setShowTopControl(false).setSupportGesture(false);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        superRecyclerView = (RecyclerView) findViewById(R.id.act_recycle_super_video_recycleview);
        fullScreen = (RelativeLayout) findViewById(R.id.full_screen);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        superRecyclerView.setLayoutManager(mLayoutManager);
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
//        mAdapter = new SuperVideoAdapter(this,dataList);
//        superRecyclerView.setAdapter(mAdapter);
        mAdapter.setPlayClick(new SuperVideoAdapter.onPlayClick() {
            @Override
            public void onPlayclick(int position, RelativeLayout image) {
                image.setVisibility(View.GONE);
                if (player.isPlaying() && lastPostion == position){
                    return;
                }

                postion = position;
                if (player.getVideoStatus() == IjkVideoView.STATE_PAUSED) {
                    if (position != lastPostion) {
                        player.stopPlayVideo();
                        player.release();
                    }
                }
                if (lastPostion != -1) {
                    player.showView(R.id.adapter_player_control);
                }

                View view = superRecyclerView.findViewHolderForAdapterPosition(position).itemView;
                FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.adapter_super_video);
                frameLayout.removeAllViews();
                player.showView(R.id.adapter_player_control);
                frameLayout.addView(player);
                player.play(data.get(position).getVideo().getUrl());
                Toast.makeText(Video2.this, "position:"+position, Toast.LENGTH_SHORT).show();
                lastPostion = position;
            }
        });
        /**
         * 播放完设置还原播放界面
         */
        player.onComplete(new Runnable() {
            @Override
            public void run() {
                ViewGroup last = (ViewGroup) player.getParent();//找到videoitemview的父类，然后remove
                if (last != null && last.getChildCount() > 0) {
                    last.removeAllViews();
                    View itemView = (View) last.getParent();
                    if (itemView != null) {
                        itemView.findViewById(R.id.adapter_player_control).setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        /***
         * 监听列表的下拉滑动
         */
        superRecyclerView.addOnChildAttachStateChangeListener(new SuperRecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                int index = superRecyclerView.getChildAdapterPosition(view);
                View controlview = view.findViewById(R.id.adapter_player_control);
                if (controlview == null) {
                    return;
                }
                view.findViewById(R.id.adapter_player_control).setVisibility(View.VISIBLE);
                if (index == postion) {
                    FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.adapter_super_video);
                    frameLayout.removeAllViews();
                    if (player != null &&
                            ((player.isPlaying()) || player.getVideoStatus() == IjkVideoView.STATE_PAUSED)) {
                        view.findViewById(R.id.adapter_player_control).setVisibility(View.GONE);
                    }
                    if (player.getVideoStatus() == IjkVideoView.STATE_PAUSED) {
                        if (player.getParent() != null)
                            ((ViewGroup) player.getParent()).removeAllViews();
                        frameLayout.addView(player);
                        return;
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                int index = superRecyclerView.getChildAdapterPosition(view);
                if ((index) == postion) {
                    if (true) {
                        if (player != null) {
                            player.stop();
                            player.release();
                            player.showView(R.id.adapter_player_control);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (player != null) {
            player.onConfigurationChanged(newConfig);
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                fullScreen.setVisibility(View.GONE);
                fullScreen.removeAllViews();
                superRecyclerView.setVisibility(View.VISIBLE);
                if (postion <= mLayoutManager.findLastVisibleItemPosition()
                        && postion >= mLayoutManager.findFirstVisibleItemPosition()) {
                    View view = superRecyclerView.findViewHolderForAdapterPosition(postion).itemView;
                    FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.adapter_super_video);
                    frameLayout.removeAllViews();
                    ViewGroup last = (ViewGroup) player.getParent();//找到videoitemview的父类，然后remove
                    if (last != null) {
                        last.removeAllViews();
                    }
                    frameLayout.addView(player);
                }
                int mShowFlags =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                fullScreen.setSystemUiVisibility(mShowFlags);
            } else {
                ViewGroup viewGroup = (ViewGroup) player.getParent();
                if (viewGroup == null)
                    return;
                viewGroup.removeAllViews();
                fullScreen.addView(player);
                fullScreen.setVisibility(View.VISIBLE);
                int mHideFlags =
                        View.SYSTEM_UI_FLAG_LOW_PROFILE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        ;
                fullScreen.setSystemUiVisibility(mHideFlags);
            }
        } else {
            fullScreen.setVisibility(View.GONE);
        }
    }


    /**
     * 下面的这几个Activity的生命状态很重要
     */

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }

        if(NetUtil.getNetworkState(this)== NetUtil.NETWORN_NONE){
            ToastUtil.showShort(this,"网络已断开");
        }
        if(!isFirst){
            superRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.onDestroy();

        }
    }

    //在全屏时返回列表
    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }


    //-------------------------------------------------------------------------------


    private void getData() {
        if(isFirst){
            task=new VideoTask(Video2.this,this);
            task.execute();
            isFirst=false;
        }
    }

    //取到数据的回调
    @Override
    public void callBackVideo(final List<VideoBean> list) {
        if(list!=null){
            data=list;
            mVideoFrameImageLoader = new VideoFrameImageLoader(this, superRecyclerView,list);
//            adapter=new VideoAdapter(this,list,mVideoFrameImageLoader);
//            listView.setAdapter(adapter);

            mAdapter = new SuperVideoAdapter(this,data);
            superRecyclerView.setAdapter(mAdapter);

            initAdapter();
        }
    }

    @Override
    public void onFooterRefresh(PullToRefreshView view) {

    }

    //下拉
    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        mPullToRefreshView.postDelayed(new Runnable() {
            @Override
            public void run() {
                isFirst=true;
                getData();
                //刷新适配器
                mAdapter.notifyDataSetChanged();
                //设置更新时间
                mPullToRefreshView.onHeaderRefreshComplete();
                isFirst=false;
            }
        },1000);
    }

}
