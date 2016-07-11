package com.hutong.supersdk.mysql.inst.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Repository;

@Repository
public class DataBaseDao {

	private final Log logger = LogFactory.getLog(DataBaseDao.class);

	@Autowired
	@Qualifier("hibernateTemplate")
	private HibernateTemplate hibernateTemplate;

	private static final String CREATE_APP_TABLES1 = "DROP TABLE IF EXISTS T_CHECK_ORDER_QUEUE;";
	private static final String CREATE_APP_TABLES2 = "CREATE TABLE T_CHECK_ORDER_QUEUE (	Order_Id VARCHAR (64) NOT NULL,	Notice_Times TINYINT (8) UNSIGNED NOT NULL DEFAULT '0',	Queue_Type VARCHAR (16) NOT NULL COMMENT '队列类型',	App_Id VARCHAR (64) NOT NULL,	PRIMARY KEY (Order_Id, App_Id)) ENGINE = MyISAM DEFAULT CHARSET = utf8;";
	private static final String CREATE_APP_TABLES3 = "DROP TABLE IF EXISTS T_PAY_NOTICE_QUEUE;";
	private static final String CREATE_APP_TABLES4 = "CREATE TABLE T_PAY_NOTICE_QUEUE (	Order_Id VARCHAR (64) NOT NULL,	Notice_Times TINYINT (8) UNSIGNED NOT NULL DEFAULT '0',	Queue_Type VARCHAR (16) NOT NULL COMMENT '队列类型',	App_Id VARCHAR (64) NOT NULL,	PRIMARY KEY(Order_Id, App_Id),	KEY QueueType (Queue_Type) USING BTREE) ENGINE = MyISAM DEFAULT CHARSET = utf8;";
	private static final String CREATE_APP_TABLES5 = "DROP TABLE IF EXISTS T_PAYMENT_ORDER;";
	private static final String CREATE_APP_TABLES6 = "CREATE TABLE T_PAYMENT_ORDER (	Order_Id VARCHAR (64) NOT NULL COMMENT '订单唯一标示',	Supersdk_Uid VARCHAR (64) NOT NULL COMMENT 'superSDK,用户唯一标示',	App_Id VARCHAR (64) NOT NULL,	SDK_Id VARCHAR (32) DEFAULT NULL,	SDK_Order_Id VARCHAR (64) DEFAULT NULL,	App_Channel_Id VARCHAR (32) NOT NULL,	App_Game_Uid VARCHAR (64) DEFAULT NULL COMMENT 'APP玩家角色唯一标示',	App_Server_Id VARCHAR (32) DEFAULT NULL COMMENT 'APP游戏服务器唯一标示',	App_Product_Id VARCHAR (64) DEFAULT NULL COMMENT '购买商品的唯一标示',	App_Product_Name VARCHAR (64) DEFAULT NULL,	App_Product_Count VARCHAR (32) DEFAULT NULL COMMENT '购买商品数量',	App_Data VARCHAR (2048) DEFAULT NULL,	App_Role_Id VARCHAR (32) DEFAULT NULL,	App_Role_Name VARCHAR (64) DEFAULT NULL,	App_Role_Grade VARCHAR (8) DEFAULT NULL,	App_Role_Balance VARCHAR (16) DEFAULT NULL,	Order_Amount DOUBLE (11, 2) DEFAULT NULL COMMENT '订单金额',	Currency_Type VARCHAR (32) DEFAULT NULL COMMENT '货币类型',	Pay_Amount DOUBLE (11, 2) NOT NULL COMMENT '实际支付金额',	Pay_Status VARCHAR (16) NOT NULL COMMENT '订单支付状态',	Pay_Type VARCHAR (32) NOT NULL COMMENT '订单支付方式',	Pay_Time TIMESTAMP NULL DEFAULT NULL,	Enable_Flag INT (11) DEFAULT '1' COMMENT '启用状态(0:禁用,1:启用)',	Create_Time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',	Source VARCHAR (2048) DEFAULT NULL COMMENT '扩展字段',	Extra VARCHAR (2048) DEFAULT NULL,	PRIMARY KEY (Order_Id),	KEY fk_super_sdk_uid (Supersdk_Uid) USING BTREE) ENGINE = MyISAM DEFAULT CHARSET = utf8;";
	private static final String CREATE_APP_TABLES7 = "DROP TABLE IF EXISTS T_SDK_CONFIG;";
	private static final String CREATE_APP_TABLES8 = "CREATE TABLE T_SDK_CONFIG (	App_Channel_Id VARCHAR (32) NOT NULL DEFAULT '',	App_Id VARCHAR (64) DEFAULT NULL,	SDK_Name VARCHAR (32) DEFAULT NULL,	Config_Info VARCHAR (2048) DEFAULT NULL,	SDK_Id VARCHAR (32) DEFAULT NULL,	Push_Notice_Info VARCHAR (2048) DEFAULT NULL COMMENT '推送消息配置信息',	Handle_Bean VARCHAR (32) DEFAULT NULL,	Notice_Url VARCHAR (2048) DEFAULT NULL,	Version VARCHAR (16) DEFAULT NULL,	Enable_Flag INT (11) DEFAULT '1' COMMENT '启用状态(0:禁用,1:启用)',	Create_Time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',	Extra VARCHAR (1024) DEFAULT NULL COMMENT '扩展字段',	PRIMARY KEY (App_Channel_Id)) ENGINE = MyISAM DEFAULT CHARSET = utf8 COMMENT = 'PlatformId：用来标示360，小米，UC等ChannelId：用来标示IOS，安卓，IOS越狱，海';";
	private static final String CREATE_APP_TABLES9 = "DROP TABLE IF EXISTS T_SDKCLIENT_CONFIG;";
	private static final String CREATE_APP_TABLES10 = "CREATE TABLE T_SDKCLIENT_CONFIG (	App_Channel_Id VARCHAR (32) NOT NULL,	App_Id VARCHAR (64) DEFAULT NULL,	SDK_Id VARCHAR (32) DEFAULT NULL,	Config_Info VARCHAR (4096) NOT NULL,	Version VARCHAR (16) DEFAULT NULL,	Enable_Flag INT (11) DEFAULT '1' COMMENT '启用状态(0:禁用,1:启用)',	Create_Time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',	Extra VARCHAR (1024) DEFAULT NULL COMMENT '扩展字段',	PRIMARY KEY (App_Channel_Id)) ENGINE = MyISAM DEFAULT CHARSET = utf8;";

