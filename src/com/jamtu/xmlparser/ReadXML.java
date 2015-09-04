package com.jamtu.xmlparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;

import com.jamtu.bean.Joke;
import com.jamtu.joke.R;

public class ReadXML {
	private Context context;
	private Joke joke = null;
	private Map<String, Object> map = null;
	private List<Map<String, Object>> joke_all_list = null;
	private List<Joke> jokes = null;

	public ReadXML(Context context) {
		this.context = context;
		joke_all_list = getData();
		jokes = new ArrayList<Joke>();
	}

	public List<Joke> getJokes() {
		return jokes;
	}

	public String getStr() throws IOException {
		InputStream is = context.getResources().openRawResource(R.raw.jokelist);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String temp = null;
		while ((temp = br.readLine()) != null) {
			sb.append(temp);
		}
		return sb.toString();
	}

	public List<Map<String, Object>> getData() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			XMLHandler handler = new XMLHandler();
			XMLReader reader = parser.getXMLReader();
			reader.setContentHandler(handler);
			reader.parse(new InputSource(new StringReader(getStr())));
			return handler.getListmap();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getType() {
		for (Iterator<Map<String, Object>> it = joke_all_list.iterator(); it
				.hasNext();) {
			map = it.next();
			return (String) map.get("type");
		}
		return null;
	}

	public Joke getJoke() {
		for (Iterator<Map<String, Object>> it = joke_all_list.iterator(); it
				.hasNext();) {
			map = it.next();
			List<Map<String, Object>> jokes_list = (List<Map<String, Object>>) map
					.get("joke_list");
			for (Iterator<Map<String, Object>> iter = jokes_list.iterator(); iter
					.hasNext();) {
				for (Map.Entry<String, Object> entry : iter.next().entrySet()) {
					if ("joke".equals(entry.getKey())) {
						joke = (Joke) entry.getValue();
						jokes.add(joke);
						// System.out.println("content : " + joke.getContent());
					}
				}
			}
		}
		return joke;
	}
}
