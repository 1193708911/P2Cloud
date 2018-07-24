package movi.ui.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ctvit.p2cloud.R;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import io.vov.vitamio.utils.StringUtils;
import kr.co.namee.permissiongen.internal.Utils;
import movi.MainApplication;
import movi.net.biz.NewsUpdateSuCaiNameBiz;
import movi.ui.activity.CloudSearchActivity;
import movi.ui.adapter.CloudListAdapter;
import movi.base.BaseActivity;
import movi.base.BaseFragment;
import movi.base.Response;
import movi.ui.bean.CloudListBean;
import movi.net.biz.NewsCloudBiz;
import movi.net.config.CloudStaticConfig;
import movi.net.config.ServerConfig;
import movi.ui.fragment.main.HomeFragment;
import movi.net.request.NetCallBack;
import movi.ui.view.CustomDialog;
import movi.utils.CommonUtils;
import movi.utils.Constants;
import movi.utils.ToastUtils;
import movi.ui.view.AddPopWindow;
import movi.ui.view.MonthDateView;
import movi.ui.view.XListView;
import movi.utils.Util;

import static android.R.attr.data;
import static com.ctvit.p2cloud.R.id.txtTitle;
import static com.ctvit.p2cloud.R.style.dialog;

/**
 * Created by chjj on 2016/5/10.
 * 云端素材
 */
//        shot_id     素材id
//        ProgramName   别名

@ContentView(R.layout.cloud_fram)
public class CloudFragment extends BaseFragment implements View.OnClickListener,
        MonthDateView.OnCalanderItemClickListener,
        AdapterView.OnItemClickListener,
        NetCallBack,
        XListView.IXListViewListener,
        CloudListAdapter.OnMediaNameClick {
    @ViewInject(R.id.lv_cloud_msg)
    private XListView lv_cloud_msg;
    @ViewInject(R.id.mCalander)
    private TextView mCalander;
    @ViewInject(R.id.imgClear)
    private ImageView imgClear;
    @ViewInject(R.id.search)
    private ImageView search;
    @ViewInject(R.id.et_search)
    private TextView et_search;
    @ViewInject(R.id.ll_search)
    private LinearLayout ll_search;
    private ArrayList<CloudListBean.CloudBean> cloudList = new ArrayList<>();

    private MonthDateView dateView;
    private TextView tv_today;
    private int pager = 1;
    private CloudListAdapter adapter;
    private static final int REFRESH_CODE = 1;
    private static final int LOADER_CODE = 2;
    private static int DEFAULT = 1;
    private static String DEFAULT_DATE = "0";
    private static String DEFAULT_TEXT = "0";
    private static final int DEFAULT_DEFAULT = 3;
    private boolean isOnclick = true;
    private boolean isLoadMore = false;

    private boolean isUpdateSucaiName = false;
    private AddPopWindow popwindow;

    /**
     * 缓存当前year  month  day
     *
     * @return
     */
    private static Calendar calendar = Calendar.getInstance();
    public static int onClickYear = calendar.get(Calendar.YEAR);
    public static int onClickMonth = calendar.get(Calendar.MONTH);
    public static int onClickDay = calendar.get(Calendar.DAY_OF_MONTH);
    private CustomDialog updateDilog;
    public String programName;
    public CloudListBean.CloudBean updateCloudBean;

    @Override
    protected int getResource() {
        return R.layout.cloud_fram;
    }

    //展示日历控件
    private void showCalander(View view) {

        int[] locations = new int[2];
        view.getLocationOnScreen(locations);
        isOnclick = true;
        popwindow.showCaleandarPopupWindow(view, view, locations);
        popwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isOnclick = false;
            }
        });

    }

    /**
     * 初始化日历
     */
    public void initCaleandar() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.date_item, null);
        popwindow = new AddPopWindow(getActivity(), rootView);
        ImageView imagePres = (ImageView) rootView.findViewById(R.id.iv_left);
        ImageView imageNext = (ImageView) rootView.findViewById(R.id.iv_right);
        TextView tv_date = (TextView) rootView.findViewById(R.id.date_text);
        TextView tv_week = (TextView) rootView.findViewById(R.id.week_text);

        tv_today = (TextView) rootView.findViewById(R.id.tv_today);
        dateView = (MonthDateView) rootView.findViewById(R.id.monthDateView);
        tv_today.setOnClickListener(this);
        imagePres.setOnClickListener(this);
        imageNext.setOnClickListener(this);
        dateView.setOnCalanderItemClickListener(this);
        dateView.setTextView(tv_date);
        popwindow.setTouchable(true);
        popwindow.setOutsideTouchable(false);
    }

    @Override
    protected void afterViews() {
        super.afterViews();
        initAdapter();
        initView();
        initCaleandar();
        sendGetCloudRequest(1, DEFAULT_TEXT, DEFAULT_DATE);

    }

    //初始化通用设置
    private void initView() {
        mCalander.setOnClickListener(this);
        imgClear.setOnClickListener(this);
        ll_search.setOnClickListener(this);
        search.setOnClickListener(this);
        imgClear.setOnClickListener(this);
        /**
         * 绑定日期
         */
        String day = Constants.DATE_FORMAT_DAY.format(new Date());
        mCalander.setText(day);

    }

    //发送网络请求绑定数据
    private void sendGetCloudRequest(int page, String str, String date) {
        BaseActivity.showProgressDialog(context);
        NewsCloudBiz biz = new NewsCloudBiz();
        biz.setNetCallBack(this);
        biz.requestCloud(ServerConfig.SERVER_PATH_CLOUD, String.valueOf(page), str, date);
    }

    //1/3/2015-05-03
    private void initAdapter() {
        adapter = new CloudListAdapter(context, cloudList);
        adapter.setOnSuCaiNameClick(this);
        lv_cloud_msg.setAdapter(adapter);
        lv_cloud_msg.setPullLoadEnable(true);
        lv_cloud_msg.setPullRefreshEnable(true);
        lv_cloud_msg.setXListViewListener(this);
        lv_cloud_msg.setOnItemClickListener(this);
    }

    //监听事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mCalander:
                showCalander(v);
                break;
            case R.id.iv_left:
                dateView.onLeftClick();
                break;
            case R.id.iv_right:
                dateView.onRightClick();
                break;
            case R.id.tv_today:
                dateView.setTodayToView();
            case R.id.imgClear:
                et_search.setText("");
                break;
            case R.id.search:
                //发送请求
                if (TextUtils.isEmpty(et_search.getText().toString())) {
                    ToastUtils.showToast(context, "请输入搜索条件");
                    return;
                }
                DEFAULT = DEFAULT_DEFAULT;
