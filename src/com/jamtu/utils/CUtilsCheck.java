package com.jamtu.utils;

import java.util.Calendar;

import android.content.Context;

public class CUtilsCheck {

	/**
	 * 检查日期
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static boolean checkStepByDay(Context context, String key) {
		boolean isOK = false;
		int day = SharedUtil.getInt(context, key, 0);
		int curDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
		if (day != curDay) {
			isOK = true;
		}
		return isOK;
	}

	/**
	 * 更新日期
	 * 
	 * @param context
	 * @param key
	 */
	public static void updateStepByDay(Context context, String key) {
		SharedUtil.putInt(context, key, Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
	}

	/**
	 * 检查时间间隔
	 * 
	 * @param context
	 * @param key
	 *            关键字
	 * @param step
	 *            时间间隔
	 * @return true：满足 false：不满足
	 */
	public static boolean checkStep(Context context, String key, long step) {
		boolean isOK = false;

		long curTime = System.currentTimeMillis();
		long exeTime = SharedUtil.getLong(context, key, -1);
		if (Math.abs(curTime - exeTime) > step) {
			isOK = true;
		}

		return isOK;
	}

	/**
	 * 更新间隔时间
	 * 
	 * @param context
	 * @param key
	 *            关键字
	 */
	public static void updateStep(Context context, String key) {
		SharedUtil.putLong(context, key, System.currentTimeMillis());
	}
}
