package movi.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ctvit.p2cloud.R;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;
import movi.lib.camera.FUckTest;
import movi.ui.db.VideoInfo;
import movi.ui.fragment.UpLoadFragment;
import movi.ui.view.RangeSeekBar;
import movi.utils.AppManager;
import movi.utils.MyTextUtils;
import movi.utils.PreferencesUtil;
import movi.utils.TimeUtils;
import movi.utils.ToastUtils;
import movi.utils.VideoUtils;
import movi.lib.videocompress.FFMPEGUtils;
import movi.ui.view.CustomDialog;
import movi.ui.view.MyAlertDialog;
import movi.utils.XmlUtils;

/**
 * Created by chjj on 2016/5/11.
 * 剪辑列表
 */
@ContentView(value = R.layout.activity_news_cut)
public class NewsLocalCutActivity extends Activity {
    @ViewInject(R.id.layout)
    private LinearLayout layout;
    @ViewInject(R.id.mVideoView)
    private VideoView videoView;
    @ViewInject(R.id.pg_iv)
    private ProgressBar pg_iv;
    @ViewInject(R.id.txtTimeLenth)
    private TextView txtTimeLenth;
    @ViewInject(R.id.imgPlay)
    private ImageView imgPlay;
    @ViewInject(R.id.img_bac)
    private ImageView img_bac;
    private RangeSeekBar<Integer> seekBar;
    public Integer MinValue = 0;
    public Integer MaxValue = 0;
    private CustomDialog dialog;
    //标志位 用来设置红框背景 默认是第一个
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    private int[] thumbs = new int[]{R.mipmap.ic_normal, R.mipmap.ic_press};
    private String path = "";
    private static final String TAG = NewsLocalCutActivity.class.getSimpleName();
    private android.media.MediaPlayer mediaPlayer;
    private int TIME_MILLS = 40;

