package application;
import java.util.*;
import java.math.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import java.io.*;
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.binding.NumberBinding;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.control.TableCell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.io.Serializable;
import java.net.URLEncoder;

public class Trade implements Comparable<Trade>, Serializable,StockScraping{
	
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
	private final ReadOnlyStringWrapper lastUpdate;
	private final ReadOnlyDoubleWrapper  lotSize;
	private final PauseTransition delay;
	
	private ReadOnlyBooleanWrapper caution;
	
	private final ScheduledService<StockScrapedInfo> stockService = new ScheduledService<StockScrapedInfo>() {
		@Override
	    public Task<StockScrapedInfo> createTask(){
			return new Task<StockScrapedInfo>() {
				@Override
				public StockScrapedInfo call() throws Exception {		
					return getDataFromAAStock(getStockTicker());
				}
			};
		}
	};
	
	
	
	public Trade(BuySell buySell, LocalDate transactionDate, String stockTicker, double volume, double price){
		this.buySell = new SimpleStringProperty(buySell.toString());
		this.remarks = new SimpleStringProperty("");
		this.transactionDate = new SimpleObjectProperty<LocalDate>(transactionDate);
		this.stockTicker = new SimpleStringProperty(stockTicker);
		this.volume = new SimpleDoubleProperty(volume);
		this.price = new SimpleDoubleProperty(price);
		this.transactionFee = new ReadOnlyDoubleWrapper();
		this.transactionFee.bind(this.price.multiply(this.volume).multiply(0.0025));

		// thread counter keeps refreshing
		counter = 0;
		
		// Object creation time
		creationTime = Calendar.getInstance();
		// multi-threading current price
		Random rn = new Random();
		int sec = (30 + rn.nextInt((180-30)+1));
    	System.out.println("T random time !!!!!!!!!!!!!!!!!       " + sec + "   "  + LocalDateTime.now());
		stockService.setPeriod(Duration.seconds(sec));
		stockService.setOnFailed(e -> stockService.getException().printStackTrace());
		stockService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		     @Override
		     public void handle(WorkerStateEvent t) {
		 		Random rn = new Random();
				int sec = (30 + rn.nextInt((180-30)+1));
		    	System.out.println("T setOnSucceeded!!!!!!!!!!!!!!!!!       " + sec + "   "  + LocalDateTime.now());
				stockService.setPeriod(Duration.seconds(sec));
		     }
		});
		this.currentPrice = new ReadOnlyDoubleWrapper(0);
		this.stockName = new ReadOnlyStringWrapper("");
		this.lotSize = new ReadOnlyDoubleWrapper(0);
		this.lastUpdate = new ReadOnlyStringWrapper("");
		this.currentPrice.bind(Bindings.createDoubleBinding(() -> {
	    							if(stockService.getLastValue() != null){
	    								return stockService.getLastValue().getCurrentPrice();
	    							} else{
	    								return (Double) 0.0;
	    							}
	    						}, stockService.lastValueProperty()));
		this.stockName.bind(Bindings.createStringBinding(() -> {
									if(stockService.getLastValue() != null){
										return stockService.getLastValue().getStockName();
									} else{
										return "";
									}
								}, stockService.lastValueProperty()));
		this.lastUpdate.bind(Bindings.createStringBinding(() -> {
			if(stockService.getLastValue() != null){
				return stockService.getLastValue().getLastUpdate();
			} else{
				return "";
			}
		}, stockService.lastValueProperty()));
		this.lotSize.bind(Bindings.createDoubleBinding(() -> {
			if(stockService.getLastValue() != null){
				return stockService.getLastValue().getLotSize();
			} else{
				return (Double) 0.0;
			}
		}, stockService.lastValueProperty()));

		
		this.caution = new ReadOnlyBooleanWrapper();
		this.caution.bind(currentPriceProperty().greaterThan(priceProperty()));
		delay = new PauseTransition(Duration.seconds(30));
		delay.setOnFinished(e -> stopMonitoring());
		tempMonitoring();
		
		stockTickerProperty().addListener(new ChangeListener(){
			@Override
			public void changed(ObservableValue o, Object oldVal, Object newVal){
				tempMonitoring();
			}
		});
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
		//stockTickerProperty().set("-1");
		//int st = Integer.parseInt(stockTicker);
		stockTickerProperty().set(stockTicker);
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
	 
	public ReadOnlyDoubleProperty lotSizeProperty(){
		return this.lotSize.getReadOnlyProperty();
	}
	 
	public final double getLotSize(){
		return lotSizeProperty().get();
	}
	 
	public ReadOnlyStringProperty lastUpdateProperty(){
		return this.lastUpdate.getReadOnlyProperty();
	}
	 
	public String getLastUpdate(){
		return lastUpdateProperty().get();
	}
	 
	public ReadOnlyBooleanProperty cautionProperty(){
		return this.caution.getReadOnlyProperty();
	}
	 
	public final Boolean getCaution(){
		return cautionProperty().getValue();
	}
	 
	 
	 // multi-threading
	public final void startMonitoring() {
		System.out.println("!!!!!!!!!!!!!!!!!!!!   RESTARTING     !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		stockService.restart();
	}

	public final void stopMonitoring() {
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! STOP");
		stockService.cancel();
	}
	
	// temporary monitoring
	public final void tempMonitoring(){
		System.out.println("TEMP MON !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		startMonitoring();
		delay.play();
		//delay.stop();
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
	/*
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
	 */

	
}
