package movi.lib.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ctvit.p2cloud.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import movi.utils.LogUtils;
import movi.utils.ToastUtils;

public class VideoNewActivity1 extends CameraBaseActivity implements SurfaceHolder.Callback {

	/** 视频最大支持15秒 */
	public static final int VIDEO_TIME_END = 15;
	/** 视频最少必须5秒 */
	public static final int VIDEO_TIME = 5;
	/** 最少得录制多少秒 */
	private ImageView img_at_last;
	/** 闪现光标图片 */
	private ImageView img_shan;
	/** 删除录制 */
	private ImageView img_delete;
	/** 开始录制 */
	private ImageButton img_start;
	/** 确认 */
//	private ImageView img_enter;
	private  TextView img_enter;
	/** 闪光灯切换 */
	private ImageView img_flashlight;
	/** 摄像头切换 */
	private ImageView img_camera;
	/** 选择录像 */
	private ImageView img_video;
	/** 返回按钮 */
	private ImageView img_back;
	/** 计时器 */
	private TimeCount timeCount;
	/** 录制了多少秒 */
	private int now;
	/** 每次录制结束时是多少秒 */
	private int old;

	/** 录制进度控件 */
	private LinearLayout linear_seekbar;
	/** 屏幕宽度 */
	private int width;
	/** 偶数才执行 */
	private int even;
	/** 是否点击删除了一次 */
	private boolean isOnclick = false;
	/** 录制视频集合 */
	private ArrayList<VideoNewBean> list;
	/** 录制bean */
	private VideoNewBean bean;
	/** 为了能保存到bundler 录制bean */
	private VideoNewParentBean parent_bean;
	/** 录制视频保存文件 */
	private String vedioPath;
	/** 合并之后的视频文件 */
	private String videoPath_merge;
	/** 是否满足视频的最少播放时长 */
	private boolean isMeet = false;

	/** 录制视频的类 */
	private MediaRecorder mMediaRecorder;
	/** 摄像头对象 */
	private Camera mCamera;
	/** 显示的view */
	private SurfaceView surfaceView;
	/** 摄像头参数 */
	private Parameters mParameters;
	// /** 视频输出质量 */
	private CamcorderProfile mProfile;
	/** 文本属性获取器 */
	private SharedPreferences mPreferences;
	/** 刷新界面的回调 */
	private SurfaceHolder mHolder;
	/** 1表示后置，0表示前置 */
	private int cameraPosition = 1;
	/**声明一个SoundPool*/
	private SoundPool sp;
	 /**定义一个整型用load（）；来设置suondID*/
	private int music;
	/**录制时间的ui显示*/
	private TextView tv_New_Time;
	/** 初始化分辨率*/
	private int mScreenWidht = 1280;
	private int mScreenHeight = 720;

	/** 路径 */
	private String Ppath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ugc/";
	private boolean isTouch1;


	// /** 压缩jni */
	// private LoadJNI vk;




