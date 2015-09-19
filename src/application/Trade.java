package application;
import java.util.*;
import java.math.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.io.*;
import javafx.util.Duration;
import java.time.LocalDate;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.control.TableCell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;


public class Trade implements Comparable<Trade>{
	
	// attributes of each trade that go into the Transaction log
	//String buySell;
	//LocalDate transactionDate;
	//String stockTicker;
	//String stockName;
	//double volume;
	//double price;
	//double transactionFee;
	
	// properties
	private Calendar creationTime;
	private int counter;
	private ObjectProperty<LocalDate> transactionDate;
	private StringProperty stockTicker;
	private StringProperty remarks;
	private StringProperty buySell;
	private DoubleProperty volume;
	private DoubleProperty price;
	private ReadOnlyDoubleWrapper transactionFee;
	
	private final ReadOnlyDoubleWrapper currentPrice;
    private final ReadOnlyStringWrapper stockName;
	private ReadOnlyBooleanWrapper caution;
	
	private final ScheduledService<StockScrapedInfo> stockService = new ScheduledService<StockScrapedInfo>() {
		@Override
	    public Task<StockScrapedInfo> createTask(){
			return new Task<StockScrapedInfo>() {
				@Override
				public StockScrapedInfo call() throws InterruptedException, IOException {					
					return getCurrentPriceFromAAStock();
					//return getCurrentPriceFromGoogle();
				}
			};
		}
	};
	
	
	
	public Trade(BuySell buySell, LocalDate transactionDate, int stockTicker, double volume, double price){
		this.buySell = new SimpleStringProperty(buySell.toString());
		this.remarks = new SimpleStringProperty("");
		this.transactionDate = new SimpleObjectProperty<LocalDate>(transactionDate);
		this.stockTicker = new SimpleStringProperty(String.format("%04d",stockTicker));
		this.volume = new SimpleDoubleProperty(volume);
		this.price = new SimpleDoubleProperty(price);
		this.transactionFee = new ReadOnlyDoubleWrapper();
		this.transactionFee.bind(this.price.multiply(this.volume).multiply(0.0025));

		// thread counter keeps refreshing
		counter = 0;
		
		// Object creation time
		creationTime = Calendar.getInstance();
		// multi-threading current price
		
		stockService.setPeriod(Duration.seconds(10));
		stockService.setOnFailed(e -> stockService.getException().printStackTrace());
		
		this.currentPrice = new ReadOnlyDoubleWrapper(0);
		this.stockName = new ReadOnlyStringWrapper("");
		this.currentPrice.bind(Bindings.createDoubleBinding(() -> {
	    							if(stockService.getLastValue() != null){
	    								return stockService.getLastValue().getCurrentPrice();
	    							} else{
	    								return (Double) 0.0;
	    							}
	    						}, stockService.lastValueProperty()
	    							));
			this.stockName.bind(Bindings.createStringBinding(() -> {
									if(stockService.getLastValue() != null){
										return stockService.getLastValue().getStockName();
									} else{
										return "";
									}
								}, stockService.lastValueProperty()));
									


		this.caution = new ReadOnlyBooleanWrapper();
		this.caution.bind(this.currentPrice.greaterThan(this.price));
		startMonitoring();
		
	}	
	
	public Calendar getCreationTime(){
		return this.creationTime;
	}
	
	// getters
	public String getBuySell(){
		return this.buySell.get();
	}
	
	// return Property Object
	public StringProperty buySellProperty(){
		return this.buySell;
	}
	
	// setters
	public void setBuySell(String buySell){
		//this.buySell.set(buySell.toString());
		this.buySell.set(buySell);
	}
	
	public LocalDate getTransactionDate(){
		return this.transactionDate.getValue();
	}
	
	public ObjectProperty<LocalDate> transactionDateProperty(){
		return this.transactionDate;
	}
	
	public void setTransactionDate(LocalDate transactionDate){
		this.transactionDate.set(transactionDate);
	}
	
	
	
	public String getStockTicker(){
		return stockTickerProperty().get();
	}
	
	
	public StringProperty stockTickerProperty(){
		return this.stockTicker;
	}
	
	public void setStockTicker(String stockTicker){
		stockTickerProperty().set("-1");
		int st = Integer.parseInt(stockTicker);
		stockTickerProperty().set(String.format("%04d",st));
	}
	
	public double getVolume(){
		return this.volume.get();
	}
	
	public DoubleProperty volumeProperty(){
		return this.volume;
	}
	
	public void setVolume(double volume){
		this.volume.set(volume);
	}
	
	
	public double getPrice(){
		return this.price.get();
	}	
	
	public DoubleProperty priceProperty(){
		return this.price;
	}
	
	public void setPrice(double price){
		this.price.set(price);
	}
	
	public String getRemarks(){
		return this.remarks.getValue();
	}
	
	public StringProperty remarksProperty(){
		return this.remarks;
	}
	
	public void setRemarks(String remarks){
		this.remarks.set(remarks);
	}

	
	public ReadOnlyDoubleProperty transactionFeeProperty() {
		 return this.transactionFee.getReadOnlyProperty();
	 }

	 public final double getTransactionFee() {
		 return transactionFeeProperty().get();
	 }
	
	 
	 public ReadOnlyDoubleProperty currentPriceProperty(){
		 return this.currentPrice.getReadOnlyProperty();
	 }
	 
	 public final double getCurrentPrice(){
		 return currentPriceProperty().get();
	 }
	 