	protected Session getSession() {
		return SessionFactoryUtils.getSession(hibernateTemplate.getSessionFactory(), true);
	}

	public boolean createDataBase(String DataBaseName) {
		try {
			SQLQuery query = getSession().createSQLQuery("CREATE DATABASE " + DataBaseName + " DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci");
			query.executeUpdate();
			return true;
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
	}

	/**
	 * 创建SuperSDK App信息表
	 * 
	 * @param DataBaseName
	 * @return
	 */
	public boolean createTable(String DataBaseName) {
		try {
			SQLQuery query = getSession().createSQLQuery("USE " + DataBaseName);
			query.executeUpdate();

			query = getSession().createSQLQuery(CREATE_APP_TABLES1);
			query.executeUpdate();

			query = getSession().createSQLQuery(CREATE_APP_TABLES2);
			query.executeUpdate();

			query = getSession().createSQLQuery(CREATE_APP_TABLES3);
			query.executeUpdate();

			query = getSession().createSQLQuery(CREATE_APP_TABLES4);
			query.executeUpdate();

			query = getSession().createSQLQuery(CREATE_APP_TABLES5);
			query.executeUpdate();

			query = getSession().createSQLQuery(CREATE_APP_TABLES6);
			query.executeUpdate();

			query = getSession().createSQLQuery(CREATE_APP_TABLES7);
			query.executeUpdate();

			query = getSession().createSQLQuery(CREATE_APP_TABLES8);
			query.executeUpdate();

			query = getSession().createSQLQuery(CREATE_APP_TABLES9);
			query.executeUpdate();

			query = getSession().createSQLQuery(CREATE_APP_TABLES10);
			query.executeUpdate();
			return true;
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
	}
}
