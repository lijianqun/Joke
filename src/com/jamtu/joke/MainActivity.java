package com.jamtu.joke;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jamtu.App;
import com.jamtu.AppManager;
import com.jamtu.common.DoubleClickExitHelper;
import com.jamtu.common.UpdateManager;
import com.jamtu.config.PrefUtil;
import com.jamtu.fragments.BDJ_JokePagerFragment;
import com.jamtu.fragments.JokePagerFragment;
import com.jamtu.fragments.LocaleJokePagerFragment;
import com.jamtu.fragments.QSBK_JokePagerFragment;
import com.jamtu.interfaces.DrawerMenuCallBack;
import com.jamtu.utils.AppUtil;
import com.jamtu.utils.SharedUtil;
import com.jamtu.widget.BadgeView;

public class MainActivity extends ActionBarActivity implements DrawerMenuCallBack {
	static final String DRAWER_MENU_TAG = "drawer_menu";
	static final String DRAWER_CONTENT_TAG = "drawer_content";

	static final String CONTENT_JOKE_LOCALE = "本地段子";
	static final String CONTENT_JOKE_NET_ONE = "网络段子1";
	static final String CONTENT_JOKE_NET_TWO = "网络段子2";

	static final String CONTENTS[] = { CONTENT_JOKE_LOCALE, CONTENT_JOKE_NET_ONE, CONTENT_JOKE_NET_TWO };

	static final String FRAGMENTS_DB[] = { JokePagerFragment.class.getName(), QSBK_JokePagerFragment.class.getName(),
			BDJ_JokePagerFragment.class.getName() };
	static final String FRAGMENTS_XML[] = { LocaleJokePagerFragment.class.getName(),
			QSBK_JokePagerFragment.class.getName(), BDJ_JokePagerFragment.class.getName() };

