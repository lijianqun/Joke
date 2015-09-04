package com.jamtu.basefragment;

import android.support.v4.app.Fragment;

import com.jamtu.App;

/**
 * 碎片基类
 * 
 * @created 2015-03-27
 * @author lijq
 */
public class BaseFragment extends Fragment {

	public App getJokeApplication() {
		return (App) getActivity().getApplication();
	}

}
