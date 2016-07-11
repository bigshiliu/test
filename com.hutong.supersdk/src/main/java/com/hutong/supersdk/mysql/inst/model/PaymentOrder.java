package com.hutong.supersdk.mysql.inst.model;

import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.bson.types.ObjectId;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.mysql.ABaseDomain;

@Entity
@Table( name = "T_PAYMENT_ORDER" )
public class PaymentOrder extends ABaseDomain {

	private static final long serialVersionUID = 1L;

	public static final String PAY_STATUS_SUCCESS = "SUCCESS";
	public static final String PAY_STATUS_NEW = "NEW";
	public static final String PAY_STATUS_FAIL = "FAIL";
	
	@Id
	@Column(name = "Order_Id")
	private String orderId;
	
	@Column(name = "SuperSDK_Uid")
	private String supersdkUid;
	
	@Column(name = "App_Id")
	private String appId;

	@Column(name = "SDK_Order_Id")
	private String sdkOrderId;
	
	@Column(name = "App_Channel_Id")
	private String appChannelId;
	
	@Column(name = "App_Game_Uid")
	private String appGameUid;
	
	@Column(name = "App_Server_Id")
	private String appServerId;
	
	@Column(name = "App_Product_Id")
	private String appProductId;
	
	@Column(name = "App_Product_Name")
	private String appProductName;
	
	@Column(name = "App_Product_Count")
	private String appProductCount;
	
	@Column(name = "App_Data")
	private String appData;
	
	@Column(name = "App_Role_Id")
	private String appRoleId;
	
	@Column(name = "App_Role_Name")
	private String appRoleName;
	
	@Column(name = "App_Role_Grade")
	private String appRoleGrade;
	
	@Column(name = "App_Role_Balance")
	private String appRoleBalance;
	
	@Column(name = "Order_Amount")
	private double orderAmount;
	
	@Column(name = "Currency_Type")
	private String currencyType;
	
	@Column(name = "Pay_Amount")
	private double payAmount;
	
	@Column(name = "Pay_Status")
	private String payStatus;
	
	@Column(name = "Pay_Type")
	private String payType;
	
	@Column(name = "Pay_Time")
	private Timestamp payTime;
	
	@Column(name = "Enable_Flag")
	private int enableFlag; 
	
	@Column(name = "Create_Time")
	private Timestamp createTime;
	
	@Column(name = "Source")
	private String source;
	
	@Column(name = "Extra")
	private String extra;
	
	public PaymentOrder() {
		this.orderId = new ObjectId().toString();
		this.payStatus = PAY_STATUS_NEW;
	}
	
	public static String randomOrderId() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public boolean isPaid() {
		return this.payStatus.equals(PAY_STATUS_SUCCESS);
	}
	
	public void paySuccess(String sdkOrderId, double payAmount, String currencyType,
						   String payType, String source) {
		if (StringUtils.isEmpty(this.orderId)) {
			this.orderId = randomOrderId();
		}

		this.sdkOrderId = sdkOrderId;
		this.payAmount = payAmount;
		this.currencyType = currencyType;
		this.payType = payType;
		this.source = source;
		
		this.payStatus = PAY_STATUS_SUCCESS;
		this.payTime = new Timestamp(System.currentTimeMillis());
	}
	
	public void payFail(String sdkOrderId) {
		this.sdkOrderId = sdkOrderId;
		
		this.payStatus = PAY_STATUS_FAIL;
	}
	
	public void Success(){
		this.payStatus = PAY_STATUS_SUCCESS;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getSupersdkUid() {
		return supersdkUid;
	}

	public void setSupersdkUid(String supersdkUid) {
		this.supersdkUid = supersdkUid;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppChannelId() {
		return appChannelId;
	}

	public void setAppChannelId(String appChannelId) {
		this.appChannelId = appChannelId;
	}

	public String getAppGameUid() {
		return appGameUid;
	}

	public void setAppGameUid(String appGameUid) {
		this.appGameUid = appGameUid;
	}

	public String getAppServerId() {
		return appServerId;
	}

	public void setAppServerId(String appServerId) {
		this.appServerId = appServerId;
	}

	public String getAppProductId() {
		return appProductId;
	}

	public void setAppProductId(String appProductId) {
		this.appProductId = appProductId;
	}

	public String getAppProductName() {
		return appProductName;
	}

	public void setAppProductName(String appProductName) {
		this.appProductName = appProductName;
	}

	public String getAppProductCount() {
		return appProductCount;
	}

	public void setAppProductCount(String appProductCount) {
		this.appProductCount = appProductCount;
	}

	public String getAppData() {
		return appData;
	}

	public void setAppData(String appData) {
		this.appData = appData;
	}

	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public Timestamp getPayTime() {
		return payTime;
	}

	public void setPayTime(Timestamp payTime) {
		this.payTime = payTime;
	}

	public int getEnableFlag() {
		return enableFlag;
	}

	public void setEnableFlag(int enableFlag) {
		this.enableFlag = enableFlag;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getSdkOrderId() {
		return sdkOrderId;
	}

	public void setSdkOrderId(String sdkOrderId) {
		this.sdkOrderId = sdkOrderId;
	}

	public double getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(double orderAmount) {
		this.orderAmount = orderAmount;
	}

	public String getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}

	public double getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(double payAmount) {
		this.payAmount = payAmount;
	}

	public String getAppRoleId() {
		return appRoleId;
	}

	public void setAppRoleId(String appRoleId) {
		this.appRoleId = appRoleId;
	}

	public String getAppRoleName() {
		return appRoleName;
	}

	public void setAppRoleName(String appRoleName) {
		this.appRoleName = appRoleName;
	}

	public String getAppRoleGrade() {
		return appRoleGrade;
	}

	public void setAppRoleGrade(String appRoleGrade) {
		this.appRoleGrade = appRoleGrade;
	}

	public String getAppRoleBalance() {
		return appRoleBalance;
	}

	public void setAppRoleBalance(String appRoleBalance) {
		this.appRoleBalance = appRoleBalance;
	}

}
