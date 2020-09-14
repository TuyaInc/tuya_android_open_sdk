package com.tuya.smart.android.demo.config;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tuya.smart.android.common.utils.L;
import com.tuya.smart.android.demo.R;
import com.tuya.smart.android.demo.base.activity.BaseActivity;
import com.tuya.smart.android.demo.family.FamilyManager;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.builder.TuyaGwActivatorBuilder;
import com.tuya.smart.home.sdk.builder.TuyaGwSubDevActivatorBuilder;
import com.tuya.smart.sdk.api.ITuyaActivator;
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken;
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener;
import com.tuya.smart.sdk.bean.DeviceBean;

public class ZigbeeConfigActivity extends BaseActivity {
    private static final String TAG = "ZigbeeConfigActivity";
    private static final int INFO_MESSAGE = 1;
    private EditText infoEt;
    private String gwId;
    private ITuyaActivator mTuyaActivator;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INFO_MESSAGE:
                    if (msg.obj != null)
                        infoEt.append((String) msg.obj);
                    else
                        Toast.makeText(ZigbeeConfigActivity.this, "msg null", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zigbee_config);
        initToolbar();
        infoEt = findViewById(R.id.et_info);
        setTitle("gateway config");
        setDisplayHomeAsUpEnabled();
    }

    public void requestGWToken() {
        TuyaHomeSdk.getActivatorInstance().getActivatorToken(FamilyManager.getInstance().getCurrentHomeId(), new ITuyaActivatorGetToken() {
            @Override
            public void onSuccess(String token) {
                startBindGatewayDevice(token);
            }

            @Override
            public void onFailure(String s, String s1) {
                L.d("AddDeviceTypeActivity", "get token error:::" + s + " ++ " + s1);
                Toast.makeText(getApplicationContext(), "get token error: " + s + " ++ " + s1, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startGatewaySubDevConfig() {
        if (TextUtils.isEmpty(gwId)) {
            Toast.makeText(this, "gwId =null", Toast.LENGTH_SHORT).show();
            return;
        }

        TuyaGwSubDevActivatorBuilder builder = new TuyaGwSubDevActivatorBuilder()
                .setDevId(gwId)
                .setTimeOut(120)
                .setListener(new ITuyaSmartActivatorListener() {
                    @Override
                    public void onError(String s, String s1) {
                        Log.e(TAG, "subDevConfig onFailure");
                        Message msg = Message.obtain();
                        msg.what = INFO_MESSAGE;
                        msg.obj = s + "--" + s1;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onActiveSuccess(DeviceBean deviceBean) {
                        Log.e(TAG, "config success：deviceResBean id:" + deviceBean.getDevId());
                        Message msg = Message.obtain();
                        msg.what = INFO_MESSAGE;
                        msg.obj = "config device id：" + deviceBean.getDevId();
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onStep(String s, Object o) {
                    }
                });

        mTuyaActivator = TuyaHomeSdk.getActivatorInstance().newTuyaGwActivator().newSubDevActivator(builder);
        mTuyaActivator.start();
    }

    private void startBindGatewayDevice(String token) {
        TuyaGwActivatorBuilder builder = new TuyaGwActivatorBuilder()
                .setContext(this)
                .setToken(token)
                .setListener(new ITuyaSmartActivatorListener() {
                    @Override
                    public void onError(String errorCode, String errorMsg) {
                        Log.e(TAG, "error:" + errorCode + " msg:" + errorMsg);
                        Message msg = Message.obtain();
                        msg.what = INFO_MESSAGE;
                        msg.obj = errorCode + "--" + errorMsg;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onActiveSuccess(DeviceBean deviceBean) {
                        Log.e(TAG, "onActiveSuccess");
                        Message msg = Message.obtain();
                        msg.what = INFO_MESSAGE;
                        msg.obj = "config device id：" + deviceBean.getDevId();
                        mHandler.sendMessage(msg);
                        gwId = deviceBean.getDevId();
                        Log.d(TAG, "gwId:" + gwId);
                    }

                    @Override
                    public void onStep(String s, Object o) {
                        L.e("AddDeviceTypeActivity", "s=" + s);
                    }
                });
        mTuyaActivator = TuyaHomeSdk.getActivatorInstance().newGwActivator(builder);
        mTuyaActivator.start();
    }

    public void zigbeeConfig(View view) {
        requestGWToken();
    }


    public void subDevConfig(View view) {
        startGatewaySubDevConfig();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTuyaActivator != null) {
            mTuyaActivator.onDestroy();
        }
    }

//    public void jump(View view) {
//        startActivity(new Intent(this,MainActivity.class));
//    }

    class InfoRunnable implements Runnable {
        private String info;

        public InfoRunnable(String in) {
            info = in;
            Log.d("info", info);
        }

        @Override
        public void run() {
            infoEt.setText(info);
        }
    }
}
