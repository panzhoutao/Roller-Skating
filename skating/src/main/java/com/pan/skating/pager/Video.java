package com.pan.skating.pager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.pan.skating.R;
import com.pan.skating.bean.VideoBean;
import com.pan.skating.utils.NetUtil;
import com.pan.skating.utils.ToastUtil;
import com.pan.skating.video.VideoAdapter;
import com.pan.skating.video.VideoFrameImageLoader;
import com.pan.skating.video.VideoInterface;
import com.pan.skating.video.VideoTask;
import com.pan.skating.view.PullToRefreshView;
import com.superplayer.library.SuperPlayer;
import com.superplayer.library.SuperPlayerManage;

import java.util.List;

import butterknife.BindView;

public class Video extends Fragment implements VideoInterface,PullToRefreshView.OnHeaderRefreshListener,PullToRefreshView.OnFooterRefreshListener{

    private View view;

    private ListView listView;
    private PullToRefreshView mPullToRefreshView;
    private boolean isFirst=true;
    private VideoTask task;
    List<VideoBean> data;
    private VideoAdapter adapter;
    private VideoFrameImageLoader mVideoFrameImageLoader;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_video, container, false);
        listView= (ListView) view.findViewById(R.id.fm_list);
        //上下拉刷新
        mPullToRefreshView = (PullToRefreshView)view.findViewById(R.id.main_pull_refresh_view);
        mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //fragment可见时执行加载数据或者进度条等
            getData();
        } else {
            //不可见时不执行操作

        }
    }

    private void getData() {
        if(isFirst){
            task=new VideoTask(getActivity(),this);
            task.execute();
            isFirst=false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(NetUtil.getNetworkState(getActivity())== NetUtil.NETWORN_NONE){
            ToastUtil.showShort(getActivity(),"网络已断开");
        }
        if(!isFirst){
            listView.setAdapter(adapter);
        }
    }

    //取到数据的回调
    @Override
    public void callBackVideo(final List<VideoBean> list) {
        if(list!=null){
            data=list;
            mVideoFrameImageLoader = new VideoFrameImageLoader(getActivity(), listView,list);
            adapter=new VideoAdapter(getActivity(),list,mVideoFrameImageLoader);
            listView.setAdapter(adapter);
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
                adapter.notifyDataSetInvalidated();
                //设置更新时间
                mPullToRefreshView.onHeaderRefreshComplete();
                isFirst=false;
            }
        },1000);
    }

}
