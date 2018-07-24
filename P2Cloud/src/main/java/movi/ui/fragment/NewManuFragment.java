package movi.ui.fragment;

import android.animation.LayoutTransition;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ctvit.p2cloud.R;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import movi.ui.adapter.CutListAdapter;
import movi.base.BaseActivity;
import movi.base.BaseFragment;
import movi.base.Response;
import movi.ui.bean.HeadPicList;
import movi.ui.bean.NewsWenGaoInfoListBean;
import movi.net.biz.NewsEditWenGaoBiz;
import movi.net.biz.NewsGetWenGaoBiz;
import movi.net.biz.NewsHeadPicBiz;
import movi.net.config.ServerConfig;
import movi.ui.fragment.main.HomeFragment;
import movi.net.request.NetCallBack;
import movi.ui.inter.OnItemClickListenner;
import movi.ui.view.MyAlertDialog;
import movi.utils.CommonUtils;
import movi.utils.MyTextUtils;
import movi.utils.ToastUtils;
import movi.ui.view.lib.RefreshCallBack;
import movi.ui.view.lib.SimpleRefreshHeader;
import movi.view.HorirontalHeaderLayout;
import movi.view.lib.HorizontalRefreshLayout;

import static android.R.id.list;
import static android.media.CamcorderProfile.get;
import static com.ctvit.p2cloud.R.id.layout;
import static com.ctvit.p2cloud.R.id.layoutHeader;
import static com.ctvit.p2cloud.R.id.txtsave;
import static com.ctvit.p2cloud.R.style.dialog;
import static movi.view.HorirontalHeaderLayout.STATE_NEWS_MANU_FRAGMENT;

/**
 * Created by chjj on 2016/5/11.
 * 新闻文稿
 */
