package com.jamtu.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.jamtu.App;
import com.jamtu.config.PrefUtil;
import com.jamtu.utils.AppUtil;
import com.jamtu.utils.SharedUtil;

public class JokeService extends Service {

	AlarmManager alarm;
	PendingIntent mPending;

	@Override
	public void onCreate() {
		super.onCreate();
		setForeground();
		setAlarm(this);
		AppUtil.registerTimeReceiver(this);
	}

	private void setForeground() {
		Notification notification = new Notification();
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
		startForeground(0, notification);
	}

	private void setAlarm(Context context) {
		Intent intent = new Intent(context, getClass());
		alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
		mPending = PendingIntent.getService(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		long now = System.currentTimeMillis();
		alarm.setInexactRepeating(AlarmManager.RTC, now, 1 * 60 * 1000, mPending);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, START_STICKY, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopForeground(true);
		if (null != alarm && null != mPending)
			alarm.cancel(mPending);
		if (!SharedUtil.getBoolean(this, PrefUtil.PRE_KILL_SERVICE, false)) {
			App.launchService(getApplicationContext());
		}
		AppUtil.unregisterTimeReceiver(this);
	}

}
