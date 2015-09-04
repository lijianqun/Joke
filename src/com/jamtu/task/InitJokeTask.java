package com.jamtu.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.jamtu.config.PrefUtil;
import com.jamtu.utils.AppUtil;
import com.jamtu.utils.FileUtil;
import com.jamtu.utils.SharedUtil;

/**
 * 初始化本地笑话数据库
 * 
 * 解密
 * 
 */
public class InitJokeTask extends AsyncTask<Object, Void, Boolean> {
	public static boolean isRunning = false;
	private Context mContext;

	public synchronized static void start(final Context context) {
		if (isRunning) {
			return;
		}
		new Handler(context.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				AppUtil.executeTask(new InitJokeTask(context));
			}
		});
	}

	private InitJokeTask(Context context) {
		mContext = context;
	}

	@Override
	protected void onPreExecute() {
		if (isRunning) {
			cancel(true);
			return;
		}
		isRunning = true;
	}

	@Override
	protected Boolean doInBackground(Object... params) {
		try {
			FileUtil.copyFile(mContext);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (FileUtil.isCopySuccess(mContext))// 数据初始化成功
			SharedUtil.putBoolean(mContext, PrefUtil.PRE_INIT_DB_DATA, true);
		else
			// 数据初始化失败
			SharedUtil.putBoolean(mContext, PrefUtil.PRE_INIT_DB_DATA, false);
		isRunning = false;
	}
}
