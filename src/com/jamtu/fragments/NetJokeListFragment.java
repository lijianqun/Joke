package com.jamtu.fragments;

import java.util.List;

import android.os.Bundle;
import android.widget.BaseAdapter;

import com.jamtu.AppException;
import com.jamtu.adapter.JokeListAdapter;
import com.jamtu.basefragment.BaseSwipeRefreshFragment;
import com.jamtu.bean.CommonList;
import com.jamtu.bean.Joke;
import com.jamtu.bean.MessageData;
import com.jamtu.joke.R;

public class NetJokeListFragment extends BaseSwipeRefreshFragment<Joke, CommonList<Joke>> {
	public final static String JOKE_URL = "joke_url";
	private String jokeUrl = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		jokeUrl = args.getString(JOKE_URL);
	}

	@Override
	public BaseAdapter getAdapter(List<Joke> list) {
		return new JokeListAdapter(getActivity(), list, R.layout.exploreproject_listitem);
	}

	@Override
	protected MessageData<CommonList<Joke>> asyncLoadList(int page, boolean refresh) {
		MessageData<CommonList<Joke>> msg = null;
		try {
			CommonList<Joke> list = getList(page, refresh);
			msg = new MessageData<CommonList<Joke>>(list);
		} catch (AppException e) {
			e.makeToast(mApplication);
			e.printStackTrace();
			msg = new MessageData<CommonList<Joke>>(e);
		}
		return msg;
	}

	private CommonList<Joke> getList(int page, boolean refresh) throws AppException {
		// List<Joke> jokes = HtmlParser.getJokes(type, page);
		// if (null == jokes)
		// jokes = new ArrayList<Joke>();
		// CommonList<Joke> list = new CommonList<Joke>();
		// list.setList(jokes);
		// list.setCount(jokes.size());
		// list.setPageSize(jokes.size());
		// return list;
		return mApplication.getNetJokes(jokeUrl, page, refresh);
	}

	@Override
	public void onItemClick(int position, Joke project) {
	}

}