	static final String TITLES[] = CONTENTS;
	private static DrawerNavigationMenu mMenu = DrawerNavigationMenu.newInstance();
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private FragmentManager mFragmentManager;
	private DoubleClickExitHelper mDoubleClickExitHelper;
	// 当前显示的界面标识
	private String mCurrentContentTag;
	private static String mTitle;// actionbar标题
	public static BadgeView mNotificationBadgeView;
	private ActionBar mActionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		App app = (App) getApplication();
		// 将activity加入到AppManager堆栈中
		AppManager.getAppManager().addActivity(this);
		initView(savedInstanceState);
		if (app.isCheckUp()) {
			UpdateManager.getUpdateManager().checkAppUpdate(this, false);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mTitle != null) {
			mActionBar.setTitle(mTitle);
		}
		if (mCurrentContentTag != null && mMenu != null) {
			if (mCurrentContentTag.equalsIgnoreCase(CONTENTS[1])) {
				if (false) {// 未登录
					onClickExplore();
					mMenu.highlightExplore();
				}
			}
		}
		if (AppUtil.isScreenOff) {// 锁屏解锁进入
			AppUtil.registerTimeReceiver(this);
			AppUtil.isScreenOff = false;
		}
	}

	private void initView(Bundle savedInstanceState) {
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setHomeButtonEnabled(true);

		mDoubleClickExitHelper = new DoubleClickExitHelper(this);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerListener(new DrawerMenuListener());
		// 设置滑出菜单的阴影效果
		// mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,GravityCompat.START);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, 0, 0);

		mFragmentManager = getSupportFragmentManager();
		if (null == savedInstanceState) {
			setExploreShow();
		}
	}

	private void setExploreShow() {
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.main_slidingmenu_frame, mMenu, DRAWER_MENU_TAG);
		boolean isFirstStart = SharedUtil.getBoolean(this, PrefUtil.PRE_APP_FIRST_START, true);
		boolean dbDataReady = SharedUtil.getBoolean(this, PrefUtil.PRE_INIT_DB_DATA, false);
		boolean accessMarket = AppUtil.isAccess(this);
		// 数据库数据初始化完成 && 通过市场审核
		if (isFirstStart) {// 第一次启动采用xml的笑话
			SharedUtil.putBoolean(this, PrefUtil.PRE_APP_FIRST_START, false);
			ft.replace(R.id.main_content, LocaleJokePagerFragment.newInstance(), DRAWER_CONTENT_TAG);
		} else if (!isFirstStart && dbDataReady && accessMarket) {
			ft.replace(R.id.main_content, JokePagerFragment.newInstance(), DRAWER_CONTENT_TAG);
		} else
			ft.replace(R.id.main_content, LocaleJokePagerFragment.newInstance(), DRAWER_CONTENT_TAG);
		ft.commit();

		mTitle = "本地段子";
		mActionBar.setTitle(mTitle);
		mCurrentContentTag = CONTENT_JOKE_LOCALE;
	}

	private class DrawerMenuListener implements DrawerLayout.DrawerListener {
		@Override
		public void onDrawerOpened(View drawerView) {
			mDrawerToggle.onDrawerOpened(drawerView);
		}

		@Override
		public void onDrawerClosed(View drawerView) {
			mDrawerToggle.onDrawerClosed(drawerView);
		}

		@Override
		public void onDrawerSlide(View drawerView, float slideOffset) {
			mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
		}

		@Override
		public void onDrawerStateChanged(int newState) {
			mDrawerToggle.onDrawerStateChanged(newState);
		}
	}

	@Override
	public void onClickLogin() {
		// TODO 登录
		// App.launchActivity(this, MySelfInfoActivity.class, null);
	}

	@Override
	public void onClickSetting() {
		// 设置
		App.launchActivity(this, SettingActivity.class, null);
	}

	@Override
	public void onClickExplore() {
		// 本地笑话
		showMainContent(0);
	}

	@Override
	public void onClickMySelf() {
		// 网络笑话1
		showMainContent(1);
	}

	@Override
	public void onClickLanguage() {
		// 网络笑话2
		showMainContent(2);
	}

	@Override
	public void onClickShake() {
		// 摇一摇
	}

	@Override
	public void onClickExit() {
		// TODO 推出
		this.finish();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_actionbar_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.main_actionbar_menu_search:
			// TODO 搜索
			Intent intent = new Intent(this, SearchActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;
		case R.id.main_actionbar_menu_notification:
			// TODO 显示通知
			// onClickNotice();
			return true;
		default:
			break;
		}
		return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 判断菜单是否打开
			if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
				mDrawerLayout.closeDrawers();
				return true;
			}
			return mDoubleClickExitHelper.onKeyDown(keyCode, event);
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
				mDrawerLayout.closeDrawers();
				return true;
			} else {
				mDrawerLayout.openDrawer(Gravity.START);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/** 显示内容 */
	private void showMainContent(int pos) {
		mDrawerLayout.closeDrawers();
		String tag = CONTENTS[pos];
		if (tag.equalsIgnoreCase(mCurrentContentTag))
			return;

		FragmentTransaction ft = mFragmentManager.beginTransaction();
		if (mCurrentContentTag != null) {
			Fragment fragment = mFragmentManager.findFragmentByTag(mCurrentContentTag);
			if (fragment != null) {
				ft.remove(fragment);
			}
		}

		boolean dbDataReady = SharedUtil.getBoolean(this, PrefUtil.PRE_INIT_DB_DATA, false);
		boolean accessMarket = AppUtil.isAccess(this);

		// 数据库数据初始化完成 && 通过市场审核
		if (dbDataReady && accessMarket)
			ft.replace(R.id.main_content, Fragment.instantiate(this, FRAGMENTS_DB[pos]), tag);
		else
			ft.replace(R.id.main_content, Fragment.instantiate(this, FRAGMENTS_XML[pos]), tag);
		ft.commit();

		mActionBar.setTitle(TITLES[pos]);
		mTitle = mActionBar.getTitle().toString();// 记录主界面的标题
		mCurrentContentTag = tag;
	}
}
