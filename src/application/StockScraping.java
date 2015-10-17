package application;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
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
	
	/*
	public default StockScrapedInfo getDataFromGoogleFinance(String ticker) throws InterruptedException, IOException{
		String url = "https://www.google.com.hk/finance?q=" + String.format("%04d", Integer.parseInt(ticker)) + "&ei=yF14VYC4F4Wd0ASb64CoCw";
		Document doc = Jsoup.connect(url).get();
		//System.out.println(doc);
		Element content = doc.select("meta[itemprop=price]").first();
		double cp =  Double.parseDouble(content.attr("content"));
		return new StockScrapedInfo("abc", cp,"", 100, "abc","","","","","","","","","","","","");

	}
	*/
		
	public default Map<String,String> getAAStockCookies(String url) throws Exception {
		//java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
	    try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {

	        final HtmlPage page = webClient.getPage(url);
	        
	        HtmlInput input = page.getHtmlElementById("PY_txt");
	        input.type("1");
	        final HtmlPage page2 = page.getHtmlElementById("imgHKStockSubmit").click();
	        System.out.println(page2.getUrl());
	        System.out.println(page2.asText());
	        System.out.println("***************************** Cookies:");
	        System.out.println(webClient.getCookieManager().getCookies());
	        
	        Map<String,String> cookies = new HashMap<String,String>();
	        cookies.put("AALTP", "1");
	        return cookies;
	    }
	}
	
	public default StockScrapedInfo getDataFromAAStock(String ticker) throws Exception{
		String[] userAgentStr = new String[]{
								"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36",
								"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.1 Safari/537.36",
								"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36",
								"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36",
								"Opera/9.80 (X11; Linux i686; Ubuntu/14.10) Presto/2.12.388 Version/12.16",
								"Opera/12.80 (Windows NT 5.1; U; en) Presto/2.10.289 Version/12.02",
								"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) AppleWebKit/537.75.14 (KHTML, like Gecko) Version/7.0.3 Safari/7046A194A",
								"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/537.13+ (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2",
								"Mozilla/5.0 (Windows; U; Windows NT 6.1; sv-SE) AppleWebKit/533.19.4 (KHTML, like Gecko) Version/5.0.3 Safari/533.19.4",
								"Mozilla/5.0 (Windows; U; Windows NT 5.2; en-US) AppleWebKit/533.17.8 (KHTML, like Gecko) Version/5.0.1 Safari/533.17.8",
								"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1",
								"Mozilla/5.0 (X11; Linux i586; rv:31.0) Gecko/20100101 Firefox/31.0"
		};
		
		String url = "http://www.aastocks.com/en/stock/detailquote.aspx?&symbol=" + ticker;
		//System.out.println(url);
		Map<String,String> cookies = new HashMap<String,String>();
		cookies.put("AALTP", "1");
		Document doc = Jsoup.connect(url)
							.cookies(cookies)
							.userAgent(userAgentStr[(int) Math.random()*(userAgentStr.length-1)])
							.referrer("http://www.google.com.hk")
							.timeout(12000)
							.get();
		
		//getAAStockCookies(url);
		//System.out.println(doc.html());
		
		// AAStock
		// Scrape current price
		Elements scrape1 = doc.select("ul:contains(Last) + ul>li>span");
		// Scrape +ve / -ve indicator of current price
		String posNegForLast = scrape1.attr("class");
		double cp = Double.parseDouble(scrape1.get(0).ownText());

		// Scrape Chg
		Elements scrape2 = doc.select("ul:contains(Chg) + ul>li>span");
		String posNegForChg = scrape2.attr("class");
		String chg = scrape2.get(0).ownText();
		
		// Scrape Chg%
		Elements scrape3 = doc.select("ul:contains(Chg(%)) + ul>li>span");
		String posNegForChgPercent = scrape3.attr("class");
		String chgPercent = scrape3.get(0).ownText();
		
		// Scrape Stock Name
		Elements scrape4 = doc.select("title");
		String[] title = scrape4.get(0).ownText().split("\\(");
		String stockName = title[0];
		
		// Scrape lot size
		Elements scrape5 = doc.select("td:contains(Lot Size) + td");
		double ls = Double.parseDouble(scrape5.get(0).ownText());
		
		// Scrape bid(delayed)
		String bid_delayed = scrapeAAStockElement(doc,"ul:contains(Bid(Delayed)) + ul>li");

		// Scrape ask(delayed)
		String ask_delayed = scrapeAAStockElement(doc,"ul:contains(Ask(Delayed)) + ul>li");

		// Scrape High
		String high = scrapeAAStockElement(doc,"ul:contains(high) + ul>li");

		// Scrape low
		String low = scrapeAAStockElement(doc,"ul:matches(Low) + ul>li");

		// Scrape Open
		String open = scrapeAAStockElement(doc,"ul:contains(Open) + ul>li");

		// Scrape Prev Close
		String prev_close = scrapeAAStockElement(doc,"ul:contains(Prev Close) + ul>li");
		
		// Scrape Volume
		String volume = scrapeAAStockElement(doc,"ul:matches(Volume) + ul>li");
		
		// Scrape Turnover
		String turnover = scrapeAAStockElement(doc,"ul:matches(Turnover) + ul>li");
		
		// Scrape Spread
		String spread = scrapeAAStockElement(doc,"td:matches(Spread) + td");
		
		// Scrape Spread
		String peRatio = scrapeAAStockElement(doc,"td:matches(P/E Ratio) + td");
		
		// Scrape Yield
		String yield = scrapeAAStockElement(doc,"td:matches(Yield) + td");
		
		// Scrape Dividend Payout
		String dividend_payout = scrapeAAStockElement(doc,"td:matches(Dividend Payout) + td");

		// Scrape EPS
		String eps = scrapeAAStockElement(doc,"td:matches(EPS) + td");

		// Scrape Market Cap
		String market_cap = scrapeAAStockElement(doc,"td:matches(Market Cap) + td");
		
		// Scrape Market Cap
		String nav = scrapeAAStockElement(doc,"td:matches(NAV) + td");
		
		// Scrape dividend per share
		String dps = scrapeAAStockElement(doc,"td:matches(DPS) + td>span" );

		// Scrape 1 month range, 2 month range, 3 month range, 52 week range, Rate Ratio, Volume Ratio
		String oneMonthRange = scrapeAAStockElement(doc,"ul:matches(1 Month Range) + ul>li");
		String twoMonthRange = scrapeAAStockElement(doc,"ul:matches(2 Month Range) + ul>li");
		String threeMonthRange = scrapeAAStockElement(doc,"ul:matches(3 Month Range) + ul>li");
		String fiftyTwoWeekRange = scrapeAAStockElement(doc,"ul:matches(52 Week Range) + ul>li");
		String rateRatio = scrapeAAStockElement(doc,"ul:matches(Rate Ratio) + ul>li");
		String volumeRatio = scrapeAAStockElement(doc,"ul:matches(Volume Ratio) + ul>li");

		// Scrape SMA 10, 20, 50, 100, 250
		String sma10 = scrapeAAStockElement(doc,"div:contains(SMA 10) + div");
		String sma20 = scrapeAAStockElement(doc,"div:contains(SMA 20) + div");
		String sma50 = scrapeAAStockElement(doc,"div:contains(SMA 50) + div");
		String sma100 = scrapeAAStockElement(doc,"div:contains(SMA 100) + div");
		String sma250 = scrapeAAStockElement(doc,"div:contains(SMA 250) + div");

		// Scrape RSI 10,14,20
		String rsi10 = scrapeAAStockElement(doc,"div:contains(rsi 10) + div");
		String rsi14 = scrapeAAStockElement(doc,"div:contains(rsi 14) + div");
		String rsi20 = scrapeAAStockElement(doc,"div:contains(rsi 20) + div");

		// Scrape MACD(8/17), MACD(12/25)
		String macd8_17 = scrapeAAStockElement(doc,"div:contains(MACD(8/17 Day)) + div");
		String macd12_25 = scrapeAAStockElement(doc,"div:contains(MACD(12/25 Day)) + div");
		
		// Scrape Last Update time
		Elements lastUpdateTime = doc.select("font:contains(Last Update) + font");
		Elements suspension = doc.select("font:contains(Suspension)");
		String lastUpdate;
		if(suspension.text().contains("Suspension")){
			lastUpdate = "Suspension";
		} else{
			lastUpdate = lastUpdateTime.get(0).ownText();
		}
		
		//System.out.println("Trade: " + stockName + ", cp: " + cp + ", lot size:" + ls + ", last Update: " + lastUpdate);
		//stockName, currentPrice, posNegForLast, lotSize, lastUpdate, chg, posNegForChg, chgPercent, posNegForChgPercent, spread, peRatio, yield, dividendPayout, eps, marketCap, nav, dps, bid_delayed, ask_delayed, high, low, open, prev_close, volume, turnover, oneMonthRange, twoMonthRange, threeMonthRange, fiftyTwoWeekRange, rateRatio, volumeRatio, sma10, sma20, sma50, sma100, sma250, rsi10, rsi14, rsi20, macd8_17, macd12_25

		return new StockScrapedInfo(stockName, cp, posNegForLast,ls, lastUpdate, chg, posNegForChg, chgPercent, posNegForChgPercent, spread, peRatio, yield, dividend_payout,eps, market_cap, nav, dps, bid_delayed, ask_delayed, high, low,open, prev_close, volume, turnover, 		oneMonthRange, twoMonthRange, threeMonthRange, fiftyTwoWeekRange, rateRatio, volumeRatio, sma10, sma20, sma50, sma100, sma250, rsi10, rsi14, rsi20, macd8_17, macd12_25);
	}

	public default String scrapeAAStockElement(Document doc, String lookupQuery){
		Elements elements = doc.select(lookupQuery);
		//System.out.println(lookupQuery + ": " + elements.get(0).ownText());
		return  elements.get(0).ownText();
		
	}
	
	
	 public default StockScrapedInfo getDataFromEtnet(String ticker) throws Exception{
			String[] userAgentStr = new String[]{
									"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36",
									"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.1 Safari/537.36",
									"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36",
									"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36",
									"Opera/9.80 (X11; Linux i686; Ubuntu/14.10) Presto/2.12.388 Version/12.16",
									"Opera/12.80 (Windows NT 5.1; U; en) Presto/2.10.289 Version/12.02",
									"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) AppleWebKit/537.75.14 (KHTML, like Gecko) Version/7.0.3 Safari/7046A194A",
									"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/537.13+ (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2",
									"Mozilla/5.0 (Windows; U; Windows NT 6.1; sv-SE) AppleWebKit/533.19.4 (KHTML, like Gecko) Version/5.0.3 Safari/533.19.4",
									"Mozilla/5.0 (Windows; U; Windows NT 5.2; en-US) AppleWebKit/533.17.8 (KHTML, like Gecko) Version/5.0.1 Safari/533.17.8",
									"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1",
									"Mozilla/5.0 (X11; Linux i586; rv:31.0) Gecko/20100101 Firefox/31.0"
			};
			
			String url = "http://www.etnet.com.hk/www/eng/stocks/realtime/quote.php?code=" + ticker;
			//System.out.println(url);
			Map<String,String> cookies = new HashMap<String,String>();
			cookies.put("AALTP", "1");
			Document doc = Jsoup.connect(url)
								.cookies(cookies)
								.userAgent(userAgentStr[(int) Math.random()*(userAgentStr.length-1)])
								.referrer("http://www.google.com.hk")
								.timeout(12000)
								.get();
			
			// Scrape ETNET
			
			String url2 = "http://www.etnet.com.hk/www/eng/stocks/realtime/quote.php?code=" + ticker;
			Document doc2 = Jsoup.connect(url2)
								.userAgent(userAgentStr[(int) Math.random()*(userAgentStr.length-1)])
								.referrer("http://www.google.com.hk")
								.timeout(12000)
								.get();

			// Last
			Element scrape = doc2.select("div#StkDetailMainBox td").get(0).select("span").get(0);
			String posNegForLast = getUpOrDown(scrape.attr("class"));
			System.out.println("posNegForLast:" + posNegForLast);
			NumberFormat nf = NumberFormat.getInstance(Locale.US);
			double cp = nf.parse(scrape.ownText()).doubleValue();
			System.out.println("Last: " + cp);
			
			// Up / Down for Chg + Chg + Up / Down for Chg % + chg%
			String chgChgPercent = scrapeEtnetStkDetailMainBoxElement(doc2,"div#StkDetailMainBox td",0,1);
			// Up / Down for Chg 
			String posNegForChg = getUpOrDown(Character.toString(chgChgPercent.split(" ")[0].charAt(0)));
			// Chg
			String chg = chgChgPercent.split(" ")[0];
			// Up / Down for Chg %
			String posNegForChgPercent = getUpOrDown(Character.toString(chgChgPercent.split(" ")[1].charAt(1)));
			// Chg % 
			String chgPercent = chgChgPercent.split(" ")[1];

			System.out.println("split: " + posNegForChg + "/"+ chg + "/" + posNegForChgPercent + "/" +chgPercent);
			// high
			String high = scrapeEtnetStkDetailMainBoxElement(doc2,"div#StkDetailMainBox td",1,1);
			// volume
			String volume = scrapeEtnetStkDetailMainBoxElement(doc2,"div#StkDetailMainBox td",2,1);
			// prev close
			String prev_close = scrapeEtnetStkDetailMainBoxElement(doc2,"div#StkDetailMainBox td",3,0);
			// 1 month high
			String one_month_high = scrapeEtnetStkDetailMainBoxElement(doc2,"div#StkDetailMainBox td",4,0);
			// market cap
			String marketCap = scrapeEtnetStkDetailMainBoxElement(doc2,"div#StkDetailMainBox td",5,0);
			// low
			String low = scrapeEtnetStkDetailMainBoxElement(doc2,"div#StkDetailMainBox td",6,1);
			// turnover
			String turnover = scrapeEtnetStkDetailMainBoxElement(doc2,"div#StkDetailMainBox td",7,1);
			// open
			String open = scrapeEtnetStkDetailMainBoxElement(doc2,"div#StkDetailMainBox td",8,0);
			// 1 month low
			String one_month_low = scrapeEtnetStkDetailMainBoxElement(doc2,"div#StkDetailMainBox td",9,0);
			// short sell
			String short_sell = scrapeEtnetStkDetailMainBoxElement(doc2,"div#StkDetailMainBox td",10,1);

			// bid
			String bid = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,1);
			// sma10
			String sma10 = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,3);
			// ask
			String ask = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,5);
			// sma20
			String sma20 = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,7);
			// number of trades
			String no_of_trade = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,9);
			// sma50
			String sma50 = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,11);
			// amount per trade
			String amount_per_trade = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,13);
			// sma250
			String sma250 = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,15);
			// vwap
			String vwap = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,17);
			// 52 week high
			String fifty_two_week_high = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,19);
			// 52 week low
			String fifty_two_week_low = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,23);
			// board lot
			double board_lot = Double.parseDouble(scrapeEtnetStklistElement(doc2,"div#StkList ul",0,25));
			// rsi14
			String rsi14 = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,27);
			// admission fee
			String admission_fee = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,29);
			// 10-D R. of Return
			String rate_of_return_10_day = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,31);
			// Spread
			String spread = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,33);
			// Rate of risk
			String rate_of_risk = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,35);
			// PE Ratio
			String peRatio = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,37);
			// Risk of return rate 
			String risk_return_rate = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,39);
			// yield
			String yield = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,41);
			// beta
			String beta = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,43);
			// eps
			String eps = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,45);
			// dps
			String dps = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,49);
			// nbv per share
			String nbv_per_share = scrapeEtnetStklistElement(doc2,"div#StkList ul",0,53);
			// dividend
			String dividend = scrapeEtnetStkDividendStkDetailTimeElement(doc2, "div#StkDividend div.Text",0);
			// ex date
			String ex_date = scrapeEtnetStkDividendStkDetailTimeElement(doc2, "div#StkDividend div.Text",1);
			// payable date
			String payable_date = scrapeEtnetStkDividendStkDetailTimeElement(doc2, "div#StkDividend div.Text",2);
			// Last Updated Time
			String last_updated_time = scrapeEtnetStkDividendStkDetailTimeElement(doc2, "div#StkDetailTime",0);
			String stockName = scrapeEtnetStkNameElement(doc2,"div#StkQuoteHeader");
			//stockName, currentPrice, posNegForLast, lotSize, lastUpdate, chg, posNegForChg, chgPercent, posNegForChgPercent, spread, peRatio, yield, dividendPayout, eps, marketCap, nav, dps, bid_delayed, ask_delayed, high, low, open, prev_close, volume, turnover, oneMonthRange, twoMonthRange, threeMonthRange, fiftyTwoWeekRange, rateRatio, volumeRatio, sma10, sma20, sma50, sma100, sma250, rsi10, rsi14, rsi20, macd8_17, macd12_25
			return new StockScrapedInfo(stockName ,cp, posNegForLast, board_lot, last_updated_time, chg, posNegForChg, chgPercent, posNegForChgPercent, spread, peRatio, yield, "", eps, marketCap,nbv_per_share, dps, bid,ask, high,low, prev_close, volume, turnover,  one_month_low + " - "+ one_month_high, "","",fifty_two_week_low + " - " + fifty_two_week_high, "", "",sma10, sma20, sma50, "", sma250, "", rsi14,"", "","", ""  );
	 
	 }
	 
	// Scrape Etnet StkDetailMainBox 
	public default String scrapeEtnetStkDetailMainBoxElement(Document doc, String lookupQuery, int i,  int j){
		Elements elements = doc.select(lookupQuery);
		//System.out.println(lookupQuery + ": " + elements.get(10));
		//System.out.println(doc);
		String str = elements.get(i).select("span").get(j).ownText();
		System.out.println(lookupQuery + ": " + str);
		return str;
	}
	
	public default String scrapeEtnetStklistElement(Document doc, String lookupQuery, int i,  int j){
		Elements elements = doc.select(lookupQuery);
		//System.out.println(lookupQuery + ": " + elements);
		//System.out.println(doc);
		String str = elements.get(i).select("li").get(j).ownText();
		System.out.println(lookupQuery + ": " + str);
		return str;
	}
	
	public default String scrapeEtnetStkDividendStkDetailTimeElement(Document doc, String lookupQuery, int i){
		Elements elements = doc.select(lookupQuery);
		//System.out.println(lookupQuery + ": " + elements);
		//System.out.println(doc);
		String str = ((elements.get(0).html().split("<br>"))[i]).trim();
		System.out.println(lookupQuery + ": " + str);
		return str;
	}
	
	public default String scrapeEtnetStkNameElement(Document doc, String lookupQuery){
		Elements elements = doc.select(lookupQuery);
		//System.out.println(lookupQuery + ": " + elements);
		//System.out.println(doc);
		String str = elements.get(0).ownText();
		System.out.println(lookupQuery + ": " + str);
		return str;
	}
	
	public default String getUpOrDown(String str){
		if(str.contains("down") || str.contains("-")){
			return "neg bold";
		} else if(str.contains("up") || str.contains("+")){
			return "pos bold";
		} else{
			return "unc bold";
		}
	}
	
	/*
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
		return new StockScrapedInfo("abc", 1000,"" ,100,"abc","","","","","","","","","","","","");

		}
		*/
	
}
