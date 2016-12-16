package com.pan.skating.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.pan.skating.R;
import com.pan.skating.bean.FindBean;
import com.pan.skating.pager.Find;
import com.pan.skating.pager.Video2;
import com.pan.skating.tieba.TiebaActivity;
import com.pan.skating.zhibo.ZhiBoActivity;

import java.util.List;

/**
 * Created by 潘洲涛 on 2016/10/18.
 */

public class FindAdapter extends RecyclerView.Adapter{
    private List<FindBean> list;
    private Context context;

    public FindAdapter(List<FindBean> list, Context activity) {
        this.list=list;
        this.context=activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HalderViewHolder(LayoutInflater.from(context).inflate(R.layout.find_item,null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((HalderViewHolder)holder).iv.setImageResource(list.get(position).getHead());
        ((HalderViewHolder)holder).tv.setText(list.get(position).getName());
        ((HalderViewHolder)holder).item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(position).getName().equals("贴吧")){
                    context.startActivity(new Intent(context, TiebaActivity.class));
                }else{
                    //Toast.makeText(context,"直播",Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, Video2.class));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class HalderViewHolder extends RecyclerView.ViewHolder{
        @ViewInject(R.id.find_item_iv)
        private ImageView iv;
        @ViewInject(R.id.find_item_tv)
        private TextView tv;
        @ViewInject(R.id.find_item_item)
        private LinearLayout item;

        public HalderViewHolder(View itemView) {
            super(itemView);
            ViewUtils.inject(this,itemView);
        }

    }
}
