package com.tuya.smart.android.demo.device.common;

import java.util.concurrent.CountDownLatch;

/**
 * Created by letian on 16/7/12.
 */
public class DpCountDownLatch extends CountDownLatch {

    public static final int STATUS_ERROR = 1;
    public static final int STATUS_SUCCESS = 2;
    public static final int STATUS_SEND_ERROR = 3;
    private int status;
    private boolean isFromCloud;
    private String returnValue;
    private String sendDpId;

    /**
     * Constructs a {@code CountDownLatch} initialized with the given count.
     *
     * @param count the number of times {@link #countDown} must be invoked
     *              before threads can pass through {@link #await}
     * @throws IllegalArgumentException if {@code count} is negative
     */
    public DpCountDownLatch(int count) {
        super(count);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(String dpValue) {
        this.returnValue = dpValue;
    }

    public boolean isFromCloud() {
        return isFromCloud;
    }

    public void setFromCloud(boolean fromCloud) {
        this.isFromCloud = fromCloud;
    }

    public String getSendDpId() {
        return sendDpId;
    }

    public void setSendDpId(String sendDpId) {
        this.sendDpId = sendDpId;
    }
}
