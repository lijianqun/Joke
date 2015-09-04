package com.jamtu.fragments;

import static com.jamtu.fragments.LocaleJokeListFragment.JOKE_TYPE;
import android.os.Bundle;

import com.jamtu.adapter.ViewPageFragmentAdapter;
import com.jamtu.basefragment.BaseViewPagerFragment;
import com.jamtu.joke.R;
import com.jamtu.utils.AppOthers;

/**
 * 发现页面
 * 
 * @author lijq
 * @created 2015-03-29
 */
public class LocaleJokePagerFragment extends BaseViewPagerFragment {

	public static LocaleJokePagerFragment newInstance() {
		return new LocaleJokePagerFragment();
	}

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		AppOthers.showBothApp(getJokeApplication());
		AppOthers.showInnerApp(getJokeApplication());
		String[] tabNames = getResources().getStringArray(R.array.joke_title_array);
		for (byte i = 0; i < tabNames.length; i++) {
			Bundle bundle = new Bundle();
			bundle.putByte(JOKE_TYPE, i);
			adapter.addTab(tabNames[i], tabNames[i], LocaleJokeListFragment.class, bundle);
		}
	}
}
