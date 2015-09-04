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

public class JokeListFragment extends BaseSwipeRefreshFragment<Joke, CommonList<Joke>> {
	public final static String JOKE_TYPE = "joke_type";
	private String type = "children";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication.initDbUtils();
		Bundle args = getArguments();
		type = args.getString(JOKE_TYPE);

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
		// CommonList<Joke> list = new CommonList<Joke>();
		// int size = jokeList.size();
		// int startIndex = (page - 1) * App.PAGE_SIZE;
		// int lastIndex = page * App.PAGE_SIZE;
		// if (size < lastIndex)
		// lastIndex = size;
		// List<Joke> jokes = new ArrayList<Joke>();
		// jokes = jokeList.subList(startIndex, lastIndex);
		// list.setList(jokes);
		// list.setCount(jokes.size());
		// list.setPageSize(jokes.size());
		return mApplication.getDbJokes(type, page);
	}

	@Override
	public void onItemClick(int position, Joke project) {
	}

}
