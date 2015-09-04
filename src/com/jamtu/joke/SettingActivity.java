package com.jamtu.joke;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

import com.jamtu.App;
import com.jamtu.AppManager;
import com.jamtu.common.FileUtils;
import com.jamtu.common.MethodsCompat;
import com.jamtu.common.UpdateManager;
import com.jamtu.config.Version;
import com.jamtu.utils.AppUtil;

/**
 * 设置界面
 * 
 * @created 2014-07-02
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 */
public class SettingActivity extends PreferenceActivity implements OnPreferenceClickListener {

	private Preference cache;
	private Preference feedback;
	private Preference update;
	private Preference about;

	private CheckBoxPreference voice;
	private CheckBoxPreference checkup;

	private App mAppContext;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		initView();
		AppManager.getAppManager().addActivity(this);
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		mAppContext = (App) getApplication();

		// 提示声音
		voice = (CheckBoxPreference) findPreference("voice");
		voice.setChecked(mAppContext.isVoice());
		if (mAppContext.isVoice()) {
			voice.setSummary("已开启提示声音");
		} else {
			voice.setSummary("已关闭提示声音");
		}
		voice.setOnPreferenceClickListener(this);

		checkup = (CheckBoxPreference) findPreference("checkup");
		checkup.setChecked(mAppContext.isCheckUp());
		checkup.setOnPreferenceClickListener(this);

		cache = (Preference) findPreference("cache");
		cache.setSummary(calCache());
		cache.setOnPreferenceClickListener(this);

		feedback = (Preference) findPreference("feedback");
		update = (Preference) findPreference("update");
		about = (Preference) findPreference("about");

		feedback.setOnPreferenceClickListener(this);
		update.setOnPreferenceClickListener(this);
		about.setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference == voice) {
			onVoice();
		} else if (preference == checkup) {
			mAppContext.setConfigCheckUp(checkup.isChecked());
		} else if (preference == cache) {
			onCache();
		} else if (preference == feedback) {
			onFeedBack();
		} else if (preference == update) {
			UpdateManager.getUpdateManager().checkAppUpdate(this, true);
		} else if (preference == about) {
			showAbout();
		}
		return true;
	}

	private void onVoice() {
		mAppContext.setConfigVoice(voice.isChecked());
		if (voice.isChecked()) {
			voice.setSummary("已开启提示声音");
		} else {
			voice.setSummary("已关闭提示声音");
		}
	}

	private void onCache() {
		AppUtil.clearAppCache(this);
		cache.setSummary("0KB");
	}

	private String calCache() {
		long fileSize = 0;
		String cacheSize = "0KB";
		File filesDir = getFilesDir();
		File cacheDir = getCacheDir();

		fileSize += FileUtils.getDirSize(filesDir);
		fileSize += FileUtils.getDirSize(cacheDir);
		// 2.2版本才有将应用缓存转移到sd卡的功能
		if (App.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
			File externalCacheDir = MethodsCompat.getExternalCacheDir(this);
			fileSize += FileUtils.getDirSize(externalCacheDir);
		}
		if (fileSize > 0)
			cacheSize = FileUtils.formatFileSize(fileSize);
		return cacheSize;
	}

	/**
	 * 发送反馈意见到指定的邮箱
	 */
	private void onFeedBack() {
		Intent i = new Intent(Intent.ACTION_SEND);
		// i.setType("text/plain"); //模拟器
		i.setType("message/rfc822"); // 真机
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { "361932463@qq.com" });
		i.putExtra(Intent.EXTRA_SUBJECT, "用户反馈【IJoke-笑话惨了-V" + Version.VERSION + "】客户端");
		i.putExtra(Intent.EXTRA_TEXT, "");
		startActivity(Intent.createChooser(i, "发邮件给作者"));
	}

	private void showAbout() {
		App.launchActivity(this, About.class, null);
	}
}
