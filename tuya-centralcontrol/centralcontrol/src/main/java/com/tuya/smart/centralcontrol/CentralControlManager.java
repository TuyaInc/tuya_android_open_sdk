package com.tuya.tuyasmart.tyrctpublicmanager;

import android.util.Log;

public class TYRCTPublicManagerUtils {

    public static final int DEBUG_LEVEL_OFF = 0;
    public static final int DEBUG_LEVEL_ERROR = 1;
    public static final int DEBUG_LEVEL_WARNING = 2;
    public static final int DEBUG_LEVEL_INFO = 3;
    public static final int DEBUG_LEVEL_DEBUG = 4;
    public static final int DEBUG_LEVEL_VERBOSE = 5;

    private static String TAG = TYRCTPublicManagerUtils.class.getSimpleName();
    private static int LOG_LEVEL = DEBUG_LEVEL_OFF;

    public void tag(final String tag) {
        if (tag != null) {
            TAG = tag;
        } else {
            TAG = TYRCTPublicManagerUtils.class.getSimpleName();
        }
    }

    public static void logLevel(final int level) {
        LOG_LEVEL = level;
    }

    public static int logLevel() {
        return LOG_LEVEL;
    }

    public static void v() {
        if (LOG_LEVEL >= DEBUG_LEVEL_VERBOSE) Log.v(TAG, getMetaInfo());
    }

    public static void v(final String message) {
        if (LOG_LEVEL >= DEBUG_LEVEL_VERBOSE) Log.v(TAG, getMetaInfo() + null2str(message));
    }

    public static void d() {
        if (LOG_LEVEL >= DEBUG_LEVEL_DEBUG) Log.d(TAG, getMetaInfo());
    }

    public static void d(final String message) {
        if (LOG_LEVEL >= DEBUG_LEVEL_DEBUG) Log.d(TAG, getMetaInfo() + null2str(message));
    }

    public static void i() {
        if (LOG_LEVEL >= DEBUG_LEVEL_INFO) Log.i(TAG, getMetaInfo());
    }

    public static void i(final String message) {
        if (LOG_LEVEL >= DEBUG_LEVEL_INFO) Log.i(TAG, getMetaInfo() + null2str(message));
    }

    public static void w(final String message) {
        if (LOG_LEVEL >= DEBUG_LEVEL_WARNING) Log.w(TAG, getMetaInfo() + null2str(message));
    }

    public static void w(final String message, final Throwable e) {
        if (LOG_LEVEL >= DEBUG_LEVEL_WARNING) {
            Log.w(TAG, getMetaInfo() + null2str(message), e);
            printThrowable(e);
            if (e.getCause() != null) {
                printThrowable(e.getCause());
            }
        }
    }

    public static void e(final String message) {
        if (LOG_LEVEL >= DEBUG_LEVEL_ERROR) Log.e(TAG, getMetaInfo() + null2str(message));
    }

    public static void e(final String message, final Throwable e) {
        if (LOG_LEVEL >= DEBUG_LEVEL_ERROR) {
            Log.e(TAG, getMetaInfo() + null2str(message), e);
            printThrowable(e);
            if (e.getCause() != null) {
                printThrowable(e.getCause());
            }
        }
    }

    public static void e(final Throwable e) {
        if (LOG_LEVEL >= DEBUG_LEVEL_ERROR) {
            printThrowable(e);
            if (e.getCause() != null) {
                printThrowable(e.getCause());
            }
        }
    }

    private static String null2str(final String string) {
        if (string == null) {
            return "(null)";
        }
        return string;
    }

    /**
     * 例外のスタックトレースをログに出力する
     *
     * @param e
     */
    private static void printThrowable(final Throwable e) {
        Log.e(TAG, e.getClass().getName() + ": " + e.getMessage());
        for (final StackTraceElement element : e.getStackTrace()) {
            Log.e(TAG, "  at " + getMetaInfo(element));
        }
    }

    private static String getMetaInfo() {
        final StackTraceElement element = Thread.currentThread().getStackTrace()[4];
        return getMetaInfo(element);
    }

    public static String getMetaInfo(final StackTraceElement element) {
        final String fullClassName = element.getClassName();
        final String simpleClassName = fullClassName.substring(fullClassName
                .lastIndexOf(".") + 1);
        final String methodName = element.getMethodName();
        final int lineNumber = element.getLineNumber();
        final String metaInfo = "[" + simpleClassName + "#" + methodName + ":"
                + lineNumber + "]";
        return metaInfo;
    }
}
