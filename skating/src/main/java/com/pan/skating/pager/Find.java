package com.pan.skating.pager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pan.skating.R;
import com.pan.skating.bean.FindBean;
import com.pan.skating.adapter.FindAdapter;

import java.util.ArrayList;
import java.util.List;


public class Find extends Fragment {

    private View view;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_find, container, false);
        recyclerView= (RecyclerView) view.findViewById(R.id.fm_find_recycle);
        List<FindBean> list=new ArrayList<>();
        list.add(new FindBean(R.drawable.head_zhibo,"直播"));
        list.add(new FindBean(R.drawable.head_tieba,"贴吧"));

        FindAdapter adapter=new FindAdapter(list,getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));

        return view;
    }

}
