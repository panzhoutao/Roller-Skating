package com.pan.skating.pager;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.pan.skating.R;
import com.pan.skating.home.ActAdapter;
import com.pan.skating.base.BaseApplication;
import com.pan.skating.bean.ActBean;
import com.pan.skating.home.ActDetailed;
import com.pan.skating.home.ActInterface;
import com.pan.skating.home.HomeTask;
import com.pan.skating.utils.NetUtil;
import com.pan.skating.utils.ToastUtil;
import com.pan.skating.view.PullToRefreshView;
import java.util.List;

public class Home extends Fragment implements ActInterface,PullToRefreshView.OnHeaderRefreshListener,PullToRefreshView.OnFooterRefreshListener{
    private View view;
    private HomeTask task;
    private ListView listView;
    private Boolean isFirst=true;
    List<ActBean> data;
    private ActAdapter adapter;
    private PullToRefreshView mPullToRefreshView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_home, container, false);
        listView= (ListView) view.findViewById(R.id.fm_list);
        //上下拉刷新
        mPullToRefreshView = (PullToRefreshView)view.findViewById(R.id.main_pull_refresh_view);
        mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        getData();//获取数据
        itemClick();
        return view;
    }

    private void getData() {
        if(isFirst){
            task=new HomeTask(getActivity(),this,view);
            task.execute();
            isFirst=false;
        }
    }

    private void itemClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BaseApplication.app.setAct(data.get(position));
                startActivity(new Intent(getActivity(),ActDetailed.class));
            }
        });
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

    @Override
    public void callBackAct(List<ActBean> list) {
        if(list!=null && BaseApplication.app.getPoint()!=null){
            data=list;
            adapter=new ActAdapter(getActivity(),list);
            listView.setAdapter(adapter);
        }
    }

    //上拉
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
