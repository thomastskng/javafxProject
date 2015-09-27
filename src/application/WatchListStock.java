package application;
import java.util.*;
import java.math.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.io.*;
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.io.Serializable;

public class WatchListStock implements Serializable, StockScraping{
	private StringProperty stockTicker;
	private DoubleProperty target;
	private final ReadOnlyDoubleWrapper currentPrice;
    private final ReadOnlyStringWrapper stockName;
	private StringProperty condition;
	private ReadOnlyBooleanWrapper alert;

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

	public WatchListStock(String condition, String stockTicker, double target){

		this.condition = new SimpleStringProperty(condition);
		this.stockTicker = new SimpleStringProperty(stockTicker);
		this.target = new SimpleDoubleProperty(target);
		Random rn = new Random();
		int sec = (30 + rn.nextInt((180-30)+1));
    	System.out.println("WL random time !!!!!!!!!!!!!!!!!       " + sec + "   "  + LocalDateTime.now());
		stockService.setPeriod(Duration.seconds(sec));
		stockService.setOnFailed(e -> stockService.getException().printStackTrace());
		stockService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		     @Override
		     public void handle(WorkerStateEvent t) {
		 		Random rn = new Random();
				int sec = (30 + rn.nextInt((180-30)+1));
		    	System.out.println("WL setOnSucceeded!!!!!!!!!!!!!!!!!       " + sec + "   "  +LocalDateTime.now());
				stockService.setPeriod(Duration.seconds(sec));
		     }
		});
		
		this.currentPrice = new ReadOnlyDoubleWrapper(0);
		this.stockName = new ReadOnlyStringWrapper("");
		this.currentPrice.bind(Bindings.createDoubleBinding(() -> {
	    							if(stockService.getLastValue() != null){
	    								return stockService.getLastValue().getCurrentPrice();
	    							} else{
	    								return (Double) 0.0;
	    							}
	    						}, stockService.lastValueProperty())
		);
			
		this.stockName.bind(Bindings.createStringBinding(() -> {
									if(stockService.getLastValue() != null){
										return stockService.getLastValue().getStockName();
									} else{
										return "";
									}
								}, stockService.lastValueProperty())
		);
		
		System.out.println("*************************");
		System.out.println(getCondition());
		//System.out.println(WatchListCondition.LastGreaterThanEqualToTarget.toString());
		//System.out.println(getCondition().equals(WatchListCondition.LastGreaterThanEqualToTarget.toString()));
		System.out.println("*************************");
		
		this.alert =  new ReadOnlyBooleanWrapper();
		if(getCondition().equals("Last >= Target")){
			this.alert.bind(currentPriceProperty().greaterThanOrEqualTo(targetProperty()));
		} else{
			this.alert.bind(currentPriceProperty().lessThanOrEqualTo(targetProperty()));
		}
		
		startMonitoring();

	}
	
	// getters
	public String getCondition(){
		return this.condition.get();
	}
	
	// return Property Object
	public StringProperty conditionProperty(){
		return this.condition;
	}
	
	public void setCondition(String condition){
		this.condition.set(condition);		
		this.alert.unbind();
		if(getCondition().equals("Last >= Target")){
			this.alert.bind(currentPriceProperty().greaterThanOrEqualTo(targetProperty()));
		} else{
			this.alert.bind(currentPriceProperty().lessThanOrEqualTo(targetProperty()));
		}
	}
	
	
	public String getStockTicker(){
		return stockTickerProperty().get();
	}
	
	
	public StringProperty stockTickerProperty(){
		return this.stockTicker;
	}
	
	public void setStockTicker(String stockTicker){
		stockTickerProperty().set(stockTicker);
	}

	public double getTarget(){
		return this.target.get();
	}	
	
	public DoubleProperty targetProperty(){
		return this.target;
	}
	
	public void setTarget(double target){
		this.target.set(target);
	}
	
	public ReadOnlyBooleanProperty alertProperty(){
		return this.alert.getReadOnlyProperty();
	}
	 
	public final Boolean getAlert(){
		return alertProperty().getValue();
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
	
	// multi-threading
	public final void startMonitoring() {
		stockService.restart();
	}

	public final void stopMonitoring() {
		stockService.cancel();
	}
	
	public String toString(){
		return 	"Stock Ticker: " + getStockTicker() +  
				", Target price: " + getTarget() + 
				", alert: " + getAlert() + 
				", condition: " + getCondition();
	}
}
