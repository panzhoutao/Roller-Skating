package com.pan.skating.recorder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.pan.skating.R;
import com.pan.skating.bean.User;
import com.pan.skating.bean.VideoBean;
import java.io.File;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class FFmpegPreviewActivity extends Activity implements TextureView.SurfaceTextureListener
	,OnClickListener,OnCompletionListener{

	private String path;
	private TextureView surfaceView;
	private Button cancelBtn,publish;
	private MediaPlayer mediaPlayer;
	private ImageView imagePlay;
	private EditText edit;
	private ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ffmpeg_preview);

		cancelBtn = (Button) findViewById(R.id.play_cancel);
		cancelBtn.setOnClickListener(this);

		publish= (Button) findViewById(R.id.play_next);
		publish.setOnClickListener(this);

		edit= (EditText) findViewById(R.id.act_publishvideo_edit);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		surfaceView = (TextureView) findViewById(R.id.preview_video);
		
		RelativeLayout preview_video_parent = (RelativeLayout)findViewById(R.id.preview_video_parent);
		LayoutParams layoutParams = (LayoutParams) preview_video_parent
				.getLayoutParams();
		layoutParams.width = displaymetrics.widthPixels;
		layoutParams.height = displaymetrics.widthPixels;
		preview_video_parent.setLayoutParams(layoutParams);
		
		surfaceView.setSurfaceTextureListener(this);
		surfaceView.setOnClickListener(this);
		
		path = getIntent().getStringExtra("path");
		
		imagePlay = (ImageView) findViewById(R.id.previre_play);
		imagePlay.setOnClickListener(this);
		
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
	}

	@Override
	protected void onStop() {
		if(mediaPlayer.isPlaying()){
			mediaPlayer.pause();
			imagePlay.setVisibility(View.GONE);
		}
		super.onStop();
	}

	private void prepare(Surface surface) {
		try {
			mediaPlayer.reset();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			// 设置需要播放的视频
			mediaPlayer.setDataSource(path);
			// 把视频画面输出到Surface
			mediaPlayer.setSurface(surface);
			mediaPlayer.setLooping(true);
			mediaPlayer.prepare();
			mediaPlayer.seekTo(0);
		} catch (Exception e) {
		}
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1,
			int arg2) {
		prepare(new Surface(arg0));
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
		return false;
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,
			int arg2) {
		
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play_cancel:
			stop();
			break;
		case R.id.previre_play:
			if(!mediaPlayer.isPlaying()){
				mediaPlayer.start();
			}
			imagePlay.setVisibility(View.GONE);
			break;
		case R.id.preview_video:
			if(mediaPlayer.isPlaying()){
				mediaPlayer.pause();
				imagePlay.setVisibility(View.VISIBLE);
			}
			break;
		//发布
		case R.id.play_next:
			progress=new ProgressDialog(FFmpegPreviewActivity.this);
			progress.setMessage("正在发布");
			progress.show();
			final BmobFile file=new BmobFile(new File(path));
			file.uploadblock(this, new UploadFileListener() {
				@Override
				public void onSuccess() {
					progress.dismiss();
					User user= BmobUser.getCurrentUser(FFmpegPreviewActivity.this,User.class);
					VideoBean video=new VideoBean();
					video.setAuthor(user);
					video.setIntroduce(edit.getText().toString());
					video.setVideo(file);
					publishvideo(video);
					Toast.makeText(FFmpegPreviewActivity.this,"上传完成",Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onFailure(int i, String s) {

				}
			});

			break;
		}
	}

	private void publishvideo(VideoBean videoBean) {
		videoBean.save(FFmpegPreviewActivity.this, new SaveListener() {
			@Override
			public void onSuccess() {
				Toast.makeText(FFmpegPreviewActivity.this,"发布成功!",Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int i, String s) {
				Toast.makeText(FFmpegPreviewActivity.this,"发布失败!",Toast.LENGTH_SHORT).show();
			}
		});

	}

	private void stop(){
		mediaPlayer.stop();
		Intent intent = new Intent(this,FFmpegRecorderActivity.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	public void onBackPressed() {
		stop();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		imagePlay.setVisibility(View.VISIBLE);
	}
}