	 public ReadOnlyStringProperty stockNameProperty(){
		 return this.stockName.getReadOnlyProperty();
	 }
	 
	 public String getStockName(){
		 return stockNameProperty().get();
	 }
	 
	 
	 public ReadOnlyBooleanProperty cautionProperty(){
		 return this.caution.getReadOnlyProperty();
	 }
	 
	 public final Boolean getCaution(){
		 return cautionProperty().getValue();
	 }
	 
	 // multi-threading
	 public final void startMonitoring() {
		 stockService.restart();
	 }

	 public final void stopMonitoring() {
		 stockService.cancel();
	 }
	
	public double getCurrentPriceFromGoogle() throws InterruptedException, IOException{
		String url = "https://www.google.com.hk/finance?q=" + getStockTicker() + "&ei=yF14VYC4F4Wd0ASb64CoCw";
		Document doc = Jsoup.connect(url).get();
		Element content = doc.select("meta[itemprop=price]").first();
		double cp =  Double.parseDouble(content.attr("content"));
		return cp;
		}
	 
	 
	public StockScrapedInfo getCurrentPriceFromAAStock() throws InterruptedException, IOException{
		String url = "http://www.aastocks.com/en/stock/detailquote.aspx?&symbol=" + getStockTicker();
		Document doc = Jsoup.connect(url).get();
		Elements elements = doc.select("ul:contains(Last) + ul>li>span");
		double cp = Double.parseDouble(elements.get(0).ownText());
		Elements sn = doc.select("title");
		String[] title = sn.get(0).ownText().split("\\(");
		String stockName = title[0];
		System.out.println("Trade Ticker: " + getStockTicker() + ", cp: " + cp);
		//if("null".equals(cp) || stockName == null){
		//	return new StockScrapedInfo("",0.0);
		//} else{
			return new StockScrapedInfo(stockName, cp);
		//}
	}
	 
	
	public String toString(){
		return  //"caution: " + getCaution() + 
				//", Creation Time: " + getCreationTime() + 
				//", Current price: " + getCurrentPrice() + 
				"Stock Ticker: " + getStockTicker() +  
				", Transasction date: " + getTransactionDate() 
				+ ", Buy: " + getBuySell() + 
				", Volume: " + getVolume() + 
				", Price: " + getPrice() + 
				", Transaction fee: " + 
				getTransactionFee() +  
				", Remarks: " + getRemarks();
	}
	
	
	public int hashCode(){
		return (int) Integer.parseInt(getStockTicker())*31; 
	}
	
	public boolean equals(Trade o){
		if(o == this){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		return getTransactionDate().isEqual(o.getTransactionDate()) && getPrice() == o.getPrice() && getVolume() == o.getVolume() && getStockTicker().equals(o.getStockTicker()) && getBuySell().equals(o.getBuySell()); 
	}
	
	public int compareTo(Trade o){
		
		if(getStockTicker() == null || o.getStockTicker() == null || getTransactionDate() == null || o.getTransactionDate() == null || getBuySell() == null || o.getBuySell() == null){
			//System.out.println("0: NULL fields!");
			return 0;
		}
		
		int i = getStockTicker().compareTo(o.getStockTicker());
		if(i != 0){
			//System.out.println("Comparing Stock Ticker: " + i);
			return i;
		}
		
		i = getTransactionDate().compareTo(o.getTransactionDate());
		if(i != 0){
			//System.out.println("Comparing Transaction Date: " + i);
			return i;
		}
		
		i = getCreationTime().compareTo(o.getCreationTime()); 
		//System.out.println("Comparing Creation Time: " + i);
		return i;
	}
	
	
	// Calendar.getInstance().getTime()
	public static void main(String[] args) throws InterruptedException, IOException{
		// Comparison test
		Trade trade1 = new Trade(BuySell.Buy, LocalDate.now(),50,1,1);
		// stock Ticker different
		Trade trade2 = new Trade(BuySell.Buy, LocalDate.now().plusDays(0),51,2,2);
		Trade trade3 = new Trade(BuySell.Buy, LocalDate.now().plusDays(0),49,2,2);
		// transaction Date different
		Trade trade4 = new Trade(BuySell.Buy, LocalDate.now().plusDays(1),50,2,2);
		Trade trade5 = new Trade(BuySell.Buy, LocalDate.now().minusDays(1),50,2,2);
		// buy sell different
		Trade trade6 = new Trade(BuySell.Buy, LocalDate.now().plusDays(0),50,3,3);
		Trade trade7 = new Trade(BuySell.Sell, LocalDate.now().plusDays(0),50,3,3);

		//System.out.println(trade1.getCurrentPrice());
		//System.out.println(trade1.getCurrentPriceFromAAStock());
		//System.out.println(trade1.creationTime);
		//System.out.println(trade2);
		//System.out.println(trade3);
		//System.out.println(trade4);
		//System.out.println(trade5);
		//System.out.println(trade6);
		//System.out.println(trade7);

		//trade1.compareTo(trade2);
		//trade1.compareTo(trade3);
		//trade1.compareTo(trade4);
		//trade1.compareTo(trade5);
		//trade1.compareTo(trade6);
		//trade1.compareTo(trade7);

		//System.out.println(trade1.getCurrentPriceFromGoogle());
		//System.out.println(trade1.getCurrentPriceFromAAStock());
		//trade1.setPrice(1);
		//System.out.println(trade1);
	}


	
}
