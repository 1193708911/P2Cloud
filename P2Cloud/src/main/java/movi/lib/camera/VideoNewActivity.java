package movi.lib.camera;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ctvit.p2cloud.R;

import org.xutils.common.util.LogUtil;

public class VideoNewActivity extends CameraBaseActivity implements SurfaceHolder.Callback {

    /**
     * 视频最大支持15秒
     */
    public static final int VIDEO_TIME_END = 150;
    /**
     * 视频不支持最小录制时间
     */
    public static final int VIDEO_TIME = 0;
    /**
     * 最少得录制多少秒
     */
    private ImageView img_at_last;
    /**
     * 闪现光标图片
     */
    private ImageView img_shan;
    /**
     * 删除录制
     */
    private ImageView img_delete;
    /**
     * 开始录制
     */
    private ImageButton img_start;
    /**
     * 确认
     */
    private ImageView img_enter;
    /**
     * 闪光灯切换
     */
    private ImageView img_flashlight;
    /**
     * 摄像头切换
     */
    private ImageView img_camera;
    /**
     * 选择录像
     */
    private ImageView img_video;
    /**
     * 返回按钮
     */
    private ImageView img_back;
    /**
     * 计时器
     */
    private TimeCount timeCount;
    /**
     * 录制了多少秒
     */
    private int now;
    /**
     * 每次录制结束时是多少秒
     */
    private int old;
    /**
     * 控制闪光灯等布局文件
     */
    private RelativeLayout rl_Title;
    /**
     * 控制录制时长计时器
     */
    private Chronometer mChrometer;
    /**
     * 录制进度控件
     */
    private LinearLayout linear_seekbar;
    /**
     * 屏幕宽度
     */
    private int width;
    /**
     * 偶数才执行
     */
    private int even;
    /**
     * 是否点击删除了一次
     */
    private boolean isOnclick = false;
    /**
     * 录制视频集合
     */
    private ArrayList<VideoNewBean> list;
    /**
     * 录制bean
     */
    private VideoNewBean bean;
    /**
     * 为了能保存到bundler 录制bean
     */
    private VideoNewParentBean parent_bean;
    /**
     * 录制视频保存文件
     */
    private String vedioPath;
    /**
     * 合并之后的视频文件
     */
    private String videoPath_merge;
    /**
     * 是否满足视频的最少播放时长
     */
    private boolean isMeet = false;

    /**
     * 录制视频的类
     */
    private MediaRecorder mMediaRecorder;
    /**
     * 摄像头对象
     */
    private Camera mCamera;
    /**
     * 显示的view
     */
    private SurfaceView surfaceView;
    /**
     * 摄像头参数
     */
    private Parameters mParameters;
    // /** 视频输出质量 */
    private CamcorderProfile mProfile;
    /**
     * 文本属性获取器
     */
    private SharedPreferences mPreferences;
    /**
     * 刷新界面的回调
     */
    private SurfaceHolder mHolder;
    /**
     * 1表示后置，0表示前置
     */
    private int cameraPosition = 1;
    /**
     * 路径
     */
    private String Ppath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/video/";
    private boolean isVideo = false;
    // /** 压缩jni */
    // private LoadJNI vk;

    /**
     * 初始化分辨率
     *
     * @return
     */
    private int mScreenWidht = 1280;
    private int mScreenHeight = 720;
    /**
     * 是否按压下了
     */
    private boolean isOnTouch = false;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_video_new;
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
        img_enter = (ImageView) findViewById(R.id.video_new_img_enter);
        img_back = (ImageView) findViewById(R.id.video_new_img_back);
        img_video = (ImageView) findViewById(R.id.video_new_img_video);
        linear_seekbar = (LinearLayout) findViewById(R.id.video_new_seekbar);
        mChrometer = (Chronometer) findViewById(R.id.mChrometer);
        rl_Title = (RelativeLayout) findViewById(R.id.rl_Title);
        width = getWindowManager().getDefaultDisplay().getWidth();
//        LayoutParams layoutParam = (LayoutParams) surfaceView.getLayoutParams();
//        // 高：宽 4 : 3
//        layoutParam.height = width / 3 * 4;
//        // 隐藏多少dp才能让屏幕显示正常像素
//        layoutParam.topMargin = -(width / 3 * 4 - width - DisplayUtil.dip2px(VideoNewActivity.this, 44));
//        surfaceView.setLayoutParams(layoutParam);
//        LayoutParams layoutParams = (LayoutParams) img_at_last.getLayoutParams();
//        layoutParams.leftMargin = width / VIDEO_TIME_END * VIDEO_TIME;
//        img_at_last.setLayoutParams(layoutParams);
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
        if(null!=parent_bean){
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

    }

