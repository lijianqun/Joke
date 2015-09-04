package com.jamtu.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.jamtu.config.PrefUtil;
import com.jamtu.config.Version;

/**
 * MODE_MULTI_PROCESS 跨进程通信
 * 
 * @author lijq
 * @date 2013-10-9 下午04:52:08
 * @version 1.0
 */
public class SharedUtil {

	private static SharedPreferences eckShared;
	private static DESUtils currDesUtils = new DESUtils();// 当前加密对象
	private static DESUtils oldDesUtils = new DESUtils();// 旧版本加密对象

	private static SharedPreferences getEckShared(Context context) {
		if (null == eckShared) {
			eckShared = context.getSharedPreferences(PrefUtil.PRE_FILE_NAME, Context.MODE_MULTI_PROCESS);
		}
		return eckShared;
	}

	// 将ShardPreferences中旧的K-V数据 更新为最新的
	// 新旧K-V数据的不同 只是加密的密钥变了
	public static void copy(Context context) {
		try {
			SharedPreferences sharedPreferences = getEckShared(context);
			Map<String, ?> map = sharedPreferences.getAll();
			Set<String> keySet = map.keySet();
			Iterator<String> iterator = keySet.iterator();
			int isFindAndSet = 0;
			while (iterator.hasNext()) {
				String key = iterator.next();
				if (isFindAndSet == 0)
					isFindAndSet = oldDesUtils.findAndSetKey(key);
				switch (isFindAndSet) {
				case -1:
					if (Version.VERSION >= 1) {
						copy(context, key, map.get(key));
						removeOld(context, key);
					}
					break;
				case 1:
					Object objVal = map.get(key);
					// 只有String类型的数据 采用KV加密，其他类型只加密K
					if (objVal instanceof String) {
						copy(context, oldDesUtils.decryptStr(key), oldDesUtils.decryptStr(objVal.toString()));
					} else {
						copy(context, oldDesUtils.decryptStr(key), objVal);
					}
					removeOld(context, key);
					break;
				}
				iterator.remove();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 更新为最新的K-V数据后，旧的数据可以删除
	private static void removeOld(Context context, String keyMi) {
		SharedPreferences sharedPreferences = getEckShared(context);
		Editor editor = sharedPreferences.edit();
		editor.remove(keyMi);
		editor.commit();
	}

	// 用最新的密钥对旧的K-V数据进行加密
	private static void copy(Context context, String key, Object value) {
		String val = value.toString();
		if (value instanceof String) {
			putString(context, key, val);
		} else if (value instanceof Boolean) {
			putBoolean(context, key, Boolean.parseBoolean(val));
		} else if (value instanceof Integer) {
			putInt(context, key, Integer.parseInt(val));
		} else if (value instanceof Long) {
			putLong(context, key, Long.parseLong(val));
		}
	}

	public static void putString(Context context, String key, String value) {
		SharedPreferences sharedPreferences = getEckShared(context);
		Editor editor = sharedPreferences.edit();
		editor.putString(currDesUtils.encryptStr(key), currDesUtils.encryptStr(value));
		editor.commit();
	}

	public static String getString(Context context, String key, String defaultValue) {
		SharedPreferences sharedPreferences = getEckShared(context);
		return currDesUtils.decryptStr(sharedPreferences.getString(currDesUtils.encryptStr(key),
				currDesUtils.encryptStr(defaultValue)));
	}

	public static void putBoolean(Context context, String key, Boolean value) {
		SharedPreferences sharedPreferences = getEckShared(context);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(currDesUtils.encryptStr(key), value);
		editor.commit();
	}

	public static Boolean getBoolean(Context context, String key, Boolean defaultValue) {
		SharedPreferences sharedPreferences = getEckShared(context);
		return sharedPreferences.getBoolean(currDesUtils.encryptStr(key), defaultValue);
	}

	public static void putLong(Context context, String key, long value) {
		SharedPreferences sharedPreferences = getEckShared(context);
		Editor editor = sharedPreferences.edit();
		editor.putLong(currDesUtils.encryptStr(key), value);
		editor.commit();
	}

	public static long getLong(Context context, String key, long defaultValue) {
		SharedPreferences sharedPreferences = getEckShared(context);
		return sharedPreferences.getLong(currDesUtils.encryptStr(key), defaultValue);
	}

	public static void putInt(Context context, String key, int value) {
		SharedPreferences sharedPreferences = getEckShared(context);
		Editor editor = sharedPreferences.edit();
		editor.putInt(currDesUtils.encryptStr(key), value);
		editor.commit();
	}

	public static int getInt(Context context, String key, int defaultValue) {
		SharedPreferences sharedPreferences = getEckShared(context);
		return sharedPreferences.getInt(currDesUtils.encryptStr(key), defaultValue);
	}

	public static void remove(Context context, String key) {
		SharedPreferences sharedPreferences = getEckShared(context);
		Editor editor = sharedPreferences.edit();
		editor.remove(currDesUtils.encryptStr(key));
		editor.commit();
	}

	public static void clear(Context context) {
		SharedPreferences sharedPreferences = getEckShared(context);
		Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.commit();
	}

}
