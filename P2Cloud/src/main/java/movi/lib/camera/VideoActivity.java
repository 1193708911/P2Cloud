package movi.lib.camera;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.ctvit.p2cloud.R;


public class VideoActivity extends CameraBaseActivity {
	/**视频控件*/
	private VideoView videoview;
	/**传过来的路径*/
	private String path;
	/**播放按钮*/
	private ImageView img_start;
	/**容器*/
	private RelativeLayout relative;
	
	@Override
	protected int getContentViewId() {
		return R.layout.video;
	}

	@Override
	protected void findViews() {
		videoview = (VideoView) findViewById(R.id.videoView);
		img_start = (ImageView) findViewById(R.id.img_start);
		relative = (RelativeLayout) findViewById(R.id.relative);
	}
	
	@Override
	protected void initGetData() {
		super.initGetData();
		if (getIntent().getExtras()!=null) {
			path = getIntent().getExtras().getString("path");
		}
	}

	@Override
	protected void init() {
		videoview.setVideoPath(path);
		videoview.requestFocus();  
	}

	@Override
	protected void widgetListener() {
		relative.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (videoview.isPlaying()) {
					videoview.pause();
					img_start.setVisibility(View.VISIBLE);
				}else{
					videoview.start();
					img_start.setVisibility(View.GONE);
				}
			}
		});
	}
}
