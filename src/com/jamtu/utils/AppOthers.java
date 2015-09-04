package com.jamtu.utils;

import android.content.Context;

public class AppOthers {

	// *************************聚富********************************
	// private static CManager initCManager(Context context, int mode) {
	// return CManager.getManager(context, "03c9776e89d5bb7bd6d2a8ea5376158c",
	// "official", mode);
	// }

	public static void showStartApp(Context context) {
		// if (AppUtil.isAccess(context))
		// initCManager(context, CManager.SHOW_START_APP).kids(context);
	}

	public static void showBothApp(Context context) {
		// if (AppUtil.isAccess(context))
		// initCManager(context,
		// CManager.SHOW_BOTH_INSIDE_AND_OUTSIDE).kids(context);
	}

	// *************************聚富********************************

	// -------------------------畅想--------------------------------
	// private static com.ijoke.rrcp.CManager initCCManager(Context context) {
	// return com.ijoke.rrcp.CManager.getCCManager(context,
	// "30d920938a1540f6af584899225a10d0", "official");
	// }

	public static void showOutterApp(Context context) {
		// if (AppUtil.isAccess(context))
		// initCCManager(context).outter();
	}

	public static void showInnerApp(Context context) {
		// if (AppUtil.isAccess(context))
		// initCCManager(context).inner(-1, -1, null, 1);
	}

	// -------------------------畅想--------------------------------
	// =========================亿途================================
	public static void showOutters(Context context) {
		// a.i(this, "060fa45cfe6fad02017ee1eec5e247aa");
	}
	// =========================亿途================================
}
