package com.tuya.smart.android.demo.base.utils;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Intent;
import android.content.res.TypedArray;

import com.tuya.smart.android.common.utils.TuyaUtil;
import com.tuya.smart.android.demo.R;
import com.tuya.smart.android.demo.base.activity.BrowserActivity;
import com.tuya.smart.android.demo.base.activity.HomeActivity;
import com.tuya.smart.android.demo.config.CommonConfig;
import com.tuya.smart.sdk.TuyaSdk;


/**
 * Created by fengyu on 14/11/16.
 */
public class ActivityUtils {
    //动画标识
    public static final int ANIMATE_NONE = -1;
    public static final int ANIMATE_FORWARD = 0;
    public static final int ANIMATE_BACK = 1;
    public static final int ANIMATE_EASE_IN_OUT = 2;
    public static final int ANIMATE_SLIDE_TOP_FROM_BOTTOM = 3;
    public static final int ANIMATE_SLIDE_BOTTOM_FROM_TOP = 4;
    public static final int ANIMATE_SCALE_IN = 5;
    public static final int ANIMATE_SCALE_OUT = 6;

    public static void gotoActivity(Activity from, Class<? extends Activity> clazz, int direction, boolean finished) {
        if (clazz == null) return;
        Intent intent = new Intent();
        intent.setClass(from, clazz);
        startActivity(from, intent, direction, finished);
    }

    public static void gotoLauncherActivity(Activity activity, int direction, boolean finished) {
        Intent intent = new Intent(activity, LauncherActivity.class);
        startActivity(activity, intent, direction, finished);
    }

    public static void startActivity(Activity activity, Intent intent, int direction, boolean finishLastActivity) {
        if (activity == null) return;
        activity.startActivity(intent);
        if (finishLastActivity) activity.finish();
        overridePendingTransition(activity, direction);
    }

    public static void startActivityForResult(Activity activity, Intent intent, int backCode, int direction, boolean finishLastActivity) {
        if (activity == null) return;
        activity.startActivityForResult(intent, backCode);
        if (finishLastActivity) activity.finish();
        overridePendingTransition(activity, direction);
    }

    public static void back(Activity activity) {
        activity.finish();
        overridePendingTransition(activity, ANIMATE_BACK);
    }

    public static void back(Activity activity, int direction) {
        activity.finish();
        overridePendingTransition(activity, direction);
    }

    public static void overridePendingTransition(Activity activity, int direction) {
        if (direction == ANIMATE_FORWARD) {
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (direction == ANIMATE_BACK) {
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if (direction == ANIMATE_EASE_IN_OUT) {
            activity.overridePendingTransition(R.anim.easein, R.anim.easeout);
        } else if (direction == ANIMATE_SLIDE_TOP_FROM_BOTTOM) {
            activity.overridePendingTransition(R.anim.slide_bottom_to_top, R.anim.slide_none_medium_time);
        } else if (direction == ANIMATE_SLIDE_BOTTOM_FROM_TOP) {
            activity.overridePendingTransition(R.anim.slide_none_medium_time, R.anim.slide_top_to_bottom);
        } else if (direction == ANIMATE_SCALE_IN) {
            activity.overridePendingTransition(R.anim.popup_scale_in, R.anim.slide_none);
        } else if (direction == ANIMATE_SCALE_OUT) {
            activity.overridePendingTransition(R.anim.slide_none, R.anim.popup_scale_out);
        } else if (direction == ANIMATE_NONE) {
            //do nothing
        } else {
//            activity.overridePendingTransition(R.anim.magnify_fade_in, R.anim.slide_none);
            activity.overridePendingTransition(R.anim.magnify_fade_in, R.anim.fade_out);
        }
    }

    public static void gotoHomeActivity(Activity context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(context, intent, ANIMATE_NONE, true);
    }

    public static void gotoAddDeviceHelpActivity(Activity activity, String title) {
        Intent intent = new Intent(activity, BrowserActivity.class);
        intent.putExtra(BrowserActivity.EXTRA_LOGIN, false);
        intent.putExtra(BrowserActivity.EXTRA_REFRESH, true);
        intent.putExtra(BrowserActivity.EXTRA_TOOLBAR, true);
        intent.putExtra(BrowserActivity.EXTRA_TITLE, title);

        TypedArray a = activity.obtainStyledAttributes(new int[]{
                R.attr.is_add_device_help_get_from_native});
        boolean isAddDeviceHelpAsset = a.getBoolean(0, false);
        if (isAddDeviceHelpAsset) {
            boolean isChinese = TuyaUtil.isZh(TuyaSdk.getApplication());
            if (isChinese) {
                intent.putExtra(BrowserActivity.EXTRA_URI, "file:///android_asset/add_device_help_cn.html");
            } else {
                intent.putExtra(BrowserActivity.EXTRA_URI, "file:///android_asset/add_device_help_en.html");
            }
        } else {
            intent.putExtra(BrowserActivity.EXTRA_URI, CommonConfig.RESET_URL);
        }
        a.recycle();

        activity.startActivity(intent);
    }

}
