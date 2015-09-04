package com.jamtu.config;

public interface PrefUtil {
	// SharedPreference 文件名
	String PRE_FILE_NAME = "joke";
	// 主动调用stopService 方法 设置此值为 true
	String PRE_KILL_SERVICE = "ks";
	// 初始化数据库数据
	String PRE_INIT_DB_DATA = "ida";
	// 通过市场审核
	String PRE_ACCESS_APP_MARKET = "aam";
	// 数据库名称
	String PRE_DATABASE_NAME = "ijoke";
	// properties文件
	String PRE_PROPERTIES_NAME = "joke.properties";
	// 是否到达开启检查市场通过线程时间
	String PRE_ACCESS_MARKET_VALID = "amv";
	// 设置是否发出提示音
	String PRE_CONF_VOICE = "conf_voice";
	// 设置启用检查更新
	String PRE_CONF_CHECKUP = "conf_checkup";
	// APP 检查更新 1天/次
	// String PRE_APP_UPDATE = "au";
	// APP 第一次启动
	String PRE_APP_FIRST_START = "afs";

	// =======================URL======================
	String ACCESS_MARKET_URL = "http://7xj1fi.com1.z0.glb.clouddn.com/joke_v5.txt";
	String UPDATE_URL = "http://7xj1fi.com1.z0.glb.clouddn.com/update.txt";
}
