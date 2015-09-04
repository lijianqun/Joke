package com.jamtu.fragments;

import static com.jamtu.fragments.LocaleJokeListFragment.JOKE_TYPE;
import android.os.Bundle;

import com.jamtu.adapter.ViewPageFragmentAdapter;
import com.jamtu.basefragment.BaseViewPagerFragment;
import com.jamtu.utils.AppOthers;

/**
 * 发现页面
 * 
 * @author lijq
 * @created 2015-03-29
 */
public class JokePagerFragment extends BaseViewPagerFragment {

	private final String[] TITLES = { "爱情", "幽默", "校园", "古代", "军事", "职场", "顺口溜", "成人", "儿童", "愚人", "短信", "尴尬" };
	private final String[] TYPES = { "love", "humor", "campus", "ancient", "army", "job", "jingle", "adult",
			"children", "fool", "sms", "embarrassed" };

	public static JokePagerFragment newInstance() {
		return new JokePagerFragment();
	}

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		AppOthers.showBothApp(getJokeApplication());
		AppOthers.showInnerApp(getJokeApplication());
		// String[] tabNames =
		// getResources().getStringArray(R.array.joke_title_array);
		int len = TITLES.length;
		for (byte i = 0; i < len; i++) {
			Bundle bundle = new Bundle();
			bundle.putString(JOKE_TYPE, TYPES[i]);
			adapter.addTab(TITLES[i], TITLES[i], JokeListFragment.class, bundle);
		}
	}
}
