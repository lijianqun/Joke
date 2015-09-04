package com.jamtu.htmlparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jamtu.App;
import com.jamtu.AppException;
import com.jamtu.bean.Author;
import com.jamtu.bean.CommonList;
import com.jamtu.bean.Joke;
import com.jamtu.fragments.QSBK_JokePagerFragment;

public class HtmlParser {

	private static int currPage = 1;

	private static List<Joke> parser(String url) throws AppException {
		List<Joke> jokes = new ArrayList<Joke>();
		try {
			Document doc = Jsoup
					.connect(url)
					.userAgent(
							"Mozilla/5.0 (Windows NT 5.1; zh-CN) AppleWebKit/535.12 (KHTML, like Gecko) Chrome/22.0.1229.79 Safari/535.12")
					.timeout(30 * 1000).get();
			Elements all = doc.select("div[class$=article block untagged mb15]");
			int size = all.size();
			int currIndex = (currPage - 1) * App.PAGE_SIZE;
			for (int i = 0; i < size; i++) {
				Element element = all.get(i);
				Joke joke = new Joke();
				joke.set_Id("Page_" + (currIndex + i));
				Elements childrens = element.children();
				for (Element children : childrens) {
					if (children.hasClass("author")) {
						Element link = children.select("a").first();
						Element img = link.select("img").first();
						Author author = new Author();
						author.setId(link.attr("href"));
						author.setIcon(img.attr("src"));
						author.setName(img.attr("alt"));
						joke.setAuthor(author);
					} else if (children.hasClass("content")) {
						joke.setContent(children.text());
					} else if (children.hasClass("thumb")) {
						joke.setImg(children.select("img").first().attr("src"));
					} else if (children.hasClass("stats")) {
						joke.setStatsVote(children.select("span.stats-vote").text());
						// joke.setStatsComments(children.select("span.stats-comments").text());
					}
				}
				jokes.add(joke);
			}
		} catch (Exception e) {
			throw AppException.http(e);
		}
		return jokes;
	}

	private static List<Joke> htmlParser(String url, boolean isQsbk) throws AppException {
		try {
			Document doc = Jsoup
					.connect(url)
					.userAgent(
							"Mozilla/5.0 (Windows NT 5.1; zh-CN) AppleWebKit/535.12 (KHTML, like Gecko) Chrome/22.0.1229.79 Safari/535.12")
					.timeout(30 * 1000).get();
			return isQsbk ? parserQsbk(doc) : parserBdj(doc);
		} catch (Exception e) {
			throw AppException.http(e);
		}
	}

	private static List<Joke> parserQsbk(Document doc) {
		List<Joke> jokes = new ArrayList<Joke>();
		Elements all = doc.select("div[class$=article block untagged mb15]");
		int size = all.size();
		// int currIndex = (currPage - 1) * App.PAGE_SIZE;
		for (int i = 0; i < size; i++) {
			Element element = all.get(i);
			Joke joke = new Joke();
			// joke.set_Id("Page_" + (currIndex + i));
			joke.set_Id(element.attr("id"));
			Elements childrens = element.children();
			for (Element children : childrens) {
				if (children.hasClass("author")) {
					Element link = children.select("a").first();
					Element img = link.select("img").first();
					Author author = new Author();
					author.setId(link.attr("href"));
					author.setIcon(img.attr("src"));
					author.setName(img.attr("alt"));
					joke.setAuthor(author);
				} else if (children.hasClass("content")) {
					joke.setContent(children.text());
				} else if (children.hasClass("thumb")) {
					joke.setImg(children.select("img").first().attr("src"));
				} else if (children.hasClass("stats")) {
					joke.setStatsVote(children.select("span.stats-vote").text());
					// joke.setStatsComments(children.select("span.stats-comments").text());
				}
			}
			jokes.add(joke);
		}
		return jokes;
	}

	private static List<Joke> parserBdj(Document doc) {
		List<Joke> jokes = new ArrayList<Joke>();
		Elements all = doc.select("div[class$=web_left floatl test] > div.white_border");
		int size = all.size();
		// int currIndex = (currPage - 1) * App.PAGE_SIZE;
		for (int i = 0; i < size; i++) {
			Element element = all.get(i);
			Joke joke = new Joke();
			// joke.set_Id("Page_" + (currIndex + i));
			Elements childrens = element.children();
			for (Element children : childrens) {
				if (children.hasClass("user_info")) {
					Elements ul = children.select("ul > li > *");
					Author author = new Author();
					String icon = ul.first().attr("src");
					author.setIcon(icon);
					author.setName(ul.get(1).text());
					author.setId(ul.last().text());
					joke.setAuthor(author);
				} else if (children.attr("class").equals("web_conter clear")) {
					Elements divs = children.select("div > div");

					Element contentDiv = divs.first();
					Element webSizeElement = contentDiv.select("p.web_size").first();
					joke.set_Id(webSizeElement.attr("id"));
					joke.setContent(webSizeElement.text());
					Elements tmp = contentDiv.select("p > a > img");
					if (tmp != null && !tmp.isEmpty())
						joke.setImg(tmp.first().attr("src"));

					joke.setStatsVote(divs.last().select("a[class$=dolove no_love]").text() + " 好笑");
					// joke.setContent(divs.first().text());
					// joke.setContent(children.select("div.post-body > p").first().text());
				}
			}
			jokes.add(joke);
		}
		return jokes;
	}

	private static List<Joke> getNetJokes(String jokeUrl, String type, int page) throws AppException {
		currPage = page;
		boolean isQsbk = Arrays.asList(QSBK_JokePagerFragment.QSBK_TYPES).contains(type);
		if (isQsbk)
			return htmlParser(jokeUrl + "page/" + page, true);
		else
			return htmlParser(jokeUrl + page, false);
	}

	public static CommonList<Joke> getJokess(String jokeUrl, String type, int page) throws AppException {
		List<Joke> jokes = getNetJokes(jokeUrl, type, page);
		if (null == jokes)
			jokes = new ArrayList<Joke>();
		CommonList<Joke> list = new CommonList<Joke>();
		list.setList(jokes);
		list.setCount(jokes.size());
		list.setPageSize(jokes.size());
		return list;
	}

}
