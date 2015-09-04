package com.jamtu.fragments;

import static com.jamtu.fragments.NetJokeListFragment.JOKE_URL;
import android.os.Bundle;

import com.jamtu.adapter.ViewPageFragmentAdapter;
import com.jamtu.basefragment.BaseViewPagerFragment;
import com.jamtu.utils.AppOthers;

/**
 * 网络段子1
 * 
 * @author lijq
 * @created 2015-03-29
 */
public class QSBK_JokePagerFragment extends BaseViewPagerFragment {

	public static QSBK_JokePagerFragment newInstance() {
		return new QSBK_JokePagerFragment();
	}

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		AppOthers.showBothApp(getJokeApplication());
		AppOthers.showInnerApp(getJokeApplication());
		adapter.addTab("热门段子", "热门段子", NetJokeListFragment.class, getQSBKBundle(0));
		adapter.addTab("热门趣图", "热门趣图", NetJokeListFragment.class, getQSBKBundle(1));
		adapter.addTab("最新段子", "最新段子", NetJokeListFragment.class, getQSBKBundle(2));
		adapter.addTab("最新趣图", "最新趣图", NetJokeListFragment.class, getQSBKBundle(3));
	}

	private final String QSBK_URL_Prefix = "http://www.qiushibaike.com/";
	// 热门 8小时, 24小时, 穿越
	private static final String[] QSBK_TYPES_HOT_TOPIC = { "8hr", "hot", "history" };
	// 纯图 最热, 最新
	private static final String[] QSBK_PURE_IMG = { "imgrank", "pic" };
	// 纯文 最热, 最新
	private static final String[] QSBK_PURE_TEXT = { "text", "textnew" };
	public static final String[] QSBK_TYPES = { QSBK_PURE_TEXT[0], QSBK_PURE_IMG[0], QSBK_PURE_TEXT[1],
			QSBK_PURE_IMG[1] };

	private Bundle getQSBKBundle(int index) {
		Bundle bundle = new Bundle();
		bundle.putString(JOKE_URL, QSBK_URL_Prefix + QSBK_TYPES[index]);
		return bundle;
	}
}
