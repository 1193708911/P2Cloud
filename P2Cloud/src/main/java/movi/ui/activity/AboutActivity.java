package movi.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctvit.p2cloud.R;

import org.xutils.x;

import movi.MainApplication;
import movi.ui.bean.ChannelListBean;
import movi.utils.AppManager;
import movi.utils.CommonUtils;

public class AboutActivity extends Activity {
    private TextView txtBack;
    private TextView txtTitle;
    private TextView txtVersion;
    private TextView txtName;
    private ImageView imgLogle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);
        AppManager.getAppManager().addActivity(this);
        initView();
        setData();
    }
    private void initView() {
        txtBack = (TextView) findViewById(R.id.txtBack);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtVersion = (TextView) findViewById(R.id.txtVersion);
        txtName = (TextView) findViewById(R.id.txtName);
        imgLogle = (ImageView) findViewById(R.id.imgLogle);
        txtVersion.setText( CommonUtils.getAppVersionName(this));
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * 初始化相关的信息
     */
    private void setData() {
        ChannelListBean.ChannelBean channelBean = MainApplication.getInstance().getChannelBean();
        if (channelBean != null) {
            txtName.setText(channelBean.getTitle());
            txtTitle.setText(channelBean.getCorporatemsg());
            x.image().bind(imgLogle, channelBean.getApplogobig());
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
}
