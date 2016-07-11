package com.hutong.supersdk.common.constant;

public class DataKeys {
	
	public static final String DEBUG = "debug";
	
	public static final String JSON_DATA = "jsonData";
	
	public static class Common {
		public static final String APP_ID = "app_id";
		public static final String APP_NAME = "app_name";
		public static final String NOTICE_URL = "notice_url";
		public static final String CALL_BACK_URL = "call_back_url";
		public static final String SDK_ID = "sdk_id";
		public static final String SDK_NAME = "sdk_name";
		public static final String SHORT_ID = "short_id";
		public static final String CHANNEL_ID = "channel_id";
		public static final String SIGN = "sign";
		public static final String TIME = "time";
		public static final String SUPERSDK_UID = "supersdk_uid";
		public static final String SUPERSDK_ORDER_ID = "supersdk_order_id";
		public static final String APP_DATA = "app_data";
		public static final String CREATE_TIME = "create_time";
	}
	
	public static class Error {
		public static final String ERROR = "error";
		public static final String ERROR_NO = "error_no";
		public static final String ERROR_MSG = "error_msg";
	}
	
	public static class LoginCheck extends Common {
		public static final String SDK_UID = "sdk_uid";
		public static final String ACCESS_TOKEN = "access_token";
		public static final String REFRESH_TOKEN = "refresh_token";
	}
	
	public static class Payment extends Common {
		public static final String ORDER_ID = "order_id";
		public static final String ORDER_AMOUNT = "order_amount";
		public static final String SDK_ORDER_ID = "sdk_order_id";
		public static final String CURRENCY_TYPE = "currency_type";
		public static final String PAY_AMOUNT = "pay_amount";
		public static final String PAY_STATUS = "pay_status";
		public static final String PAY_TIME = "pay_time";
		public static final String PAY_TYPE = "pay_type";
		public static final String GAME_UID = "game_uid";
		public static final String SERVER_ID = "server_id";
		public static final String PRODUCT_ID = "product_id";
		public static final String PRODUCT_COUNT = "product_count";
		public static final String PRODUCT_NAME = "product_name";
		public static final String PRODUCT_DESC = "product_desc";
		public static final String SOURCE = "source";
		public static final String APP_UID = "app_uid";
		public static final String ROLE_ID = "role_id";
		public static final String ROLE_NAME = "role_name";
		public static final String ROLE_GRADE = "role_grade";
		public static final String ROLE_BALANCE = "role_balance";
	}
	
	public static class Push {
		public static final String PUSH_SCOPE = "push_scope"; 
		public static final String PUSH_MESSAGE = "push_message";
		public static final String PUSH_ALIAS_MAP = "push_alias_map";
		public static final String TIME_TO_LIVE = "time_to_live";
	}
	
	public static class QueryOrder extends Common  {
		//查询订单最大数量
		public static final String QUERY_MAX_ORDER_NUM = "num";
		//查询起止日期
		public static final String ORDER_START_TIME = "start_time";
		//查询截止日期
		public static final String ORDER_END_TIME = "end_time";
		//orderList返回的key
		public static final String ORDER_LIST_KEY = "order_list";
	}
	
	public static class Desktop extends Common {
		//默认数据源ID
		public static final String DEFAULT_DB_ID = "default";
		//用户名
		public static final String USER_NAME = "username";
		//密码
		public static final String PASSWORD = "password";
		//新密码
		public static final String NEW_PASSWORD = "new_password";
		//电子邮箱
		public static final String EMAIL = "email";
		//appId队列
		public static final String APP_ID_ARRAY = "apps";
		//访问token
		public static final String TOKEN = "token";
		//客户端配置信息
		public static final String CLIENT_CONFIG = "client_config";
		//服务器配置信息
		public static final String SERVER_CONFIG = "server_config";
		//配置信息数量
		public static final String CONFIG_COUNT = "config_count";
		//渠道编号列表
		public static final String CHANNEL_IDS = "channel_ids";
		//接入客户端使用的AppSecret参数
		public static final String APP_SECRET = "app_secret";
		//接入客户端使用的PrivateKey参数
		public static final String PRIVATE_KEY = "private_key";
		//接入服务器使用的EncryptKey参数
		public static final String ENCRYPT_KEY = "encrypt_key";
		//接入服务器使用的PaymentSecret参数
		public static final String PAYMENT_SECRET = "payment_secret";
	}
	
	//platform应用使用参数
	public static class Platform extends Common {
		//platform服务地址
//		public static final String PLATFORM_URL = "http://192.168.2.101:8080/platform/";
		//platform验证token接口地址
		public static final String CHECK_TOKEN_URL = "api/verify/token";
		//platform请求参数用户ID
		public static final String PLATFORM_USER_ID = "id";
		//用户token
		public static final String PLATFORM_USER_TOKEN = "token";
		//appId
		public static final String APP_ID = "app_id";
		//sign
		public static final String SIGN = "sign";
	}
}
