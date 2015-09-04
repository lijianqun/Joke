package com.jamtu.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jamtu.task.AccessMarketTask;

/**
 * 一分钟一次广播
 * 
 * @author Administrator
 * 
 */
public class TimeReceiver extends BroadcastReceiver {
	private static TimeReceiver mInstance;

	public synchronized static TimeReceiver getInstance() {
		if (mInstance == null) {
			mInstance = new TimeReceiver();
		}

		return mInstance;
	}

	// 不能在其他类创建实例，同时也不能再Manifest中配置
	private TimeReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		AccessMarketTask.start(context);
	}

}
