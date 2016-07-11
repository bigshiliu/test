package com.hutong.supersdk.sdk.modeltools.qihu360;

public class QiHu360IOSSDKInfo {


    /**
     * 奇虎360验证token参数
     */
    private String requestUrl = "";

    /**
     * 苹果支付参数
     */
    //是否测试模式
    private Boolean debug = false;

    //测试模式的url
    private String testUrl = "";

    //生产模式的url
    private String productUrl = "";

    private String bundleIds = "";

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public String getTestUrl() {
        return testUrl;
    }

    public void setTestUrl(String testUrl) {
        this.testUrl = testUrl;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getBundleIds() {
        return bundleIds;
    }

    public void setBundleIds(String bundleIds) {
        this.bundleIds = bundleIds;
    }
}