	@Override
	protected int getContentViewId() {
		return R.layout.activity_video_new_new;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void findViews() {
		img_camera = (ImageView) findViewById(R.id.video_new_img_right);
		img_flashlight = (ImageView) findViewById(R.id.video_new_img_flashlight);
		surfaceView = (SurfaceView) findViewById(R.id.video_new_surfaceview);
		img_at_last = (ImageView) findViewById(R.id.video_new_img_time_atlast);
		img_shan = (ImageView) findViewById(R.id.video_new_img_time_start);
		img_delete = (ImageView) findViewById(R.id.video_new_img_delete);
		img_start = (ImageButton) findViewById(R.id.video_new_img_start);
		img_enter = (TextView) findViewById(R.id.video_new_img_enter);
		img_back = (ImageView) findViewById(R.id.video_new_img_back);
		img_video = (ImageView) findViewById(R.id.video_new_img_video);
		linear_seekbar = (LinearLayout) findViewById(R.id.video_new_seekbar);
		tv_New_Time = (TextView) findViewById(R.id.tv_new_time);

		width = getWindowManager().getDefaultDisplay().getWidth();
//		------------------视频录制分辨率的宽高比例----------------
//		LayoutParams layoutParam = (LayoutParams) surfaceView.getLayoutParams();
//		// 高：宽 4 : 3
//		layoutParam.height = width / 3 * 4;
//		// 隐藏多少dp才能让屏幕显示正常像素
//		layoutParam.topMargin = -(width / 3 * 4 - width - DisplayUtil.dip2px(VideoNewActivity.this, 44));
//		surfaceView.setLayoutParams(layoutParam);
//		------------------视频录制分辨率的宽高比例----------------

//		------------------录制视频的进度条----------------
//		LayoutParams layoutParams = (LayoutParams) img_at_last.getLayoutParams();
//		layoutParams.leftMargin = width / VIDEO_TIME_END * VIDEO_TIME;
//		img_at_last.setLayoutParams(layoutParams);
//		------------------录制视频的进度条----------------

		//添加录屏结束声音
		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
		music = sp.load(this, R.raw.start , 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		parent_bean.setList(list);
		outState.putSerializable("parent_bean", parent_bean);

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		parent_bean = (VideoNewParentBean) savedInstanceState.getSerializable("parent_bean");
		if(null != parent_bean) {
			list = parent_bean.getList();
		}

		super.onRestoreInstanceState(savedInstanceState);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void init() {
		handler.postDelayed(runnable, 0);
		even = 0;
		old = 0;
		// 创建文件夹
		File file = new File(Ppath);
		if (!file.exists()) {
			file.mkdir();
		}
		list = new ArrayList<VideoNewBean>();
		parent_bean = new VideoNewParentBean();
		// 安装一个SurfaceHolder.Callback
		mHolder = surfaceView.getHolder();
		mHolder.addCallback(this);
		// 针对低于3.0的Android
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		readVideoPreferences();
		initCameraScreentParams();

	}
	private AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if(success)//success表示对焦成功
			{
				Log.i("---", "myAutoFocusCallback: success...");
//				mCamera.setOneShotPreviewCallback(null);

			}
			else
			{
				//未对焦成功
				Log.i("---", "myAutoFocusCallback: 失败了...");

			}
		}
	};


	@Override
	protected void onStart() {
		super.onStart();
		// 获取Camera实例
		mCamera = getCamera();
		if (mCamera != null) {
			// 因为android不支持竖屏录制，所以需要顺时针转90度，让其游览器显示正常
			mCamera.setDisplayOrientation(0);//此项目更改的，原来是竖屏设置90
			mCamera.lock();
			initCameraParameters();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
	}

	/**
	 * 获取摄像头实例
	 *
	 * @version 1.0
	 * @createTime 2015年6月16日,上午10:44:11
	 * @updateTime 2015年6月16日,上午10:44:11
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 * @return
	 */
	private Camera getCamera() {
		Camera camera ;
		try {
			camera = Camera.open();
		} catch (Exception e) {
			camera = null;
			Toast.makeText(this , "该摄像头没有初始化成功" , Toast.LENGTH_SHORT).show();
		}
		return camera;
	}

	private Handler handler = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			LogUtils.e("---" , "now=" + now);
			int minute = (now + old) / 60000;
			int second = (now + old) - minute*60000;
			String timeString = String.format("%02d:%02d", minute , second/1000);
			tv_New_Time.setText(timeString);
			handler.sendEmptyMessageDelayed(1, 1000);
		}


	};


	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			if (img_shan.isShown()) {
				img_shan.setVisibility(View.GONE);
			} else {
				img_shan.setVisibility(View.VISIBLE);
			}
			handler.postDelayed(runnable, 500);
		}
	};
	private boolean isRecording = false;
	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void widgetListener() {
		img_start.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					isTouch1 = true;
					if(!isRecording) {
						if (isOnclick) {
							( linear_seekbar.getChildAt(linear_seekbar.getChildCount() - 2)).setBackgroundColor(getResources().getColor(
									R.color.ff1f8fe4));
						}

						even = 1;

//						img_delete.setVisibility(View.VISIBLE);
						img_enter.setVisibility(View.GONE);
						img_start.setImageResource(R.mipmap.camera_pause);
//						img_video.setVisibility(View.GONE);
						img_camera.setVisibility(View.GONE);
						img_flashlight.setVisibility(View.GONE);

						addView_Red();

						// 构造CountDownTimer对象
						timeCount = new TimeCount(10*60*1000 - old, 50);//最长可以录10分钟
						timeCount.start();// 开始计时
						handler.sendEmptyMessage(1);
						startRecord();
						isRecording = true;

					}else {
						old = now + old;

						if (old >= VIDEO_TIME * 1000) {
							isMeet = true;
						}

						timeCount.cancel();

						addView_black();

						stopRecord();
						handler.removeMessages(1);
						isRecording = false;
						img_start.setImageResource(R.mipmap.camera_start);
						img_enter.setVisibility(View.VISIBLE);
						img_flashlight.setVisibility(View.VISIBLE);
					}

					break;
				case MotionEvent.ACTION_UP:
//					old = now + old;
//
//					if (old >= VIDEO_TIME * 1000) {
//						isMeet = true;
//					}
//
//					timeCount.cancel();
//
//					addView_black();
//
//					stopRecord();

					isTouch1 = false;
					break;
				}
				return false;
			}
		});

