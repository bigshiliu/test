package com.hutong.supersdk.sdk.modeltools.pp;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

public class PPUserObj {
	
	@JsonProperty("id")
	private Integer id;
	
	@JsonProperty("state")
	private Map<String, Object> state = new HashMap<String, Object>();
	
	@JsonProperty("data")
	private Map<String, String> data = new HashMap<String, String>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Map<String, Object> getState() {
		return state;
	}

	public void setState(Map<String, Object> state) {
		this.state = state;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}
	
	

}
