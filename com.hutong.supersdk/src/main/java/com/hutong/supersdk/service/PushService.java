package com.hutong.supersdk.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.constant.ErrorEnum;
import com.hutong.supersdk.common.exception.SuperSDKException;
import com.hutong.supersdk.common.iservice.IPushService;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.model.JsonResObj;
import com.hutong.supersdk.common.model.PushMessage;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.mysql.inst.dao.SDKConfigDao;
import com.hutong.supersdk.mysql.inst.model.SdkConfig;
import com.hutong.supersdk.service.modeltools.PushNoticeInfo;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.PushPayload.Builder;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;

/**
 * 推送处理Service处理实现类
 * @author QINZH
 *
 */
@Service
public class PushService implements IPushService {

    private static final int MAX_PUSH_RETRY_TIME = 3;
    
    private static final long TIME_TO_LIVE = 86400;
	
	private static Log logger = LogFactory.getLog(PushService.class);
	
	@Autowired
	private SDKConfigDao sdkConfigDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public JsonResObj pushMessage(JsonReqObj jsonData) throws SuperSDKException {
		logger.debug("PushNotice pushMessage start !!!");
		
		PushMessage pushData = new PushMessage(jsonData);
		
		//统一返回参数
		JsonResObj ret = new JsonResObj();
		//检查appid
		if(StringUtils.isEmpty(pushData.getAppId())){
			throw new SuperSDKException(ErrorEnum.APP_ID_ERROR);
		}
		//检查推送消息
		if(StringUtils.isEmpty(pushData.getPushMessage()) || StringUtils.isEmpty(pushData.getPushScope())){
			throw new SuperSDKException(ErrorEnum.PARAM_IS_NULL);
		}
		long time_to_live = 0l;
		if(StringUtils.isEmpty(pushData.getTimeToLive())){
			time_to_live = TIME_TO_LIVE;
		}else {
			time_to_live = Long.parseLong(pushData.getTimeToLive());
		}
	
		//初始化sdkConfigList
		List<SdkConfig> sdkConfigList;
		//alias推送时,使用的map
		Map<String, String> aliasMap = null;
		/**
		 * 检查推送范围,
		 * 如果是ALL,推送应用下全部渠道
		 * 如果是ALIAS,按照渠道号进行参数查询,然后分别推送各自渠道号下的alias
		 * 否则按照channelId查询然后进行推送
		 */
        String[] channelIds = {};
		if(pushData.isAll()){
			//根据AppId查询所有SdkConfig信息,并组装到sdkConfigList中
			sdkConfigList = sdkConfigDao.getAllByAppId(pushData.getAppId());
		}else if(pushData.isAlias()){
			//如果使用别名推送,获取别名map
			String aliasMapStr = pushData.getPushAliasMap();
			if(StringUtils.isEmpty(aliasMapStr))
				throw new SuperSDKException(ErrorEnum.PARAM_IS_NULL);
			aliasMap = ParseJson.getJsonContentByStr(aliasMapStr, Map.class);

			if (aliasMap == null)
                throw new SuperSDKException(ErrorEnum.PUSH_ALIAS_ERROR);

			List<String> keys = new ArrayList<String>(aliasMap.keySet());
			channelIds = new String[keys.size()];
			for(int i = 0; i < keys.size(); i++){
				channelIds[i] = keys.get(i);
			}
			sdkConfigList = sdkConfigDao.queryList(pushData.getAppId(), channelIds);
		}else{
			//如果pushScope为空或不为ALL,则说明推送对应channelId的信息
			String channelIdStr = pushData.getPushScope();
			//进行channelId分割,多个channelId用&拼接
			channelIds = channelIdStr.split("&");
			//根据channelId查询SdkConfig信息,并组装到sdkConfigList中
            sdkConfigList = sdkConfigDao.queryList(pushData.getAppId(), channelIds);
		}
		//如果sdkConfigList等于null或者小于1,则返回结果
		if(null == sdkConfigList || sdkConfigList.size() < 1){
			throw new SuperSDKException(ErrorEnum.APP_CHANNEL_ID_NOT_FOUND);
		}

		//遍历sdkConfigList,获取pushInfo信息
		for(SdkConfig sdkConfig : sdkConfigList){
			//判断tempSdkConfig
			if(StringUtils.isEmpty(sdkConfig.getPushNoticeInfo())){
				logger.error("[Fail]PushService pushMessage." + sdkConfig.getPoId().getAppId() + " APP:的" + sdkConfig.getSdkName() + "渠道,channelID:" + sdkConfig.getPoId().getAppChannelId() + "没有开通推送功能!");
				ret.setKeyData(sdkConfig.getPoId().getAppChannelId(), "Push Disabled");
				continue;
			}
			PushNoticeInfo pushInfo = ParseJson.getJsonContentByStr(sdkConfig.getPushNoticeInfo(), PushNoticeInfo.class);
			//对masterSecret,appKey进行判空处理
			if(pushInfo == null || StringUtils.isEmpty(pushInfo.getMasterSecret()) || StringUtils.isEmpty(pushInfo.getAppKey())){
				logger.error("[Fail]PushService pushMessage. Push Configuration Info is NULL");
				ret.setKeyData(sdkConfig.getPoId().getAppChannelId(), "Invalid Push Configuration");
                continue;
			}
			int maxRetryTimes = pushInfo.getMaxRetryTimes();
			if(maxRetryTimes < 1){
				//如果最大重试次数小于1,使用默认设置
				maxRetryTimes = MAX_PUSH_RETRY_TIME;
			}
			JPushClient jpushClient = new JPushClient(pushInfo.getMasterSecret(), pushInfo.getAppKey(), maxRetryTimes);
			PushPayload payload;
			//推送alias,用&拼接
			String aliasStr = null;
			//判断推送类型是否为alias
			if(pushData.isAlias()){
                //noinspection ConstantConditions
                aliasStr = aliasMap.get(sdkConfig.getPoId().getAppChannelId());

                if (StringUtils.isEmpty(aliasStr)) {
                    logger.error("aliasStr is null or blank.");
                    continue;
                }

				String[] alias = aliasStr.split("&");
				payload = buildPushObject_all_alias_alert(pushData.getPushMessage(), alias, time_to_live);
			}else{
				payload = buildPushObject_all_all_alert(pushData.getPushMessage(), time_to_live);
			}
			try {
				PushResult pushResult = jpushClient.sendPush(payload);
                if(pushData.isAlias()){
                	ret.setKeyData(sdkConfig.getPoId().getAppChannelId(), "alias:[" + aliasStr + "] success");
                	logger.debug("[SUCCESS]PushService pushMessage." + sdkConfig.getPoId().getAppId() + " APP:的" + sdkConfig.getSdkName() + "渠道,channelID:" + sdkConfig.getPoId().getAppChannelId() + " 的alias:" + aliasStr + "消息推送成功!");
                }else{
                	ret.setKeyData(sdkConfig.getPoId().getAppChannelId(), "success");
                	logger.debug("[SUCCESS]PushService pushMessage." + sdkConfig.getPoId().getAppId() + " APP:的" + sdkConfig.getSdkName() + "渠道,channelID:" + sdkConfig.getPoId().getAppChannelId() + "消息推送成功!");
                }
				logger.debug("[SUCCESS]PushService pushMessage. Got result:" + ParseJson.encodeJson(pushResult));
			} catch(APIConnectionException e){
				logger.error("[Fail]PushService pushMessage. Connection error, should retry later", e);
                ret.setKeyData(sdkConfig.getPoId().getAppChannelId(), "JPush Connection Error");
			} catch (APIRequestException e) {
				if(pushData.isAlias()){
					logger.error("[Fail]PushService pushMessage." + sdkConfig.getPoId().getAppId() + " APP:的" + sdkConfig.getSdkName() + "渠道,channelID:" + sdkConfig.getPoId().getAppChannelId() + " 的alais:" + aliasStr + "客户端初始化失败!");
	                ret.setKeyData(sdkConfig.getPoId().getAppChannelId(), "JPush Not Found Any Valid Device");
				}else{
					logger.error("[Fail]PushService pushMessage." + sdkConfig.getPoId().getAppId() + " APP:的" + sdkConfig.getSdkName() + "渠道,channelID:" + sdkConfig.getPoId().getAppChannelId() + "客户端初始化失败!");
	                ret.setKeyData(sdkConfig.getPoId().getAppChannelId(), "JPush Not Found Any Valid Device");
				}
			}
		}

        List<String> illegalChannels = new ArrayList<String>();
        for (String channel : channelIds) {
            if (!ret.getData().containsKey(channel)) {
                illegalChannels.add(channel);
            }
        }
        for (String channel : illegalChannels) {
            ret.setKeyData(channel, "Illegal Channel ID");
        }

		return ret.ok();
	}
	
