package com.hutong.supersdk.mysql.dynamic;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.springframework.core.io.Resource;

public class DynamicResource implements Resource {
	private ADynamicBean dynamicBean;
	
	public DynamicResource(ADynamicBean dynamicBean){
		this.dynamicBean = dynamicBean;
	}
	
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(dynamicBean.getXml().getBytes("UTF-8"));
	}
	
	//其他实现方法省略
	public long contentLength() throws IOException {
		return 0;
	}
	
	public Resource createRelative(String arg0) throws IOException {
		return null;
	}
	
	public boolean exists() {
		return false;
	}
	
	public String getDescription() {
		return null;
	}
	
	public File getFile() throws IOException {
		return null;
	}
	
	public String getFilename() {
		return null;
	}
	
	public URI getURI() throws IOException {
		return null;
	}
	
	public URL getURL() throws IOException {
		return null;
	}
	
	public boolean isOpen() {
		return false;
	}
	
	public boolean isReadable() {
		return false;
	}
	public long lastModified() throws IOException {
		return 0;
	}
}