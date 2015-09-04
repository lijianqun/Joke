package com.jamtu.utils;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.view.WindowManager;

public class SystemServiceUtils {

	private static NotificationManager notificationManager = null;

	private static TelephonyManager telephonyManager = null;

	private static ConnectivityManager connectivityManager = null;

	private static WindowManager windowManager = null;

	private static ActivityManager activityManager = null;

	private static PowerManager powerManager = null;

	/**
	 * 取得通知管理器服务
	 * 
	 * @param context
	 * @return
	 */
	public static PowerManager getPowerManager(Context context) {
		if (context != null && powerManager == null)
			powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		return powerManager;
	}

	/**
	 * 取得通知管理器服务
	 * 
	 * @param context
	 * @return
	 */
	public static NotificationManager getNotificationManager(Context context) {
		if (context != null && notificationManager == null)
			notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		return notificationManager;
	}

	/**
	 * 取得电话管理器服务
	 * 
	 * @param context
	 * @return
	 */
	public static TelephonyManager getTelephonyManager(Context context) {
		if (context != null && telephonyManager == null)
			telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager;
	}

	/**
	 * 网络连接的服务
	 * 
	 * @param context
	 * @return
	 */
	public static ConnectivityManager getConnectivityManager(Context context) {
		if (context != null && connectivityManager == null)
			connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectivityManager;
	}

	/**
	 * 窗口管理器服务
	 * 
	 * @param context
	 * @return
	 */
	public static WindowManager getWindowManager(Context context) {
		if (context != null && windowManager == null)
			windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return windowManager;
	}

	/**
	 * 获取ActivityManager
	 * 
	 * @param context
	 * @return
	 */
	public static ActivityManager getActivityManager(Context context) {
		if (null != context && null == activityManager)
			return (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		return activityManager;
	}

}
