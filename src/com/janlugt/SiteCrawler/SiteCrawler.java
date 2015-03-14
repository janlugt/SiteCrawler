package com.janlugt.SiteCrawler;

import java.util.HashMap;
import java.util.Map;

public class SiteCrawler {

	final String START_URL = "http://www.jma.org/";
	final int MAXIMUM_DEPTH = 2;

	Map<String, Page> allPages;
	
	SiteCrawler() {
		allPages = new HashMap<String, Page>();
	}
	
	public void start() throws Exception {
		Page root = new Page(this, START_URL, 0);
		root.expand();
	}

	public static void main(String[] args) {
		try {
			new SiteCrawler().start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
