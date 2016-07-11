package com.hutong.supersdk.sdk.modeltools.uc;

public class UCSDKInfo {
	private String gameId;
	private String cpId;
	private String apiKey;
	private String requestUrl;
	private String deBugRequestUrl;

	public String getDeBugRequestUrl() {
		return deBugRequestUrl;
	}

	public void setDeBugRequestUrl(String deBugRequestUrl) {
		this.deBugRequestUrl = deBugRequestUrl;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getCpId() {
		return cpId;
	}

	public void setCpId(String cpId) {
		this.cpId = cpId;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
}
