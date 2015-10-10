package application;

public class StockScrapedInfo {
	private String stockName;
	private double currentPrice;
	private String lastUpdate;
	private double lotSize;
	private String posNegForLast;
	
	public StockScrapedInfo(String stockName, double currentPrice, String posNegForLast, double lotSize,String lastUpdate){
		this.stockName = stockName;
		this.currentPrice = currentPrice;
		this.lotSize = lotSize;
		this.lastUpdate = lastUpdate;
		this.posNegForLast = posNegForLast;
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
}
