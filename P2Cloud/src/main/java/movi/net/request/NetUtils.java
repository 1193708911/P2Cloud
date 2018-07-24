package movi.net.request;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Map;

import movi.MainApplication;
import movi.ui.activity.LoginActivity;
import movi.base.BaseActivity;
import movi.base.Response;
import movi.net.config.ServerConfig;
import movi.utils.AppManager;
import movi.utils.CommonUtils;
import movi.utils.PreferencesUtil;
import movi.utils.ToastUtils;
import movi.ui.view.MyAlertDialog;

/**
 * 网络封装请求类
 * 请求发送
 */
public class NetUtils {
    public static void httpCommonPost(String type, String urlType, Map<String, String> parmMap, Callback.CommonCallback<String> callBack) {
        if (!CommonUtils.isNetworkAvailable(MainApplication.getContext())) {
            ToastUtils.showToast(MainApplication.getContext(), "当前网络不可用");
            return;
        }
        String url = "";
        if (urlType.equals(ServerConfig.SERVER_PATH_APP + CommonUtils.stringMD5("中视云"))) {
            url = urlType;
        } else if (urlType.equals(ServerConfig.SERVER_PATH_ADVISE)) {
            url = urlType;
        } else if (urlType.equals(ServerConfig.SERVER_PATH_NOTIFY_UPLOAD)) {
            url = urlType;
        } else if(urlType.equals(MainApplication.getInstance().getServerPath()+ServerConfig.SERVER_PATH_NOTIFY_UPLOAD)){
            url=urlType;
        }else {
            if (MainApplication.getInstance().getServerPath() != null && !MainApplication.getInstance().getServerPath().equals("")) {
                url = MainApplication.getInstance().getServerPath() + urlType;
            } else {
                if (PreferencesUtil.getString("address") != null) {
                    url = PreferencesUtil.getString("address") + urlType;
                }else{
                    return;
                }
            }
        }
        LogUtil.d("当前的url的地址为" + url);
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(15000);
        params.setCharset("UTF-8");
        if (parmMap != null) {
            for (Map.Entry<String, String> entry : parmMap.entrySet()) {
                params.addParameter(entry.getKey(), entry.getValue());
                LogUtil.d("请求参数列表为" + entry.getKey() + "=" + entry.getValue());
            }
        } else {
            ToastUtils.showToast(MainApplication.getContext(), "请求参数为空");
        }
        if (type.equals(ServerConfig.GET)) {
            x.http().get(params, callBack);
        } else if (type.equals(ServerConfig.POST)) {
            x.http().post(params, callBack);

        }
    }

    /**
     * 接收返回参数处理
     *
     * @param type
     * @param urlType
     * @param paramMap
     * @param callback
     */
    public static void httpRequest(String type, String urlType, Map<String, String> paramMap, final RequestCallback callback) {

        httpCommonPost(type, urlType, paramMap, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.d("当前的返回结果"+result);
                if (callback != null) {
                    Class<? extends Response> resultType = callback.getResultType();
                    Response error = new Response();
                    LogUtil.d("后台返回参数" + result);
                    if (resultType == null) {
                        resultType = Response.class;
                    }
                    error.setStatus(1);
                    if (TextUtils.isEmpty(result)) {
                        callback.onFailure(error);
                        return;
                    }
                    Response resObj = null;
                    try {
                        resObj = JSON.parseObject(result, resultType);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (resObj == null) {
                        error.setStatus(1);
                        error.setMsg("解析错误");
                        callback.onFailure(resObj);
                        return;
                    } else {
                        /**
                         * 进行判断后台是否
                         * 失效
                         */
                        if (resObj.getStatus() == 414) {
                            showAletDilog();
                            return;
                        }
                        callback.onSuccess(resObj);
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
                LogUtil.d("请求调用返回错误信息接口为" + e.getMessage());
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Response error = new Response();
                error.setStatus(1);
                callback.onFailure(error);
            }

            @Override
            public void onFinished() {
            }
        });
    }


    /**
     * 退出登录
     * 杀死进程
     */
    private static void exit() {
        try {
            MainApplication.getDbManager().dropDb();
            android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
            System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
            AppManager.getAppManager().finishAllActivity();
            Intent intent = new Intent(MainApplication.getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MainApplication.getContext().startActivity(intent);
            LogUtil.d("当前执行了退出接口");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static void showAletDilog() {
        final MyAlertDialog dialog = new MyAlertDialog(BaseActivity.mContext).builder();
        dialog.setTitle("提示");
        dialog.setMsg("登录超时，请重新登录!");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                exit();

            }
        });
        dialog.show();


    }
}
