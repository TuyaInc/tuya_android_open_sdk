package com.tuya.smart.android.demo.base.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import com.alibaba.fastjson.JSONObject;
import com.tuya.smart.android.demo.R;
import com.tuya.smart.android.demo.base.utils.DialogUtil;
import com.tuya.smart.android.demo.device.SwitchActivity;
import com.tuya.smart.android.demo.device.SwitchBean;
import com.tuya.smart.android.demo.base.utils.ProgressUtil;
import com.tuya.smart.android.demo.base.utils.ToastUtil;
import com.tuya.smart.android.demo.device.ISwitchView;
import com.tuya.smart.android.demo.device.common.DpCountDownLatch;
import com.tuya.smart.android.mvp.presenter.BasePresenter;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


/**
 * Created by letian on 16/7/21.
 */
public class SwitchPresenter extends BasePresenter implements IDevListener {
    private final ISwitchView mView;
    private final Context mContext;
    private SwitchBean mSwitchBean;
    private ITuyaDevice mTuyaDevice;
    private String mDevId;
    private DeviceBean mDevBean;
    private DpCountDownLatch mDownLatch;

    public SwitchPresenter(Context context, ISwitchView view) {
        mView = view;
        mContext = context;
        initData();
        initListener();
    }


    private void initListener() {
        mTuyaDevice = TuyaHomeSdk.newDeviceInstance(mDevId);
        mTuyaDevice.registerDevListener(this);
    }


    private void initData() {
        mDevId = ((Activity) mContext).getIntent().getStringExtra(SwitchActivity.INTENT_DEVID);
        mDevBean = TuyaHomeSdk.getDataInstance().getDeviceBean(mDevId);
        if (mDevBean == null) {
            ((Activity) mContext).finish();
        } else {
            boolean open = (boolean) TuyaHomeSdk.getDataInstance().getDp(mDevBean.getDevId(), SwitchBean.SWITCH_DPID);
            mSwitchBean = new SwitchBean(open);
        }
    }

    public String getTitle() {
        return mDevBean == null ? "" : mDevBean.getName();
    }

    public void onClickSwitch() {
        if (mDownLatch != null) {
            return;
        }
        mSwitchBean.setOpen(!mSwitchBean.isOpen());
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendCommand();
                try {
                    mDownLatch.await(2, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                checkResult();
                mDownLatch = null;
            }
        }).start();
    }

    private void checkResult() {
        if (mDownLatch.getStatus() == DpCountDownLatch.STATUS_ERROR) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mView.showErrorTip();
                }
            });
        }
    }

    private void sendCommand() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(SwitchBean.SWITCH_DPID, mSwitchBean.isOpen());
        mDownLatch = new DpCountDownLatch(1);
        mTuyaDevice.publishDps(JSONObject.toJSONString(hashMap), new IResultCallback() {
            @Override
            public void onError(String s, String s1) {
                mDownLatch.setStatus(DpCountDownLatch.STATUS_ERROR);
                mDownLatch.countDown();
            }

            @Override
            public void onSuccess() {

            }
        });
    }

    @Override
    public void onDpUpdate(String devId, String dps) {
        JSONObject jsonObject = JSONObject.parseObject(dps);
        Boolean open = (Boolean) jsonObject.get(SwitchBean.SWITCH_DPID);
        if (open) mView.showOpenView();
        else mView.showCloseView();
        if (mDownLatch != null) {
            mDownLatch.countDown();
        }
    }

    @Override
    public void onRemoved(String devId) {
        mView.showRemoveTip();
    }

    @Override
    public void onStatusChanged(String devId, boolean status) {
        mView.statusChangedTip(status);
    }

    @Override
    public void onNetworkStatusChanged(String devId, boolean status) {
        mView.changeNetworkErrorTip(status);
    }

    @Override
    public void onDevInfoUpdate(String devId) {
        mView.devInfoUpdateView();
    }


    public void renameDevice() {
        DialogUtil.simpleInputDialog(mContext, mContext.getString(R.string.rename), getTitle(), false, new DialogUtil.SimpleInputDialogInterface() {
            @Override
            public void onPositive(DialogInterface dialog, String inputText) {
                int limit = mContext.getResources().getInteger(R.integer.change_device_name_limit);
                if (inputText.length() > limit) {
                    ToastUtil.showToast(mContext, R.string.ty_modify_device_name_length_limit);
                } else {
                    renameTitleToServer(inputText);
                }
            }

            @Override
            public void onNegative(DialogInterface dialog) {

            }
        });
    }

    private void renameTitleToServer(final String titleName) {
        ProgressUtil.showLoading(mContext, R.string.loading);
        mTuyaDevice.renameDevice(titleName, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                ProgressUtil.hideLoading();
                ToastUtil.showToast(mContext, error);
            }

            @Override
            public void onSuccess() {
                ProgressUtil.hideLoading();
                mView.updateTitle(titleName);
            }
        });
    }

    public void resetFactory() {
        DialogUtil.simpleConfirmDialog(mContext, mContext.getString(R.string.ty_control_panel_factory_reset_info),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            ProgressUtil.showLoading(mContext, R.string.ty_control_panel_factory_reseting);
                            mTuyaDevice.resetFactory(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    ProgressUtil.hideLoading();
                                    ToastUtil.shortToast(mContext, R.string.ty_control_panel_factory_reset_fail);
                                }

                                @Override
                                public void onSuccess() {
                                    ProgressUtil.hideLoading();
                                    ToastUtil.shortToast(mContext, R.string.ty_control_panel_factory_reset_succ);
                                    ((Activity) mContext).finish();
                                }
                            });
                        }
                    }
                });
    }


    public void removeDevice() {
        ProgressUtil.showLoading(mContext, R.string.loading);
        mTuyaDevice.removeDevice(new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                ProgressUtil.hideLoading();
                ToastUtil.showToast(mContext, error);
            }

            @Override
            public void onSuccess() {
                ProgressUtil.hideLoading();
                ((Activity) mContext).finish();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTuyaDevice.onDestroy();
    }
}
