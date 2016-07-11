package com.hutong.supersdk.sdk.utils.uc;

import java.util.List;

/**
 * 获取Ip列表响应类。
 */
public class IPResponse extends BaseResponse{
	IPResponseData data;

	@Override
	public IPResponseData getData() {
		return data;
	}

	public void setData(IPResponseData data) {
		this.data = data;
	}

	

	public class IPResponseData {
		List<IP> list;

		public List<IP> getList() {
			return list;
		}

		public void setList(List<IP> list) {
			this.list = list;
		}
	}
}
