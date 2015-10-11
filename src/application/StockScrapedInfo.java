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
	
	
	public StockScrapedInfo(String stockName, double currentPrice, String posNegForLast, double lotSize,String lastUpdate, String chg, String posNegForChg, String chgPercent, String posNegForChgPercent, String spread, String peRatio, String yield, String dividendPayout, String eps, String marketCap, String nav, String dps){
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
	
}
