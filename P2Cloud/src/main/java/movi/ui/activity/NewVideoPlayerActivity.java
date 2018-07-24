package movi.ui.activity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ctvit.p2cloud.R;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import movi.ui.bean.NewsPicListBean;
import movi.ui.view.CustomVideoView;
import movi.utils.CommonUtils;
import movi.utils.TimeUtils;
import movi.utils.ToastUtils;
import movi.view.lib.SystemBarTintManager;

import static android.R.attr.action;
import static com.ctvit.p2cloud.R.id.imgNext;
import static com.ctvit.p2cloud.R.id.imgPre;

public class NewVideoPlayerActivity extends Activity implements View.OnTouchListener {
    private TextView txtNewsStatus;
    private CustomVideoView videoView;
    private SeekBar sb;
    private ProgressBar pg_iv;
    private ImageButton btn_start_pause;
    private TextView txtStart;
    private TextView txtDuration;
    private TextView txtBack;
    private ImageView imgPre, imgNext;
    private String path = "";
    private static final String TAG = "VideoFragment1";
    private NewsPicListBean.NewsPicBean picBean;
    //声明一个boolean类型的值判断一下是否已经播放完毕
    private boolean isComplet = true;
    private boolean isShow = true;
    private boolean isNoSchedule = true;
    private static final int DELAY_TIME = 5000;
    private boolean isSeek;
    //记录是否缓存完了
    private boolean isSeekedComplet;

    private int currentVideo;

    private static final int STATE_PREFERENCE = 0x001;
    private static final int STATE_NEXT = 0x002;
    Handler videoHandler = new Handler();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //转换相应的时间
            int currentPosition = (int) videoView.getCurrentPosition();
            String time = TimeUtils.formatDuration(currentPosition / 40);
            sb.setProgress((int) currentPosition);
            txtStart.setText(time);

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_ACTION_BAR);//去掉标题栏
        setContentView(R.layout.activity_fr_video);