    @Override
    protected void onStart() {
        super.onStart();
        // 获取Camera实例
        mCamera = getCamera();
        if (mCamera != null) {
            // 因为android不支持竖屏录制，所以需要顺时针转90度，让其游览器显示正常
//            mCamera.setDisplayOrientation(90);
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
     * @return
     * @version 1.0
     * @createTime 2015年6月16日, 上午10:44:11
     * @updateTime 2015年6月16日, 上午10:44:11
     * @createAuthor WangYuWen
     * @updateAuthor WangYuWen
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    private Camera getCamera() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            camera = null;
            Toast.makeText(this , "该摄像头没有初始化成功" , Toast.LENGTH_SHORT).show();
        }
        return camera;
    }

    private Handler handler = new Handler();

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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void widgetListener() {
        /**
         * 测试
         * 录像请求
         */
        img_start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOnTouch) {
                    if (isOnclick) {
                        ((ImageView) linear_seekbar.getChildAt(linear_seekbar.getChildCount() - 2)).setBackgroundColor(getResources().getColor(
                                R.color.ff1f8fe4));
                    }
                    even = 1;
                    img_delete.setVisibility(View.VISIBLE);
                    img_enter.setVisibility(View.VISIBLE);
                    img_video.setVisibility(View.GONE);
//					img_camera.setVisibility(View.GONE);

                    addView_Red();

                    // 构造CountDownTimer对象
                    timeCount = new TimeCount(30000 - old, 50);
                    timeCount.start();// 开始计时
                    startRecord();
                    isOnTouch = true;
                    /**
                     * 隐藏闪光灯并开始计时
                     */
                    rl_Title.setVisibility(View.GONE);
                    mChrometer.setVisibility(View.VISIBLE);
                } else {
                    stopRecorder();
                    deleteVideo();

                }
            }
        });

        /** 删除按钮 */
        img_delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isOnTouch) {
                    deleteVideo();
                }

            }
        });
        /** 开启或关闭闪光灯 */
        img_flashlight.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (cameraPosition != 0) {// 前置摄像头的时候不能切换闪光灯
                    mParameters = mCamera.getParameters();
                    if (mParameters != null) {
                        if (mParameters.getFlashMode() != null && mParameters.getFlashMode().equals(Parameters.FLASH_MODE_TORCH)) {
                            mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                            img_flashlight.setImageResource(R.mipmap.img_video_new_flashlight_close);
                        } else if (mParameters.getFlashMode() != null && mParameters.getFlashMode().equals(Parameters.FLASH_MODE_OFF)) {
                            mParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
                            img_flashlight.setImageResource(R.mipmap.img_video_new_flashlight_open);
                        }
                        if (mCamera != null) {
                            mCamera.setParameters(mParameters);
                        }
                    }
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
                stopRecorder();
                int size = list.size();
                String[] strs = new String[size];
                videoPath_merge = Ppath + new Date().getTime() + ".mp4";
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

                      /*  Intent it = new Intent(VideoNewActivity.this, VideoActivity.class);
                        it.putExtra("path", videoPath_merge);
                        startActivity(it);*/
                    Intent intent = new Intent();
                    intent.putExtra("path", videoPath_merge);
                    intent.putExtra("timeLeanth", mChrometer.getText().toString());
                    setResult(RESULT_OK, intent);
                    //同时停止计时恢复重置
                    mChrometer.setBase(SystemClock.elapsedRealtime());//复位计时器，停止计时
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        /** 选择录像按钮 */
        img_video.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VideoNewActivity.this, VideoNewSelectActivity.class);
                startActivity(intent);
            }
        });

        img_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (list.size() > 0) {
                    exitVideoNewDialog();
                } else {
                    releaseCamera();
                    finish();
                }
            }
        });

        surfaceView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
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

    private void deleteVideo() {
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
//                    img_delete.setVisibility(View.GONE);
//                    img_enter.setVisibility(View.GONE);
//                    img_video.setVisibility(View.VISIBLE);
//                    img_camera.setVisibility(View.VISIBLE);
                    //同时重置sysytemClock的值
                    mChrometer.setBase(SystemClock.elapsedRealtime());//复位计时器，停止计时
                }
            }
        } else {
            if (linear_seekbar.getChildCount() > 1) {
                isOnclick = true;
                ((ImageView) linear_seekbar.getChildAt(linear_seekbar.getChildCount() - 2)).setBackgroundColor(getResources().getColor(
                        R.color.ff135689));
            }
        }
        even++;
    }

    private void stopRecorder() {
        isOnTouch = false;
        old = now + old;
        if (old >= VIDEO_TIME * 1000) {
            isMeet = true;
        }
        timeCount.cancel();
        addView_black();
        /**
         * 抬起显示闪光灯并且隐藏计时器
         */
        rl_Title.setVisibility(View.VISIBLE);
        mChrometer.setVisibility(View.GONE);
        mChrometer.stop();
        stopRecord();
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
     * @createTime 2015年6月16日, 下午3:45:35
     * @updateTime 2015年6月16日, 下午3:45:35
     * @createAuthor WangYuWen
     * @updateAuthor WangYuWen
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    private void exitVideoNewDialog() {

        Builder builder = new Builder(VideoNewActivity.this);
        builder.setMessage("确定放弃这段视频吗？");
        builder.setTitle("温馨提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
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
     * @createTime 2015年6月16日, 上午10:40:17
     * @updateTime 2015年6月16日, 上午10:40:17
     * @createAuthor WangYuWen
     * @updateAuthor WangYuWen
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    @SuppressLint("NewApi")
    private void switchCamera() {
        // 切换前后摄像头
        int cameraCount = 0;
        CameraInfo cameraInfo = new CameraInfo();
        cameraCount = Camera.getNumberOfCameras();// 得到摄像头的个数

        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);// 得到每一个摄像头的信息
            if (cameraPosition == 1) {
                // 现在是后置，变更为前置
                if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {// 代表摄像头的方位，CAMERA_FACING_FRONT前置
                    // CAMERA_FACING_BACK后置
                    // 前置摄像头时必须关闭闪光灯，不然会报错
                    mParameters = mCamera.getParameters();
                    if (mParameters != null) {
                        if (mParameters.getFlashMode() != null && mParameters.getFlashMode().equals(Parameters.FLASH_MODE_TORCH)) {
                            mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                            img_flashlight.setImageResource(R.mipmap.img_video_new_flashlight_close);
                        }
                        if (mCamera != null) {
                            mCamera.setParameters(mParameters);
                        }
                    }

                    // 释放Camera
                    releaseCamera();

                    // 打开当前选中的摄像头
                    mCamera = Camera.open(i);


//                    mCamera.setDisplayOrientation(90);
                    mCamera.lock();

                    // 通过surfaceview显示取景画面
                    setStartPreview(mHolder);

                    cameraPosition = 0;

                    break;
                }
            } else {
                // 现在是前置， 变更为后置
                if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {// 代表摄像头的方位，CAMERA_FACING_FRONT前置
                    // CAMERA_FACING_BACK后置
                    // 释放Camera
                    releaseCamera();
                    // 打开当前选中的摄像头
                    mCamera = Camera.open(i);
                    mCamera.setDisplayOrientation(90);
                    mCamera.lock();

                    // 通过surfaceview显示取景画面
                    setStartPreview(mHolder);

                    cameraPosition = 1;

                    break;
                }
            }

        }
    }

    /**
     * 定义一个倒计时的内部类
     *
     * @author
     * @version 1.0
     * @Description
     * @date 2015-5-25
     * @Copyright: Copyright (c) 2015 Shenzhen Utoow Technology Co., Ltd.
     * All rights reserved.
     */
    private class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发

        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            now = (int) (30000 - millisUntilFinished - old);
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
     * @createTime 2015年6月15日, 下午4:53:41
     * @updateTime 2015年6月15日, 下午4:53:41
     * @createAuthor WangYuWen
     * @updateAuthor WangYuWen
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    @SuppressWarnings("deprecation")
    private void initCameraParameters() {
        // 初始化摄像头参数
        mParameters = mCamera.getParameters();
        LogUtil.d("00000000000000000当前的屏幕分辨率支持为" + mParameters.flatten());
//        mParameters.setPreviewSize(mProfile.videoFrameWidth, mProfile.videoFrameHeight);
        mParameters.setPreviewFrameRate(mProfile.videoFrameRate);
        mParameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
        //设置图片大小
//        mParameters.setPictureSize(mScreenWidht, mScreenHeight);
        //设置预览大小
        mParameters.setPreviewSize(mScreenWidht, mScreenHeight);
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
     * 开始录制
     *
     * @version 1.0
     * @createTime 2015年6月15日, 下午4:48:49
     * @updateTime 2015年6月15日, 下午4:48:49
     * @createAuthor WangYuWen
     * @updateAuthor WangYuWen
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    @SuppressLint("NewApi")
    private void startRecord() {
        try {
            initRecorder();
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            //首先进行复位
            mChrometer.setBase(SystemClock.elapsedRealtime());//复位计时器，停止计时
            mChrometer.start();
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
     * 初始化当前录制参数
     */
    private void initRecorder() {
        initCameraScreentParams();
        bean = new VideoNewBean();
        vedioPath = Ppath + System.currentTimeMillis() + ".mp4";
        bean.setPath(vedioPath);
        mMediaRecorder = new MediaRecorder();// 创建mediaRecorder对象
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        // 设置录制视频源为Camera(相机)
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setProfile(mProfile);
        //视频帧率
        mMediaRecorder.setVideoFrameRate(25);
        //指定输出的视频大小
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);

        mMediaRecorder.setVideoSize(mScreenWidht, mScreenHeight);
        LogUtil.d("---------视频分辨率" + mScreenWidht + mScreenHeight);
        mMediaRecorder.setVideoEncodingBitRate(1024 * 1024 * 4);// 设置视频一次写多少字节(可调节视频空间大小)
        // 最大期限
        mMediaRecorder.setMaxDuration(35 * 1000);
        // 第4步:指定输出文件 ， 设置视频文件输出的路径

        mMediaRecorder.setOutputFile(vedioPath);
        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());
        // // 设置保存录像方向
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (cameraPosition == 1) {
                //由于不支持竖屏录制，后置摄像头需要把视频顺时针旋转90度、、但是视频本身在电脑上看还是逆时针旋转了90度
//                    mMediaRecorder.setOrientationHint(90);
            } else if (cameraPosition == 0) {
                //由于不支持竖屏录制，前置摄像头需要把视频顺时针旋转270度、、而前置摄像头在电脑上则是顺时针旋转了90度
//                    mMediaRecorder.setOrientationHint(270);
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

    }

    /**
     * 初始化分辨率
     */
    private void initCameraScreentParams() {
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
     * 异常处理
     *
     * @version 1.0
     * @createTime 2015年6月16日, 上午10:49:18
     * @updateTime 2015年6月16日, 上午10:49:18
     * @createAuthor WangYuWen
     * @updateAuthor WangYuWen
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    private void recodError() {
        Builder builder = new Builder(VideoNewActivity.this);
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
     * @createTime 2015年6月15日, 下午4:48:53
     * @updateTime 2015年6月15日, 下午4:48:53
     * @createAuthor WangYuWen
     * @updateAuthor WangYuWen
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    private void stopRecord() {

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
                mMediaRecorder.setOnErrorListener(null);
                mMediaRecorder.setOnInfoListener(null);
                mMediaRecorder.stop();
                // 清除recorder配置
                mMediaRecorder.reset();
                // 释放recorder对象
                mMediaRecorder.release();
                mMediaRecorder = null;
                // 没超过3秒就删除录制所有数据
                if (old < 3000) {
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
     * @createTime 2015年6月25日, 下午6:04:28
     * @updateTime 2015年6月25日, 下午6:04:28
     * @createAuthor WangYuWen
     * @updateAuthor WangYuWen
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    private void clearList() {
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
//                img_delete.setVisibility(View.GONE);
//                img_enter.setVisibility(View.GONE);
//                img_video.setVisibility(View.VISIBLE);
//                img_camera.setVisibility(View.VISIBLE);
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
     * @createTime 2015年6月15日, 下午5:12:31
     * @updateTime 2015年6月15日, 下午5:12:31
     * @createAuthor WangYuWen
     * @updateAuthor WangYuWen
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
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

        videoQualityHigh = false;
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
     * @createTime 2015年6月15日, 下午3:04:21
     * @updateTime 2015年6月15日, 下午3:04:21
     * @createAuthor WangYuWen
     * @updateAuthor WangYuWen
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    private void addView_Red() {
        ImageView img = new ImageView(VideoNewActivity.this);
        img.setBackgroundColor(getResources().getColor(R.color.ff1f8fe4));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DisplayUtil.dip2px(VideoNewActivity.this, 1), LinearLayout.LayoutParams.MATCH_PARENT);
        img.setLayoutParams(layoutParams);
        linear_seekbar.addView(img);
    }

    /**
     * 添加黑色断条
     *
     * @version 1.0
     * @createTime 2015年6月15日, 下午3:03:52
     * @updateTime 2015年6月15日, 下午3:03:52
     * @createAuthor WangYuWen
     * @updateAuthor WangYuWen
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    private void addView_black() {
        ImageView img = new ImageView(VideoNewActivity.this);
        img.setBackgroundColor(Color.BLACK);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DisplayUtil.dip2px(VideoNewActivity.this, 2), LinearLayout.LayoutParams.MATCH_PARENT);
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
     * @param holder
     * @version 1.0
     * @createTime 2015年6月16日, 上午10:48:15
     * @updateTime 2015年6月16日, 上午10:48:15
     * @createAuthor WangYuWen
     * @updateAuthor WangYuWen
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
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
     * @createTime 2015年6月16日, 上午10:38:08
     * @updateTime 2015年6月16日, 上午10:38:08
     * @createAuthor WangYuWen
     * @updateAuthor WangYuWen
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();// 停掉原来摄像头的预览
            mCamera.release();
            mCamera = null;
        }
    }

}
