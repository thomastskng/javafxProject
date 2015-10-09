package application;

public class StockScrapedInfo {
	private String stockName;
	private double currentPrice;
	private String lastUpdate;
	private double lotSize;
	private boolean posNegBoldForLast;
	
	public StockScrapedInfo(String stockName, double currentPrice, String posNegBoldForLast, double lotSize,String lastUpdate){
		this.stockName = stockName;
		this.currentPrice = currentPrice;
		this.lotSize = lotSize;
		this.lastUpdate = lastUpdate;
		setUpDownForLast(posNegBoldForLast);
		this.posNegBoldForLast = getUpDownForLast();
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
	
	public boolean getUpDownForLast(){
		return this.posNegBoldForLast;
	}
	
	public void setUpDownForLast(String posNegBoldForLast){
		if(posNegBoldForLast.equals("")){
			this.posNegBoldForLast = true;
		} else{
			this.posNegBoldForLast = false;
		}
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
