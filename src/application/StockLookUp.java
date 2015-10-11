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
    private final ReadOnlyStringWrapper chg;
    private final ReadOnlyStringWrapper chgPercent;
    private final ReadOnlyStringWrapper posNegForChg;
    private final ReadOnlyStringWrapper posNegForChgPercent;
    private final ReadOnlyStringWrapper spread;
    private final ReadOnlyStringWrapper peRatio;
    private final ReadOnlyStringWrapper yield;
    private final ReadOnlyStringWrapper dividendPayout;
    private final ReadOnlyStringWrapper eps;
    private final ReadOnlyStringWrapper marketCap;
    private final ReadOnlyStringWrapper nav;
    private final ReadOnlyStringWrapper dps;

    
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
		this.chg = new ReadOnlyStringWrapper("");
		this.posNegForChg = new ReadOnlyStringWrapper("");
		this.chgPercent = new ReadOnlyStringWrapper("");
		this.posNegForChgPercent = new ReadOnlyStringWrapper("");
		this.spread = new ReadOnlyStringWrapper("");
		this.peRatio = new ReadOnlyStringWrapper("");
		this.yield = new ReadOnlyStringWrapper("");
		this.dividendPayout = new ReadOnlyStringWrapper("");
		this.eps = new ReadOnlyStringWrapper("");
		this.marketCap = new ReadOnlyStringWrapper("");
		this.nav = new ReadOnlyStringWrapper("");
		this.dps = new ReadOnlyStringWrapper("");

		// last
		this.currentPrice.bind(Bindings.createDoubleBinding(() -> {
	    							if(stockService.getLastValue() != null){
	    								return stockService.getLastValue().getCurrentPrice();
	    							} else{
	    								return (Double) 0.0;
	    							}
	    						}, stockService.lastValueProperty()));
		// Up / Down Last
		this.posNegForLast.bind(Bindings.createStringBinding(() -> {
									if(stockService.getLastValue() != null){
										return stockService.getLastValue().getPosNegForLast();
									} else{
										return "";
									}
								}, stockService.lastValueProperty()));
		// stock name
		this.stockName.bind(Bindings.createStringBinding(() -> {
									if(stockService.getLastValue() != null){
										return stockService.getLastValue().getStockName();
									} else{
										return "";
									}
								}, stockService.lastValueProperty()));
		
		// Last Update
		this.lastUpdate.bind(Bindings.createStringBinding(() -> {
			if(stockService.getLastValue() != null){
				return stockService.getLastValue().getLastUpdate();
			} else{
				return "";
			}
		}, stockService.lastValueProperty()));
		
		// lot size
		this.lotSize.bind(Bindings.createDoubleBinding(() -> {
			if(stockService.getLastValue() != null){
				return stockService.getLastValue().getLotSize();
			} else{
				return (Double) 0.0;
			}
		}, stockService.lastValueProperty()));
		
		// Chg
		this.chg.bind(Bindings.createStringBinding(() -> {
			if(stockService.getLastValue() != null){
				return stockService.getLastValue().getChg();
			} else{
				return "";
			}
		}, stockService.lastValueProperty()));
		
		// Up / Down Chg
		this.posNegForChg.bind(Bindings.createStringBinding(() -> {
			if(stockService.getLastValue() != null){
				return stockService.getLastValue().getPosNegForChg();
			} else{
				return "";
			}
		}, stockService.lastValueProperty()));
		// Chg %
		this.chgPercent.bind(Bindings.createStringBinding(() -> {
			if(stockService.getLastValue() != null){
				return stockService.getLastValue().getChgPercent();
			} else{
				return "";
			}
		}, stockService.lastValueProperty()));
		// Up / Down Chg %
		this.posNegForChgPercent.bind(Bindings.createStringBinding(() -> {
			if(stockService.getLastValue() != null){
				return stockService.getLastValue().getPosNegForChgPercent();
			} else{
				return "";
			}
		}, stockService.lastValueProperty()));
		// Spread
		this.spread.bind(Bindings.createStringBinding(() -> {
			if(stockService.getLastValue() != null){
				return stockService.getLastValue().getSpread();
			} else{
				return "";
			}
		}, stockService.lastValueProperty()));
		// PE Ratio
		this.peRatio.bind(Bindings.createStringBinding(() -> {
			if(stockService.getLastValue() != null){
				return stockService.getLastValue().getPeRatio();
			} else{
				return "";
			}
		}, stockService.lastValueProperty()));
		// Yield
		this.yield.bind(Bindings.createStringBinding(() -> {
			if(stockService.getLastValue() != null){
				return stockService.getLastValue().getYield();
			} else{
				return "";
			}
		}, stockService.lastValueProperty()));
		// Dividend Payout
		this.dividendPayout.bind(Bindings.createStringBinding(() -> {
			if(stockService.getLastValue() != null){
				return stockService.getLastValue().getDividendPayout();
			} else{
				return "";
			}
		}, stockService.lastValueProperty()));
		// EPS
		this.eps.bind(Bindings.createStringBinding(() -> {
			if(stockService.getLastValue() != null){
				return stockService.getLastValue().getEps();
			} else{
				return "";
			}
		}, stockService.lastValueProperty()));
		// Market Cap
		this.marketCap.bind(Bindings.createStringBinding(() -> {
			if(stockService.getLastValue() != null){
				return stockService.getLastValue().getMarketCap();
			} else{
				return "";
			}
		}, stockService.lastValueProperty()));
		// nav
		this.nav.bind(Bindings.createStringBinding(() -> {
			if(stockService.getLastValue() != null){
				return stockService.getLastValue().getNav();
			} else{
				return "";
			}
		}, stockService.lastValueProperty()));
		// DPS
		this.dps.bind(Bindings.createStringBinding(() -> {
			if(stockService.getLastValue() != null){
				return stockService.getLastValue().getDps();
			} else{
				return "";
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
		
	public ReadOnlyStringProperty chgProperty(){
		return this.chg.getReadOnlyProperty();
	}
	
	public String getChg(){
		return chgProperty().get();
	}
	
	public ReadOnlyStringProperty posNegForChgProperty(){
		return this.posNegForChg.getReadOnlyProperty();
	}
	
	public String getPosNegForChg(){
		return this.posNegForChgProperty().get();
	}
	
	public ReadOnlyStringProperty chgPercentProperty(){
		return this.chgPercent.getReadOnlyProperty();
	}
	
	public String getChgPercent(){
		return this.chgPercentProperty().get();
	}
	
	public ReadOnlyStringProperty posNegForChgPercentProperty(){
		return this.posNegForChgPercent.getReadOnlyProperty();
	}
	
	public String getPosNegForChgPercent(){
		return posNegForChgPercentProperty().get();
	}
	
	public ReadOnlyStringProperty spreadProperty(){
		return this.spread.getReadOnlyProperty();
	}
	
	public String getSpread(){
		return this.spreadProperty().get();
	}
	
	public ReadOnlyStringProperty peRatioProperty(){
		return this.peRatio.getReadOnlyProperty();
	}
	
	public String getPeRatio(){
		return this.peRatioProperty().get();
	}
	
	public ReadOnlyStringProperty yieldProperty(){
		return this.yield.getReadOnlyProperty();
	}
	
	public String getYield(){
		return this.yieldProperty().get();
	}
	
	public ReadOnlyStringProperty dividendPayoutProperty(){
		return this.dividendPayout.getReadOnlyProperty();
	}
	
	public String getDividendPayout(){
		return this.dividendPayoutProperty().get();
	}
	
	public ReadOnlyStringProperty epsProperty(){
		return this.eps.getReadOnlyProperty();
	}
	
	public String getEps(){
		return this.epsProperty().get();
	}
	
	public ReadOnlyStringProperty marketCapProperty(){
		return this.marketCap.getReadOnlyProperty();
	}
	
	public String getMarketCap(){
		return this.marketCapProperty().get();
	}
	
	public ReadOnlyStringProperty navProperty(){
		return this.nav.getReadOnlyProperty();
	}
	
	public String getNav(){
		return this.navProperty().get();
	}
	
	public ReadOnlyStringProperty dpsProperty(){
		return this.dps.getReadOnlyProperty();
	}
	
	public String getDps(){
		return this.dpsProperty().get();
	}
	
	
	// multi-threading
	public final void startMonitoring() {
		stockService.restart();
	}

	public final void stopMonitoring() {
		stockService.cancel();
	}

}
