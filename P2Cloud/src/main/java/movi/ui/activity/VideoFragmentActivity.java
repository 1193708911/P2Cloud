package movi.ui.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ctvit.p2cloud.R;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import movi.MainApplication;
import movi.base.BaseActivity;
import movi.base.Response;
import movi.net.biz.NewsClipStatusBiz;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.ui.bean.NewsPicListBean;
import movi.ui.view.CustomVideoView;
import movi.utils.AppManager;
import movi.utils.CommonUtils;
import movi.utils.TimeUtils;
import movi.utils.ToastUtils;

import static java.lang.reflect.Modifier.STATIC;

/**
 * Created by chjj on 2016/6/17.
 */
@ContentView(R.layout.activity_fr_video)
public class VideoFragmentActivity extends BaseActivity implements View.OnTouchListener {
    @ViewInject(R.id.txtNewsStatus)
    private TextView txtNewsStatus;
    @ViewInject(R.id.s_view)
    private CustomVideoView videoView;
    @ViewInject(R.id.sb)
    private SeekBar sb;
    @ViewInject(R.id.pg_iv)
    private ProgressBar pg_iv;
    @ViewInject(R.id.btn_start_pause)
    private ImageButton btn_start_pause;
    @ViewInject(R.id.txtStart)
    private TextView txtStart;
    @ViewInject(R.id.txtDuration)
    private TextView txtDuration;
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
        AppManager.getAppManager().addActivity(this);
        x.view().inject(this);
        initBundle();
        //初始化并播放
        initAndPlay();
        //设置所有监听
        setListener();

    }

    /**
     * 初始化当前的数据
     */
    private void initBundle() {
        picBean = (NewsPicListBean.NewsPicBean) getIntent().getSerializableExtra("bean");
        if (picBean != null && picBean.getMp4().size() > 0) {
            this.path = (String) picBean.getMp4().get(currentVideo);
            LogUtil.d("当前视频资源的地址为"+path);
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
               if(i!=picBean.getClip_status().size()-1){
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
                    ToastUtils.showToast(VideoFragmentActivity.this, "视频正在缓冲中，请稍后");
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
                                Thread.sleep(50);
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
        videoView.setOnTouchListener(VideoFragmentActivity.this);
    }


    @Event({R.id.txtBack, R.id.btn_start_pause,R.id.imgPre,R.id.imgNext})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtBack:
                this.finish();
                break;
            case R.id.imgPre:
                showPreOrNext(STATE_PREFERENCE);
                break;
            case R.id.imgNext:
                showPreOrNext(STATE_NEXT);
                break;

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
                currentVideo = picBean.getMp4().size()-1;
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
