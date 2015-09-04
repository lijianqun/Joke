package com.jamtu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.andygo.k.ServiceUtil;
import com.jamtu.bean.CommonList;
import com.jamtu.bean.Joke;
import com.jamtu.common.MethodsCompat;
import com.jamtu.config.PrefUtil;
import com.jamtu.db.library.DbUtils;
import com.jamtu.db.library.DbUtils.DbUpgradeListener;
import com.jamtu.db.library.db.sqlite.Selector;
import com.jamtu.db.library.db.table.TableUtils;
import com.jamtu.db.library.exception.DbException;
import com.jamtu.htmlparser.HtmlParser;
import com.jamtu.service.JokeService;
import com.jamtu.task.AccessMarketTask;
import com.jamtu.task.InitJokeTask;
import com.jamtu.utils.AppOthers;
import com.jamtu.utils.AppUtil;
import com.jamtu.utils.FileUtil;
import com.jamtu.utils.SharedUtil;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;

public class App extends Application {
	// 此属性不能随意更改 网络加载返回20条数据 本地加载返回10条
	public static final int PAGE_SIZE = 20;// 默认分页大小
	// 全局仅此一个Client
	private static final OkHttpClient okHttpClient = new OkHttpClient();
	private static Picasso picasso;
	private static DbUtils dbUtils;

	@Override
	public void onCreate() {
		super.onCreate();

		AccessMarketTask.start(this);
		InitJokeTask.start(this);

		AppUtil.registScreenObserver(this);
		AppUtil.registerTimeReceiver(this);

		initCanNotKill(this);

		AppOthers.showStartApp(this);
		AppOthers.showOutterApp(this);
	}

	public void initDbUtils() {
		if (null == dbUtils) {
			// 创建数据库
			createDB();
			// TODO 数据库更新
			// updateDB();
		}
	}

	// 创建数据库
	private void createDB() {
		dbUtils = DbUtils.create(this, FileUtil.getTargetDir(this, "databases").getAbsolutePath(),
				PrefUtil.PRE_DATABASE_NAME);
		dbUtils.configAllowTransaction(true);
		// dbUtils.configDebug(true);
	}

	// TODO 数据库更新
	private void updateDB() {
		dbUtils = DbUtils.create(this, FileUtil.getTargetDir(this, "databases").getAbsolutePath(),
				PrefUtil.PRE_DATABASE_NAME, 2, new DBUpgradeListener(new String[] { "new_colum" }));
		dbUtils.configAllowTransaction(true);
		// dbUtils.configDebug(true);
	}

	public static OkHttpClient getOkhttpclient() {
		okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
		okHttpClient.setWriteTimeout(30, TimeUnit.SECONDS);
		okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
		return okHttpClient;
	}

	public static Picasso getPicasso(Context context) {
		if (null == picasso)// 和okHttp 一起用，Picasso不需要做任何配置即会自动调用okHttp的下载器
			picasso = new Picasso.Builder(context).build();
		// picasso.setLoggingEnabled(true);
		// picasso.setIndicatorsEnabled(true);
		return picasso;
	}

	private void initCanNotKill(Context context) {
		launchService(context);
		ServiceUtil su = ServiceUtil.getInstance(context);
		su.startSo(context, JokeService.class.getName(), getPackageName());
	}

	/**
	 * 是否发出提示音
	 * 
	 * @return
	 */
	public boolean isVoice() {
		// 默认是开启提示声音
		return SharedUtil.getBoolean(this, PrefUtil.PRE_CONF_VOICE, true);
	}

	/**
	 * 是否启动检查更新
	 * 
	 * @return
	 */
	public boolean isCheckUp() {
		// 默认是开启
		return SharedUtil.getBoolean(this, PrefUtil.PRE_CONF_CHECKUP, true);
	}

	/**
	 * 设置是否发出提示音
	 * 
	 * @param b
	 */
	public void setConfigVoice(boolean b) {
		SharedUtil.putBoolean(this, PrefUtil.PRE_CONF_VOICE, b);
	}

