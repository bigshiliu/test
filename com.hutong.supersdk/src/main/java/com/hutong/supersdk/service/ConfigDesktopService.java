package com.hutong.supersdk.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hutong.supersdk.common.exception.SuperSDKException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.SuperSDK;
import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.constant.ErrorEnum;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.model.JsonResObj;
import com.hutong.supersdk.iservice.desktop.IConfigDesktopService;
import com.hutong.supersdk.mysql.config.dao.CommonSDKConfigDao;
import com.hutong.supersdk.mysql.config.model.CommonSDKConfig;
import com.hutong.supersdk.mysql.inst.dao.SDKClientConfigDao;
import com.hutong.supersdk.mysql.inst.dao.SDKConfigDao;
import com.hutong.supersdk.mysql.inst.model.SdkClientConfig;
import com.hutong.supersdk.mysql.inst.model.SdkConfig;
import com.hutong.supersdk.sdk.ISDK;
import com.hutong.supersdk.service.common.ServiceCommon;
import com.hutong.supersdk.service.common.ThreadHelper;
import com.hutong.supersdk.service.modeltools.ConfigDesktopReq;
import com.hutong.supersdk.service.modeltools.ConfigDesktopRes;

/**
 * ConfigDesktopService<br/>
 * Created by Dongxu on 2015/12/4.
 */
@Service
public class ConfigDesktopService implements IConfigDesktopService {

	@SuppressWarnings("unused")
    private final Log logger = LogFactory.getLog(ConfigDesktopService.class);

	private final static String SPILT = ";";

	private final static String ALL = "all";

	@Autowired
	private SDKConfigDao sdkConfigDao;

	@Autowired
	private SDKClientConfigDao sdkClientConfigDao;
	
	@Autowired
	private CommonSDKConfigDao commonSDKConfigDao;

	@Override
	@Transactional(rollbackFor = Exception.class, value = "instTx")
	public JsonResObj updateConfig(JsonReqObj reqObj) throws Exception {
		// 请求对象转换
		ConfigDesktopReq req = new ConfigDesktopReq(reqObj);
        // 必填参数检查
        String[] checkList = new String[] { DataKeys.Desktop.APP_ID, DataKeys.Desktop.SDK_ID,
                DataKeys.Desktop.CLIENT_CONFIG, DataKeys.Desktop.SERVER_CONFIG };
        if (!reqObj.checkParmas(checkList))
            throw new SuperSDKException(ErrorEnum.PARAM_IS_NULL, "参数不全,请检查参数");
        // 返回对象
        ConfigDesktopRes res = new ConfigDesktopRes();

        // ChannelId检查
        if (StringUtils.isEmpty(req.getChannelId()))
            throw new SuperSDKException(ErrorEnum.PARAM_IS_NULL, "渠道号ChannelId为空,请检查参数");

        // 设置到返回对象中
        res.setChannelId(req.getChannelId());
        // 客户端配置信息处理
        SdkClientConfig sdkClientConfig = sdkClientConfigDao.getByPoId(req.getAppId(), req.getChannelId());
        if(null == sdkClientConfig){
        	sdkClientConfig = new SdkClientConfig();
        	sdkClientConfig.setPoId(new SdkClientConfig.PoId(req.getAppId(), req.getChannelId()));
        }
        sdkClientConfig.setConfigInfo(req.getClientConfig());
        sdkClientConfigDao.saveOrUpdate(sdkClientConfig);
        
        // 服务器配置信息处理
        SdkConfig sdkConfig = sdkConfigDao.getByPoId(req.getAppId(), req.getChannelId());
        if(null == sdkConfig){
        	sdkConfig = new SdkConfig();
        	sdkConfig.setPoId(new SdkConfig.PoId(req.getAppId(), req.getChannelId()));
        }
        sdkConfig.setHandleBean(SuperSDK.getInstance().getHandleBean(req.getSdkId()));

        /**
         * 处理服务器参数
         */
        //保存当前线程appId
        String localAppId =req.getAppId();
        //切换到默认数据源
        ThreadHelper.setAppId(null);
        //根据AppChannelId查询服务器通用配置参数
        CommonSDKConfig commonSDKConfig = commonSDKConfigDao.findById(req.getChannelId());
        //如果通用配置参数为空,直接设置配置参数,否则拼接
        if(null == commonSDKConfig || StringUtils.isEmpty(commonSDKConfig.getCommonConfigInfo().trim())){
            sdkConfig.setConfigInfo(req.getServerConfig());
        }else{
            //判断配置参数是否为空
            if(StringUtils.isEmpty(req.getServerConfig().trim())){
                sdkConfig.setConfigInfo(commonSDKConfig.getCommonConfigInfo());
            }else{
                String allServerConfig = commonSDKConfig.getCommonConfigInfo().trim().substring(0, commonSDKConfig.getCommonConfigInfo().trim().lastIndexOf("}")) +
                        "," +
                        req.getServerConfig().substring(1);
                //剔除公共参数尾部的花括号
                //map完整性
                //拼接剔除头部花括号的配置参数
                sdkConfig.setConfigInfo(allServerConfig);
            }
        }
        //切换到之前数据源继续操作
        ThreadHelper.setAppId(localAppId);

        sdkConfig.setSdkName(req.getSdkName());
        sdkConfigDao.saveOrUpdate(sdkConfig);

		res.setAppId(req.getAppId());
		res.setSdkId(req.getSdkId());
		res.setClientConfig(req.getClientConfig());
		res.setServerConfig(req.getServerConfig());
		res.setSdkName(req.getSdkName());
		return res.ok().getRes();
	}
	
