package com.hutong.supersdk.iservice.desktop;

import com.hutong.supersdk.common.iservice.ServiceName;
import com.hutong.supersdk.common.iservice.ServiceParam;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.model.JsonResObj;

/**
 * 
 */
@ServiceName("appDesktopService")
public interface IAppDesktopService extends IDesktopService {

    /**
     * 已登录用户请求应用列表
     * @param reqObj JSON Format Text
     *     time:时间戳
     *     data:
     *     	   id:platform用户id
     *         token:platform验证用户的token
     *     extra:
     *         key:value
     * @return JSON Format Text
     * 成功返回：
     *     status:ok
     *     data:（所属于该用户的app信息列表）
     *     apps: appId1:appName1;appId2:appName2...
     *         ...
     *     common:
     *         time:时间戳
     *     extra:
     *         key:value
     * 失败返回：
     *     status:fail
     *     data:
     *         error:错误名
     *         error_no:错误编码
     *         error_msg:错误信息
     *     common:
     *         time:时间戳
     *     extra:（扩展信息）
     */
    @ServiceParam({"jsonData"})
    public JsonResObj queryApps(JsonReqObj reqObj) throws Exception;

    /**
     * 已登录用户创建应用(默认创建应用配置为默认数据源)
     * @param reqObj JSON Format Text
     *     time:时间戳
     *     data:
     *     	   id:platform用户id
     *         token:platform验证用户的token
     *         app_id:应用的包名（用于生成appId，必须唯一）
     *         app_name:应用名称
     *         notice_url:支付通知地址
     *     extra:
     *         key:value
     * @return JSON Format Text
     * 成功返回：
     *     status:ok
     *     data:（所属于该用户的app信息列表）
     *         app_id:应用唯一id（package_name）
     *         app_name:应用名
     *         app_secret:接入时客户端使用的AppSecret参数
     *         private_key:接入时客户端使用的PrivateKey参数
     *         encrypt_key:接入时服务器使用时的EncryptKey参数
     *         payment_secret:接入服务器接收支付通知使用的PaymentSecret参数
     *         short_id:自动生成的唯一App Short Id
     *         notice_url:支付成功时通知的游戏服务器URL
     *     common:
     *         time:时间戳
     *     extra:
     *         key:value
     * 失败返回：
     *     status:fail
     *     data:
     *         error:错误名
     *         error_no:错误编码
     *         error_msg:错误信息
     *     common:
     *         time:时间戳
     *     extra:（扩展信息）
     */
    @ServiceParam({"jsonData"})
    public JsonResObj createApp(JsonReqObj reqObj) throws Exception;

    /**
     * 已登录用户查询指定APP详细信息
     * @param reqObj JSON Format Text
     *     time:时间戳
     *     data:
     *         id:platform用户id
     *         token:platform验证用户的token
     *         app_id:应用id
     *     extra:
     *         key:value
     * @return JSON Format Text
     * 成功返回：
     *     status:ok
     *     data:（所属于该用户的app信息列表）
     *         appId:应用唯一id（package_name）
     *         appName:应用名
     *         appSecret:接入时客户端使用的AppSecret参数
     *         privateKey:接入时客户端使用的PrivateKey参数
     *         encryptKey:接入时服务器使用时的EncryptKey参数
     *         paymentSecret:接入服务器接收支付通知使用的PaymentSecret参数
     *         short_id:自动生成的唯一App Short Id
     *         notice_url:支付成功时通知的游戏服务器URL
     *     common:
     *         time:时间戳
     *     extra:
     *         key:value
     * 失败返回：
     *     status:fail
     *     data:
     *         error:错误名
     *         error_no:错误编码
     *         error_msg:错误信息
     *     common:
     *         time:时间戳
     *     extra:（扩展信息）
     */
    @ServiceParam({"jsonData"})
    public JsonResObj queryApp(JsonReqObj reqObj) throws Exception;

    /**
     * 刷新指定App的服务器接入KEY值
     * @param reqObj JSON Format Text
     *     time:时间戳
     *     data:
     *     	   id:platform用户id
     *         token:platform验证用户的token
     *         app_id:SuperSDK在platform注册的appId
     *         sign:签名
     *     extra:
     *         key:value
     * @return JSON Format Text
     * 成功返回：
     *     status:ok
     *     data:（所属于该用户的app信息列表）
     *         appId:应用id
     *         encryptKey:新的EncryptKey
     *         paymentSecret:新的PaymentSecret
     *     common:
     *         time:时间戳
     *     extra:
     *         key:value
     * 失败返回：
     *     status:fail
     *     data:
     *         error:错误名
     *         error_no:错误编码
     *         error_msg:错误信息
     *     common:
     *         time:时间戳
     *     extra:（扩展信息）
     */
    @ServiceParam({"jsonData"})
    public JsonResObj refreshServerKey(JsonReqObj reqObj) throws Exception;

    /**
     * 更新APP的信息
     * @param reqObj JSON Format Text
     *     time:时间戳
     *     data:
     *     	   id:platform用户id
     *         token:platform验证用户的token
     *         app_id:应用id
     *         app_name:新的AppName
     *         notice_url:新的游戏通知地址
     *     extra:
     *         key:value
     * @return JSON Format Text
     * 成功返回：
     *     status:ok
     *     data:（所属于该用户的app信息列表）
     *         appId:应用id
     *         app_name:新的AppName
     *         notice_url:新的游戏通知地址
     *     common:
     *         time:时间戳
     *     extra:
     *         key:value
     * 失败返回：
     *     status:fail
     *     data:
     *         error:错误名
     *         error_no:错误编码
     *         error_msg:错误信息
     *     common:
     *         time:时间戳
     *     extra:（扩展信息）
     */
    @ServiceParam({"jsonData"})
    public JsonResObj updateAppProfile(JsonReqObj reqObj) throws Exception;
}
