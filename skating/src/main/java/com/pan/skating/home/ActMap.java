package com.pan.skating.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.pan.skating.R;
import com.pan.skating.base.BaseApplication;
import com.pan.skating.bean.PointBean;
import com.pan.skating.interfaces.BaseInterface;
import com.pan.skating.utils.AppPackageUtil;
import com.pan.skating.utils.ToastUtil;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobGeoPoint;

public class ActMap extends AppCompatActivity implements BaseInterface{
    @ViewInject(R.id.act_map_map)
    private MapView mapView;

    public MyLocationListenner myListener = new MyLocationListenner();
    private BaiduMap baiduMap=null;
    private BmobGeoPoint actpoint;
    private LocationClient mLocClient;
    boolean isFirstLoc = true; // 是否首次定位
    private PointBean dis=new PointBean();
    private Button btn;
    private ArrayList<String> maps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_map);
        ViewUtils.inject(this);
        initViews();
        map();
        initDatas();
        initOpers();//地图导航
    }

    @Override
    public void initViews() {
        //活动地点
        actpoint=BaseApplication.app.getAct().getPoint();
        baiduMap=mapView.getMap();
        btn= (Button) findViewById(R.id.act_map_btn);
        //判断地图
        maps=new ArrayList();
        if(AppPackageUtil.isAvilible(this,"com.baidu.BaiduMap")){
            maps.add(new String("百度地图"));
        }
        if(AppPackageUtil.isAvilible(this,"com.autonavi.minimap")){
            maps.add(new String("高德地图"));
        }
        if(AppPackageUtil.isAvilible(this,"com.baidu.BaiduMap")==false
                &&AppPackageUtil.isAvilible(this,"com.autonavi.minimap")==false){
            ToastUtil.showShort(this,"未安装可以导航的地图");
            navigation();
        }
    }

    private void navigation() {
        LatLng pt_start = new LatLng(BaseApplication.app.getPoint().getLat(),BaseApplication.app.getPoint().getLon());
        LatLng pt_end = new LatLng(actpoint.getLatitude(),actpoint.getLongitude());
        // 构建 route搜索参数以及策略，起终点也可以用name构造
        RouteParaOption para = new RouteParaOption()
                .startPoint(pt_start)
                .endPoint(pt_end)
                .busStrategyType(RouteParaOption.EBusStrategyType.bus_recommend_way);
        try {
            BaiduMapRoutePlan.openBaiduMapTransitRoute(para, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void map() {
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    @Override
    public void initDatas() {
        //定义Maker坐标点
        LatLng point = new LatLng(actpoint.getLatitude(),actpoint.getLongitude());
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_marka);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        baiduMap.addOverlay(option);
    }

    @Override
    public void initOpers() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String[] item=new String[maps.size()];
                for (int i=0;i<maps.size();i++){
                    item[i]=maps.get(i);
                }
                AlertDialog.Builder builder=new AlertDialog.Builder(ActMap.this);
                builder.setTitle("请选择地图");
                builder.setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(item[i].equals("百度地图")){
                            navigation();
                        }
                        if(item[i].equals("高德地图")){
                            try {
                                Intent intent = Intent.getIntent("androidamap://route?sourceApplication=skating&poiname=我的目的地&dlat="+
                                        actpoint.getLatitude()+"&dlon="+actpoint.getLongitude()+"&dev=0&t=1");
                                startActivity(intent);
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                dis.setLat(ll.latitude);
                dis.setLon(ll.longitude);
                BaseApplication.app.setPoint(dis);
            }
        }
        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
}