//        setStatusColor();
        initView();
        initBundle();
        //初始化并播放
        initAndPlay();
        //设置所有监听
        setListener();

    }

   /* public void setStatusColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.drawable.header_layout);
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }*/
    private void initView() {
        txtBack= (TextView) findViewById(R.id.txtBack);
        imgPre= (ImageView) findViewById(R.id.imgPre);
        imgNext= (ImageView) findViewById(R.id.imgNext);
        txtNewsStatus = (TextView) findViewById(R.id.txtNewsStatus);
        videoView = (CustomVideoView) findViewById(R.id.s_view);
        sb = (SeekBar) findViewById(R.id.sb);
        pg_iv = (ProgressBar) findViewById(R.id.pg_iv);
        btn_start_pause = (ImageButton) findViewById(R.id.btn_start_pause);
        txtStart = (TextView) findViewById(R.id.txtStart);
        txtDuration = (TextView) findViewById(R.id.txtDuration);
        txtBack.setOnClickListener(new OnClick());
        imgPre.setOnClickListener(new OnClick());
        imgNext.setOnClickListener(new OnClick());
    }

    /**
     * 初始化当前的数据
     */
    private void initBundle() {
        picBean = (NewsPicListBean.NewsPicBean) getIntent().getSerializableExtra("bean");
        if (picBean != null && picBean.getMp4().size() > 0) {
            this.path = (String) picBean.getMp4().get(currentVideo);
            LogUtil.d("当前视频资源的地址为" + path);
            setData();
            if (path.equals("0")) {
                btn_start_pause.setClickable(false);
                btn_start_pause.setEnabled(false);
            } else {
                btn_start_pause.setClickable(true);
                btn_start_pause.setEnabled(true);

            }
        }
    }

    //绑定相关数据显示状态
    public void setData() {
        txtNewsStatus.clearComposingText();
        txtNewsStatus.setText("");
        if (picBean.getClip_status() != null) {
            for (int i = 0; i < picBean.getClip_status().size(); i++) {
                txtNewsStatus.append(CommonUtils.checkStateColor(String.valueOf(picBean.getClip_status().get(i))));
                if (i != picBean.getClip_status().size() - 1) {
                    txtNewsStatus.append("-");
                }

            }
//            txtNewsStatus.append(CommonUtils.checkStateColor(picBean.getClip_status_end()));
        }


    }

    /**
     * 设置所有监听
     */
    private void setListener() {
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                removePauseHandler();
                reStart();
            }
        });

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    isSeekedComplet = false;
                    pg_iv.setVisibility(View.VISIBLE);
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(1);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        btn_start_pause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!isSeekedComplet) {
                    ToastUtils.showToast(NewVideoPlayerActivity.this, "视频正在缓冲中，请稍后");
                    return;
                }
                {
                    if (!videoView.isPlaying()) {
                        playVideo();
                    } else {
                        pauseVideo();
                    }
                }
            }
        });
        videoView.setOnTouchListener(this);


    }

    /**
     * 初始化并播放
     */
    private void initAndPlay() {
        //第一次和重新播放
        reStart();
    }


    //播放视频
    private void playVideo() {
        removeHandlerDelay();
        videoView.start();
    }

    private void removeHandlerDelay() {
        isNoSchedule = false;
        videoHandler.postDelayed(MyTask, DELAY_TIME);
        btn_start_pause.setImageResource(R.mipmap.imb_play);

    }

    /**
     * 暂停或者停止
     */
    private void pauseVideo() {
        removePauseHandler();
        isNoSchedule = true;
    }

    /**
     * 移除定时任务
     */
    private void removePauseHandler() {
        videoHandler.removeCallbacks(MyTask);
        btn_start_pause.setImageResource(R.mipmap.imb_play_normal);
        btn_start_pause.setVisibility(View.VISIBLE);
        videoView.setOnTouchListener(null);
        videoView.pause();
        isShow = true;
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (isShow) {
                    btn_start_pause.setVisibility(View.GONE);
                    videoHandler.removeCallbacks(MyTask);
                    isShow = false;

                } else {
                    btn_start_pause.setVisibility(View.VISIBLE);
                    videoHandler.postDelayed(MyTask, DELAY_TIME);
                    isShow = true;
                }
                break;

        }

        return true;
    }

    //定时任务
    Runnable MyTask = new Runnable() {

        @Override
        public void run() {

            btn_start_pause.setVisibility(View.GONE);
            isNoSchedule = true;
            isShow = false;
            videoHandler.removeCallbacks(MyTask);


        }

    };
    private boolean isPlaying = false;

    private void reStart() {
        if (path.equals("0")) {
            ToastUtils.showToast(this, "视频地址无效");
            return;
        }
        videoView.setVideoPath(path);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(android.media.MediaPlayer mp) {
                int duration = (int) videoView.getDuration();
                sb.setMax(duration);
                String timeAllLeanth = TimeUtils.formatDuration(duration / 40);
                txtDuration.setText(timeAllLeanth);
                isPlaying = true;
                new Thread() {
                    public void run() {
                        isPlaying = true;
                        while (isPlaying) {
                            handler.sendEmptyMessage(1);
                            try {
                                Thread.sleep(40);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
                mp.setOnSeekCompleteListener(new android.media.MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(android.media.MediaPlayer mp) {
                        if (isSeek) {
                            mp.pause();
                            isSeek = false;
                        }
                        isSeekedComplet = true;
                        pg_iv.setVisibility(View.GONE);
                    }
                });
            }
        });
        pg_iv.setVisibility(View.VISIBLE);
        isSeek = true;
        isSeekedComplet = false;
        videoView.start();
        videoView.seekTo(40);
        videoView.setOnTouchListener(NewVideoPlayerActivity.this);
    }


    private class OnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.txtBack:
                    finish();
                    break;
                case R.id.imgPre:
                    showPreOrNext(STATE_PREFERENCE);
                    break;
                case R.id.imgNext:
                    showPreOrNext(STATE_NEXT);
                    break;

            }
        }
    }

    public void showPreOrNext(int state) {
        if (state == STATE_PREFERENCE) {
            currentVideo = currentVideo - 1;
            if (currentVideo < 0) {
                ToastUtils.showToast(this, "没有上一个视频片段");
                currentVideo = 0;
                return;
            }
            this.path = (String) picBean.getMp4().get(currentVideo);
            reStart();
        } else if (state == STATE_NEXT) {
            currentVideo = currentVideo + 1;
            if (currentVideo >= picBean.getMp4().size()) {
                ToastUtils.showToast(this, "没有下一个视频片段");
                currentVideo = picBean.getMp4().size() - 1;
                return;
            }
            this.path = (String) picBean.getMp4().get(currentVideo);
            reStart();
        }
    }

    @Override
    protected void onDestroy() {
        videoView.stopPlayback();
        super.onDestroy();

    }
}

