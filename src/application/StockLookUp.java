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


public class StockLookUp implements StockScraping{
	
	private StringProperty stockTicker;
	private final ReadOnlyDoubleWrapper currentPrice;
	private final ReadOnlyStringWrapper posNegForLast;
	private final ReadOnlyDoubleWrapper lotSize;
    private final ReadOnlyStringWrapper stockName;
    private final ReadOnlyStringWrapper lastUpdate;
	private final ScheduledService<StockScrapedInfo> stockService = new ScheduledService<StockScrapedInfo>() {
		@Override
	    public Task<StockScrapedInfo> createTask(){
			return new Task<StockScrapedInfo>() {
				@Override
				public StockScrapedInfo call() throws Exception {					
					return getDataFromAAStock(getStockTicker());
					//return getCurrentPriceFromGoogle();
				}
			};
		}
	};
	
	
	public StockLookUp(String stockTicker){
		this.stockTicker = new SimpleStringProperty(stockTicker);
	
		// multi-threading current price	
		Random rn = new Random();
		int sec = (30 + rn.nextInt((180-30)+1));
    	System.out.println("SL random time !!!!!!!!!!!!!!!!!       " + sec + "   "  + LocalDateTime.now());
		stockService.setPeriod(Duration.seconds(sec));
		stockService.setOnFailed(e -> stockService.getException().printStackTrace());
		stockService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		     @Override
		     public void handle(WorkerStateEvent t) {
		 		Random rn = new Random();
				int sec = (30 + rn.nextInt((180-30)+1));
		    	System.out.println("SL setOnSucceeded!!!!!!!!!!!!!!!!!       " + sec + "   "  + LocalDateTime.now());
				stockService.setPeriod(Duration.seconds(sec));
		     }
		});
		
		this.currentPrice = new ReadOnlyDoubleWrapper(0);
		this.posNegForLast = new ReadOnlyStringWrapper("");
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
		
		this.posNegForLast.bind(Bindings.createStringBinding(() -> {
									if(stockService.getLastValue() != null){
										return stockService.getLastValue().getPosNegForLast();
									} else{
										return "";
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
		
		startMonitoring();
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
	
	public ReadOnlyDoubleProperty currentPriceProperty(){
		return this.currentPrice.getReadOnlyProperty();
	}
	 
	public final double getCurrentPrice(){
		return currentPriceProperty().get();
	}
	 
	public ReadOnlyStringProperty posNegForLastProperty(){
		return this.posNegForLast.getReadOnlyProperty();
	}
	
	public final String getPosNegForLastProperty(){
		return posNegForLastProperty().get();
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
	
	// multi-threading
	public final void startMonitoring() {
		stockService.restart();
	}

	public final void stopMonitoring() {
		stockService.cancel();
	}

}
