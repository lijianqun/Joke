package com.jamtu.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.BaseAdapter;

import com.jamtu.App;
import com.jamtu.AppException;
import com.jamtu.adapter.JokeListAdapter;
import com.jamtu.basefragment.BaseSwipeRefreshFragment;
import com.jamtu.bean.CommonList;
import com.jamtu.bean.Joke;
import com.jamtu.bean.MessageData;
import com.jamtu.joke.R;
import com.jamtu.xmlparser.RandomServices;

public class LocaleJokeListFragment extends BaseSwipeRefreshFragment<Joke, CommonList<Joke>> {
	public final static String JOKE_TYPE = "joke_type";
	private byte type = 0;

	private RandomServices randomServices;
	private List<Joke> jokeList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		type = args.getByte(JOKE_TYPE);
	}

	@Override
	public BaseAdapter getAdapter(List<Joke> list) {
		return new JokeListAdapter(getActivity(), list, R.layout.exploreproject_listitem);
	}

	@Override
	protected MessageData<CommonList<Joke>> asyncLoadList(int page, boolean refresh) {
		if (null == randomServices)
			randomServices = new RandomServices(getActivity());
		if (null == jokeList)
			jokeList = randomServices.getJokes(type);
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
		CommonList<Joke> list = new CommonList<Joke>();
		int size = jokeList.size();
		int startIndex = (page - 1) * App.PAGE_SIZE;
		int lastIndex = page * App.PAGE_SIZE;
		if (size < lastIndex)
			lastIndex = size;
		List<Joke> jokes = new ArrayList<Joke>();
		jokes = jokeList.subList(startIndex, lastIndex);
		list.setList(jokes);
		list.setCount(jokes.size());
		list.setPageSize(jokes.size());
		return list;
	}

	@Override
	public void onItemClick(int position, Joke project) {
	}

}
