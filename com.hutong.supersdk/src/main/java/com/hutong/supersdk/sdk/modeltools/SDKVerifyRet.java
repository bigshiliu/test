package com.hutong.supersdk.sdk.modeltools;

import java.util.HashMap;
import java.util.Map;

public class SDKVerifyRet {

    public static final String STATUS_OK = "ok";
    public static final String STATUS_FAIL = "fail";

    private String status = STATUS_FAIL;
    private String sdkUid = "";
    private String sdkAccessToken = "";
    private String sdkRefreshToken = "";
    private Map<String, String> extra = new HashMap<String, String>();
    private String msg = "";
    private String userExtra = "";

    public SDKVerifyRet success() {
        this.status = STATUS_OK;
        return this;
    }

    public SDKVerifyRet fail() {
        this.status = STATUS_FAIL;
        return this;
    }

    public boolean isSuccess() {
        return this.status.equals(STATUS_OK);
    }

    public void setExtra(String key, String value) {
        this.extra.put(key, value);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSdkUid() {
        return sdkUid;
    }

    public void setSdkUid(String sdkUid) {
        this.sdkUid = sdkUid;
    }

    public String getSdkAccessToken() {
        return sdkAccessToken;
    }

    public void setSdkAccessToken(String sdkAccessToken) {
        this.sdkAccessToken = sdkAccessToken;
    }

    public String getSdkRefreshToken() {
        return sdkRefreshToken;
    }

    public void setSdkRefreshToken(String sdkRefreshToken) {
        this.sdkRefreshToken = sdkRefreshToken;
    }

    public String getErrorMsg() {
        return msg;
    }

    public void setErrorMsg(String errorMsg) {
        this.msg = errorMsg;
    }

    public Map<String, String> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, String> extra) {
        this.extra = extra;
    }

    public String getUserExtra() {
        return null == userExtra || "".equals(userExtra) ? "" : userExtra;
    }

    public void setUserExtra(String userExtra) {
        this.userExtra = userExtra;
    }
}