//		img_start.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				switch (event.getAction()) {
//				case MotionEvent.ACTION_DOWN:
//					isTouch1 = true;
//					if (isOnclick) {
//						((ImageView) linear_seekbar.getChildAt(linear_seekbar.getChildCount() - 2)).setBackgroundColor(getResources().getColor(
//										R.color.ff1f8fe4));
//					}
//
//					even = 1;
//
//					img_delete.setVisibility(View.VISIBLE);
//					img_enter.setVisibility(View.VISIBLE);
////					img_video.setVisibility(View.GONE);
////					img_camera.setVisibility(View.GONE);
//
//					addView_Red();
//
//					// 构造CountDownTimer对象
//					timeCount = new TimeCount(30000 - old, 50);
//					timeCount.start();// 开始计时
//
//					startRecord();
//
//					break;
//				case MotionEvent.ACTION_UP:
//					old = now + old;
//
//					if (old >= VIDEO_TIME * 1000) {
//						isMeet = true;
//					}
//
//					timeCount.cancel();
//
//					addView_black();
//
//					stopRecord();
//
//					isTouch1 = false;
//					break;
//				}
//				return false;
//			}
//		});
		/** 删除按钮 */
		img_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isOnclick = false;
				if (even % 2 == 0) {
					if (linear_seekbar.getChildCount() > 1) {
						linear_seekbar.removeViewAt(linear_seekbar.getChildCount() - 1);
						linear_seekbar.removeViewAt(linear_seekbar.getChildCount() - 1);
					}
					if (list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							File file = new File(list.get(list.size() - 1).getPath());
							if (file.exists()) {
								file.delete();
							}
						}
						old -= list.get(list.size() - 1).getTime();
						list.remove(list.size() - 1);
						if (old < VIDEO_TIME * 1000) {
							isMeet = false;
						}
						if (list.size() <= 0) {
//							img_delete.setVisibility(View.GONE);
							img_enter.setVisibility(View.GONE);
//							img_video.setVisibility(View.VISIBLE);
							img_camera.setVisibility(View.VISIBLE);
						}
					}
				} else {
					if (linear_seekbar.getChildCount() > 1) {
						isOnclick = true;
						(linear_seekbar.getChildAt(linear_seekbar.getChildCount() - 2)).setBackgroundColor(getResources().getColor(
								R.color.ff135689));
					}
				}
				even++;
			}
		});
		/** 开启或关闭闪光灯 */
		img_flashlight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cameraPosition == 0) {// 前置摄像头的时候不能切换闪光灯
					return;
				}
				try {
					if (mParameters != null && mCamera != null) {
                        if (mParameters.getFlashMode() != null && mParameters.getFlashMode().equals(Parameters.FLASH_MODE_TORCH)) {
                            mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                            img_flashlight.setImageResource(R.mipmap.camera_nolight);
                        } else if (mParameters.getFlashMode() != null && mParameters.getFlashMode().equals(Parameters.FLASH_MODE_OFF)) {
                            mParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
                            img_flashlight.setImageResource(R.mipmap.camera_light);
                        }
                        mCamera.setParameters(mParameters);


                    }
				} catch (Exception e) {
					e.printStackTrace();
					LogUtils.e("flashlight" , "setFlashMode:"+e);
				}

			}
		});
		/** 摄像头切换 */
		img_camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switchCamera();
			}
		});

		/** 确认按钮 */
		img_enter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				img_enter.setEnabled(false);
				if (isMeet) {

					int size = list.size();
					String[] strs = new String[size];
					videoPath_merge = Ppath + System.currentTimeMillis() + ".mp4";
					for (int i = 0; i < size; i++) {
						strs[i] = list.get(i).getPath();
					}
					try {
						FUckTest.appendVideo(strs, videoPath_merge);

						for (int i = size - 1; i >= 0; i--) {
							File file = new File(list.get(i).getPath());
							if (file.exists()) {
								file.delete();
							}
							list.remove(i);
						}

						Intent it = new Intent();
						it.putExtra("path", videoPath_merge);
						setResult(Activity.RESULT_OK, it);

						finish();


					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(VideoNewActivity1.this, "视频最少必须录制5秒以上才能用！", Toast.LENGTH_SHORT).show();
				}
			}
		});
		/** 选择录像按钮 */
		img_video.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(VideoNewActivity1.this, VideoNewSelectActivity.class);
				startActivity(intent);
			}
		});

		img_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (list.size() > 0) {
					exitVideoNewDialog();
				} else {
					stopRecord();
//					releaseCamera();
					finish();
				}
			}
		});

		surfaceView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(isTouch1) {
				    return;
				}
				if (mParameters != null && mCamera != null) {
					mParameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
					try {
						mCamera.setParameters(mParameters);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
				if (list.size() > 0) {
					exitVideoNewDialog();
				} else {
					releaseCamera();
					finish();
				}
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	/**
	 * 弹出对话框
	 *
	 * @version 1.0
	 * @createTime 2015年6月16日,下午3:45:35
	 * @updateTime 2015年6月16日,下午3:45:35
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 */
	private void exitVideoNewDialog() {

		Builder builder = new Builder(VideoNewActivity1.this);
		builder.setMessage("确定放弃这段视频吗？");
		builder.setTitle("温馨提示");
		builder.setPositiveButton("确认",    new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				for (int i = 0; i < list.size(); i++) {
					File file = new File(list.get(i).getPath());
					if (file.exists()) {
						file.delete();
					}
				}
				finish();
			}

		});
		builder.create().show();
	}

	/**
	 * 切换摄像头
	 *
	 * @version 1.0
	 * @createTime 2015年6月16日,上午10:40:17
	 * @updateTime 2015年6月16日,上午10:40:17
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 */
	@SuppressLint("NewApi")
	private void switchCamera() {
		// 切换前后摄像头,现在是后置，变更为前置
			if (cameraPosition == 1) {
				// 前置摄像头时必须关闭闪光灯，不然会报错
				if (mParameters != null && mCamera != null) {
					if (mParameters.getFlashMode() != null && mParameters.getFlashMode().equals(Parameters.FLASH_MODE_TORCH)) {
						mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
						img_flashlight.setImageResource(R.mipmap.camera_nolight);
						mCamera.setParameters(mParameters);
					}
				}

				// 释放Camera
				releaseCamera();
				cameraPosition = 0;
				startPreviewDis(1);
			} else{
				// 现在是前置， 变更为后置
				// 释放Camera
				releaseCamera();
				cameraPosition = 1;
				startPreviewDis(0);
			}
	}

	private void startPreviewDis(int j) {
		// 打开当前选中的摄像头
		mCamera = Camera.open(j);
		mCamera.setDisplayOrientation(0);//原来是90度
		mCamera.lock();
		initCameraParameters();

		// 通过surfaceview显示取景画面
		setStartPreview(mHolder);
	}

	/**
	 * 定义一个倒计时的内部类
	 *
	 * @Description
	 * @author
	 * @version 1.0
	 * @date 2015-5-25
	 * @Copyright: Copyright (c) 2015 Shenzhen Utoow Technology Co., Ltd.
	 *             All rights reserved.
	 */
	private class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			ToastUtils.showToast(VideoNewActivity1.this , "已超最长拍摄时间");
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			now = (int) (10*60*1000 - millisUntilFinished - old);//最长可以录10分钟
			if ((old > 0 && old > VIDEO_TIME * 1000) || (old == 0 && now > VIDEO_TIME * 1000)) {
				img_enter.setEnabled(true);
			}
			if (linear_seekbar.getChildCount() > 0) {
				ImageView img = (ImageView) linear_seekbar.getChildAt(linear_seekbar.getChildCount() - 1);
				LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) img.getLayoutParams();
				layoutParams.width = (int) (((float) now / 1000f) * (width / VIDEO_TIME_END)) + 1;
				img.setLayoutParams(layoutParams);
			}
		}
	}

	/**
	 * 初始化摄像头参数
	 *
	 * @version 1.0
	 * @createTime 2015年6月15日,下午4:53:41
	 * @updateTime 2015年6月15日,下午4:53:41
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 */
	@SuppressWarnings("deprecation")
	private void initCameraParameters() {
		// 初始化摄像头参数
		mParameters = mCamera.getParameters();

//		mParameters.setPreviewSize(mProfile.videoFrameWidth, mProfile.videoFrameHeight);//原来的预览大小
		mParameters.setPreviewFrameRate(mProfile.videoFrameRate);

		mParameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);

		//设置图片大小,后加的,加此参数华为荣耀6plus（双摄像头）点拍摄和闪光灯都不支持
