package com.hutong.supersdk.mysql.config.dao;

import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.hutong.supersdk.mysql.config.model.SuperSDKUser;
import com.hutong.supersdk.mysql.inst.dao.ABaseInstDao;

/**
 * User对象Dao
 *
 * @author Administrator
 */
@Repository
@SuppressWarnings("unused")
public class SuperSDKUserDao extends ABaseConfigDao<SuperSDKUser> {

    private static final Logger logger = LoggerFactory.getLogger(SuperSDKUserDao.class);

    /**
     * 根据第三方SDK用户ID查询有无关联的SUPERSDK账号信息
     *
     * @param sdkUid
     * @return 对应的sdkUid的User对象, 如果不存在, 返回null
     */
    @SuppressWarnings("unchecked")
    public SuperSDKUser getBySdkUserInfo(String sdkId, String sdkUid) {
        String GET_BY_SDK_USER_INFO = " from SuperSDKUser u where u.sdkId = ? and u.sdkUid = ?";
        List<SuperSDKUser> users = getHibernateTemplate().find(GET_BY_SDK_USER_INFO, sdkId, sdkUid);
        if (0 == users.size()) {
            return null;
        } else {
            return users.get(0);
        }
    }

    /**
     * 根据第三方SDK用户ID创建SUPERSDK关联账号信息
     *
     * @param user
     * @return 创建成功返回true, 否则返回false
     */
    public boolean create(SuperSDKUser user) {
        return getResultBySave(user);
    }

    /**
     * 保存后提交事务
     *
     * @param superSDKUser
     */
    public void saveOrUpdateOnFlush(SuperSDKUser superSDKUser) {
        Session session = getHibernateTemplate().getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.flush();
        session.clear();
        session.saveOrUpdate(superSDKUser);
        tx.commit();
    }
}
