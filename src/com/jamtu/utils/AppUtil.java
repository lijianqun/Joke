package com.jamtu.utils;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.jamtu.App;
import com.jamtu.config.PrefUtil;
import com.jamtu.config.Version;
import com.jamtu.receiver.TimeReceiver;
import com.jamtu.utils.ScreenObserver.ScreenStateListener;

public class AppUtil {
	public static boolean isAccess(Context context) {
		return SharedUtil.getBoolean(context, PrefUtil.PRE_ACCESS_APP_MARKET, false);
	}

	// 是否到达开启检查市场通过线程时间
	public static boolean checkValid(Context context) {
		boolean isOK = CUtilsCheck.checkStep(context, PrefUtil.PRE_ACCESS_MARKET_VALID, 1000 * 60 * 10);
		if (isOK) {
			CUtilsCheck.updateStep(context, PrefUtil.PRE_ACCESS_MARKET_VALID);
		}
		return isOK;
	}

	/**
	 * 检查网络连接状态
	 * 
	 * @param context
	 * @return true:网络可用 <br>
	 *         false:网络不可用
	 */
	public synchronized static boolean checkNetWork(Context context) {
		boolean status = false;
		ConnectivityManager connMgr = SystemServiceUtils.getConnectivityManager(context);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null) {
			status = networkInfo.isConnected();
		}
		return status;
	}

	// AsyncTask使用注意事项:http://blog.csdn.net/gf771115/article/details/17248037
	// 线程池:不限制线程数
	// private static ExecutorService TASK_EXECUTOR_FULL = (ExecutorService)
	// Executors.newCachedThreadPool();
	// 线程池:线程数上限10
	private static ExecutorService TASK_EXECUTOR_LIMITED = (ExecutorService) Executors.newFixedThreadPool(20);

	public synchronized static void executeTask(AsyncTask task) {
		if (checkSDKVersion(Build.VERSION_CODES.HONEYCOMB)) {
			task.executeOnExecutor(TASK_EXECUTOR_LIMITED);
		} else {
			task.execute();
		}
	}

	/**
	 * 检查系统SDK版本号是否大于(或者等于)指定版本号
	 * 
	 * @param version
	 * @return true: 满足<br>
	 *         false: 不满足
	 */
	public static boolean checkSDKVersion(int version) {
		boolean isOK = false;
		if (Build.VERSION.SDK_INT >= version) {
			isOK = true;
		}
		return isOK;
	}

	/**
	 * 检查屏幕状态
	 * 
	 * @param context
	 * @return true:屏幕亮<br>
	 *         false:屏幕黑
	 */
	public synchronized static boolean checkScreenStatus(Context context) {
		return SystemServiceUtils.getPowerManager(context).isScreenOn();
	}

	/**
	 * 注册时间变化事件（一分钟一次）
	 */
	public synchronized static void registerTimeReceiver(Context context) {
		if (!checkScreenStatus(context)) {
			// 处于锁屏状态
			return;
		}

		try {
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_TIME_TICK);
			context.getApplicationContext().registerReceiver(TimeReceiver.getInstance(), filter);
		} catch (Exception e) {
		}
	}

	public static void unregisterTimeReceiver(Context context) {
		try {
			context.getApplicationContext().unregisterReceiver(TimeReceiver.getInstance());
		} catch (Exception e) {
		}
	}

	private static ScreenObserver mScreenObserver;
	public static boolean isScreenOff = false;

	public static void registScreenObserver(final Context context) {
		if (mScreenObserver == null) {
			mScreenObserver = new ScreenObserver(context);
			mScreenObserver.requestScreenStateUpdate(new ScreenStateListener() {
				@Override
				public void onScreenOff() {
					AppUtil.unregisterTimeReceiver(context);
					isScreenOff = true;
					System.out.println(String.format("Version = %d", Version.VERSION));
				}
			});
		}
	}

	/**
	 * 清除app缓存
	 * 
	 * @param activity
	 */
	public static void clearAppCache(Activity activity) {
		final App ac = (App) activity.getApplication();
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					ToastMessage(ac, "缓存清除成功");
				} else {
					ToastMessage(ac, "缓存清除失败");
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					ac.clearAppCache();
					msg.what = 1;
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	/**
	 * 弹出Toast消息
	 * 
	 * @param msg
	 */
	public static void ToastMessage(Context cont, String msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 获得屏幕的截图
	 * 
	 * @param activity
	 * 
	 * @return
	 */
	public static Bitmap takeScreenShot(Activity activity) {
		// View是你需要截图的View
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();
		// 图片允许最大空间 单位：KB
		double maxSize = 100.00;
		// 将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		b1.compress(Bitmap.CompressFormat.JPEG, 70, baos);
		byte[] b = baos.toByteArray();
		// 将字节换成KB
		double mid = b.length / 1024;
		// 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
		if (mid > maxSize) {
			// 获取bitmap大小 是允许最大大小的多少倍
			double i = mid / maxSize;
			// 开始压缩 此处用到平方根 将宽带和高度压缩掉对应的平方根倍
			// （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
			b1 = ImageUtils.zoomBitmap(b1, b1.getWidth() / Math.sqrt(i), b1.getHeight() / Math.sqrt(i));
		}

		return b1;
	}
}