@ContentView(value = R.layout.news_manuscript)
public class NewManuFragment extends BaseFragment
        implements NetCallBack {
    @ViewInject(R.id.etKeyWord)
    private EditText etKeyWord;
    @ViewInject(R.id.etManuTitle)
    private EditText etManuTitle;
    @ViewInject(R.id.etWenGao)
    private EditText etWenGao;
    @ViewInject(R.id.txtSave)
    private TextView txtsave;
    private HorizontalRefreshLayout refreshLayout;

    private boolean isSubmit;
    private static int DEFAULT_PIC = 0;
    private static int DEFAULT_EDIT = 1;
    private static int DEFAULT = -1;
    private static final int DEFAULT_WENGAOINFO = 2;
    private NewsWenGaoInfoListBean.NewsWenGaoInfoBean infoBean;
    private HorirontalHeaderLayout layoutHeader;
    private List<HeadPicList.HeadPic> data;

    public void setLayoutHeader(HorirontalHeaderLayout layoutHeader) {
        this.layoutHeader = layoutHeader;
        this.refreshLayout = (HorizontalRefreshLayout) layoutHeader.findViewById(R.id.refresh);
    }

    @Override
    protected void afterViews() {
        super.afterViews();
        initListenner();
    }

    private void initListenner() {
        layoutHeader.setOnManuListenner(new OnItemClickListenner() {
                                            @Override
                                            public void onManuItemClickListenner(View view, int position, List<HeadPicList.HeadPic> list, int state) {
                                                if (HorirontalHeaderLayout.STATE_NEWS_MANU_FRAGMENT == state) {
                                                    data = list;
                                                    HeadPicList.HeadPic picBean = list.get(position);
                                                    sendGetWenGaoEditRequest(picBean.getPlaylist_id());
                                                    LogUtil.d("当前第几个选中NewManu" + position);
                                                }

                                            }

                                            @Override
                                            public void onManuItemSucess(List<HeadPicList.HeadPic> l) {
                                                data = l;

                                            }
                                        }

        );


    }


    //编辑文稿信息
    private void sendEditWenGaoMsg(String id, String playlist_id, String title, String keyword, String content) {
        BaseActivity.showProgressDialog(context);
        DEFAULT = DEFAULT_EDIT;
        NewsEditWenGaoBiz biz = new NewsEditWenGaoBiz();
        biz.setNetCallBack(this);
        biz.requestWenGaoMsg(ServerConfig.POST, id, playlist_id, title, keyword, content);
    }

    @Override
    protected int getResource() {
        return R.layout.news_manuscript;
    }

    public void sendSubmit() {
        HomeFragment.homeFragment.sendSubmitCut();
    }

    @Event(value = {R.id.txtSave, R.id.txtCommit, R.id.txtTijiao})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtTijiao:
                break;
            case R.id.txtCommit:
                if(isEmpty()){
                    ToastUtils.showToast(context,"请先编辑文稿信息");
                    return;
                }
                if(checkIsSaveManu()){
                    sendSubmit();
                }
                break;
            case R.id.txtSave:
                sendSaveManu();

                break;
        }
    }

    private void sendSaveManu() {
        if (layoutHeader.isCheckSubmit()) {
            ToastUtils.showToast(context, "当前是提交状态，不能修改");
            return;
        } else {
            if (layoutHeader.currentPosition >= 0) {
                sendRequestWenGao();
            } else {
                ToastUtils.showToast(context, "请选择一个片段");
            }
        }
    }

    //发送编辑文稿信息
    private void sendRequestWenGao() {
        if (MyTextUtils.isEmpty(etManuTitle, "新闻文稿标题不能为空")
                && MyTextUtils.isEmpty(etKeyWord, "新闻关键字不能为空")
                && MyTextUtils.isEmpty(etWenGao, "新闻文稿内容不能为空")) {
            if (layoutHeader.currentPosition != -1) {
                String id = "0";
                if (infoBean != null) {
                    BaseActivity.showProgressDialog(context);
                    id = infoBean.getId();
                    if (id == null || id.equals("0")) {
                        id = "0";
                    } else {
                        id = infoBean.getId();
                    }
                }
                DEFAULT = DEFAULT_EDIT;
                sendEditWenGaoMsg(id, data.get(layoutHeader.currentPosition).getPlaylist_id(), etManuTitle.getText().toString(), etKeyWord.getText().toString(), etWenGao.getText().toString());
            } else {
                ToastUtils.showToast(context, "请选择一个片段");
            }

        }

    }


    @Override
    public void onSuccess(Response response) {
        BaseActivity.dismissProgressDialog();
        if (response instanceof NewsWenGaoInfoListBean) {
            NewsWenGaoInfoListBean listBean = (NewsWenGaoInfoListBean) response;
            if (listBean == null) {
                resetTextView();
                return;
            }
            infoBean = listBean.getData();
            setData(infoBean);
        } else {
            if (DEFAULT == DEFAULT_EDIT) {
                if (infoBean == null || infoBean.getId() == null || infoBean.getId().equals("0")) {
                    ToastUtils.showToast(context, "添加文稿成功");

                } else {
                    ToastUtils.showToast(context, "编辑文稿成功");
                }
                sendGetWenGaoEditRequest(data.get(layoutHeader.currentPosition).getPlaylist_id());

            }


        }

    }

    private void sendGetWenGaoEditRequest(String id) {
        DEFAULT = DEFAULT_WENGAOINFO;
        BaseActivity.showProgressDialog(context);
        NewsGetWenGaoBiz wenGaoBiz = new NewsGetWenGaoBiz();
        wenGaoBiz.setNetCallBack(this);
        wenGaoBiz.requestWenGaoList(ServerConfig.GET, id);
    }

    //绑定文稿信息
    private void setData(NewsWenGaoInfoListBean.NewsWenGaoInfoBean infoBean) {
        if (infoBean != null) {
            etManuTitle.setText(infoBean.getTitle());
            etWenGao.setText(infoBean.getContent());
            etKeyWord.setText(infoBean.getKeyword());
        } else {
            resetTextView();
        }


    }

    @Override
    public void onFailure(Response error) {
        BaseActivity.dismissProgressDialog();
        if (DEFAULT == DEFAULT_PIC) {
            ToastUtils.showToast(context, "获取列表失败");
            if (refreshLayout != null) {
                refreshLayout.onRefreshComplete();
            }

        } else if (DEFAULT == DEFAULT_WENGAOINFO) {
            resetTextView();
        }


    }

    public void resetTextView() {
        etManuTitle.setText("");
        etWenGao.setText("");
        etKeyWord.setText("");
    }


    public void checkSaveManu() {
        if (!checkIsSaveManu()) {
            showAlertDilaog();
        }
    }



    public   boolean  isEmpty(){
        if (TextUtils.isEmpty(etManuTitle.getText().toString()) ||
                TextUtils.isEmpty(etKeyWord.getText().toString()) ||
                TextUtils.isEmpty(etWenGao.getText().toString())) {
            return true;
        }
        return  false;
    }
    /**
     * 判断是否抽图已经保存
     * 没保存提示保存
     */
    public boolean checkIsSaveManu() {
        if (!layoutHeader.isCheckSubmit()) {
            if (layoutHeader.currentPosition >= 0) {
                if (TextUtils.isEmpty(etManuTitle.getText().toString()) ||
                        TextUtils.isEmpty(etKeyWord.getText().toString()) ||
                        TextUtils.isEmpty(etWenGao.getText().toString())) {
                    return true;
                } else {
                    //只要有一个不等于服务器返回来的数据就提示是否保存
                    if (infoBean != null) {
                        if (!infoBean.getTitle().equals(etManuTitle.getText().toString())
                                || !infoBean.getContent().equals(etWenGao.getText().toString())
                                || !infoBean.getKeyword().equals(etKeyWord.getText().toString())) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    public void showAlertDilaog() {
        final MyAlertDialog dialog = new MyAlertDialog(context).builder();
        dialog.setMsg("是否保存文稿信息");
        dialog.setPositiveButton("是", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtsave.performClick();
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("否", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTextView();
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
}
