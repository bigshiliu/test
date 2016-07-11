package com.hutong.supersdk.iservice.desktop;

import com.hutong.supersdk.common.iservice.ServiceName;
import com.hutong.supersdk.common.iservice.ServiceParam;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.model.JsonResObj;

/**
 * @author Dongxu
 * Created by Dongxu on 2015/12/4.
 * sign: 所有必传参数自然排序拼接后MD5结果(剔除sign)
 */
@ServiceName("ownerDesktopService")
public interface IOwnerDesktopService extends IDesktopService {
	
	
    /**
     * 注册用户，该接口用于SuperSDK接入用户注册
     * @param reqObj JSON Format Text
     *             time:时间戳
     *             data:
     *                 username:用户名（必须唯一）
     *                 password:密码
     *                 email:电子邮箱（不必唯一）
     *                 sign:签名
     *             extra:（扩展信息）
     *                 key:value
     * @return JSON Format Text
     * 成功返回：
     *     status:ok
     *     data:
     *         id:用户唯一标识
     *         username:用户名
     *         email:电子邮箱
     *         token:访问的token
     *         apps:（所属该用户app列表）
     *         apps内容格式 appId1:appName1;appId2:appName2...
     *     common:
     *         time:时间戳
     *     extra:（扩展信息）
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
    public JsonResObj register(JsonReqObj reqObj);

    /**
     * 登陆用户，该接口用于SuperSDK接入用户登陆
     * @param reqObj JSON Format Text
     *             time:时间戳
     *             data:
     *                 username:用户名（必须唯一）
     *                 password:密码
     *                 sign:签名
     *             extra:（扩展信息）
     *                 key:value
     * @return JSON Format Text
     * 成功返回：
     *     status:ok
     *     data:
     *         id:用户唯一标识
     *         username:用户名
     *         email:电子邮箱
     *         token:访问的token（每次登录必须重新刷新token值）
     *         apps:（所属该用户app列表）
     *             appId:appName
     *     common:
     *         time:时间戳
     *     extra:（扩展信息）
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
    public JsonResObj login(JsonReqObj reqObj);

    /**
     * 修改密码，该接口用于SuperSDK接入用户修改密码
     * @param reqObj JSON Format Text
     *             time:时间戳
     *             data:
     *                 id:用户唯一标识
     *                 token:用户访问的token
     *                 password:用户的访问密码
     *                 new_password:用户新的访问密码
     *             extra:（扩展信息）
     *                 key:value
     * @return JSON Format Text
     * 成功返回：
     *     status:ok
     *     data:
     *     common:
     *         time:时间戳
     *     extra:（扩展信息）
     *         time:时间戳
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
    public JsonResObj changePassword(JsonReqObj reqObj);

    /**
     * 更新用户信息
     * @param reqObj JSON Format Text
     *             time:时间戳
     *             data:
     *                 id:用户唯一标识
     *                 token:用户访问的token
     *                 key:value（更新信息的键值对，key代表profile的键，value代表新的值）
     *                 sign:签名
     *             extra:（扩展信息）
     *                 key:value
     * @return JSON Format Text
     * 成功返回：
     *     status:ok
     *     data:
     *         id:用户唯一标识
     *         token:用户访问的token
     *         key:value（更新后的Profile信息键值对）
     *     common:
     *         time:时间戳
     *     extra:（扩展信息）
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
    public JsonResObj updateProfile(JsonReqObj reqObj);

}
