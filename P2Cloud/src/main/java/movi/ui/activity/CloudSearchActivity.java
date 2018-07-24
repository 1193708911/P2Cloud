package movi.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ctvit.p2cloud.R;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import movi.ui.adapter.CloudListAdapter;
import movi.ui.bean.CloudListBean;
import movi.net.config.CloudStaticConfig;
import movi.ui.fragment.main.HomeFragment;
import movi.utils.AppManager;
import movi.view.lib.SystemBarTintManager;

/**
 * Created by chjj on 2016/5/10.
 * 云端素材
 */
public class CloudSearchActivity extends Activity implements AdapterView.OnItemClickListener {
    @ViewInject(R.id.lv_search)
    private ListView lv_search;
    @ViewInject(R.id.imgClear)
    private ImageView imgClear;
    @ViewInject(R.id.search)
    private ImageView search;
    @ViewInject(R.id.et_search)
    private EditText et_search;
    @ViewInject(R.id.ll_search)
    private LinearLayout ll_search;
    @ViewInject(R.id.txtBack)
    private TextView txtBack;
    @ViewInject(R.id.txtCancel)
    private TextView txtCancel;
    private ArrayList<CloudListBean.CloudBean> cloudList = new ArrayList<>();
    //过滤集合
    private ArrayList<CloudListBean.CloudBean> mCacheList = new ArrayList<>();
    //临时集合
    private ArrayList<CloudListBean.CloudBean> mCloudList = new ArrayList<>();
    private CloudListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud);
        x.view().inject(this);
        AppManager.getAppManager().addActivity(this);
//        setStatusColor();
        initAdapter();
        initData();
        initView();

    }
  /*  public void setStatusColor() {
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
    /**
     * 初始化数据
     */
    private void initData() {
        this.mCacheList = CloudStaticConfig.cloudList;
        if(mCacheList==null){
            return;
        }
        cloudList.addAll(mCacheList);
        adapter.notifyDataSetChanged();
    }

    //初始化通用设置
    private void initView() {
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    cloudList.clear();
                    cloudList.addAll(mCacheList);
                    adapter.notifyDataSetChanged();
                    imgClear.setVisibility(View.GONE);
                    return;
                }
                imgClear.setVisibility(View.VISIBLE);
                mCloudList.clear();
                for (int i = 0; i < mCacheList.size(); i++) {
                    if (mCacheList.get(i).getClip_name().contains(s.toString())) {
                        mCloudList.add(mCacheList.get(i));
                    }
                    cloudList.clear();
                    cloudList.addAll(mCloudList);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
        lv_search.setOnItemClickListener(this);

    }

    //1/3/2015-05-03
    private void initAdapter() {
        adapter = new CloudListAdapter(this, cloudList);
        lv_search.setAdapter(adapter);
    }

    @Event(value = {R.id.txtBack, R.id.imgClear,R.id.txtCancel})
    private void onclick(View view) {
        switch (view.getId()) {
            case R.id.txtBack:
                this.finish();
                break;
            case R.id.imgClear:
                et_search.setText("");
                break;
            case  R.id.txtCancel:
                this.finish();
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //发送广播通知homeactivity更改背景
        CloudListBean.CloudBean cloudBean=cloudList.get(position);
        Intent intent=new Intent(HomeFragment.ACTION_PRGRESS);
        intent.putExtra("bean",cloudBean);
        sendBroadcast(intent);
        HomeActivity.replaceHomeFragment();
        finish();

    }
}