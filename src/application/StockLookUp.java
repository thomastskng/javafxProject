package application;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
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
import javafx.beans.binding.Binding;
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
    private final ReadOnlyStringWrapper bid_delayed;
    private final ReadOnlyStringWrapper ask_delayed;
    private final ReadOnlyStringWrapper high;
    private final ReadOnlyStringWrapper low;
    private final ReadOnlyStringWrapper open;
    private final ReadOnlyStringWrapper prev_close;
    private final ReadOnlyStringWrapper volume;
    private final ReadOnlyStringWrapper turnover;
    private final ReadOnlyStringWrapper oneMonthRange;
    private final ReadOnlyStringWrapper twoMonthRange;
    private final ReadOnlyStringWrapper threeMonthRange;
    private final ReadOnlyStringWrapper fiftyTwoWeekRange;
    private final ReadOnlyStringWrapper rateRatio;
    private final ReadOnlyStringWrapper volumeRatio;
    private final ReadOnlyStringWrapper sma10;
    private final ReadOnlyStringWrapper sma20;
    private final ReadOnlyStringWrapper sma50;
    private final ReadOnlyStringWrapper sma100;
    private final ReadOnlyStringWrapper sma250;
    private final ReadOnlyStringWrapper rsi10;
    private final ReadOnlyStringWrapper rsi14;
    private final ReadOnlyStringWrapper rsi20;
    private final ReadOnlyStringWrapper macd8_17;
    private final ReadOnlyStringWrapper macd12_25;

    
	private final ScheduledService<StockScrapedInfo> stockService = new ScheduledService<StockScrapedInfo>() {
		@Override
	    public Task<StockScrapedInfo> createTask(){
			return new Task<StockScrapedInfo>() {
				@Override
				public StockScrapedInfo call() throws Exception {					
					return getDataFromAAStock(getStockTicker());
					//return getCurrentPriceFromGoogle();
					//return getDataFromEtnet(getStockTicker());
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
		this.bid_delayed= new ReadOnlyStringWrapper("");
		this.ask_delayed= new ReadOnlyStringWrapper("");
		this.high= new ReadOnlyStringWrapper("");
		this.low= new ReadOnlyStringWrapper("");
		this.open= new ReadOnlyStringWrapper("");
		this.prev_close= new ReadOnlyStringWrapper("");
		this.volume= new ReadOnlyStringWrapper("");
		this.turnover= new ReadOnlyStringWrapper("");
		this.oneMonthRange= new ReadOnlyStringWrapper("");
		this.twoMonthRange= new ReadOnlyStringWrapper("");
		this.threeMonthRange= new ReadOnlyStringWrapper("");
		this.fiftyTwoWeekRange= new ReadOnlyStringWrapper("");
		this.rateRatio= new ReadOnlyStringWrapper("");
		this.volumeRatio= new ReadOnlyStringWrapper("");
		this.sma10= new ReadOnlyStringWrapper("");
		this.sma20= new ReadOnlyStringWrapper("");
		this.sma50= new ReadOnlyStringWrapper("");
		this.sma100= new ReadOnlyStringWrapper("");
		this.sma250= new ReadOnlyStringWrapper("");
		this.rsi10= new ReadOnlyStringWrapper("");
		this.rsi14= new ReadOnlyStringWrapper("");
		this.rsi20= new ReadOnlyStringWrapper("");
		this.macd8_17= new ReadOnlyStringWrapper("");
		this.macd12_25= new ReadOnlyStringWrapper("");
		// last
		this.currentPrice.bind(Bindings.createDoubleBinding(() -> {
	    							if(stockService.getLastValue() != null){
	    								return stockService.getLastValue().getCurrentPrice();
	    							} else{
	    								return (Double) 0.0;
	    							}
	    						}, stockService.lastValueProperty()));
		// Up / Down Last
		makeStringBinding(this.posNegForLast, StockScrapedInfo::getPosNegForLast);

		// stock name
		makeStringBinding(this.stockName,StockScrapedInfo::getStockName);

		// Last Update
		makeStringBinding(this.lastUpdate, StockScrapedInfo::getLastUpdate);

		
		// lot size
		makeDoubleBinding(this.lotSize, StockScrapedInfo::getLotSize);
		
		// Chg
		this.chg.bind(Bindings.createStringBinding(() -> {
			if(stockService.getLastValue() != null){
				return stockService.getLastValue().getChg();
			} else{
				return "";
			}
		}, stockService.lastValueProperty()));
		
		// Up / Down Chg
		makeStringBinding(this.posNegForChg, StockScrapedInfo::getPosNegForChg);

		// Chg %
		makeStringBinding(this.chgPercent,StockScrapedInfo::getChgPercent);

		// Up / Down Chg %
		makeStringBinding(this.posNegForChgPercent,StockScrapedInfo::getPosNegForChgPercent);

		// Spread
		makeStringBinding(this.spread, StockScrapedInfo::getSpread);

		// PE Ratio
		makeStringBinding(this.peRatio, StockScrapedInfo::getPeRatio);

		// Yield
		makeStringBinding(this.yield, StockScrapedInfo::getYield);
		// Dividend Payout
		makeStringBinding(this.dividendPayout, StockScrapedInfo::getDividendPayout);
		// EPS
		makeStringBinding(this.eps, StockScrapedInfo::getEps);

		// Market Cap
		makeStringBinding(this.marketCap, StockScrapedInfo::getMarketCap);
		// nav
		makeStringBinding(this.nav, StockScrapedInfo::getNav);
		// DPS
		makeStringBinding(this.dps, StockScrapedInfo::getDps);
		//bid_delayed
		makeStringBinding(this.bid_delayed, StockScrapedInfo::getBid_delayed);
		//ask_delayed
		makeStringBinding(this.ask_delayed, StockScrapedInfo::getAsk_delayed);
		//high
		makeStringBinding(this.high, StockScrapedInfo::getHigh);
		//low
		makeStringBinding(this.low, StockScrapedInfo::getLow);
		//open
		makeStringBinding(this.open, StockScrapedInfo::getOpen);
		//prev_close
		makeStringBinding(this.prev_close, StockScrapedInfo::getPrev_close);
		//volume
		makeStringBinding(this.volume, StockScrapedInfo::getVolume);
		//turnover
		makeStringBinding(this.turnover, StockScrapedInfo::getTurnover);
		//oneMonthRange
		makeStringBinding(this.oneMonthRange, StockScrapedInfo::getOneMonthRange);
		//twoMonthRange
		makeStringBinding(this.twoMonthRange, StockScrapedInfo::getTwoMonthRange);
		//threeMonthRange
		makeStringBinding(this.threeMonthRange, StockScrapedInfo::getThreeMonthRange);
		//fiftyTwoWeekRange
		makeStringBinding(this.fiftyTwoWeekRange, StockScrapedInfo::getFiftyTwoWeekRange);
		//rateRatio
		makeStringBinding(this.rateRatio, StockScrapedInfo::getRateRatio);
		//volumeRatio
		makeStringBinding(this.volumeRatio, StockScrapedInfo::getVolumeRatio);
		//sma10
		makeStringBinding(this.sma10, StockScrapedInfo::getSma10);
		//sma20
		makeStringBinding(this.sma20, StockScrapedInfo::getSma20);
		//sma50
		makeStringBinding(this.sma50, StockScrapedInfo::getSma50);
		//sma100
		makeStringBinding(this.sma100, StockScrapedInfo::getSma100);
		//sma250
		makeStringBinding(this.sma250, StockScrapedInfo::getSma250);
		//rsi10
		makeStringBinding(this.rsi10, StockScrapedInfo::getRsi10);
		//rsi14
		makeStringBinding(this.rsi14, StockScrapedInfo::getRsi14);
		//rsi20
		makeStringBinding(this.rsi20, StockScrapedInfo::getRsi20);
		//macd8_17
		makeStringBinding(this.macd8_17, StockScrapedInfo::getMacd8_17);
		//macd12_25
		makeStringBinding(this.macd12_25, StockScrapedInfo::getMacd12_25);

		
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
	
	public ReadOnlyStringProperty bid_delayedProperty(){
		return this.bid_delayed.getReadOnlyProperty();
	}

	public String getBid_delayed(){
		return this.bid_delayedProperty().get();
	}
	public ReadOnlyStringProperty ask_delayedProperty(){
		return this.ask_delayed.getReadOnlyProperty();
	}

	public String getAsk_delayed(){
		return this.ask_delayedProperty().get();
	}
	public ReadOnlyStringProperty highProperty(){
		return this.high.getReadOnlyProperty();
	}

	public String getHigh(){
		return this.highProperty().get();
	}
	public ReadOnlyStringProperty lowProperty(){
		return this.low.getReadOnlyProperty();
	}

	public String getLow(){
		return this.lowProperty().get();
	}
	public ReadOnlyStringProperty openProperty(){
		return this.open.getReadOnlyProperty();
	}

	public String getOpen(){
		return this.openProperty().get();
	}
	public ReadOnlyStringProperty prev_closeProperty(){
		return this.prev_close.getReadOnlyProperty();
	}

	public String getPrev_close(){
		return this.prev_closeProperty().get();
	}
	public ReadOnlyStringProperty volumeProperty(){
		return this.volume.getReadOnlyProperty();
	}

	public String getVolume(){
		return this.volumeProperty().get();
	}
	public ReadOnlyStringProperty turnoverProperty(){
		return this.turnover.getReadOnlyProperty();
	}

	public String getTurnover(){
		return this.turnoverProperty().get();
	}
	public ReadOnlyStringProperty oneMonthRangeProperty(){
		return this.oneMonthRange.getReadOnlyProperty();
	}

	public String getOneMonthRange(){
		return this.oneMonthRangeProperty().get();
	}
	public ReadOnlyStringProperty twoMonthRangeProperty(){
		return this.twoMonthRange.getReadOnlyProperty();
	}

	public String getTwoMonthRange(){
		return this.twoMonthRangeProperty().get();
	}
	public ReadOnlyStringProperty threeMonthRangeProperty(){
		return this.threeMonthRange.getReadOnlyProperty();
	}

	public String getThreeMonthRange(){
		return this.threeMonthRangeProperty().get();
	}
	public ReadOnlyStringProperty fiftyTwoWeekRangeProperty(){
		return this.fiftyTwoWeekRange.getReadOnlyProperty();
	}

	public String getFiftyTwoWeekRange(){
		return this.fiftyTwoWeekRangeProperty().get();
	}
	public ReadOnlyStringProperty rateRatioProperty(){
		return this.rateRatio.getReadOnlyProperty();
	}

	public String getRateRatio(){
		return this.rateRatioProperty().get();
	}
	public ReadOnlyStringProperty volumeRatioProperty(){
		return this.volumeRatio.getReadOnlyProperty();
	}

	public String getVolumeRatio(){
		return this.volumeRatioProperty().get();
	}
	public ReadOnlyStringProperty sma10Property(){
		return this.sma10.getReadOnlyProperty();
	}

	public String getSma10(){
		return this.sma10Property().get();
	}
	public ReadOnlyStringProperty sma20Property(){
		return this.sma20.getReadOnlyProperty();
	}

	public String getSma20(){
		return this.sma20Property().get();
	}
	public ReadOnlyStringProperty sma50Property(){
		return this.sma50.getReadOnlyProperty();
	}

	public String getSma50(){
		return this.sma50Property().get();
	}
	public ReadOnlyStringProperty sma100Property(){
		return this.sma100.getReadOnlyProperty();
	}

	public String getSma100(){
		return this.sma100Property().get();
	}
	public ReadOnlyStringProperty sma250Property(){
		return this.sma250.getReadOnlyProperty();
	}

	public String getSma250(){
		return this.sma250Property().get();
	}
	public ReadOnlyStringProperty rsi10Property(){
		return this.rsi10.getReadOnlyProperty();
	}

	public String getRsi10(){
		return this.rsi10Property().get();
	}
	public ReadOnlyStringProperty rsi14Property(){
		return this.rsi14.getReadOnlyProperty();
	}

	public String getRsi14(){
		return this.rsi14Property().get();
	}
	public ReadOnlyStringProperty rsi20Property(){
		return this.rsi20.getReadOnlyProperty();
	}

	public String getRsi20(){
		return this.rsi20Property().get();
	}
	public ReadOnlyStringProperty macd8_17Property(){
		return this.macd8_17.getReadOnlyProperty();
	}

	public String getMacd8_17(){
		return this.macd8_17Property().get();
	}
	public ReadOnlyStringProperty macd12_25Property(){
		return this.macd12_25.getReadOnlyProperty();
	}

	public String getMacd12_25(){
		return this.macd12_25Property().get();
	}
	
	// multi-threading
	public final void startMonitoring() {
		stockService.restart();
	}

	public final void stopMonitoring() {
		stockService.cancel();
	}
	


	private void makeDoubleBinding( DoubleProperty property, Function<StockScrapedInfo, Double> propertyAccessor) {
	    property.bind(Bindings.createDoubleBinding(() -> {
	        StockScrapedInfo lastValue = stockService.getLastValue();
	        if (lastValue != null) {
	        	return propertyAccessor.apply(lastValue);
	        } else{ 
	        	return (Double) 0.0 ;
	        }
	    }, stockService.lastValueProperty()));
	}
	
	private void makeStringBinding(StringProperty property, Function<StockScrapedInfo, String> propertyAccessor) {
		   property.bind( Bindings.createStringBinding(() -> {
			   StockScrapedInfo lastValue = stockService.getLastValue();
		        if (lastValue == null) {
		            return "" ;
		        } else return propertyAccessor.apply(lastValue);
		    }, stockService.lastValueProperty()) );
		}
}
