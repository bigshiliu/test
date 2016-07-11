package com.hutong.supersdk.iservice.desktop;

import com.hutong.supersdk.common.iservice.IService;

/**
 * Desktop服务的根接口(所有的用户相关操作 统一在AOP中处理,请求platform平台验证)
 * 验证方式为 HTTP Post请求,
 * 请求格式为:
 * jsonData={time:xxxxxx,
 *		    data:{
 *		    	app_id:SuperSDK在platform中注册的唯一appId
 *		 		token:platform用户验证的token
 *				sign:所有请求参数,按照自然排序再两头拼接secret_key进行MD5加密
 *			},
 * 			extra{
 * 			}
 * 		}
 * @author QINZH
 */
public interface IDesktopService extends IService {
}
