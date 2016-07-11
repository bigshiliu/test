package com.hutong.supersdk.service;


import org.springframework.stereotype.Service;


@Service
public class MemcachedService {

//	private static final Log logger = LogFactory.getLog(MemcachedService.class);
	
	public static final int MEMCACHED_MAX_KEY_LENGTH = 250;

	public static final int MAX_EXPIRE_TIME = 60*60*24*30;
	
	public static final int SIG_EXPIRED_MEMCACHED = 60*10;

//	@Autowired
//	private MemcachedClient memcachedClient;

//	/**
//	 * 
//	 * @param key
//	 * @return
//	 */
//	public Object get(String key) {
//		key = generateGlobalMcKey(key);
//		return memcachedClient.get(key);
//	}
//
//	/**
//	 * 
//	 * @param key
//	 * @param expire
//	 *            do not exceed 60*60*30
//	 * @param o
//	 * @return
//	 */
//	public boolean set(String key, int expire, Object o) {
//		key = generateGlobalMcKey(key);
//		memcachedClient.set(key, expire, o);
//		logger.debug(key);
//		return true;
//	}
//
//	public boolean del(String key) {
//		key = generateGlobalMcKey(key);
//		memcachedClient.delete(key);
//		return true;
//	}
//	
//	public boolean add(String key, int expire, Object o) {
//		try {
//			key = generateGlobalMcKey(key);
//			return memcachedClient.add(key, expire, o).get();
//		} catch (InterruptedException e) {
//			logger.error("",e);
//			return false;
//		} catch (ExecutionException e) {
//			logger.error("",e);
//			return false;
//		}
//	}
//	
//	private String generateGlobalMcKey(String key) {
//		String gKey = ThreadHelper.getAppId() + "_" + key;
//		
//		return gKey.substring(0, MEMCACHED_MAX_KEY_LENGTH);
//	}
}

