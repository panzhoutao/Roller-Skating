package com.pan.skating.pager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pan.skating.R;
import com.pan.skating.base.BaseApplication;
import com.pan.skating.interfaces.BaseInterface;
import com.pan.skating.bean.User;
import com.pan.skating.my.Mydata;
import com.pan.skating.utils.BitmapUtils;
import com.pan.skating.utils.ImageLoaderUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class My extends Fragment implements BaseInterface,View.OnClickListener{
    private static final int REQUESTCODE_CAMERA = 4;
    private View view;
    private LinearLayout data;
    private ImageView head;
    private AlertDialog.Builder builder;
    private TextView username,describe;
    private String name,sign;
    private ImageLoader loader;
    private DisplayImageOptions options;
    private User datas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_my, container, false);
        initViews();
        initDatas();
        initOpers();
        return view;
    }

    @Override
    public void initViews() {
        data= (LinearLayout) view.findViewById(R.id.fm_my_data);
        data.setOnClickListener(this);
        head= (ImageView) view.findViewById(R.id.fm_my_head);
        head.setOnClickListener(this);
        username= (TextView) view.findViewById(R.id.fm_my_username);
        describe= (TextView) view.findViewById(R.id.fm_my_describe);
    }

    @Override
    public void onResume() {
        super.onResume();
        initDatas();
    }

    @Override
    public void initDatas() {
        datas=BaseApplication.app.getUserdatas();
        name = (String) BmobUser.getObjectByKey(getActivity(),"username");
        sign=(String) BmobUser.getObjectByKey(getActivity(),"sign");
        if(datas.getHead()!=null){
            loader = ImageLoaderUtils.getInstance(getActivity());
            options = ImageLoaderUtils.getOpt();
            loader.displayImage(datas.getHead().getFileUrl(getActivity()),head, options);
        }
    }

    @Override
    public void initOpers() {
        username.setText(name);
        if(sign!=null){
            describe.setText(sign);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fm_my_data:
                startActivity(new Intent(getActivity(), Mydata.class));
                break;
            case R.id.fm_my_head:
                dialog();
                break;
        }
    }

    //头像弹窗
    private void dialog() {
        builder=new AlertDialog.Builder(getActivity());
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
                    Bitmap headBitmap =  (Bitmap) extras.get("data");
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
                    headBmobFile.uploadblock(getActivity(), new UploadFileListener() {

                        //上传头像成功后更新用户表中的头像列
                        @Override
                        public void onSuccess() {

                            final User user=new User();
                            user.setHead(headBmobFile);
                            BmobUser bmobUser = BmobUser.getCurrentUser(getActivity());
                            user.update(getActivity(), bmobUser.getObjectId(), new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    System.out.println("上传bomb头像成功：");
                                    Toast.makeText(getActivity(), "上传头像成功", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    System.out.println("更新头像失败"+s);
                                    Toast.makeText(getActivity(), "上传头像失败", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "未找到存储卡，无法存储照片！",
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
                bmobIcon();
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
        bmobFile.uploadblock(getActivity(), new UploadFileListener() {
            //上传图片成功后更新表
            @Override
            public void onSuccess() {
                Toast.makeText(getActivity(), "上传图片成功", Toast.LENGTH_SHORT);
                final User user1 = BaseApplication.app.getUserdatas();
                final User user=new User();
                user.setHead(bmobFile);
                user.update(getActivity(), user1.getObjectId(), new UpdateListener() {
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
                Toast.makeText(getActivity(), "上传文件失败："+msg, Toast.LENGTH_SHORT);

                System.out.println("上传图像失败：");
            }
        });
    }
}
