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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.scene.control.TableCell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
public class ConsolidatedTrade implements Comparable<ConsolidatedTrade>{

	// non-user-defined instance variables
	private StringProperty stockTicker;
	private DoubleProperty avgPrice;
	private DoubleProperty volumeHeld;
	private DoubleProperty volumeSold;
	private StringProperty position;
	private DoubleProperty pnl;
	ArrayList<Double> pnl_i;
	public final ReadOnlyDoubleWrapper uPnl;
	private final ReadOnlyDoubleWrapper currentPrice;
	private final ReadOnlyIntegerWrapper uPnlState;
	private final ReadOnlyIntegerWrapper pnlState;
	public IntegerBinding pnlStateBinding ;
	public IntegerBinding uPnlStateBinding ;
	public DoubleBinding uPnlBinding;
	
	// user defined instance variables
	private DoubleProperty target;
	private DoubleProperty stopLoss;
	private ReadOnlyBooleanWrapper targetCaution;
	private ReadOnlyBooleanWrapper stopLossCaution;
	
	
	
	
	
	// Concurrent task to get price
	private final ScheduledService<Number> priceService = new ScheduledService<Number>() {
		@Override
	    public Task<Number> createTask(){
			return new Task<Number>() {
				@Override
				public Number call() throws InterruptedException, IOException {
					//System.out.println(counter++);					
					return getCurrentPriceFromAAStock();
					//return getCurrentPriceFromGoogle();
				}
			};
		}
	};
	
	
	
	public ConsolidatedTrade(String stockTicker, double avgPrice, double volumeHeld, double volumeSold, String position, ArrayList<Double> pnl_i, double target, double stopLoss){
		this.stockTicker 	= new SimpleStringProperty(String.format("%04d",Integer.parseInt(stockTicker)));
		this.avgPrice 		= new SimpleDoubleProperty(avgPrice);
		this.volumeHeld 	= new SimpleDoubleProperty(volumeHeld);
		this.volumeSold 	= new SimpleDoubleProperty(volumeSold);
		this.position 		= new SimpleStringProperty(position);
		this.pnl_i 			= pnl_i;
		this.pnl 			= new SimpleDoubleProperty(0);
		
		this.target 		= new SimpleDoubleProperty(target);
		this.stopLoss 		= new SimpleDoubleProperty(stopLoss);
		// multi-threading
		priceService.setPeriod(Duration.seconds(10));
		priceService.setOnFailed(e -> priceService.getException().printStackTrace());
		this.currentPrice 	= new ReadOnlyDoubleWrapper(0);
		this.currentPrice.bind(priceService.lastValueProperty());
		startMonitoring();
		this.uPnl			= new ReadOnlyDoubleWrapper();
		//uPnlBinding = Bindings.createDoubleBinding(() -> {
		//	System.out.println("current price: " + this.currentPrice.get()  + "avg price: "+ getAvgPrice() + "vol held: " + getVolumeHeld());
		//	return (currentPriceProperty().subtract(avgPriceProperty())).multiply(volumeHeldProperty());
		//});
		//uPnl.bind((this.currentPrice.subtract(this.avgPrice)).multiply(this.volumeHeld));
		uPnl.bind((currentPriceProperty().subtract(avgPriceProperty())).multiply(volumeHeldProperty()));
		targetCaution 		= new ReadOnlyBooleanWrapper();
		targetCaution.bind(this.currentPrice.greaterThanOrEqualTo(this.target));
		stopLossCaution 	= new ReadOnlyBooleanWrapper();
		stopLossCaution.bind(this.stopLoss.greaterThanOrEqualTo(this.currentPrice));
		pnlState = new ReadOnlyIntegerWrapper();
		uPnlState = new ReadOnlyIntegerWrapper();
		pnlStateBinding = Bindings.createIntegerBinding(() -> {
            if (getPnl() > 0) {
                return 1 ;
            } else if (getPnl() == 0) {
                return 0 ;
            } else {
                return -1 ;
            }
        }, pnlProperty());

        pnlState.bind(pnlStateBinding);
        
		uPnlStateBinding = Bindings.createIntegerBinding(() -> {
            if (getUPnl() > 0) {
                return 1 ;
            } else if (getUPnl() == 0) {
                return 0 ;
            } else {
                return -1 ;
            }
        }, uPnlProperty());

        uPnlState.bind(uPnlStateBinding);
		
	}
	
