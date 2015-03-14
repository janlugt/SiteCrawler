package com.janlugt.SiteCrawler;

import java.util.HashMap;
import java.util.Map;

import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;

public class Page {
	
	public String title;
	public String url;
	public int depth;
	public int otherLinks;
	public Map<String, Page> links;
	private SiteCrawler sc;
	private boolean expanded;
		
	Page(SiteCrawler sc, String url, int depth) {
		this.sc = sc;
		this.url = url;
		this.depth = depth;
		links = new HashMap<String, Page>();
		sc.allPages.put(url, this);
		expanded = false;
	}
	
	public void expand() {
		if (!expanded && depth <= sc.MAXIMUM_DEPTH) {
			expanded = true;
			System.out.println("expanding " + url);
			Parser parser;
			try {
				parser = new Parser(url);
				this.setTitle(parser);
				this.setLinks(parser);
			} catch (Exception e) {
			}
		}
	}
	
	private void setLinks(Parser parser) throws Exception {
		parser.reset();
		NodeList linkList = parser.extractAllNodesThatMatch(new NodeClassFilter (LinkTag.class));

		for (int i = 0; i < linkList.size(); i++){
			LinkTag linkTag = (LinkTag)linkList.elementAt(i);
			if (linkTag.isHTTPLikeLink()) {
				String link = linkTag.getLink().split("#")[0];
				if (isValid(link)) {
					if (sc.allPages.containsKey(link)) {
						Page existingPage = sc.allPages.get(link);
						links.put(link, existingPage);
						existingPage.depth = Math.min(existingPage.depth, depth + 1);
						existingPage.expand();
					} else {
						Page newPage = new Page(sc, link, depth + 1);
						links.put(link, newPage);
						newPage.expand();
					}
				} else {
					otherLinks++;
				}
			}
		}
	}
	
	private boolean isValid(String link) {
		return link.startsWith(sc.START_URL) && !link.endsWith(".jpg") && !link.endsWith(".pdf") && !link.endsWith(".png") && !link.endsWith(".gif");
	}
	
	private void setTitle(Parser parser) throws Exception {
		parser.reset();
		NodeList titles = parser.extractAllNodesThatMatch(new NodeClassFilter (TitleTag.class));
		if (titles.size() > 0) {
			TitleTag titleTag = (TitleTag) titles.elementAt(0);
			title = titleTag.getTitle();
		}
	}
}
