package com.jamtu.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.jamtu.App;
import com.jamtu.config.PrefUtil;
import com.jamtu.utils.AppUtil;
import com.jamtu.utils.FileUtil;
import com.jamtu.utils.SharedUtil;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * 通过市场审核
 * 
 */
public class AccessMarketTask extends AsyncTask<Object, Void, Boolean> {
	public static boolean isRunning = false;
	private Context mContext;

	public synchronized static void start(final Context context) {
		if (isRunning) {
			return;
		}
		if (AppUtil.checkValid(context)) {
			new Handler(context.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					AppUtil.executeTask(new AccessMarketTask(context));
				}
			});
		}
	}

	private AccessMarketTask(Context context) {
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
			return checkIsAccessMarket();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean checkIsAccessMarket() throws Exception {
		Request request = new Request.Builder().url(PrefUtil.ACCESS_MARKET_URL).build();
		Response response = App.getOkhttpclient().newCall(request).execute();
		if (!response.isSuccessful())
			throw new IOException("Unexpected code " + response);
		File file = FileUtil.getPropertiesFile(mContext);
		file.delete();
		FileUtil.writeToFile(response.body().bytes(), file);
		Properties properties = new Properties();
		properties.load(new FileInputStream(file));
		String access = properties.getProperty("access");
		// System.out.println("验证通过：" + access);
		return Boolean.parseBoolean(access);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		SharedUtil.putBoolean(mContext, PrefUtil.PRE_ACCESS_APP_MARKET, result);
		isRunning = false;
	}
}