	/**
	 * 快捷地构建推送对象:所有平台,所有设备,
	 * 内容为pushMessage的通知.
	 * @return PushPayload
	 */
	private static PushPayload buildPushObject_all_all_alert(String pushMessage, long time_to_live) {
		Builder builder = new Builder();
		if(0 > time_to_live)
			builder.setOptions(Options.newBuilder().setTimeToLive(TIME_TO_LIVE).build());
		else
			builder.setOptions(Options.newBuilder().setTimeToLive(time_to_live).build());
		builder.setPlatform(Platform.all());
		builder.setAudience(Audience.all());
		builder.setNotification(Notification.alert(pushMessage));
		return builder.build();
    }
	
	/**
	 * 构建推送对象:所有平台,推送目标是别名为 aliasArray数组,通知内容为 ALERT。
	 * @return PushPayload
	 */
	private static PushPayload buildPushObject_all_alias_alert(String pushMessage, String[] aliasArray, long time_to_live) {
		Builder builder = new Builder();
		if(0 > time_to_live)
			builder.setOptions(Options.newBuilder().setTimeToLive(TIME_TO_LIVE).build());
		else
			builder.setOptions(Options.newBuilder().setTimeToLive(time_to_live).build());
		builder.setPlatform(Platform.all());
		builder.setAudience(Audience.alias(aliasArray));
		builder.setNotification(Notification.alert(pushMessage));
		builder .build();
        return builder.build();
    }
}
