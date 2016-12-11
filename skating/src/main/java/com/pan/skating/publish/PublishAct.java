package com.pan.skating.publish;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.pan.skating.R;
import com.pan.skating.adapter.PhotoAdapter;
import com.pan.skating.adapter.RecyclerItemClickListener;
import com.pan.skating.bean.ActBean;
import com.pan.skating.bean.User;
import com.pan.skating.map.MapLabel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

public class PublishAct extends AppCompatActivity implements View.OnClickListener{
    private Calendar c = Calendar.getInstance();
    private String nameet,introduceet,starttimeet,endtimeet,locationet,xuzhiet,contactet,mapet;
    private ProgressDialog progress;

    enum RequestCode {
        ButtonMultiplePicked(R.id.act_act_pic);
        @IdRes
        final int mViewId;
        RequestCode(@IdRes int viewId) {
            mViewId = viewId;
        }
    }
    @ViewInject(R.id.act_act_starttime) private Button starttime;
    @ViewInject(R.id.act_act_endtime) private Button endtime;
    @ViewInject(R.id.act_act_map) private Button map;
    @ViewInject(R.id.act_act_create) private Button create;
    @ViewInject(R.id.act_act_rv) private RecyclerView recyclerView;
    @ViewInject(R.id.title_back) private ImageView back;
    @ViewInject(R.id.act_act_location) private EditText location;
    @ViewInject(R.id.act_act_name) private EditText name;
    @ViewInject(R.id.act_act_introduce) private EditText introduce;
    @ViewInject(R.id.act_act_xuzhi) private EditText xuzhi;
    @ViewInject(R.id.act_act_contact) private EditText contact;
    @ViewInject(R.id.act_act_starttimeem) private TextView starttimeem;
    @ViewInject(R.id.act_act_endtimeem) private TextView endtimeem;
    @ViewInject(R.id.act_act_picem) private TextView picem;
    @ViewInject(R.id.act_act_mapem) private TextView mapem;
    PhotoAdapter photoAdapter;
    ArrayList<String> selectedPhotos = new ArrayList<>();
    private BmobGeoPoint point=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setbarColor();
        setContentView(R.layout.activity_act);
        ViewUtils.inject(this);
        initDatas();
    }

    //设置沉浸式
    @TargetApi(19)
    void setbarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setStatusBarColor(this.getResources().getColor(R.color.title));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    private void initDatas() {
        starttime.setOnClickListener(this);
        endtime.setOnClickListener(this);
        map.setOnClickListener(this);
        create.setOnClickListener(this);
        back.setOnClickListener(this);
        photoAdapter = new PhotoAdapter(this, selectedPhotos);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(photoAdapter);
        findViewById(R.id.act_act_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(RequestCode.ButtonMultiplePicked);
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PhotoPreview.builder()
                        .setPhotos(selectedPhotos)
                        .setCurrentItem(position)
                        .start(PublishAct.this);
            }
        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            selectedPhotos.clear();
            if (photos != null) {
                selectedPhotos.addAll(photos);
                recyclerView.setVisibility(View.VISIBLE);
            }
            photoAdapter.notifyDataSetChanged();
        }

        //通过地图标注返回的值
        if(requestCode==100&&resultCode==101){
            point=new BmobGeoPoint();
            String addr=data.getStringExtra("location");
            point.setLongitude(data.getDoubleExtra("longitude",0));
            point.setLatitude(data.getDoubleExtra("latitude",0));
            location.setText(addr);
            map.setText("修改地址");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // permission was granted, yay!
            onClick(RequestCode.values()[requestCode].mViewId);

        } else {
            Toast.makeText(this, "No read storage permission! Cannot perform the action.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.CAMERA:
                return false;
            default:
                return true;
        }
    }
    private void checkPermission(@NonNull RequestCode requestCode) {
        int readStoragePermissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int cameraPermissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        boolean readStoragePermissionGranted = readStoragePermissionState != PackageManager.PERMISSION_GRANTED;
        boolean cameraPermissionGranted = cameraPermissionState != PackageManager.PERMISSION_GRANTED;
        if (readStoragePermissionGranted || cameraPermissionGranted) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
            } else {
                String[] permissions;
                if (readStoragePermissionGranted && cameraPermissionGranted) {
                    permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA };
                } else {
                    permissions = new String[] {
                            readStoragePermissionGranted ? Manifest.permission.READ_EXTERNAL_STORAGE
                                    : Manifest.permission.CAMERA
                    };
                }
                ActivityCompat.requestPermissions(this,
                        permissions,
                        requestCode.ordinal());
            }
        } else {
            // Permission granted
            onClick(requestCode.mViewId);
        }
    }

    private void onClick(@IdRes int viewId) {
        switch (viewId) {
            case R.id.act_act_pic:{
                PhotoPicker.builder()
                        .setPhotoCount(4)
                        .setShowCamera(true)
                        .setSelected(selectedPhotos)
                        .start(this);
                break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.act_act_starttime:
                changdate(starttime);
                break;
            case R.id.act_act_endtime:
                changtime(0,0,0,endtime);
                break;
            case R.id.act_act_map:
                startActivityForResult(new Intent(this,MapLabel.class),100);
                break;
            case R.id.title_back:
                finish();
                break;
            case R.id.act_act_create:
                progress=new ProgressDialog(PublishAct.this);
                progress.setMessage("正在发布");
                progress.show();
                starttimeem.setVisibility(View.GONE);
                endtimeem.setVisibility(View.GONE);
                mapem.setVisibility(View.GONE);
                picem.setVisibility(View.GONE);
                empty();
                break;
        }
    }

    private void empty() {
        nameet=name.getText().toString().trim();
        introduceet=introduce.getText().toString().trim();
        starttimeet=starttime.getText().toString().trim();
        endtimeet=endtime.getText().toString().trim();
        locationet=location.getText().toString().trim();
        xuzhiet=xuzhi.getText().toString().trim();
        contactet=contact.getText().toString().trim();
        mapet=map.getText().toString().trim();
        if(TextUtils.isEmpty(nameet)){
            Toast.makeText(PublishAct.this,"名称未填写",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(introduceet)){
            Toast.makeText(PublishAct.this,"介绍未填写",Toast.LENGTH_SHORT).show();
        }else if(starttimeet.equals("请选择")){
            starttimeem.setVisibility(View.VISIBLE);
        }else if(endtimeet.equals("请选择")){
            endtimeem.setVisibility(View.VISIBLE);
        }else if(TextUtils.isEmpty(locationet)){
            Toast.makeText(PublishAct.this,"位置未填写",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(xuzhiet)){
            Toast.makeText(PublishAct.this,"活动须知未填写",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(contactet)){
            Toast.makeText(PublishAct.this,"联系方式未填写",Toast.LENGTH_SHORT).show();
        }else if(selectedPhotos.size()==0){
            picem.setVisibility(View.VISIBLE);
        }else if(mapet.equals("地图标注")){
            mapem.setVisibility(View.VISIBLE);
        }else {
            publish();
        }
    }

    private void publish() {
        String[] filepath=new String[selectedPhotos.size()];
        for(int i=0;i<selectedPhotos.size();i++){
            filepath[i]=selectedPhotos.get(i);
        }
        Bmob.uploadBatch(PublishAct.this, filepath, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> list1) {
                if(list1.size()==selectedPhotos.size()){
                    ActBean actBean=new ActBean();
                    User user=BmobUser.getCurrentUser(PublishAct.this,User.class);
                    actBean.setAuthor(user);
                    actBean.setName(nameet);
                    actBean.setIntroduce(introduceet);
                    actBean.setStarttime(starttimeet);
                    actBean.setEndtime(endtimeet);
                    actBean.setLocation(locationet);
                    actBean.setXuzhi(xuzhiet);
                    actBean.setContact(contactet);
                    actBean.setPoint(point);
                    actBean.setPic(list);
                    addAct(actBean);
                }else {
                    //有可能上传不完整，中间可能会存在未上传成功的情况，可以自行处理
                }
            }

            @Override
            public void onProgress(int i, int i1, int i2, int i3) {

            }

            @Override
            public void onError(int i, String s) {
                progress.dismiss();
                Toast.makeText(PublishAct.this,"图片上传失败!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addAct(BmobObject actBean) {

        actBean.save(PublishAct.this, new SaveListener() {
            @Override
            public void onSuccess() {
                progress.dismiss();
                Toast.makeText(PublishAct.this,"发布成功!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String s) {
                progress.dismiss();
                Toast.makeText(PublishAct.this,"发布失败!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //日期时间选择
    private void changdate(final Button btn) {
        DatePickerDialog datedialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
                        changtime(year,month,dayOfMonth,btn);
                    }
                },
                c.get(Calendar.YEAR), // 传入年份
                c.get(Calendar.MONTH), // 传入月份
                c.get(Calendar.DAY_OF_MONTH)// 传入天数
        );
        datedialog.show();
    }

    private void changtime(final int year,final int month,final int dayOfMonth,final Button btn) {
        TimePickerDialog time=new TimePickerDialog(
                this,
                new OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        if(year==0){
                            btn.setText(i+":"+i1);
                        }else{
                            btn.setText(year + "年" + (month + 1) + "月" + dayOfMonth + "日 "+i+":"+i1);
                        }
                    }
                },
                c.get(Calendar.MINUTE),
                c.get(Calendar.HOUR_OF_DAY),
                true
        );
        time.show();
    }
}
