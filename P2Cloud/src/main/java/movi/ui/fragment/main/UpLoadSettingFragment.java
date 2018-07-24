package movi.ui.fragment.main;

import android.view.View;
import android.widget.ImageView;

import com.ctvit.p2cloud.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import movi.base.BaseFragment;
import movi.utils.PreferencesUtil;

/**
 * Created by chjj on 2016/7/27.
 * 设置界面
 *
 * @author xufuqiang
 */
@ContentView(R.layout.fragment_upload_setting)
public class UpLoadSettingFragment extends BaseFragment {
    @ViewInject(R.id.img360)
    private ImageView img360;
    @ViewInject(R.id.img480)
    private ImageView img480;
    @ViewInject(R.id.img720)
    private ImageView img720;
    @ViewInject(R.id.imgBondUpLoad)
    private ImageView imgBondUpLoad;
    @ViewInject(R.id.imgSelfUpLoad)
    private ImageView imgSelfUpLoad;
    @ViewInject(R.id.imgHigh)
    private ImageView imgHigh;
    @ViewInject(R.id.imgCenter)
    private ImageView imgCenter;
    @ViewInject(R.id.imgLov)
    private ImageView imgLov;


    @Override
    protected int getResource() {
        return R.layout.fragment_upload_setting;
    }

    @Override
    protected void afterViews() {
        super.afterViews();
        initData();
    }

    /**
     * 初始化相应的数据
     */
    private void initData() {
        resetSettingBac(null);
        if (PreferencesUtil.getInt("FBL") == 360) {
            img360.setVisibility(View.VISIBLE);
        } else if (PreferencesUtil.getInt("FBL") == 480) {
            img480.setVisibility(View.VISIBLE);
        } else if (PreferencesUtil.getInt("FBL") == 720) {
            img720.setVisibility(View.VISIBLE);
        } else {
            img480.setVisibility(View.VISIBLE);
        }
        /**
         * 自动上载还是手动上载
         */
        resetUpLoadBac(null);
        if (PreferencesUtil.getInt("UL") == 1) {
            imgSelfUpLoad.setVisibility(View.VISIBLE);
        } else if (PreferencesUtil.getInt("UL") == 2) {
            imgBondUpLoad.setVisibility(View.VISIBLE);
        } else {
            imgBondUpLoad.setVisibility(View.VISIBLE);
        }
        /**
         * 画质设置
         */

        resetBiteBac(null);
        if (PreferencesUtil.getString("bite").equalsIgnoreCase("5000k")) {
            imgHigh.setVisibility(View.VISIBLE);
        } else if (PreferencesUtil.getString("bite").equalsIgnoreCase("480k")) {
            imgLov.setVisibility(View.VISIBLE);
        } else {
            imgCenter.setVisibility(View.VISIBLE);
        }


    }

    /**
     * 点击监听事件
     *
     * @param view
     */
    @Event(value = {R.id.rlSelfUpLoad, R.id.rlBondUpLoad, R.id.rl720, R.id.rl480, R.id.rl360, R.id.rlHigh, R.id.rlCenter, R.id.rlLov})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl360:
                resetSettingBac(img360);
                PreferencesUtil.putInt("FBL", 360);
                break;
            case R.id.rl480:
                resetSettingBac(img480);
                PreferencesUtil.putInt("FBL", 480);
                break;
            case R.id.rl720:
                resetSettingBac(img720);
                PreferencesUtil.putInt("FBL", 720);
                break;
            case R.id.rlBondUpLoad:
                resetUpLoadBac(imgBondUpLoad);
                PreferencesUtil.putInt("UL", 2);
                break;
            case R.id.rlSelfUpLoad:
                resetUpLoadBac(imgSelfUpLoad);
                PreferencesUtil.putInt("UL", 1);
                break;

            case R.id.rlHigh:
                resetBiteBac(imgHigh);
                PreferencesUtil.putString("bite", "5000k");
                break;

            case R.id.rlCenter:
                resetBiteBac(imgCenter);
                PreferencesUtil.putString("bite", "2500k");
                break;

            case R.id.rlLov:
                resetBiteBac(imgLov);
                PreferencesUtil.putString("bite", "800k");
                break;
        }
    }


    /**
     * 重置背景
     */
    private void resetSettingBac(ImageView imageView) {
        img360.setVisibility(View.GONE);
        img480.setVisibility(View.GONE);
        img720.setVisibility(View.GONE);
        if (imageView == null) {
            return;
        }
        imageView.setVisibility(View.VISIBLE);

    }

    /**
     * 重置码率背景
     */
    public void resetBiteBac(ImageView imageView) {
        imgHigh.setVisibility(View.GONE);
        imgCenter.setVisibility(View.GONE);
        imgLov.setVisibility(View.GONE);
        if (imageView == null) {
            return;
        }
        imageView.setVisibility(View.VISIBLE);
    }


    /**
     * 重置上载背景
     */
    private void resetUpLoadBac(ImageView imageView) {
        imgBondUpLoad.setVisibility(View.GONE);
        imgSelfUpLoad.setVisibility(View.GONE);
        if (imageView == null) {
            return;
        }
        imageView.setVisibility(View.VISIBLE);
    }

}
