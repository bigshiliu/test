package com.hutong.supersdk.mysql.config.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.util.StringUtils;

import com.hutong.supersdk.mysql.ABaseDomain;

/**
 * Created by Dongxu on 2015/12/4.
 */
@Entity
@Table(name = "T_GAME_OWNER")
public class GameOwner extends ABaseDomain {
	private static final long serialVersionUID = 1L;
	//AppIdArray分隔符
	private final static String APP_ID_ARRAY_SPILT = ";";
	//App与AppName分隔符
	private final static String APP_ID_SPILT = ":";
	
	@Id
    @Column(name = "Platform_User_Id")
    private String platformUserId;
    @Column(name = "App_Id_Array")
    private String appIdArray;
    @Column(name = "Enable_Flag")
	private int enableFlag;
	@Column(name = "Create_Time")
	private Timestamp createTime;
	@Column(name = "Extra")
	private String extra;
    
    //构造器中增加初始化信息
    public GameOwner() {
    	this.enableFlag = 1;
		this.createTime = new Timestamp(System.currentTimeMillis());
	}
    
    /**
     * 解析appArrayList得到AppConfig list
     * @param appIdArray 格式appId1:appName1;appId2:appName2....
     * @return List<AppConfig>
     */
    public static List<AppConfig> getAppList(String appIdArray){
    	List<AppConfig> appList = new ArrayList<AppConfig>();
    	if(StringUtils.isEmpty(appIdArray)){
    		return appList;
    	}
    	String[] appIds = appIdArray.split(APP_ID_ARRAY_SPILT);
    	for(String temp: appIds){
    		AppConfig appTemp = new AppConfig();
    		String[] appAndAppName = temp.split(APP_ID_SPILT);
    		appTemp.setAppId(appAndAppName[0]);
    		appTemp.setAppName(appAndAppName[1]);
    		appList.add(appTemp);
    	}
    	return appList;
    }
    
	public String getPlatformUserId() {
		return platformUserId;
	}

	public void setPlatformUserId(String platformUserId) {
		this.platformUserId = platformUserId;
	}

	public String getAppIdArray() {
		if(StringUtils.isEmpty(appIdArray))
			return "";
		return appIdArray;
	}

	public void setAppIdArray(String appIdArray) {
		this.appIdArray = appIdArray;
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

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}
    
	public static void main(String[] args) {
		List<AppConfig> test = getAppList("aaaa:1111;bbbb:22222;ccccc:33333");
		for(AppConfig temp: test){
			System.out.println(temp.getAppId());
			System.out.println(temp.getAppName());
		}
	}

	public boolean existsAppId(String appId) {
        if (StringUtils.isEmpty(this.appIdArray))
            return false;

		String[] appIdArr = this.appIdArray.split(APP_ID_ARRAY_SPILT);
        List<String> appIds = Arrays.asList(appIdArr);
        return appIds.contains(appId);
	}

    public GameOwner addAppId(String appId) {
        if (this.existsAppId(appId))
            return this;

        if (StringUtils.isEmpty(this.appIdArray))
            this.appIdArray = appId;
        else
            this.appIdArray += APP_ID_ARRAY_SPILT + appId;

        return this;
    }
}
