package com.tuya.smart.android.demo.base.utils;

import java.util.Collection;

public class CollectionUtils {

    private CollectionUtils() {}

    public static boolean isEmpty(Collection collection) {
        return null == collection || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection collection) {
        return null != collection && !collection.isEmpty();
    }

}
