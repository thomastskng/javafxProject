package application;

public class StockScrapedInfo {
	private String stockName;
	private double currentPrice;
	
	public StockScrapedInfo(String stockName, double currentPrice){
		this.stockName = stockName;
		this.currentPrice = currentPrice;
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
	
	
}
