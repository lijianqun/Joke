package com.jamtu.xmlparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import android.content.Context;

import com.jamtu.bean.Joke;

public class RandomServices {
	ReadXML readXML = null;
	List<Map<String, Object>> joke_all_list = null;
	Map<String, Object> map = null;

	List<Map<String, Object>> jokes_list = null;
	List<Map<String, Object>> temp_list_map = null;
	List<Joke> joke_list = null;
	Joke joke = null;

	public RandomServices(Context context) {
		readXML = new ReadXML(context);
		joke_list = new ArrayList<Joke>();
	}

	private int getRandomTypeId() {
		return new Random().nextInt(6);
	}

	private int getRandomJokeId(int random_id) {
		return new Random().nextInt((getList(random_id).size()));
	}

	// 此Map封装了笑话Type和笑话List
	private Map<String, Object> getMap(int temp) {
		joke_all_list = readXML.getData();
		return joke_all_list.get(temp);
	}

	// 根据指定的笑话Type获取其对应的笑话List
	private List<Map<String, Object>> getList(int random_id) {
		map = getMap(random_id);
		if ("humor".equals(map.get("type"))) {
			return (List<Map<String, Object>>) map.get("joke_list");
		}
		if ("couple".equals(map.get("type"))) {
			return (List<Map<String, Object>>) map.get("joke_list");
		}
		if ("folk".equals(map.get("type"))) {
			return (List<Map<String, Object>>) map.get("joke_list");
		}
		if ("fool".equals(map.get("type"))) {
			return (List<Map<String, Object>>) map.get("joke_list");
		}
		if ("nausea".equals(map.get("type"))) {
			return (List<Map<String, Object>>) map.get("joke_list");
		}
		if ("interesting".equals(map.get("type"))) {
			return (List<Map<String, Object>>) map.get("joke_list");
		}
		return null;
	}

	// 获取封裝笑话对象的List 用于显示ListView
	public List<Joke> getJokes(int random_id) {
		jokes_list = getList(random_id);
		for (Iterator<Map<String, Object>> iter = jokes_list.iterator(); iter.hasNext();) {
			for (Map.Entry<String, Object> entry : iter.next().entrySet()) {
				if ("joke".equals(entry.getKey())) {
					joke_list.add((Joke) entry.getValue());
				}
			}
		}
		return joke_list;
	}

	public List<Joke> getJokes(String type) {
		List<Joke> jokes = new ArrayList<Joke>();
		List<Map<String, Object>> list_all = readXML.getData();
		for (Iterator<Map<String, Object>> iter = list_all.iterator(); iter.hasNext();) {
			Map<String, Object> map = iter.next();
			if (map.get("type").equals(type)) {
				List<Map<String, Object>> jokes_list = (List<Map<String, Object>>) map.get("joke_list");
				for (Iterator<Map<String, Object>> it = jokes_list.iterator(); it.hasNext();) {
					for (Map.Entry<String, Object> entry : it.next().entrySet()) {
						if ("joke".equals(entry.getKey())) {
							Joke joke = (Joke) entry.getValue();
							jokes.add(joke);
						}
					}
				}
			}
		}
		return jokes;
	}

	public List<Joke> getSearch(String type, String title) {
		List<Joke> temp_list = new ArrayList<Joke>();
		for (int i = 0; i < getJokes(type).size(); i++) {
			Joke joke = getJokes(type).get(i);
			if (joke.getTitle().indexOf(title) != -1) {
				temp_list.add(joke);
			}
		}
		return temp_list;
	}

	public List<Joke> getSearch(String searchStr) {
		List<Joke> searchs = new ArrayList<Joke>();
		for (int i = 0; i < 6; i++) {
			List<Joke> jokes = getJokes(i);
			for (Joke joke : jokes) {
				if (joke.getContent().contains(searchStr))
					searchs.add(joke);
				if (searchs.size() > 15)
					break;
			}
			if (searchs.size() > 15)
				break;
		}
		return searchs;
	}

	// 随机产生Joke对象
	public Joke getRandomJoke(int random_id) {
		jokes_list = getList(random_id);
		Map<String, Object> m = jokes_list.get(getRandomJokeId(random_id));
		return (Joke) m.get("joke");
	}

	// 随机获取单个类型joke list
	public List<Joke> getSingleTypeJokeList(int random_id) {
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < 6; i++) {
			Joke joke = getRandomJoke(random_id);
			String title = joke.getTitle();
			if (set.add(title)) {
				joke_list.add(joke);
			}
		}
		return joke_list;
	}

	// 随机产生各种类型Joke list 并且无重复项
	public List<Joke> getRandomTypeJokeList(int length) {
		int[] temp = new int[length];
		temp_list_map = new ArrayList<Map<String, Object>>();
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < length; i++) {
			temp[i] = getRandomTypeId();
			List<Joke> temp_list = new ArrayList<Joke>();
			Map<String, Object> map = new HashMap<String, Object>();
			// System.out.println("外层 ：temp[" + i + "] = " + temp[i]);
			for (int j = 0; j < temp.length; j++) {
				Joke joke = getRandomJoke(temp[i]);
				// System.out.println("内层：temp[" + i + "] = " + temp[i]);
				String title = joke.getTitle();
				if (set.add(title)) {
					joke_list.add(joke);
					temp_list.add(joke);
					// System.out.println("内层：j = " + j + " title : " + title);
				}
			}
			map.put("joke_type", temp[i]);
			map.put("temp_list", temp_list);
			temp_list_map.add(map);
		}
		return joke_list;
	}

	public List<Map<String, Object>> getTemp_List_Map() {
		return temp_list_map;
	}

	public String getValue() {
		joke = getRandomJoke(getRandomTypeId());
		return joke.getTitle();
	}

	public Joke getJoke() {
		joke = getRandomJoke(getRandomTypeId());
		return joke;
	}
}