//		mParameters.setPictureSize(mScreenHeight, mScreenWidht);
		//设置预览大小,后加的，不加此参数大部分机型拍摄时有缩放的效果
		mParameters.setPreviewSize(mScreenWidht, mScreenHeight);//现在的预览大小

		// 设置白平衡参数。
		String whiteBalance = mPreferences.getString("pref_camera_whitebalance_key", "auto");
		if (isSupported(whiteBalance, mParameters.getSupportedWhiteBalance())) {
			mParameters.setWhiteBalance(whiteBalance);
		}

		// 参数设置颜色效果。
		String colorEffect = mPreferences.getString("pref_camera_coloreffect_key", "none");
		if (isSupported(colorEffect, mParameters.getSupportedColorEffects())) {
			mParameters.setColorEffect(colorEffect);
		}

		try {
			mCamera.setParameters(mParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 初始化分辨率
	 */
	private void initCameraScreentParams() {
//		if (width == 1920) {
//			mScreenWidht = 1920;
//			mScreenHeight = 1080;
//		} else if (width == 1280) {
//			mScreenWidht = 1280;
//			mScreenHeight = 720;
//		}
		mScreenHeight = getIntent().getIntExtra("FBL", -1);
		if (mScreenHeight == 720) {
			mScreenWidht = 1280;
		} else if (mScreenHeight == 480) {
			mScreenWidht = 640;
		} else if (mScreenHeight == 360) {
			mScreenWidht = 480;
		}else{
			mScreenWidht=1280;
			mScreenHeight=720;
		}
	}

	/**
	 * 开始录制
	 *
	 * @version 1.0
	 * @createTime 2015年6月15日,下午4:48:49
	 * @updateTime 2015年6月15日,下午4:48:49
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 */
	@SuppressLint("NewApi")
	private void startRecord() {

		try {
			bean = new VideoNewBean();
			vedioPath = Ppath + System.currentTimeMillis() + ".mp4";
			bean.setPath(vedioPath);

			mCamera.unlock();
			mMediaRecorder = new MediaRecorder();// 创建mediaRecorder对象
			mMediaRecorder.setCamera(mCamera);
			// 设置录制视频源为Camera(相机)
			mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder.setProfile(mProfile);
			//设置视频大小（分辨率）
			mMediaRecorder.setVideoSize(mScreenWidht, mScreenHeight);
			//设置码率
			mMediaRecorder.setVideoEncodingBitRate(4 * 1024 * 1024);// 设置视频一次写多少字节(可调节视频空间大小)
			//设置帧率
//			mMediaRecorder.setVideoFrameRate(25);
			// 最大期限
			mMediaRecorder.setMaxDuration(10*60*1000);

			// 第4步:指定输出文件 ， 设置视频文件输出的路径

			mMediaRecorder.setOutputFile(vedioPath);

			mMediaRecorder.setPreviewDisplay(mHolder.getSurface());

			// // 设置保存录像方向
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				if (cameraPosition == 1) {
					//由于不支持竖屏录制，后置摄像头需要把视频顺时针旋转90度、、但是视频本身在电脑上看还是逆时针旋转了90度
					mMediaRecorder.setOrientationHint(0);//做此项目改的，原来是90
				} else if (cameraPosition == 0) {
					//由于不支持竖屏录制，前置摄像头需要把视频顺时针旋转270度、、而前置摄像头在电脑上则是顺时针旋转了90度
					mMediaRecorder.setOrientationHint(0);//做此项目改的，原来是90
				}
			}

			mMediaRecorder.setOnInfoListener(new OnInfoListener() {

				@Override
				public void onInfo(MediaRecorder mr, int what, int extra) {

				}
			});

			mMediaRecorder.setOnErrorListener(new OnErrorListener() {

				@Override
				public void onError(MediaRecorder mr, int what, int extra) {
					recodError();
				}
			});

			// 第6步:根据以上配置准备MediaRecorder

			mMediaRecorder.prepare();
			mMediaRecorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			recodError();
		} catch (IOException e) {
			e.printStackTrace();
			recodError();
		} catch (RuntimeException e) {
			e.printStackTrace();
			recodError();
		}

	}

	/**
	 * 异常处理
	 *
	 * @version 1.0
	 * @createTime 2015年6月16日,上午10:49:18
	 * @updateTime 2015年6月16日,上午10:49:18
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 */
	private void recodError() {
		Builder builder = new Builder(VideoNewActivity1.this);
		builder.setMessage("该设备暂不支持视频录制");
		builder.setTitle("出错啦");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}

		});
		builder.create().show();

	}


	/**
	 * 结束录制
	 *
	 * @version 1.0
	 * @createTime 2015年6月15日,下午4:48:53
	 * @updateTime 2015年6月15日,下午4:48:53
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 */
	private void stopRecord() {
		sp.play(music, 1, 1, 0, 0, 1);
		if (bean != null) {
			if (list.size() > 0) {
				bean.setTime(now - list.get(list.size() - 1).getTime());
			} else {
				bean.setTime(now);
			}
			bean.setCameraPosition(cameraPosition);
			list.add(bean);
		}

		if (mMediaRecorder != null) {
			try {
				// 停止录像，释放camera
//				//设置后不会崩
				mMediaRecorder.setOnErrorListener(null);
				mMediaRecorder.setPreviewDisplay(null);
//				mMediaRecorder.setOnErrorListener(null);
//				mMediaRecorder.setOnInfoListener(null);
				mMediaRecorder.stop();
				// 清除recorder配置
				mMediaRecorder.reset();
				// 释放recorder对象
				mMediaRecorder.release();
				mMediaRecorder = null;
				// 没超过3秒就删除录制所有数据
				if (old < 3000) {
					//Toast.makeText(VideoNewActivity.this, "单次录制视频最少3秒", Toast.LENGTH_LONG).show();
					clearList();
				}
			} catch (Exception e) {
				clearList();
			}
		}
	}





	/**
	 * 清楚数据
	 *
	 * @version 1.0
	 * @createTime 2015年6月25日,下午6:04:28
	 * @updateTime 2015年6月25日,下午6:04:28
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 */
	private void clearList() {
//Toast.makeText(VideoNewActivity.this, "单次录制视频最少3秒", Toast.LENGTH_LONG).show();
		if (linear_seekbar.getChildCount() > 1) {
			linear_seekbar.removeViewAt(linear_seekbar.getChildCount() - 1);
			linear_seekbar.removeViewAt(linear_seekbar.getChildCount() - 1);
		}
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				File file = new File(list.get(list.size() - 1).getPath());
				if (file.exists()) {
					file.delete();
				}
			}
			list.remove(list.size() - 1);
			if (list.size() <= 0) {
//				img_delete.setVisibility(View.GONE);
				img_enter.setVisibility(View.GONE);
//				img_video.setVisibility(View.VISIBLE);
				img_camera.setVisibility(View.VISIBLE);
			}
		}
	}

	private static boolean isSupported(String value, List<String> supported) {
		return supported == null ? false : supported.indexOf(value) >= 0;
	}

	public static boolean getVideoQuality(String quality) {
		return "youtube".equals(quality) || "high".equals(quality);
	}

	/**
	 * 设置摄像头参数
	 *
	 * @version 1.0
	 * @createTime 2015年6月15日,下午5:12:31
	 * @updateTime 2015年6月15日,下午5:12:31
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 */
	private void readVideoPreferences() {
		String quality = mPreferences.getString("pref_video_quality_key", "high");

		boolean videoQualityHigh = getVideoQuality(quality);

		// 设置视频质量。
		Intent intent = getIntent();
		if (intent.hasExtra(MediaStore.EXTRA_VIDEO_QUALITY)) {
			int extraVideoQuality = intent.getIntExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
			videoQualityHigh = (extraVideoQuality > 0);
		}

//		videoQualityHigh = false;//我自行在实验link时改的，因为他总是false
		mProfile = CamcorderProfile.get(videoQualityHigh ? CamcorderProfile.QUALITY_HIGH : CamcorderProfile.QUALITY_LOW);
		mProfile.videoFrameWidth = (int) (mProfile.videoFrameWidth * 2.0f);
		mProfile.videoFrameHeight = (int) (mProfile.videoFrameHeight * 2.0f);
		mProfile.videoBitRate = 256000 * 3;

		CamcorderProfile highProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
		mProfile.videoCodec = highProfile.videoCodec;
		mProfile.audioCodec = highProfile.audioCodec;
		mProfile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
	}

	/**
	 * 添加红色进度条
	 *
	 * @version 1.0
	 * @createTime 2015年6月15日,下午3:04:21
	 * @updateTime 2015年6月15日,下午3:04:21
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 */
	private void addView_Red() {
		ImageView img = new ImageView(VideoNewActivity1.this);
		img.setBackgroundColor(getResources().getColor(R.color.ff1f8fe4));
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DisplayUtil.dip2px(VideoNewActivity1.this, 1), LinearLayout.LayoutParams.MATCH_PARENT);
		img.setLayoutParams(layoutParams);
		linear_seekbar.addView(img);
	}

	/**
	 * 添加黑色断条
	 *
	 * @version 1.0
	 * @createTime 2015年6月15日,下午3:03:52
	 * @updateTime 2015年6月15日,下午3:03:52
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 */
	private void addView_black() {
		ImageView img = new ImageView(VideoNewActivity1.this);
		img.setBackgroundColor(Color.BLACK);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DisplayUtil.dip2px(VideoNewActivity1.this, 2), LinearLayout.LayoutParams.MATCH_PARENT);
		img.setLayoutParams(layoutParams);
		linear_seekbar.addView(img);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setStartPreview(holder);

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

		// 先开启在关闭 先开启录制在关闭可以 解决游览的时候比较卡顿的现象，但是会有视频开启时声音。打开这个功能时较慢
		// startRecord();
		// stopRecord();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		releaseCamera();
	}

	/**
	 * 设置camera显示取景画面,并预览
	 *
	 * @version 1.0
	 * @createTime 2015年6月16日,上午10:48:15
	 * @updateTime 2015年6月16日,上午10:48:15
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 * @param holder
	 */
	private void setStartPreview(SurfaceHolder holder) {
		try {
			if (mCamera != null) {
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();
			}
		} catch (IOException e) {

		}
	}

	/**
	 * 释放Camera
	 *
	 * @version 1.0
	 * @createTime 2015年6月16日,上午10:38:08
	 * @updateTime 2015年6月16日,上午10:38:08
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 */
	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();// 停掉原来摄像头的预览
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus && Build.VERSION.SDK_INT >= 19) {
			View decorView = getWindow().getDecorView();
			decorView.setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
			);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		handler.removeCallbacksAndMessages(null);
	}
}
