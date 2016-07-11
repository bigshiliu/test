package com.hutong.supersdk.mysql.config.dao;

import org.springframework.stereotype.Repository;

import com.hutong.supersdk.mysql.config.model.AppToSuperSDKUser;

import java.util.List;

@Repository
public class AppToSSDKUserDao extends ABaseConfigDao<AppToSuperSDKUser> {

    public void save(String supersdkUid, String appId) {
        AppToSuperSDKUser entity = new AppToSuperSDKUser();
        entity.setAppToSUid(new AppToSuperSDKUser.AppToUid(supersdkUid, appId));
        this.saveOrUpdate(entity);
    }

    public AppToSuperSDKUser getBySuperSDKUidAndAppId(String supersdkUid, String appId) {
        String GET_BY_SUPERSDKUID_AND_APPID_USER_INFO = " from AppToSuperSDKUser u where u.appToSUid.superSDKUid = ? and u.appToSUid.appId = ?";
        List<AppToSuperSDKUser> users = getHibernateTemplate().find(GET_BY_SUPERSDKUID_AND_APPID_USER_INFO, supersdkUid, appId);
        if (0 == users.size()) {
            return null;
        } else {
            return users.get(0);
        }
    }

}
