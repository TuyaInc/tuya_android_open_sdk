package com.tuya.smart.android.demo.config;

import android.content.Context;

import com.tuya.smart.android.common.utils.SafeHandler;
import com.tuya.smart.android.mvp.model.BaseModel;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.builder.ActivatorBuilder;
import com.tuya.smart.sdk.api.ITuyaActivator;
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.enums.ActivatorAPStepCode;
import com.tuya.smart.sdk.enums.ActivatorEZStepCode;
import com.tuya.smart.sdk.enums.ActivatorModelEnum;

import static com.tuya.smart.sdk.enums.ActivatorModelEnum.TY_AP;
import static com.tuya.smart.sdk.enums.ActivatorModelEnum.TY_EZ;

/**
 * Created by letian on 16/3/30.
 */
public class DeviceBindModel extends BaseModel implements IDeviceBindModel {
    public static final String STATUS_FAILURE_WITH_NETWORK_ERROR = "1001";
    public static final String STATUS_FAILURE_WITH_BIND_GWIDS = "1002";
    public static final String STATUS_FAILURE_WITH_BIND_GWIDS_1 = "1003";
    public static final String STATUS_FAILURE_WITH_GET_TOKEN = "1004";
    public static final String STATUS_FAILURE_WITH_CHECK_ONLINE_FAILURE = "1005";
    public static final String STATUS_FAILURE_WITH_OUT_OF_TIME = "1006";
    public static final String STATUS_DEV_CONFIG_ERROR_LIST = "1007";
    public static final int WHAT_EC_ACTIVE_ERROR = 0x02;
    public static final int WHAT_EC_ACTIVE_SUCCESS = 0x03;
    public static final int WHAT_AP_ACTIVE_ERROR = 0x04;
    public static final int WHAT_AP_ACTIVE_SUCCESS = 0x05;
    public static final int WHAT_EC_GET_TOKEN_ERROR = 0x06;
    public static final int WHAT_DEVICE_FIND = 0x07;
    public static final int WHAT_BIND_DEVICE_SUCCESS = 0x08;
    private static final long CONFIG_TIME_OUT = 100;

    private ITuyaActivator mTuyaActivator;
    private ActivatorModelEnum mModelEnum;

    public DeviceBindModel(Context ctx, SafeHandler handler) {
        super(ctx, handler);
    }


    @Override
    public void start() {
        if (mTuyaActivator != null) {
            mTuyaActivator.start();
        }
    }

    @Override
    public void cancel() {
        if (mTuyaActivator != null) {
            mTuyaActivator.stop();
        }
    }

    @Override
    public void setEC(String ssid, String password, String token) {
        mModelEnum = TY_EZ;
        mTuyaActivator = TuyaHomeSdk.getActivatorInstance().newMultiActivator(new ActivatorBuilder()
                .setSsid(ssid)
                .setContext(mContext)
                .setPassword(password)
                .setActivatorModel(TY_EZ)
                .setTimeOut(CONFIG_TIME_OUT)
                .setToken(token).setListener(new ITuyaSmartActivatorListener() {
                    @Override
                    public void onError(String s, String s1) {
                        switch (s) {
                            case STATUS_FAILURE_WITH_GET_TOKEN:
                                resultError(WHAT_EC_GET_TOKEN_ERROR, "wifiError", s1);
                                return;
                        }
                        resultError(WHAT_EC_ACTIVE_ERROR, s, s1);
                    }

                    @Override
                    public void onActiveSuccess(DeviceBean gwDevResp) {
                        resultSuccess(WHAT_EC_ACTIVE_SUCCESS, gwDevResp);
                    }

                    @Override
                    public void onStep(String s, Object o) {
                        switch (s) {
                            case ActivatorEZStepCode.DEVICE_BIND_SUCCESS:
                                resultSuccess(WHAT_BIND_DEVICE_SUCCESS, o);
                                break;
                            case ActivatorEZStepCode.DEVICE_FIND:
                                resultSuccess(WHAT_DEVICE_FIND, o);
                                break;
                        }
                    }
                }));
    }

    @Override
    public void setAP(String ssid, String password, String token) {
        mModelEnum = TY_AP;
        mTuyaActivator = TuyaHomeSdk.getActivatorInstance().newActivator(new ActivatorBuilder()
                .setSsid(ssid)
                .setContext(mContext)
                .setPassword(password)
                .setActivatorModel(TY_AP)
                .setTimeOut(CONFIG_TIME_OUT)
                .setToken(token).setListener(new ITuyaSmartActivatorListener() {
                    @Override
                    public void onError(String error, String s1) {
                        resultError(WHAT_AP_ACTIVE_ERROR, error, s1);
                    }

                    @Override
                    public void onActiveSuccess(DeviceBean gwDevResp) {
                        resultSuccess(WHAT_AP_ACTIVE_SUCCESS, gwDevResp);
                    }

                    @Override
                    public void onStep(String step, Object o) {
                        switch (step) {
                            case ActivatorAPStepCode.DEVICE_BIND_SUCCESS:
                                resultSuccess(WHAT_BIND_DEVICE_SUCCESS, o);
                                break;
                            case ActivatorAPStepCode.DEVICE_FIND:
                                resultSuccess(WHAT_DEVICE_FIND, o);
                                break;
                        }
                    }
                }));

    }

    @Override
    public void configFailure() {
        if (mModelEnum == null) return;
        if (mModelEnum == TY_AP) {
            //ap超时提示错误页面
            resultError(WHAT_AP_ACTIVE_ERROR, "TIME_ERROR", "OutOfTime");
        } else {
            //ez超时进入ap配网
            resultError(WHAT_EC_ACTIVE_ERROR, "TIME_ERROR", "OutOfTime");
        }
    }

    @Override
    public void onDestroy() {
        if (mTuyaActivator != null)
            mTuyaActivator.onDestroy();

    }
}
