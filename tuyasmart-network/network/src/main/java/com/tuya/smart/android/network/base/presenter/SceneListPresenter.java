package com.tuya.smart.android.demo.base.presenter;

import android.app.Activity;
import android.content.Intent;

import com.tuya.smart.android.base.utils.PreferencesUtil;
import com.tuya.smart.android.demo.R;
import com.tuya.smart.android.demo.scene.activity.SceneActivity;
import com.tuya.smart.android.demo.base.app.Constant;
import com.tuya.smart.android.demo.base.utils.ActivityUtils;
import com.tuya.smart.android.demo.base.utils.ToastUtil;
import com.tuya.smart.android.demo.scene.view.ISceneListFragmentView;
import com.tuya.smart.android.mvp.presenter.BasePresenter;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.api.IResultCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * create by nielev on 2019-10-28
 */
public class SceneListPresenter extends BasePresenter {
    private Activity mActivity;
    private ISceneListFragmentView mView;
    public static final int SMART_TYPE_SCENE = 0;
    public static final int SMART_TYPE_AUTOMATION = 1;
    public static final String SMART_TYPE = "smart_type";
    public static final String SMART_IS_EDIT = "smart_is_edit";
    public SceneListPresenter(Activity activity, ISceneListFragmentView iView){
        mActivity = activity;
        mView = iView;
        Constant.HOME_ID = PreferencesUtil.getLong("homeId", Constant.HOME_ID);
    }
    public void getSceneList(){
        TuyaHomeSdk.getSceneManagerInstance().getSceneList(Constant.HOME_ID, new ITuyaResultCallback<List<SceneBean>>() {
            @Override
            public void onSuccess(List<SceneBean> result) {
                if(null == result || result.isEmpty()){
                    mView.showEmptyView();
                } else {
                    separateSceneAndAuto(result);
                }
                mView.loadFinish();
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                ToastUtil.shortToast(mActivity, errorMessage);
                mView.loadFinish();
            }
        });
    }

    private void separateSceneAndAuto(List<SceneBean> result) {
        List<SceneBean> scenes = new ArrayList<>();
        List<SceneBean> autos = new ArrayList<>();
        for (SceneBean sceneBean :
                result) {
            if (null == sceneBean.getConditions() || sceneBean.getConditions().isEmpty()){
                scenes.add(sceneBean);
            } else {
                autos.add(sceneBean);
            }
        }
        mView.showSceneListView(scenes, autos);
    }

    public void addScene() {
        Intent intent = new Intent(mActivity, SceneActivity.class);
        intent.putExtra(SMART_TYPE, SMART_TYPE_SCENE);
        intent.putExtra(SMART_IS_EDIT, false);
        ActivityUtils.startActivity(mActivity, intent, ActivityUtils.ANIMATE_FORWARD, false);
    }

    public void addAuto() {
        Intent intent = new Intent(mActivity, SceneActivity.class);
        intent.putExtra(SMART_TYPE, SMART_TYPE_AUTOMATION);
        intent.putExtra(SMART_IS_EDIT, false);
        ActivityUtils.startActivity(mActivity, intent, ActivityUtils.ANIMATE_FORWARD, false);
    }

    public void execute(SceneBean bean) {
        TuyaHomeSdk.newSceneInstance(bean.getId()).executeScene(new IResultCallback() {
            @Override
            public void onError(String code, String error) {

            }

            @Override
            public void onSuccess() {
                ToastUtil.shortToast(mActivity, R.string.success);
            }
        });
    }

    public void switchAutomation(SceneBean bean) {
        if(bean.isEnabled()){
            TuyaHomeSdk.newSceneInstance(bean.getId()).disableScene(bean.getId(), new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    ToastUtil.shortToast(mActivity, error);
                }

                @Override
                public void onSuccess() {
                    ToastUtil.shortToast(mActivity, R.string.success);
                }
            });
        } else {
            TuyaHomeSdk.newSceneInstance(bean.getId()).enableScene(bean.getId(), new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    ToastUtil.shortToast(mActivity, error);
                }

                @Override
                public void onSuccess() {
                    ToastUtil.shortToast(mActivity, R.string.success);
                }
            });
        }
    }
}
