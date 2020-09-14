package com.tuya.smart.android.demo.base.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.tuya.smart.android.common.utils.L;
import com.tuya.smart.android.demo.R;
import com.tuya.smart.android.demo.base.app.Constant;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.builder.TuyaGwActivatorBuilder;
import com.tuya.smart.home.sdk.builder.TuyaGwSubDevActivatorBuilder;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.sdk.api.ITuyaActivator;
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken;
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.List;

public class ZigBeeConfigActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ZigBeeConfigActivity";
    private ITuyaActivator iTuyaActivator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zig_bee_config);

        findViewById(R.id.btn_start_config_gw).setOnClickListener(this);
        findViewById(R.id.btn_start_config_sub_dev).setOnClickListener(this);
        findViewById(R.id.btn_local_scene).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_config_gw:
                configGW();
                break;
            case R.id.btn_start_config_sub_dev:
                configSubDev();
                break;
            case R.id.btn_local_scene:

                break;
        }

    }

    private void configSubDev() {
        List<DeviceBean> devList = TuyaHomeSdk.getDataInstance().getHomeDeviceList(Constant.HOME_ID);
        for (DeviceBean deviceBean : devList) {
            if (deviceBean.isZigBeeWifi())
                TuyaHomeSdk.getActivatorInstance().newTuyaGwActivator().newSubDevActivator(new TuyaGwSubDevActivatorBuilder()
                        .setListener(new ITuyaSmartActivatorListener() {
                            @Override
                            public void onError(String errorCode, String errorMsg) {

                            }

                            @Override
                            public void onActiveSuccess(DeviceBean devResp) {
                                L.d(TAG, " devResp: " + devResp.getDevId());
                            }

                            @Override
                            public void onStep(String step, Object data) {

                            }
                        })
                        .setDevId(deviceBean.getDevId())).start();

        }
    }

    private void configGW() {
        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
            @Override
            public void onSuccess(List<HomeBean> homeBeans) {
                TuyaHomeSdk.getActivatorInstance().getActivatorToken(homeBeans.get(0).getHomeId(), new ITuyaActivatorGetToken() {
                    @Override
                    public void onSuccess(final String token) {
                        startConfigGW(token);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {

                    }
                });
            }

            @Override
            public void onError(String errorCode, String error) {

            }
        });


    }

    private void startConfigGW(String token) {
        TuyaHomeSdk.getActivatorInstance().newGwActivator(new TuyaGwActivatorBuilder()
                .setToken(token)
                .setContext(this)
                .setListener(new ITuyaSmartActivatorListener() {
                    @Override
                    public void onError(String errorCode, String errorMsg) {

                    }

                    @Override
                    public void onActiveSuccess(DeviceBean devResp) {
                        L.d(TAG, " devResp: " + devResp.getDevId());
                    }

                    @Override
                    public void onStep(String step, Object data) {

                    }
                }).setToken(token)).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