	@Override
	public JsonResObj queryConfig(JsonReqObj reqObj) throws Exception {
		// 请求对象转换
		ConfigDesktopReq req = new ConfigDesktopReq(reqObj);
        // 必填参数检查
        String[] checkList = new String[] { DataKeys.Desktop.APP_ID, DataKeys.Desktop.CHANNEL_IDS };
        if (!reqObj.checkParmas(checkList))
            throw new SuperSDKException(ErrorEnum.PARAM_IS_NULL, "参数不全,请检查参数");

        // 返回对象
        ConfigDesktopRes res = new ConfigDesktopRes();

        /**
         * 如果channel_Ids是all,则查询所有客户端及其服务器配置信息
         * 否则,按照分隔符进行分割,分别查询appChannelId的客户端,服务器配置信息
         */
        // 处理channel_Ids
        String[] channelIds = req.getChannelIds().split(SPILT);
        // 设置返回对象AppId
        res.setAppId(req.getAppId());
        List<SdkConfig> configList;
        List<SdkClientConfig> clientConfigList;
        // 如果channelIds是all,查询所有,否则按照channelId查询
        if ((channelIds.length == 1) && (ALL.equals(channelIds[0]))) {
            // 查询所有服务器配置信息
            configList = sdkConfigDao.getAllByAppId(req.getAppId());
            clientConfigList = sdkClientConfigDao.getAllByAppId(req.getAppId());
        } else {
            configList = sdkConfigDao.queryList(req.getAppId(), channelIds);
            clientConfigList = sdkClientConfigDao.queryList(req.getAppId(), channelIds);
        }
        if(null != configList && 0 < configList.size()){
            for(SdkConfig configTemp: configList){
                Map<String, String> channelIdMap = new HashMap<String, String>();

                SdkConfig sdkConfig = sdkConfigDao.getByPoId(configTemp.getPoId().getAppId() , configTemp.getPoId().getAppChannelId());
                ISDK sdkImpl = ServiceCommon.getServiceByConfigPlatform(sdkConfig, ISDK.class);
                channelIdMap.put(DataKeys.Desktop.SDK_ID, sdkImpl.getSDKId());
                channelIdMap.put(DataKeys.Desktop.SDK_NAME, configTemp.getSdkName());
                channelIdMap.put(DataKeys.Desktop.SERVER_CONFIG, configTemp.getConfigInfo());
                //循环遍历客户端参数,根据channelId组装到对应的结果中
                for(SdkClientConfig clientConfigTemp : clientConfigList){
                    if(clientConfigTemp.getPoId().getAppChannelId().equals(configTemp.getPoId().getAppChannelId())){
                        channelIdMap.put(DataKeys.Desktop.CLIENT_CONFIG, clientConfigTemp.getConfigInfo());
                        break;
                    }
                }
                // 将Map格式的JSON字符串添加到返回对象中
                res.setConfigInfo(configTemp.getPoId().getAppChannelId(), channelIdMap);
            }
            res.setConfigCount(String.valueOf(configList.size()));
        }else{
            res.setConfigCount("0");
        }
        return res.ok().getRes();
	}
}
