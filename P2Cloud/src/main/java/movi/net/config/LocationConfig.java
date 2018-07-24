package movi.net.config;

import android.content.Context;
import android.os.Handler;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import org.xutils.common.util.LogUtil;

import java.util.Timer;
import java.util.TimerTask;

import movi.utils.ToastUtils;

/**
 * Created by admin on 2017/4/12.
 */

public class LocationConfig {
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    private Context context;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    //声明定位回调监听器
    public LocationConfig(Context context) {
        this.context = context;
        confingLocationInfo();
    }

    public void confingLocationInfo() {

//初始化定位
        mLocationClient = new AMapLocationClient(context);
//初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
//获取一次定位结果：
//该方法默认为false。
        mLocationOption.setOnceLocation(true);
        mLocationClient.setLocationOption(mLocationOption);
//设置定位回调监听
        mLocationClient.setLocationListener(mAMapLocationListener);

        startLocation();

    }


    public void startLocation() {
        //启动定位
        mLocationClient.startLocation();
    }

    //异步获取定位结果
    AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //解析定位结果
                    amapLocation.getProvince();//省信息
                    amapLocation.getCity();//城市信息
                    amapLocation.getDistrict();//城区信息
                    amapLocation.getStreet();//街道信息
                    amapLocation.getStreetNum();//街道门牌号信息
                    amapLocation.getAoiName();//获取当前定位点的AOI信息
                    amapLocation.getBuildingId();//获取当前室内定位的建筑物Id
//                    ToastUtils.showToast(context, amapLocation.toString());
                    mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
                }
            }
        }
    };
}