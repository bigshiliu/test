package com.hutong.supersdk.sdk.utils.uc;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IPListThread implements Runnable{
	private static  Log Logger = LogFactory.getLog(IPListThread.class);
	
	
	
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void run() {
		/**
		 * 203.156.231.70 上海电信2 ,117.135.151.250 上海移动,119 ,147.224.158 汕头电信,183.233.224.189 汕头移动
		 **/
		try {
			Logger.debug("【开始调用system.getIPList接口】");
			Map data = new HashMap();
			AccessProxy ap = new AccessProxy();
			String result = ap.doPost(ServiceName.IPLIST, data,Util.apiKey,Util.gameId);
			Logger.debug("[响应结果]"+result);//结果也是一个json格式字符串
			IPResponse ipresp = (IPResponse)Util.decodeJson(result, IPResponse.class);//反序列化
			Logger.debug("【结束调用system.getIPList接口】");
			if(null!=ipresp){
				Logger.debug("[id]"+ipresp.getId());
				Logger.debug("[code]"+ipresp.getState().getCode());
				Logger.debug("[msg]"+ipresp.getState().getMsg());
				 IPResponse.IPResponseData ipResponseData= ipresp.getData();
				 Logger.debug("[data]");
				if(null!=ipResponseData&&null!=ipResponseData.getList()){
						Logger.debug("[list]");
						for(IP ip : ipResponseData.getList()){
							Logger.debug("[ip]"+ip.getIp());
							Logger.debug("[port]"+ip.getPort());
							Logger.debug("[isp]"+ip.getIsp());
						}
						if(ipresp.getData().getList().size()>0){
							AccessProxy.setIpList(ipresp.getData().getList());//保存返回的IP列表到AccessProxy.ipList	
						}else{
							Logger.error("获取IPList接口list为空");
						}
						Logger.debug("[list]");
				}else{
					Logger.error("获取IPList接口data为空");
				}
				Logger.debug("[data]");
			}else{
				Logger.error("获取IPList接口返回异常");
			}
		} catch (Exception e) {
			Logger.error("访问出错获取IPList失败");
		}
	}
}