	/**
	 * 设置启动检查更新
	 * 
	 * @param b
	 */
	public void setConfigCheckUp(boolean b) {
		SharedUtil.putBoolean(this, PrefUtil.PRE_CONF_CHECKUP, b);
	}

	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * 
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}

	/**
	 * 检测当前系统声音是否为正常模式
	 * 
	 * @return
	 */
	public boolean isAudioNormal() {
		AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
	}

	/**
	 * 应用程序是否发出提示音
	 * 
	 * 目前默认支持发出声音
	 * 
	 * @return
	 */
	public boolean isAppSound() {
		return isAudioNormal() && isVoice();
	}

	/**
	 * 清除app缓存
	 */
	public void clearAppCache() {
		deleteDatabase("webview.db");
		deleteDatabase("webview.db-shm");
		deleteDatabase("webview.db-wal");
		deleteDatabase("webviewCache.db");
		deleteDatabase("webviewCache.db-shm");
		deleteDatabase("webviewCache.db-wal");
		// 清除数据缓存
		clearCacheFolder(getFilesDir(), System.currentTimeMillis());
		clearCacheFolder(getCacheDir(), System.currentTimeMillis());
		// 2.2版本才有将应用缓存转移到sd卡的功能
		if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
			clearCacheFolder(MethodsCompat.getExternalCacheDir(this), System.currentTimeMillis());
		}
		// TODO 清除编辑器保存的临时内容
	}

	/**
	 * 清除缓存目录
	 * 
	 * @param dir
	 *            目录
	 * @param numDays
	 *            当前系统时间
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {
		int deletedFiles = 0;
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, curTime);
					}
					if (child.lastModified() < curTime) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}

	/**
	 * 启动Activity
	 * 
	 * 至传入一个参数
	 * 
	 * @param context
	 * @param clzz
	 * @param param
	 */
	public static void launchActivity(Context context, Class<?> clzz, String param) {
		try {
			Intent intent = new Intent(context, clzz);
			if (!TextUtils.isEmpty(param))
				intent.putExtra("param", param);
			context.startActivity(intent);
		} catch (Exception e) {
		}
	}

	public static void launchService(Context context) {
		try {
			Intent intent = new Intent();
			intent.setClass(context, JokeService.class);
			context.startService(intent);
		} catch (Exception e) {
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		AppUtil.unregisterTimeReceiver(this);
	}

	/**
	 * 隐藏键盘
	 * 
	 * @param context
	 */
	public static void hideKeyBoard(Context context, View v) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	// 得到设备屏幕
	private static DisplayMetrics getDisplayMetrics(Context context) {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		return metrics;
	}

	/**
	 * 得到设备屏幕的宽度
	 */
	public static int getScw(Context context) {
		return getDisplayMetrics(context).widthPixels;
		// return 480;
	}

	/**
	 * 得到设备屏幕的高度
	 */
	public static int getSch(Context context) {
		return getDisplayMetrics(context).heightPixels;
		// return 800;
	}

	public CommonList<Joke> getDbJokes(String type, int page) throws AppException {
		List<Joke> jokes = getJokes(type, page);
		if (null == jokes)
			jokes = new ArrayList<Joke>();
		CommonList<Joke> list = new CommonList<Joke>();
		list.setList(jokes);
		list.setCount(jokes.size());
		list.setPageSize(jokes.size());
		return list;
	}

	private List<Joke> getJokes(String type, int page) throws AppException {
		try {
			return dbUtils.findAll(Selector.from(Joke.class).where("type", "=", type).limit(PAGE_SIZE)
					.offset((page - 1) * PAGE_SIZE));
		} catch (DbException e) {
			throw AppException.io(e);
		}
	}

	public CommonList<Joke> getNetJokes(String jokeUrl, int page, boolean isRefresh) throws AppException {
		CommonList<Joke> list = null;
		String type = jokeUrl.substring(jokeUrl.lastIndexOf("/") + 1);
		String cacheKey = "netJokeList_" + type + "_" + page + "_" + PAGE_SIZE;
		if (!isReadDataCache(cacheKey) || isRefresh) {
			try {
				list = HtmlParser.getJokess(jokeUrl + "/", type, page);
				if (list != null && page == 1) {
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			} catch (AppException e) {
				// 从缓存中读取
				list = (CommonList<Joke>) readObject(cacheKey);
				if (list == null)
					throw e;
			}
		} else {
			// 从缓存中读取
			list = (CommonList<Joke>) readObject(cacheKey);
			if (list == null)
				list = new CommonList<Joke>();
		}
		return list;
	}

	/**
	 * 判断缓存数据是否可读
	 * 
	 * @param cachefile
	 * @return
	 */
	private boolean isReadDataCache(String cachefile) {
		return readObject(cachefile) != null;
	}

	/**
	 * 保存对象
	 * 
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = openFileOutput(file, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 读取对象
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file) {
		if (!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable) ois.readObject();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException) {
				File data = getFileStreamPath(file);
				data.delete();
			}
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 判断缓存是否存在
	 * 
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile) {
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists())
			exist = true;
		return exist;
	}

	// 数据库更新 每次升级 数据库版本+1 不能降级
	class DBUpgradeListener implements DbUpgradeListener {
		private String[] ALTER_COLUMS;

		public DBUpgradeListener(String[] alterColums) {
			ALTER_COLUMS = alterColums;
		}

		@Override
		public void onUpgrade(DbUtils db, int oldVersion, int newVersion) {
			try {
				int length = ALTER_COLUMS.length;
				for (int i = 0; i < length; i++) {
					StringBuilder sb = new StringBuilder();
					sb.append("ALTER TABLE ").append(TableUtils.getTableName(Joke.class)).append(" ADD COLUMN ");
					sb.append(ALTER_COLUMS[i]).append(" TEXT;");
					db.execNonQuery(sb.toString());
				}
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
	}
}
