package com.jamtu.bean;

import java.io.Serializable;

import com.google.gson.Gson;
import com.jamtu.db.library.db.annotation.Transient;

/**
 * 实体类
 * 
 */
public abstract class Entity implements Serializable {
	@Transient
	protected String _id;

	public String get_Id() {
		return _id;
	}

	public void set_Id(String _id) {
		this._id = _id;
	}

	// 缓存的key
	@Transient
	protected String cacheKey;

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public static Object fromJson(String json, Class<?> clz) {
		Gson gson = new Gson();
		return gson.fromJson(json, clz);
	}
}
