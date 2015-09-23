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

public class WatchListStock {
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
				public StockScrapedInfo call() throws InterruptedException, IOException {					
					return getCurrentPriceFromAAStock();
					//return getCurrentPriceFromGoogle();
				}
			};
		}
	};

	public WatchListStock(String condition, int stockTicker, double target){

		this.condition = new SimpleStringProperty(condition);
		this.stockTicker = new SimpleStringProperty(String.format("%04d",stockTicker));
		this.target = new SimpleDoubleProperty(target);
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
		stockTickerProperty().set("-1");
		int st = Integer.parseInt(stockTicker);
		stockTickerProperty().set(String.format("%04d",st));
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
		Elements lastUpdateTime = doc.select("font:contains(Last Update) + font");
		Elements suspension = doc.select("font:contains(Suspension)");
		String lastUpdate;
		if(suspension.text().contains("Suspension")){
			lastUpdate = "Suspension";
		} else{
			lastUpdate = lastUpdateTime.get(0).ownText();

		}
		System.out.println("WatchList Stock: " + stockName + ", cp: " + cp + ", lot size:" + ls + ", last Update: " + lastUpdate);
		return new StockScrapedInfo(stockName, cp, ls, lastUpdate);
	}
}