    //当前一个thumb拖动时 记录当前中间的位置 记录最小位置
    private Integer currentNormal = 0;
    //成员state变量 默认是最小的thumb
    private static int STATE = 0;
    //记录大小值
    public Integer minValue = 0;
    public Integer maxValue = 0;
    private boolean reset = false;
    private boolean isPass = true;
    //标志位 用来设置红框背景 默认是第一个
    private String minText = "00:00:00:00";
    private String maxText = "00:00:00:00";
    private VideoInfo mVideoInfo;
    //默认状态
    private static final String STATUS = "等待";
    //默认进度
    private static final int PROGRESS = 0;
    private boolean isComplet = true;
    private String newFileName;
    private String newFilePath;
    private File outFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        x.view().inject(this);
        initBundle();
        initView();
    }


    //初始化一些参数
    private void initBundle() {
        mVideoInfo = (VideoInfo) getIntent().getSerializableExtra("bean");
        if (mVideoInfo == null) {
            return;
        }
        this.path = mVideoInfo.getVideoPath();
        Glide.with(this).load(mVideoInfo.getPicPath()).into(img_bac);
        img_bac.setVisibility(View.VISIBLE);
        LogUtil.d("------------" + path);

    }


    /**
     * 重置背景
     */
    public void resetBac() {
        BitmapDrawable drawable = (BitmapDrawable) img_bac.getBackground();
        if (drawable != null) {
            drawable = null;
            img_bac.setBackgroundDrawable(null);
        }
    }

    /**
     * 初始化
     */
    private void initView() {
        layout.removeAllViews();
        seekBar = new RangeSeekBar<Integer>(this, 0, 0, 1000);
        layout.addView(seekBar);
        seekBar.setInvisibleThumb(false);
        if (path == null || path.equals("")) {
            return;
        }
        img_bac.setVisibility(View.VISIBLE);
        resetBac();
        String imgUrl = mVideoInfo.getPhotoPath();
        if (imgUrl != null) {
            Glide.with(this).load(imgUrl).into(img_bac);
        }
        pg_iv.setVisibility(View.VISIBLE);
        isPass = true;
        videoView.setVideoPath(path);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                pg_iv.setVisibility(View.GONE);
                mp.setPlaybackSpeed(1.0f);
                LogUtil.d("--------当前的Long类型的最大值为" + videoView.getDuration());
                int duration = maxValue = (int) videoView.getDuration();
                LogUtil.d("--------当前int最大的duration" + duration);
                seekBar.setRangeSeekBarParam(minValue, (int) currentNormal, duration);
                LogUtil.d("--------当前的min类型的最大值为" + minValue);
                addListener();
                seekBar.invalidate();
            }
        });
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                Log.d("onInfo", what + " --- " + extra);
                pg_iv.setVisibility(View.GONE);
                if (what == android.media.MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    pause();
                } else if (what == android.media.MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    mp.start();
                    if (isPass) {
                        SystemClock.sleep(80);
                        mp.pause();
                    }
                }
                return true;
            }
        });

        //监听事件使之处于空闲状态
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mp.reset();
                currentNormal = 0;
                return false;
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                setMediaPosition(0);
                seekBar.setProgress(0);
                seekBar.setInvisibleThumb(false);
                currentNormal = 0;
                isPass = true;
                videoView.stopPlayback();
                isComplet = true;
                initView();
            }
        });
        imgPlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (reset) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        setMediaPosition(currentNormal);
                        SystemClock.sleep(500);
                    }
                    reset = false;
                }
                return false;
            }

        });
    }


    private void addListener() {
        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {

            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
                                                    Integer minValue, Integer normalValue, Integer maxValue, int state) {
                Log.d("当前拖动的seekbar为最小值 为", minValue + "");
                Log.d("当前拖动的seekbar为中等值 为", normalValue + "");
                Log.d("当前拖动的seekbar为最大值 为", maxValue + "");
                img_bac.setVisibility(View.GONE);
                bar.minText = RangeSeekBar.formatDuration(minValue / 40);
                bar.maxText = RangeSeekBar.formatDuration(maxValue / 40);
                minText = bar.minText;
                maxText = bar.maxText;

                LogUtil.d("-----------------------------" + "最小的" + minValue + "最大的" + maxValue);
                switch (state) {
                    case 0:
                        /**
                         * 代表拖动了前面的那个
                         * 设置图片背景，同时将中间重置到最小的那个位置
                         * 同时隐藏中间的thumb
                         * seek  到 最小thumb的位置
                         */
                        seekBar.setMinThumbRes(R.mipmap.ic_normal);
                        seekBar.setMaxThumbRes(R.mipmap.ic_press);
                        //隐藏中间的那个同时设置offset
                        seekBar.setInvisibleThumb(true);
                        seekBar.setProgress(minValue);
                        currentNormal = minValue;
                        Log.d("当前拖动中间thumb的时候的中等值", currentNormal + "");
                        setMediaPosition(minValue);
                        break;
                    case 2:
                        /**
                         * 代表拖动了后边的哪一个
                         * 设置图片背景中间位置不动
                         * seek到最大thumb的位置
                         */
                        seekBar.setMinThumbRes(R.mipmap.ic_press);
                        seekBar.setMaxThumbRes(R.mipmap.ic_normal);
                        currentNormal = normalValue;
                        reset = true;
                        setMediaPosition(maxValue);
                        break;
                }
                STATE = state;
                NewsLocalCutActivity.this.minValue = minValue;
                NewsLocalCutActivity.this.maxValue = maxValue;
                //同时隐藏中间进度条
                seekBar.setInvisibleThumb(false);
            }
        });
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isPass = true;
                return false;
            }
        });
    }


    private void start() {
        isComplet = false;
        img_bac.setVisibility(View.GONE);
        imgPlay.setImageResource(R.mipmap.imb_play);
        handler.postDelayed(runnable, 500);
        videoView.start();

    }

    public void pause() {
        imgPlay.setImageResource(R.mipmap.imb_play_normal);
        handler.removeCallbacks(runnable);
        videoView.pause();

    }

    private void seekTo(long position) {
        videoView.start();
        videoView.seekTo(position);
        videoView.pause();
        LogUtil.d("--------------------------当前的seekto的位置为" + position);
    }

    /**
     * seek到相应的位置同时暂停
     *
     * @param position
     */
    public void setMediaPosition(long position) {
        pause();
        seekTo(position);
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress((int) videoView.getCurrentPosition());
            LogUtil.d("-----------当前进度" + videoView.getCurrentPosition());
            currentNormal = (int) videoView.getCurrentPosition();
            handler.postDelayed(runnable, 500);
        }
    };
    private Handler handler = new Handler();

    private void showTitleDialog() {
        View rootView = LayoutInflater.from(this).inflate(R.layout.title_dialog, null);
        final EditText et_title = (EditText) rootView.findViewById(R.id.et_title);
        final MyAlertDialog dialog = new MyAlertDialog(this).builder();
        dialog.setView(rootView);
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyTextUtils.isEmpty(et_title, "视频名称不能为空，请填写")) {
                    dialog.dismiss();
                    String title = et_title.getText().toString();
                    //开始压缩
                    newFileName = compressVideo(mVideoInfo.getVideoPath());
                    LogUtil.d("----------当前保存的裁剪之后的视频为" + outFile.getAbsolutePath());
                    insertIntoSql(outFile, title);
                    finish();
                }
            }
        });
        dialog.show();
    }

    /**
     * 事件机制
     *
     * @param view
     */
    @Event(value = {R.id.imgTo, R.id.imgBack, R.id.imgPlay, R.id.txtAdd, R.id.txtBack})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtAdd:
                /**
                 * 获取相应的bean然后
                 * 保存到数据库可供获取
                 * 1获取文件路径
                 * 2创建额外保存的路径
                 */
                try {
                    if (mVideoInfo == null) {
                        return;
                    }
                    File dir = new File(Environment.getExternalStorageDirectory() + "/video/");
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    outFile = new File(dir, System.currentTimeMillis() + ".mp4");
                    if (outFile.exists()) {
                        outFile.delete();
                    }
                    outFile.createNewFile();
                    LogUtil.d("----------当前的minValue" + minValue);
                    FUckTest.startTrim(new File(mVideoInfo.getVideoPath()),
                            outFile, minValue, maxValue);
                    //开始保存相应的截取剪辑
                    if (outFile == null) {
                        ToastUtils.showToast(this, "剪辑失败");
                        return;
                    }
                    mVideoInfo.setVideoPath(outFile.getAbsolutePath());
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + outFile.getAbsolutePath())));
                    //压缩之前先设置title
                    showTitleDialog();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.imgPlay:
                img_bac.setVisibility(View.GONE);
                isPass = false;
                if (videoView.isPlaying()) {
                    pause();
                } else {
                    start();
                    //同时显示中间的thumb
                    seekBar.setInvisibleThumb(true);
                }
                break;
            case R.id.imgBack:
                isPass = true;
                //往后跳一帧
                if (STATE == 0) {
                    if (minValue - TIME_MILLS <= 0) {
                        minValue = 0;
                    } else {
                        minValue = minValue - TIME_MILLS;
                    }
                    seekBar.setMinWard(minValue);
                    setMediaPosition(minValue);
                } else if (STATE == 2) {
                    //往前跳一帧
                    if (maxValue - TIME_MILLS <= minValue) {
                        maxValue = minValue;
                    } else {
                        maxValue = maxValue - TIME_MILLS;

                    }
                    seekBar.setMaxWard(maxValue);
                    setMediaPosition(maxValue);
                }
                break;
            case R.id.imgTo:
                isPass = true;
                if (STATE == 0) {
                    if (minValue + TIME_MILLS >= maxValue) {
                        minValue = maxValue;
                    } else {
                        minValue = minValue + TIME_MILLS;
                    }
                    seekBar.setMinWard(minValue);
                    setMediaPosition(minValue);
                } else if (STATE == 2) {
                    //往前跳一帧
                    if (maxValue + TIME_MILLS >= videoView.getDuration()) {
                        maxValue = (int) videoView.getDuration();
                    } else {
                        maxValue = maxValue + TIME_MILLS;
                    }
                    seekBar.setMaxWard(maxValue);
                    setMediaPosition(maxValue);
                }
                break;
            case R.id.txtBack:
                this.finish();
                break;
        }
    }


    /**
     * 保存到数据库
     */
    /**
     * 插入到sql中
     * 1首先获取视频缩略图保存在本地并返回保存本地的路径
     * 2其次通过数据库保存   1视频  时长   2视频缩略图路径 3视频标题 4视频状态
     * 5视频进度
     */
    private void insertIntoSql(File outFile, String movieTitle) {
        try {
            String picPath = XmlUtils.saveBitMap(outFile.getAbsolutePath());
            if (picPath == null) {
                ToastUtils.showToast(this, "视频必须大于两秒");
                return;
            }
//            String title = CommonUtils.getRandomStr();
            String Status = STATUS;
            int progress = PROGRESS;
            /**
             * 开始存入数据库
             */
            VideoInfo videoInfo = new VideoInfo();
            videoInfo.setProgress(progress);
            videoInfo.setPicPath(picPath);
            videoInfo.setStatus(Status);
            //需要换算成为00:00模式
            LogUtil.d("------------videoInfo.getVideoPath" + mVideoInfo.getVideoPath());
            LogUtil.d("------------" + VideoUtils.getTimeLeanth(this, mVideoInfo.getVideoPath()));
            LogUtil.d("------------" + TimeUtils.getFenMiaoDuration(VideoUtils.getTimeLeanth(this, mVideoInfo.getVideoPath())));
            videoInfo.setTimeLeanth(TimeUtils.getFenMiaoDuration(VideoUtils.getTimeLeanth(this, mVideoInfo.getVideoPath())));
            videoInfo.setTitle(movieTitle);
            videoInfo.setVideoPath(mVideoInfo.getVideoPath());
            videoInfo.setCompressFileName(newFileName);
            videoInfo.setTxtUploadText("上载");
            if (1 == PreferencesUtil.getInt("UL")) {
                videoInfo.setUploadType(true);
            } else {
                videoInfo.setUploadType(false);
            }
            VideoInfo IvideoInfo = XmlUtils.getXmlMetaData(this, videoInfo, mVideoInfo.getVideoPath(), picPath);
            Intent intent = new Intent(UpLoadFragment.ACTION_SELECT);
            intent.putExtra("bean", IvideoInfo);
            sendBroadcast(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        videoView.stopPlayback();
        AppManager.getAppManager().finishActivity(this);
        super.onDestroy();

    }


    /**
     * 调用此方法进行视频压缩
     *
     * @param path
     */
    private String compressVideo(String path) {
        FFMPEGUtils fmu = new FFMPEGUtils(this);
//        fmu.setOnVideoCompletLitener(this);
        String newFilePath = creteNewFile(path);
        fmu.cropVideo(path, newFilePath);
        return newFilePath;
    }


    /**
     * 创建新的文件路径
     */
    String fileName = "";

    public String creteNewFile(String path) {
        if (path != null) {

            fileName = path.substring(path.lastIndexOf("/") + 1);
        }
        File dirFile = new File(Environment.getExternalStorageDirectory(), "video/video");
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File videoFile = new File(dirFile, fileName);
        if (videoFile.exists()) {
            videoFile.delete();
        }
        try {
            videoFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtil.d("--------------新的文件路径" + videoFile.getAbsolutePath());
        return videoFile.getAbsolutePath();
    }

}
