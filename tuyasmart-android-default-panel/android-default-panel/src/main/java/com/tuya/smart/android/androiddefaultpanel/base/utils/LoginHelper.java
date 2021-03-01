package com.tuya.smart.android.demo.base.utils;

import android.app.Activity;
import android.content.Context;

import com.tuya.smart.android.demo.R;
import com.tuya.smart.android.demo.login.activity.LoginActivity;
import com.tuya.smart.android.demo.base.app.Constant;
import com.tuya.smart.home.sdk.TuyaHomeSdk;


/**
 * Created by letian on 16/7/15.
 */
public class LoginHelper {


    public static void afterLogin() {

        //there is the somethings that need to set.For example the lat and lon;
        //   TuyaSdk.setLatAndLong();
    }


    /**
     * 唤起重新登录
     *
     * @param context
     */
    public static void reLogin(Context context) {
        reLogin(context, true);
    }

    public static void reLogin(Context context, boolean tip) {
        onLogout(context);
        if (tip) {
            ToastUtil.shortToast(context, R.string.login_session_expired);
        }
        ActivityUtils.gotoActivity((Activity) context, LoginActivity.class, ActivityUtils.ANIMATE_FORWARD, true);
    }

    private static void onLogout(Context context) {
        exit(context);
    }

    /**
     * 退出应用
     *
     * @param context
     */
    public static void exit(Context context) {
        Constant.finishActivity();
        TuyaHomeSdk.onDestroy();
    }
}
