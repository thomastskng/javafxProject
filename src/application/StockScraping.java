package application;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public interface StockScraping{
	
	
	public default StockScrapedInfo getDataFromGoogleFinance(String ticker) throws InterruptedException, IOException{
		String url = "https://www.google.com.hk/finance?q=" + String.format("%04d", Integer.parseInt(ticker)) + "&ei=yF14VYC4F4Wd0ASb64CoCw";
		Document doc = Jsoup.connect(url).get();
		//System.out.println(doc);
		Element content = doc.select("meta[itemprop=price]").first();
		double cp =  Double.parseDouble(content.attr("content"));
		return new StockScrapedInfo("abc", cp, 100, "abc");

		}
	
		
	public default Map<String,String> getAAStockCookies(String url) throws Exception {
		//java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
	    try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {

	        final HtmlPage page = webClient.getPage(url);
	        /*
	        HtmlInput input = page.getHtmlElementById("PY_txt");
	        input.type("1");
	        final HtmlPage page2 = page.getHtmlElementById("imgHKStockSubmit").click();
	        System.out.println(page2.getUrl());
	        System.out.println(page2.asText());
	        System.out.println("***************************** Cookies:");
	        System.out.println(webClient.getCookieManager().getCookies());
	        */
	        Map<String,String> cookies = new HashMap<String,String>();
	        cookies.put("AALTP", "1");
	        return cookies;
	    }
	}
	
	public default StockScrapedInfo getDataFromAAStock(String ticker) throws Exception{
		String url = "http://www.aastocks.com/en/stock/detailquote.aspx?&symbol=" + ticker;
		System.out.println(url);
		Map<String,String> cookies = new HashMap<String,String>();
		cookies.put("AALTP", "1");
		Document doc = Jsoup.connect(url).cookies(cookies).get();
		//getAAStockCookies(url);
		//System.out.println(doc.html());
		Elements elements = doc.select("ul:contains(Last) + ul>li>span");
		double cp = Double.parseDouble(elements.get(0).ownText());
		Elements sn = doc.select("title");
		String[] title = sn.get(0).ownText().split("\\(");
		String stockName = title[0];
		Elements lotSize = doc.select("td:contains(Lot Size) + td");
		double ls = Double.parseDouble(lotSize.get(0).ownText());
		Elements lastUpdateTime = doc.select("font:contains(Last Update) + font");
		Elements suspension = doc.select("font:contains(Suspension)");
		String lastUpdate;
		if(suspension.text().contains("Suspension")){
			lastUpdate = "Suspension";
		} else{
			lastUpdate = lastUpdateTime.get(0).ownText();

		}
		System.out.println("Trade: " + stockName + ", cp: " + cp + ", lot size:" + ls + ", last Update: " + lastUpdate);
		return new StockScrapedInfo(stockName, cp, ls, lastUpdate);
		//return new StockScrapedInfo("abc", 100, 10000, "abc");
	}
	
	public default StockScrapedInfo getStockDataFromYahooFinanceAPI() throws InterruptedException, IOException{
		String url = "https://query.yahooapis.com/v1/public/yql?q="; 
		String yql_query = "select * from yahoo.finance.quotes where symbol=\"0001.hk\"";
		//+ String.format("%04d", Integer.parseInt(getStockTicker())) + "\"";
		String fullUrl = url + URLEncoder.encode(yql_query, "UTF-8") + "%0A&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
		String fullUrl1 = fullUrl.replace("+", "%20");

		System.out.println(fullUrl1);
		Document doc = Jsoup.connect(fullUrl1).get();
		System.out.println(doc);
		//Element content = doc.select("meta[itemprop=price]").first();
		//double cp =  Double.parseDouble(content.attr("content"));
		return new StockScrapedInfo("abc", 1000, 100, "abc");

		}
}
