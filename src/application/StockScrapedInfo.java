package application;

public class StockScrapedInfo {
	private String stockName;
	private double currentPrice;
	private String lastUpdate;
	private double lotSize;
	private String posNegForLast;
	private String chg;
	private String posNegForChg;
	private String chgPercent;
	private String posNegForChgPercent;
	private String spread;
	private String peRatio;
	private String yield;
	private String dividendPayout;
	private String eps;
	private String marketCap;
	private String nav;
	private String dps;
	public String bid_delayed;
	public String ask_delayed;
	public String high;
	public String low;
	public String open;
	public String prev_close;
	public String volume;
	public String turnover;
	public String oneMonthRange;
	public String twoMonthRange;
	public String threeMonthRange;
	public String fiftyTwoWeekRange;
	public String rateRatio;
	public String volumeRatio;
	public String sma10;
	public String sma50;
	public String sma100;
	public String sma250;
	public String rsi10;
	public String rsi14;
	public String rsi20;
	public String macd8_17;
	public String macd12_25;
	public String shortSellTurnover;
	public String shortSellRatio;
	public String industry;
	
	public StockScrapedInfo(String stockName, double currentPrice, String posNegForLast, double lotSize,String lastUpdate, String chg, String posNegForChg, String chgPercent, String posNegForChgPercent, String spread, String peRatio, String yield, String dividendPayout, String eps, String marketCap, String nav, String dps,  String bid_delayed, String ask_delayed, String high, String low, String open, String prev_close, String volume, String turnover, String oneMonthRange, String twoMonthRange, String threeMonthRange, String fiftyTwoWeekRange, String rateRatio, String volumeRatio, String sma10, String sma50, String sma100, String sma250, String rsi10, String rsi14, String rsi20, String macd8_17, String macd12_25, String shortSellTurnover, String shortSellRatio, String industry){
		this.stockName = stockName;
		this.currentPrice = currentPrice;
		this.lotSize = lotSize;
		this.lastUpdate = lastUpdate;
		this.posNegForLast = posNegForLast;
		this.chg = chg;
		this.chgPercent = chgPercent;
		this.posNegForChg = posNegForChg;
		this.posNegForChgPercent = posNegForChgPercent;
		this.spread = spread;
		this.peRatio= peRatio;
		this.yield = yield;
		this.dividendPayout = dividendPayout;
		this.eps = eps;
		this.marketCap = marketCap;
		this.nav = nav;
		this.dps = dps;
		this.bid_delayed = bid_delayed;
		this.ask_delayed = ask_delayed;
		this.high = high;
		this.low = low;
		this.open = open;
		this.prev_close = prev_close;
		this.volume = volume;
		this.turnover = turnover;
		this.oneMonthRange = oneMonthRange;
		this.twoMonthRange = twoMonthRange;
		this.threeMonthRange = threeMonthRange;
		this.fiftyTwoWeekRange = fiftyTwoWeekRange;
		this.rateRatio = rateRatio;
		this.volumeRatio = volumeRatio;
		this.sma10 = sma10;
		this.sma50 = sma50;
		this.sma100 = sma100;
		this.sma250 = sma250;
		this.rsi10 = rsi10;
		this.rsi14 = rsi14;
		this.rsi20 = rsi20;
		this.macd8_17 = macd8_17;
		this.macd12_25 = macd12_25;
		this.shortSellTurnover = shortSellTurnover;
		this.shortSellRatio = shortSellRatio;
		this.industry = industry;
		
	}
	
	public String getStockName(){
		return stockName;
	}
	
	public void setStockName(String stockName){
		this.stockName = stockName;
	}
	
	public double getCurrentPrice(){
		return currentPrice;
	}
	
	public void setCurrentPrice(double currentPrice){
		this.currentPrice = currentPrice;
	}
	
	public String getPosNegForLast(){
		return this.posNegForLast;
	}
	
	public void setPosNegForLast(String posNegForLast){
		this.posNegForLast = posNegForLast;
	}
	
	public String getLastUpdate(){
		return lastUpdate;
	}
	
	public void setLastUpdate(String lastUpdate){
		this.lastUpdate = lastUpdate;
	}
	
	public double getLotSize(){
		return lotSize;
	}
	
	public void setLotSize(double lotSize){
		this.lotSize = lotSize;
	}
	
	public String getChg(){
		return this.chg;
	}
	
	public String getPosNegForChg(){
		return this.posNegForChg;
	}
	
	public String getChgPercent(){
		return this.chgPercent;
	}
	
	public String getPosNegForChgPercent(){
		return this.posNegForChgPercent;
	}
	
	public String getSpread(){
		return this.spread;
	}
	
	public String getPeRatio(){
		return this.peRatio;
	}
	
	public String getYield(){
		return this.yield;
	}
	
	public String getDividendPayout(){
		return this.dividendPayout;
	}
	
	public String getEps(){
		return this.eps;
	}
	
	public String getMarketCap(){
		return this.marketCap;
	}
	
	public String getNav(){
		return this.nav;
	}
	
	public String getDps(){
		return this.dps;
	}

	public String getBid_delayed(){
		return this.bid_delayed;
	}

	public String getAsk_delayed(){
		return this.ask_delayed;
	}

	public String getHigh(){
		return this.high;
	}

	public String getLow(){
		return this.low;
	}

	public String getOpen(){
		return this.open;
	}

	public String getPrev_close(){
		return this.prev_close;
	}

	public String getVolume(){
		return this.volume;
	}

	public String getTurnover(){
		return this.turnover;
	}

	public String getOneMonthRange(){
		return this.oneMonthRange;
	}

	public String getTwoMonthRange(){
		return this.twoMonthRange;
	}

	public String getThreeMonthRange(){
		return this.threeMonthRange;
	}

	public String getFiftyTwoWeekRange(){
		return this.fiftyTwoWeekRange;
	}

	public String getRateRatio(){
		return this.rateRatio;
	}

	public String getVolumeRatio(){
		return this.volumeRatio;
	}

	public String getSma10(){
		return this.sma10;
	}

	public String getSma50(){
		return this.sma50;
	}

	public String getSma100(){
		return this.sma100;
	}

	public String getSma250(){
		return this.sma250;
	}

	public String getRsi10(){
		return this.rsi10;
	}

	public String getRsi14(){
		return this.rsi14;
	}

	public String getRsi20(){
		return this.rsi20;
	}

	public String getMacd8_17(){
		return this.macd8_17;
	}

	public String getMacd12_25(){
		return this.macd12_25;
	}

	public String getShortSellTurnover(){
		return this.shortSellTurnover;
	}
	
	public String getShortSellRatio(){
		return this.shortSellRatio;
	}

	public String getIndustry(){
		return this.industry;
	}
}

