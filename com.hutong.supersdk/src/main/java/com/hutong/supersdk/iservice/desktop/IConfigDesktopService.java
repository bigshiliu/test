package com.hutong.supersdk.iservice.desktop;

import com.hutong.supersdk.common.iservice.ServiceName;
import com.hutong.supersdk.common.iservice.ServiceParam;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.model.JsonResObj;

/**
 * IConfigDesktopService<br/>
 * Created by Administrator on 2015/12/4.
 */
@ServiceName("configDesktopService")
public interface IConfigDesktopService extends IDesktopService {

    /**
     * 创建或更新渠道信息
     * @param reqObj JSON Format Text
     *     time:时间戳
     *     data:
     *         id:platform用户唯一标识
     *         token:platform用户访问的合法token
     *         app_id:应用ID
     *         sdk_id:第三方SDK的ID
     *         (channel_id非必填项,当存在时,为修改操作,不存在时,为新建操作.)
     *         channel_id:对应渠道的渠道号
     *         client_config:客户端配置信息的JSON字符串
     *         server_config:服务器配置信息的JSON字符串
     *     extra:
     *         key:value
     * @return JSON Format Text
     * 成功返回：
     *     status:ok
     *     data:（所属于该用户的app信息列表）
     *         app_id:应用ID
     *         sdk_id:第三方SDK的ID
     *         channel_id:对应渠道的渠道号
     *         client_config:客户端配置信息的JSON字符串
     *         server_config:服务器端配置信息的JSON字符串
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
    public JsonResObj updateConfig(JsonReqObj reqObj) throws Exception;

    /**
     * 查询渠道配置信息
     * @param reqObj JSON Format Text
     *     time:时间戳
     *     data:
     *         id:platform用户唯一标识
     *         token:platform用户访问的合法token
     *         app_id:应用ID
     *         channel_ids:需查询的渠道编号
     *               all - 查询当前APP所有可用的渠道配置信息
     *               多个渠道号以分号（;）分隔 - 标识查询范围
     *     extra:
     *         key:value
     * @return JSON Format Text
     * 成功返回：
     *     status:ok
     *     data:（所属于该用户的app信息列表）
     *         app_id:应用ID
     *         channel_id1:（Map格式的JSON字符串）
     *             sdk_id:第三方SDK的ID
     *             client_config:客户端配置信息的JSON字符串
     *             server_config:服务器端配置信息的JSON字符串
     *         channel_id2:
     *             sdk_id:第三方SDK的ID
     *             client_config:客户端配置信息的JSON字符串
     *             server_config:服务器端配置信息的JSON字符串
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
    public JsonResObj queryConfig(JsonReqObj reqObj) throws Exception;

}
