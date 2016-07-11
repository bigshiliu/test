package com.hutong.supersdk.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.model.JsonResObj;
import com.hutong.supersdk.common.util.EncryptUtil;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.common.util.StringUtil;
import com.hutong.supersdk.iservice.IPlatformService;
import com.hutong.supersdk.service.modeltools.PlatformConfig;
import com.hutong.supersdk.service.modeltools.PlatformReq;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

@Service
public class PlatformService implements IPlatformService {
	
	private  final Log logger = LogFactory.getLog(PlatformService.class);
	
	@Autowired
	private PlatformConfig platformConfig;
	
	// platformCache名称
	private static final String CACHE_NAME_PLATFORM = "userToken";
	
	public boolean checkToken(String userId, String token){
		/**
		 * 调用Platform验证用户token
		 */
		// 参数检查
		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(token)) {
			logger.info("PlatformService checkToken Error.参数不全");
			return false;
		}

		// 先去缓存中查询有无此userId的token信息,如果有,进行验证
		CacheManager manager = CacheManager.getInstance();
		Cache platformCache = manager.getCache(CACHE_NAME_PLATFORM);
		boolean checkToken4Cache = false;
		Element element = platformCache.get(userId);
		if (null != element) {
			// 如果缓存中有userId信息,则进行token比对
			if (StringUtil.equalsStringIgnoreCase(token, String.valueOf(element.getValue()))) {
				checkToken4Cache = true;
			}
		}
		if (!checkToken4Cache) {
			// token有效性验证,请求platform系统token验证接口
			PlatformReq platormReq = new PlatformReq(new JsonReqObj());
			platormReq.setToken(token);
			platormReq.setAppId(platformConfig.getPlatformAppId());
			Map<String, String> signMap = new HashMap<String, String>();
			signMap.put(DataKeys.Platform.APP_ID, platformConfig.getPlatformAppId());
			signMap.put(DataKeys.Platform.PLATFORM_USER_TOKEN, token);
			// 组装sign
			String sign = EncryptUtil.generateSign(signMap, platformConfig.getPlatformSecretKey());
			platormReq.setSign(sign);
			// 组装platform接口请求地址
			String requestUrl = platformConfig.getPlatformUrl()
					+ DataKeys.Platform.CHECK_TOKEN_URL;
			// 使用Http post请求
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("jsonData", ParseJson.encodeJson(platormReq.getReq()));
			String resultReq = HttpUtil.postForm(requestUrl, paramMap);
			// 如果返回不为空处理返回
			if (StringUtils.isEmpty(resultReq)) {
				logger.info("PlatformService checkToken Error.platform请求失败");
				return false;
			}
			JsonResObj jsonRes = ParseJson.getJsonContentByStr(resultReq, JsonResObj.class);
			// platform验证token失败
			if (null == jsonRes || !jsonRes.isOk()) {
				logger.info("PlatformService checkToken Error.platform验证token失败");
				return false;
			}

			// platform验证通过将UserId Token加入到缓存中
			platformCache.put(new Element(userId, token));
		}
		return true;
	}
}
