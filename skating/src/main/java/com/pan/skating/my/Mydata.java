package com.pan.skating.my;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pan.skating.R;
import com.pan.skating.base.BaseApplication;
import com.pan.skating.bean.User;
import com.pan.skating.citymodel.CityPopWindow;
import com.pan.skating.utils.BitmapUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class Mydata extends AppCompatActivity implements View.OnClickListener,CityPopWindow.PopCityWindow {

    private static final int REQUESTCODE_CAMERA = 4;
    private LinearLayout head1,name,city,date1;
    private EditText sign;
    private Button commit;
    private AlertDialog.Builder builder;
    private ImageView head;
    private TextView tv,date,citytv;
    private RadioButton radioButton;
    private DatePickerDialog dialog;
    private RadioGroup radioGroup;
    private CityPopWindow cityPopWindow;
    private LinearLayout main;

    private String url=new File(Environment.getExternalStorageDirectory(),
            "gather").getAbsolutePath()
            + "/head.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_mydata);

        head1= (LinearLayout) findViewById(R.id.act_mydata_head);
        head1.setOnClickListener(this);
        head= (ImageView) findViewById(R.id.act_mydata_head1);
        head.setOnClickListener(this);
        if(url!=null){
            Bitmap bm = BitmapFactory.decodeFile(url);
            head.setImageBitmap(bm);
        }
        name= (LinearLayout) findViewById(R.id.act_mydata_name);
        name.setOnClickListener(this);
        city= (LinearLayout) findViewById(R.id.act_mydata_city);
        city.setOnClickListener(this);
        date1= (LinearLayout) findViewById(R.id.act_mydata_date);
        date1.setOnClickListener(this);
        sign= (EditText) findViewById(R.id.act_mydata_sign);
        sign.setText(BaseApplication.app.getUserdatas().getSign());
        commit= (Button) findViewById(R.id.act_mydata_commit);
        commit.setOnClickListener(this);
        tv= (TextView) findViewById(R.id.act_mydata_name_tv);
        tv.setText(BaseApplication.app.getUserdatas().getUsername());
        radioGroup= (RadioGroup) findViewById(R.id.sex);
        radioButton= (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
        date= (TextView) findViewById(R.id.act_mydata_date_tv);
        date.setText(BaseApplication.app.getUserdatas().getDate());
        main = (LinearLayout) findViewById(R.id.act_mydata_main);
        citytv= (TextView) findViewById(R.id.act_mydata_city_tv);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.act_mydata_head:
                dialoghead();
                break;
            case R.id.act_mydata_name:
                dialogname();
                break;
            case R.id.act_mydata_city:
                if (cityPopWindow == null) {
                    cityPopWindow = new CityPopWindow(getApplicationContext());
                    cityPopWindow.setOnCityListener(Mydata.this);
                }
                cityPopWindow.showAtLocation(main, Gravity.CENTER
                        | Gravity.BOTTOM, 0, 0);
                break;
            case R.id.act_mydata_date:
                Calendar c = Calendar.getInstance();
                dialog = new DatePickerDialog(
                        this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
                                date.setText(year + "年" + (month+1) + "月" + dayOfMonth + "日");
                            }
                        },
                        c.get(Calendar.YEAR), // 传入年份
                        c.get(Calendar.MONTH), // 传入月份
                        c.get(Calendar.DAY_OF_MONTH) // 传入天数
                );
                dialog.show();
                break;
            case R.id.act_mydata_commit:
                User user=new User();
                user.setUsername(tv.getText().toString());
                user.setDate(date.getText().toString());
                user.setSign(sign.getText().toString());
                user.setSex(radioButton.getText().toString());
                user.setCity(citytv.getText().toString());
                BmobUser bmobUser = BmobUser.getCurrentUser(Mydata.this);
                user.update(Mydata.this, bmobUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(Mydata.this, "提交成功", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(Mydata.this, "提交失败", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    private void dialogname() {
        final EditText et=new EditText(Mydata.this);
        AlertDialog.Builder builder=new AlertDialog.Builder(Mydata.this);
        builder.setTitle("请输入");
        builder.setView(et);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tv.setText(et.getText().toString());
            }
        });
        builder.setNegativeButton("取消", null);
        AlertDialog dialog=builder.create();
        dialog.show();
    }


    //头像弹窗
    private void dialoghead() {
        builder=new AlertDialog.Builder(this);
        builder.setTitle("设置头像")
                .setMessage("选择图片来源")
                .setNegativeButton("来自相机", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUESTCODE_CAMERA);
                    }

                })
                .setPositiveButton("来自相册", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent,0);
                    }
                });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            //相机回掉
            case  REQUESTCODE_CAMERA:
                Bundle extras = data.getExtras();
                if (extras!= null) {
                    Bitmap headBitmap =  (Bitmap) data.getExtras().get("data");
                    /**
                     *  1：压缩 大小控制在50k以内 2：写入本地 3：通过本地文件的File对象创建出一个BmobFile对象
                     * 4.1：上传服务器 4.2：更改当前Ima的显示
                     */
                    // 1
                    headBitmap = BitmapUtils.getBitmap100k(headBitmap, 50);
                    File file = new File(Environment.getExternalStorageDirectory(),
                            "gather");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    File headFile = new File(file.getAbsolutePath()
                            + "/head.jpg");
                    if (headFile.exists()) {
                        headFile.delete();
                    }
                    OutputStream out = null;
                    try {
                        out = new FileOutputStream(headFile);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    // 2
                    headBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    // 3
                    final BmobFile headBmobFile = new BmobFile(headFile);
                    // 4
                    headBmobFile.uploadblock(this, new UploadFileListener() {

                        //上传头像成功后更新用户表中的头像列
                        @Override
                        public void onSuccess() {

                            final User user=new User();
                            user.setHead(headBmobFile);
                            BmobUser bmobUser = BmobUser.getCurrentUser(Mydata.this);
                            user.update(Mydata.this, bmobUser.getObjectId(), new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    System.out.println("上传bomb头像成功：");
                                    Toast.makeText(Mydata.this, "上传头像成功", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    System.out.println("更新头像失败"+s);
                                    Toast.makeText(Mydata.this, "上传头像失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                    head.setImageBitmap(headBitmap);

                }
                break;
            case 0:
                resizeImage(data.getData());
                break;

            case 1:

                if (isSdcardExisting()) {
                    resizeImage(getImageUri());
                } else {
                    Toast.makeText(Mydata.this, "未找到存储卡，无法存储照片！",
                            Toast.LENGTH_LONG).show();
                }
                break;

            case 2:
                if (data!=null) {
                    showResizeImage(data);
                }
                break;
        }
    }

    //剪切图片方法
    public void resizeImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }

    //设置图像
    private void showResizeImage(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            File file = new File(getImageUri().getPath());
            if (file.exists()) {
                file.delete();
            }
            try {
                //将剪切的图片保存到本地（SD卡）
                FileOutputStream fos = new FileOutputStream(file);
                photo.compress(Bitmap.CompressFormat.JPEG,100, fos);
                fos.flush();
                fos.close();
                //上传Bmob云
                //bmobIcon();
                @SuppressWarnings("deprecation")
                Drawable drawable = new BitmapDrawable(photo);
                head.setImageDrawable(drawable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //判断SD卡状态
    private boolean isSdcardExisting() {
        final String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    User user=new User();

    //获取Uri地址方法
    private Uri getImageUri() {
        return Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/tupian.jpg"));
    }

    //图片上传Bmob云方法
    @SuppressLint("ShowToast")
    public void bmobIcon(){
        String picPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/tupian.jpg";

        System.out.println("走到了头像上传："+picPath);

        final BmobFile bmobFile = new BmobFile(new File(picPath));
        bmobFile.uploadblock(this, new UploadFileListener() {
            //上传图片成功后更新表
            @Override
            public void onSuccess() {
                Toast.makeText(Mydata.this, "上传图片成功", Toast.LENGTH_SHORT);
                final User user1 = BaseApplication.app.getUserdatas();
                final User user=new User();
                user.setHead(bmobFile);
                user.update(Mydata.this, user1.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        System.out.println("上传bomb头像成功：");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        System.out.println("更新头像失败"+s);
                    }
                });
            }

            @Override
            public void onProgress(Integer value) {
            }

            @Override
            public void onFailure(int code, String msg) {
                Toast.makeText(Mydata.this, "上传文件失败："+msg, Toast.LENGTH_SHORT);

                System.out.println("上传图像失败：");
            }
        });
    }

    //城市的返回值
    @Override
    public void SaveData(String city) {
        citytv.setText(city);
    }
}
