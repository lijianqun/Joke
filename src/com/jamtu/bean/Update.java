package com.jamtu.bean;

import java.util.Date;

/**
 * 应用程序更新实体类
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class Update extends Entity {

	private String description;
	private String url;
	private int num_version;
	private String version;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getNum_version() {
		return num_version;
	}

	public void setNum_version(int num_version) {
		this.num_version = num_version;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
