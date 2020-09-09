package com.tuya.tuyasmart.tyrctpublicmanager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

public class ViewHelper {

    public static void setBackgroundAll(final ViewGroup vg, final int color) {
        for (int i = 0, count = vg.getChildCount(); i < count; i++) {
            final View child = vg.getChildAt(i);
            child.setBackgroundColor(color);
            if (child instanceof ViewGroup) {
                setBackgroundAll((ViewGroup) child, color);
            }
        }
    }


    public static void setBackgroundAll(final ViewGroup vg, final Drawable dr) {
        for (int i = 0, count = vg.getChildCount(); i < count; i++) {
            final View child = vg.getChildAt(i);
            child.setBackground(dr);
            if (child instanceof ViewGroup) {
                setBackgroundAll((ViewGroup) child, dr);
            }
        }
    }

    @NonNull
    public static LayoutInflater createCustomLayoutInflater(
            @NonNull final Context context, @NonNull final LayoutInflater inflater,
            @StyleRes final int themeRes) {
        final Context wrappedContext = new ContextThemeWrapper(context, themeRes);
        TYRCTPublicManagerUtils.d(wrappedContext.toString());
        return inflater.cloneInContext(wrappedContext);
    }
}