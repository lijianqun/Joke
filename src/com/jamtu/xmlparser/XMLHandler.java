package com.jamtu.xmlparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.jamtu.bean.Joke;

public class XMLHandler extends DefaultHandler {
	private Joke joke = null;
	private String tag = null;

	private List<Map<String, Object>> joke_all_list = null;
	private Map<String, Object> map_category_joke = null;

	private List<Map<String, Object>> joke_list = null;
	private Map<String, Object> map_id_joke = null;

	public List<Map<String, Object>> getListmap() {
		return joke_all_list;
	}

	@Override
	public void startDocument() throws SAXException {
		joke_all_list = new ArrayList<Map<String, Object>>();
	}

	@Override
	public void endDocument() throws SAXException {
	}

	// 类型 及 ID
	String JOKE_TYPE, JOKE_ID;
	StringBuffer sb = new StringBuffer();

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		sb.delete(0, sb.length());
		tag = localName.length() != 0 ? localName : qName;
		tag = tag.toLowerCase(Locale.getDefault());
		if ("jokes".equals(tag)) {
			JOKE_TYPE = attributes.getValue(0);
			map_category_joke = new HashMap<String, Object>();
			map_category_joke.put("type", JOKE_TYPE);
			joke_list = new ArrayList<Map<String, Object>>();
			// System.out.println("type = " + attributes.getValue(0));
		}
		if ("joke".equals(tag)) {
			JOKE_ID = attributes.getValue(0);
			map_id_joke = new HashMap<String, Object>();
			map_id_joke.put("id", JOKE_ID);
			joke = new Joke();
			// System.out.println("id = " + attributes.getValue(0));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// System.out.println("localName = = = = " + localName);
		if ("joke".equals(localName)) {
			joke.set_Id(JOKE_TYPE + "_" + JOKE_ID);
			map_id_joke.put("joke", joke);
			joke_list.add(map_id_joke);
			joke = null;
		} else if ("jokes".equals(localName)) {
			map_category_joke.put("joke_list", joke_list);
			joke_all_list.add(map_category_joke);
		}
		tag = null;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (tag != null) {
			sb.append(ch, 0, length);
			String data = new String(sb.toString());
			if ("title".equals(tag)) {
				joke.setTitle(data);
				// System.out.println("title = " + data);
			} else if ("content".equals(tag)) {
				joke.setContent(data);
				// System.out.println("content = " + data);
			}
		}
	}
}