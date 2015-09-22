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


public class StockLookUp{
	
	private StringProperty stockTicker;
	private final ReadOnlyDoubleWrapper currentPrice;
	private final ReadOnlyDoubleWrapper lotSize;
    private final ReadOnlyStringWrapper stockName;
    private final ReadOnlyStringWrapper lastUpdate;
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
	
	
	public StockLookUp(int stockTicker){
		this.stockTicker = new SimpleStringProperty(String.format("%04d",stockTicker));
	
		// multi-threading current price	
		stockService.setPeriod(Duration.seconds(10));
		stockService.setOnFailed(e -> stockService.getException().printStackTrace());
		
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
		startMonitoring();
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
	
	// multi-threading
	public final void startMonitoring() {
		stockService.restart();
	}

	public final void stopMonitoring() {
		stockService.cancel();
	}
	
	public StockScrapedInfo getCurrentPriceFromAAStock() throws InterruptedException, IOException{
		String url = "http://www.aastocks.com/en/stock/detailquote.aspx?&symbol=" + getStockTicker();
		Document doc = Jsoup.connect(url).get();
		//System.out.println(doc);
		Elements elements = doc.select("ul:contains(Last) + ul>li>span");
		double cp = Double.parseDouble(elements.get(0).ownText());
		Elements sn = doc.select("title");
		String[] title = sn.get(0).ownText().split("\\(");
		String stockName = title[0];
		Elements lotSize = doc.select("td:contains(Lot Size) + td");
		double ls = Double.parseDouble(lotSize.get(0).ownText());
		System.out.println("Trade Ticker: " + getStockTicker() + ", cp: " + cp);
		Elements lastUpdateTime = doc.select("font:contains(Last Update) + font");
		System.out.println("lot size:" + ls);
		Elements suspension = doc.select("font:contains(Suspension)");
		String lastUpdate;
		if(suspension.text().contains("Suspension")){
			lastUpdate = "Suspension";
		} else{
			lastUpdate = lastUpdateTime.get(0).ownText();

		}
		return new StockScrapedInfo(stockName, cp, ls, lastUpdate);
	}

}