	// stock ticker
	public StringProperty stockTickerProperty(){
		return this.stockTicker;
	}
	
	public String getStockTicker(){
		return stockTickerProperty().get();
	}
	
	public void setStockTicker(String stockTicker){
		stockTickerProperty().set("-1");
		int st = Integer.parseInt(stockTicker);
		stockTickerProperty().set(String.format("%04d",st));
	}
	
	// avg Price
	public double getAvgPrice(){
		return this.avgPrice.get();
	}
	
	public DoubleProperty avgPriceProperty(){
		return this.avgPrice;
	}
	
	public void setAvgPrice(double avgPrice){
		this.avgPrice.set(avgPrice);
	}
	
	
	// volume held
	public double getVolumeHeld(){
		return this.volumeHeld.get();
	}
	
	public DoubleProperty volumeHeldProperty(){
		return this.volumeHeld;
	}
	
	public void setVolumeHeld(double volumeHeld){
		this.volumeHeld.set(volumeHeld);
	}
	
	
	// volume sold
	public double getVolumeSold(){
		return this.volumeSold.get();
	}
	
	public DoubleProperty volumeSoldProperty(){
		return this.volumeSold;
	}
	
	public void setVolumeSold(double volumeSold){
		this.volumeSold.set(volumeSold);
	}
	
	
	// position
	public String getPosition(){
		return this.position.getValue();
	}
	
	public StringProperty positionProperty(){
		return this.position;
	}
	
	public void setPosition(String position){
		this.position.set(position);
	}
	
	// pnl
	public Double getPnl(){
		Double val =  0.0;
		for(Double i : pnl_i){
			val += i;
		}
		this.pnl.set(val);
		return pnlProperty().getValue();
	}
	
	public DoubleProperty pnlProperty(){
		return this.pnl;
	}
	
	public void setPnl(Double pnl){
		pnlProperty().set(pnl);
	}
	
	// pnl
	public final Double getUPnl(){
		//this.uPnl.bind((this.currentPrice.subtract(this.avgPrice)).multiply(this.volumeHeld));
		//System.out.println("Unrealized: " + this.uPnl.getValue());
		return uPnlProperty().getValue();
	}
	
	public ReadOnlyDoubleProperty uPnlProperty(){
		return this.uPnl;
	}
	

	
	// target
	public double getTarget(){
		return this.target.get();
	}
	
	public DoubleProperty targetProperty(){
		return this.target;
	}
	
	public void setTarget(double target){
		this.target.set(target);
	}
	
	// stopLoss
	public double getStopLoss(){
		return this.stopLoss.get();
	}
	
	public DoubleProperty stopLossProperty(){
		return this.stopLoss;
	}
	
	public void setStopLoss(double stopLoss){
		this.stopLoss.set(stopLoss);
	}
	 
	public ReadOnlyDoubleProperty currentPriceProperty(){
		 return this.currentPrice.getReadOnlyProperty();
	}
	 
	public final double getCurrentPrice(){
		return currentPriceProperty().get();
	}
	
	public String getPnl_i(){
		return pnl_i.toString();
	}
	
	// targetCaution
	 public ReadOnlyBooleanProperty targetCautionProperty(){
		 return this.targetCaution.getReadOnlyProperty();
	 }
	 
	 public final Boolean getTargetCaution(){
		 return targetCautionProperty().getValue();
	 }

