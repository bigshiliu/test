package com.hutong.supersdk.sdk.utils.uc;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * 工具类。
 */
public class Util {
	public static String gameId = "550764";
	public static String apiKey = "bf08b4398f31933c733fe646af83f535";

	private static  Log Logger = LogFactory.getLog(CheckDomainThread.class);
	 private static ObjectMapper objectMapper = new ObjectMapper();
	    static{
	    	objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
	      	objectMapper.setDeserializationConfig(objectMapper.getDeserializationConfig().without(                  
	    		       DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES));
	      	int intervalTime = ConfigHelper.getIntervalTime();
			//启动线程定期获取IP列表【默认间隔24小时取一次】
			Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new IPListThread(),0,intervalTime,TimeUnit.HOURS);
	    }
	
		/**
		 * 使用对象进行json反序列化。
		 * @param json json串
		 * @param pojoClass 类类型
		 * @return
		 * @throws Exception
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public static Object decodeJson(String json, Class pojoClass) throws Exception{		
			try{
				return objectMapper.readValue(json, pojoClass);
			}catch(Exception e){
				throw e;
			}
		}
		
	    /**
	     * 将JSON字符串根据指定的Class反序列化成Java对象。
	     * 
	     * @param json JSON字符串
	     * @param reference 类型引用
	     * @return 反序列化生成的Java对象
	     * @throws Exception 如果反序列化过程中发生错误，将抛出异常
	     */
		public static Object decode(String json, TypeReference<?> reference) throws Exception {
			try {
				return objectMapper.readValue(json, reference);
			} catch (Exception e) {
				throw e;
			}
		}
		
		/**
		 * 将对象序列化。
		 * @param o 实体对象
		 * @return 序列化后json
		 * @throws Exception
		 */
		public static String encodeJson(Object o) throws Exception{
			try{
				return objectMapper.writeValueAsString(o);
			}catch(Exception e){
				throw e;
			}
		}

	/** 
     * MD5 加密 
     */  
    public static String getMD5Str(String str) {  
        MessageDigest messageDigest = null;  
  
        try {  
            messageDigest = MessageDigest.getInstance("MD5");  
            messageDigest.reset();  
            messageDigest.update(str.getBytes("UTF-8"));  
        } catch (NoSuchAlgorithmException e) {  
            Logger.error("NoSuchAlgorithmException caught!");
            System.exit(-1);  
        } catch (UnsupportedEncodingException e) {  
            Logger.error(e.toString());
        }  
  
        byte[] byteArray = messageDigest.digest();  
  
        StringBuffer md5StrBuff = new StringBuffer();  
  
        for (int i = 0; i < byteArray.length; i++) {              
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)  
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));  
            else  
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));  
        }  
  
        return md5StrBuff.toString();  
    }
    
    
    /**
	 * 将Map组装成待签名数据。 待签名的数据必须按照一定的顺序排列
	 * 
	 * @param params
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public static String getSignData(Map params) {
		StringBuffer content = new StringBuffer();

		// 按照key做排序
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		int index = 0;
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key) == null ? "" : params.get(key).toString();
			if (value != null) {
				content.append( key + "=" + value);
			} else {
				content.append(key + "=");
			}
		}

		return content.toString();
	}
	
	/**
	 * 替换所有换行符。
	 * @param str 原字符串
	 * @return 替换结果
	 */
	public static String trim(String str){
		if(StringUtils.isEmpty(str)){
			return str;
		}
		return str.replaceAll("\r", "").replaceAll("\n", "");
	}
	
	/**
	 * 
	 * 进行url编码。
	 * @param str 原字符串
	 * @return 
	 */
	public static String urlEncode(String str){
		try {
			return URLEncoder.encode(str,"UTF-8");
		} catch (Exception e) {
			return str;
		}
	}
}
