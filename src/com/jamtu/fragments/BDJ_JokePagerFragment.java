package com.jamtu.fragments;

import static com.jamtu.fragments.NetJokeListFragment.JOKE_URL;
import android.os.Bundle;

import com.jamtu.adapter.ViewPageFragmentAdapter;
import com.jamtu.basefragment.BaseViewPagerFragment;
import com.jamtu.utils.AppOthers;

/**
 * 网络段子2
 * 
 * @author lijq
 * @created 2015-03-29
 */
public class BDJ_JokePagerFragment extends BaseViewPagerFragment {

	public static BDJ_JokePagerFragment newInstance() {
		return new BDJ_JokePagerFragment();
	}

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		AppOthers.showBothApp(getJokeApplication());
		AppOthers.showInnerApp(getJokeApplication());
		adapter.addTab("段子精选", "段子精选", NetJokeListFragment.class, getBDJBundle(0));
		adapter.addTab("图片精选", "图片精选", NetJokeListFragment.class, getBDJBundle(1));
		adapter.addTab("最新段子", "最新段子", NetJokeListFragment.class, getBDJBundle(2));
		adapter.addTab("最新趣图", "最新趣图", NetJokeListFragment.class, getBDJBundle(3));
		adapter.addTab("热门段子", "热门段子", NetJokeListFragment.class, getBDJBundle(4));
		adapter.addTab("热门趣图", "热门趣图", NetJokeListFragment.class, getBDJBundle(5));
	}

	// 纯图 精选，最新，近期热门
	private static final String[] BDJ_PURE_IMG = { "", "new-p", "hotpic" };
	// 纯文 精选，最新，近期热门
	private static final String[] BDJ_PURE_TEXT = { "duanzi", "new-d", "hotdoc" };
	private final String BDJ_URL_Prefix = "http://www.budejie.com/";
	private String[] BDJ_TYPES = { BDJ_PURE_TEXT[0], BDJ_PURE_IMG[0], BDJ_PURE_TEXT[1], BDJ_PURE_IMG[1],
			BDJ_PURE_TEXT[2], BDJ_PURE_IMG[2] };

	private Bundle getBDJBundle(int index) {
		Bundle bundle = new Bundle();
		bundle.putString(JOKE_URL, BDJ_URL_Prefix + BDJ_TYPES[index]);
		return bundle;
	}
}