	// stopLossCaution
	 public ReadOnlyBooleanProperty stopLossCautionProperty(){
		 return this.stopLossCaution.getReadOnlyProperty();
	 }
	 
	 public final Boolean getStopLossCaution(){
		 return stopLossCautionProperty().getValue();
	 }
	 
	 public ReadOnlyIntegerProperty pnlStateProperty() {
		 return pnlState.getReadOnlyProperty();
	 }
	 
	 public int getpnlState() {
		 return pnlStateProperty().get();
	 }
	 
	 public ReadOnlyIntegerProperty uPnlStateProperty() {
		 return uPnlState.getReadOnlyProperty();
	 }
	 
	 public int getUPnlState() {
		 return uPnlStateProperty().get();
	 }
	 
	 
	// multi-threading
	public final void startMonitoring() {
		 priceService.restart();
	}

	public final void stopMonitoring() {
		priceService.cancel();
	}
	
	public double getCurrentPriceFromGoogle() throws InterruptedException, IOException{
		String url = "https://www.google.com.hk/finance?q=" + getStockTicker() + "&ei=yF14VYC4F4Wd0ASb64CoCw";
		Document doc = Jsoup.connect(url).get();
		Element content = doc.select("meta[itemprop=price]").first();
		double cp =  Double.parseDouble(content.attr("content"));
		return cp;
		}
	 
	 
	public double getCurrentPriceFromAAStock() throws InterruptedException, IOException{		
		String url = "http://www.aastocks.com/en/stock/detailquote.aspx?&symbol=" + getStockTicker();
		Document doc = Jsoup.connect(url).get();
		Elements elements = doc.select("ul:contains(Last) + ul>li>span");
		double cp = Double.parseDouble(elements.get(0).ownText());
		System.out.println("Consolidated Ticker: " + getStockTicker() + ", cp: " + cp);
		System.out.println("getCurrentPrice(): " + getCurrentPrice());
		return cp;
		}
	
	
	public String toString(){
		return getStockTicker() + ": " + 
				", avg Price: " + getAvgPrice() + 
				", Vol Held: " + getVolumeHeld() + 
				", Vol Sold: " + getVolumeSold() + 
				", Pos: " + getPosition() + 
				", pnl_i: " + pnl_i +
				", PnL: " + getPnl() +
				", uPnL: " + getUPnl() + 
				", Stop Loss caution: " + getStopLossCaution() + 
				", Target caution: " + getTargetCaution();
	}
	
	public int compareTo(ConsolidatedTrade o){
		if(getPosition() == null || o.getPosition() == null){
			return 0;
		}
		
		int i = o.getPosition().compareTo(getPosition());
		if(i != 0){
			//System.out.println("Comparing Position: " + i);
			return i;
		}

		i = o.getStopLossCaution().compareTo(getStopLossCaution());
		if(i != 0){
			//System.out.println("Comparing Stop Loss: " + i);
			return i;
		}
		
		i = o.getTargetCaution().compareTo(getTargetCaution()); 
		if(i!= 0){
			//System.out.println("Comparing Target: " + i);
			return i;
		}
		
		i = getStockTicker().compareTo(o.getStockTicker());
		return i;
	}
	
	public int hashCode(){
		return (int) Integer.parseInt(getStockTicker())*31; 
	}
	
	public boolean equals(ConsolidatedTrade o){
		if(o == this){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		return getStockTicker().equals(o.getStockTicker()) && getAvgPrice() == o.getAvgPrice() && getVolumeHeld() == o.getVolumeHeld() && getVolumeSold() == o.getVolumeSold() && getPnl() == o.getPnl(); 
	}
	
	public static void main(String[] args) throws InterruptedException, IOException{
		ConsolidatedTrade ct = new ConsolidatedTrade("0001", 2,2,2,"Open", new ArrayList<Double>(),2,2);
		System.out.println(ct.getCurrentPriceFromGoogle());
		System.out.println(ct.getCurrentPriceFromAAStock());

	}
}