//                loadRefreshEnable(false);
                sendGetCloudRequest(0, et_search.getText().toString(), "0");

                break;
            case R.id.ll_search:
                CloudStaticConfig.cloudList = cloudList;
                Intent intent = new Intent(context, CloudSearchActivity.class);
                context.startActivity(intent);
                break;
        }
    }

    //日历监听事件
    @Override
    public void onClickOnDate(String date) {
        if (isOnclick) {
            isOnclick = false;
            return;
        }
        if (date != null) {
            pager = 1;
            cloudList.clear();
            adapter.notifyDataSetChanged();
            DEFAULT_DATE = String.format("%02d", dateView.getmSelYear()) + "-" + String.format("%02d", dateView.getmSelMonth()) + "-" + String.format("%02d", dateView.getmSelDay());
            LogUtil.d("当前选择的日期为" + DEFAULT_DATE);
            sendGetCloudRequest(pager, DEFAULT_TEXT, DEFAULT_DATE);
            /**
             * 缓存时间
             */
            this.onClickYear = dateView.getmSelYear();
            this.onClickMonth = dateView.getmSelMonth();
            this.onClickDay = dateView.getmSelDay();
            popwindow.dismiss();
        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HomeFragment.homeFragment.setNewsCutFragment(cloudList.get(position - 1));
    }

    @Override
    public void onSuccess(Response response) {
        if (isUpdateSucaiName) {
            ToastUtils.showToast(context, response.getMsg());
            isUpdateSucaiName = false;
            /**
             * 同步后台服务器
             */
            for (int i = 0; i < cloudList.size(); i++) {
                if (cloudList.get(i).getShot_id().equals(updateCloudBean.getShot_id())) {
                    cloudList.get(i).setProgram_name(programName);
                    adapter.notifyDataSetChanged();
                }
            }
        } else {
            isLoadMore = false;
            BaseActivity.dismissProgressDialog();
            onLoad();
            CloudListBean cloudListBean = (CloudListBean) response;
            MainApplication.getInstance().setCloudBean(cloudListBean.getData().get(0));
            Message msg = Message.obtain();
            msg.what = DEFAULT;
            msg.obj = cloudListBean;
            handler.sendMessage(msg);
        }
    }

    @Override
    public void onFailure(Response error) {
        ToastUtils.showToast(context, "没有最新素材列表");
        BaseActivity.dismissProgressDialog();
        onLoad();
        if (cloudList.size() > 0 && isLoadMore) {
//            loadRefreshEnable(false);
            return;
        } else {
            cloudList.clear();
            adapter.notifyDataSetChanged();
        }
        isLoadMore = false;
    }

    //获得数据后一定要加onLoad()方法，否则刷新会一直进行，根本停不下来
    private void onLoad() {
        lv_cloud_msg.stopRefresh();
        lv_cloud_msg.stopLoadMore();
        lv_cloud_msg.setRefreshTime("刚刚");
    }

    //下拉刷新
    @Override
    public void onRefresh() {
        DEFAULT = REFRESH_CODE;
        pager = 1;
        loadRefreshEnable(true);
        DEFAULT_DATE = "0";
        DEFAULT_TEXT = "0";
        initCaleandar();
        sendGetCloudRequest(pager, DEFAULT_TEXT, DEFAULT_DATE);
    }

    //上拉加载
    @Override
    public void onLoadMore() {
        DEFAULT = LOADER_CODE;
        isLoadMore = true;
        pager++;
        sendGetCloudRequest(pager, DEFAULT_TEXT, DEFAULT_DATE);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_CODE:
                    cloudList.clear();
                    setCloudData((CloudListBean) msg.obj);
                    break;
                case LOADER_CODE:
                    setCloudData((CloudListBean) msg.obj);
                    break;
                case DEFAULT_DEFAULT:
//                    lv_cloud_msg.setPullLoadEnable(false);
//                    lv_cloud_msg.setPullLoadEnable(false);
                    cloudList.clear();
                    setCloudData((CloudListBean) msg.obj);
                    break;

            }
        }
    };

    //设置数据
    private void setCloudData(CloudListBean cloudListBean) {
        ArrayList<CloudListBean.CloudBean> mcloudList = cloudListBean.getData();
        if (mcloudList == null) {
            return;
        }
        cloudList.addAll(mcloudList);
        CloudStaticConfig.cloudList = cloudList;
        adapter.notifyDataSetChanged();
    }

    /**
     * 提供两个方法
     * 用于控制是否加载更多
     */
    public void loadRefreshEnable(boolean isLoadRefres) {
        lv_cloud_msg.setPullRefreshEnable(isLoadRefres);
        lv_cloud_msg.setPullLoadEnable(isLoadRefres);
    }

    /**
     * 修改素材名称接口
     *
     * @param bean
     */
    @Override
    public void onMediaName(CloudListBean.CloudBean bean) {
        isUpdateSucaiName = true;
        showUpdateProgram(bean);

    }

    /**
     * 更改shotMark字段
     *
     * @param bean
     */
    public void showUpdateProgram(final CloudListBean.CloudBean bean) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        View rootView = LayoutInflater.from(context).inflate(R.layout.alert_dialog, null);
        builder.setContentView(rootView);
        updateDilog = builder.create();
        updateDilog.show();
        final EditText etTitle = (EditText) rootView.findViewById(R.id.etTitle);
        TextView txtSave = (TextView) rootView.findViewById(R.id.txtSave);
        TextView txtCancel = (TextView) rootView.findViewById(R.id.txtCancel);
        TextView txtTitle = (TextView) rootView.findViewById(R.id.txtTitle);
        txtTitle.setText("节目名称:");
        txtSave.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etTitle.getText().toString())) {
                    ToastUtils.showToast(context, "修改别名不能为空");
                    return;
                }
//                //过滤特殊符号
                CloudFragment.this.programName = etTitle.getText().toString();
                CloudFragment.this.updateCloudBean = bean;
                Map<String, String> params = new HashMap<>();
                params.put("shot_id", bean.getShot_id());
                params.put("programname", Util.filterString(context, etTitle.getText().toString()));
                NewsUpdateSuCaiNameBiz biz = new NewsUpdateSuCaiNameBiz();
                biz.setNetCallBack(CloudFragment.this);
                biz.requestUpdateSuCaiName(params);
                updateDilog.dismiss();

            }
        });
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDilog.dismiss();
            }
        });

        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                String editable = etTitle.getText().toString();
                String str = Util.filterString(context, editable.toString());
                if (!editable.equals(str)) {
                    etTitle.setText(str);
                    etTitle.setSelection(str.length()); //光标置后
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }
}