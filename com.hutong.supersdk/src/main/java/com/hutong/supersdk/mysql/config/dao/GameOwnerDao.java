package com.hutong.supersdk.mysql.config.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hutong.supersdk.mysql.config.model.GameOwner;

/**
 * Created by Dongxu on 2015/12/4.
 */
@Repository
public class GameOwnerDao extends ABaseConfigDao<GameOwner> {

	private static final String FIND_BY_USERNAME = "from GameOwner go where go.username = ?";

	private static final String FIND_BY_USERNAME_PASSWORD = "from GameOwner go where go.username = ? and go.password = ?";

	private static final String CHECK_TOKEN_BY_USER_ID = "from GameOwner go where go.id = ? and go.token = ?";
	
	private static final String FIND_BY_USERID_PASSWORD = "from GameOwner go where go.id = ? and go.password = ?";

	// 检查username
	@SuppressWarnings("unchecked")
	public GameOwner findByUserName(String username) {
		List<GameOwner> GameOwners = (List<GameOwner>) getHibernateTemplate().find(FIND_BY_USERNAME, username);
		if (GameOwners.size() == 0) {
			return null;
		} else {
			return GameOwners.get(0);
		}
	}

	// 检查username,password是否匹配
	@SuppressWarnings("unchecked")
	public GameOwner findByUserNamePassword(String username, String password) {
		List<GameOwner> GameOwners = (List<GameOwner>) getHibernateTemplate().find(FIND_BY_USERNAME_PASSWORD, username,
				password);
		if (GameOwners.size() == 0) {
			return null;
		} else {
			return GameOwners.get(0);
		}
	}

	// 检查token,根据用户ID
	public GameOwner checkTokenByUserId(String userId, String token) {
		@SuppressWarnings("unchecked")
		List<GameOwner> GameOwners = (List<GameOwner>) getHibernateTemplate().find(CHECK_TOKEN_BY_USER_ID, userId,
				token);
		if (GameOwners.size() == 0) {
			return null;
		} else {
			return GameOwners.get(0);
		}
	}

	// 检查userId,password是否匹配
	@SuppressWarnings("unchecked")
	public GameOwner findByUserIdPassword(String userId, String password) {
		List<GameOwner> GameOwners = (List<GameOwner>) getHibernateTemplate().find(FIND_BY_USERID_PASSWORD, userId,
				password);
		if (GameOwners.size() == 0) {
			return null;
		} else {
			return GameOwners.get(0);
		}
	}
}
