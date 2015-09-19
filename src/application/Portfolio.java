package application;
import java.util.*;
import javafx.stage.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Application;
import java.net.URL;
import java.time.LocalDate;
import java.lang.*;
import javafx.scene.control.cell.*;
import javafx.collections.*;
import javafx.scene.control.MultipleSelectionModel.*;
import javafx.scene.control.SelectionModel.*;
import javafx.util.converter.*;
import javafx.scene.control.TableColumn.*;
import javafx.scene.control.TableCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;

import java.util.Collections.*;
import java.util.stream.Collectors;

public class Portfolio {

	HashMap<String, ArrayList<Trade>> portfolio;
	HashMap<String,ConsolidatedTrade> tempConsolidatedTradeHashMap;
    /**
     * The data as an observable list of Consolidated Trades.
     */
	
	private ObservableList<ConsolidatedTrade> observableListOfConsolidatedTrades = FXCollections.observableArrayList(consolidatedTrade -> 
		new Observable[]{
			consolidatedTrade.avgPriceProperty(),
			consolidatedTrade.stockTickerProperty(),
			consolidatedTrade.positionProperty(),
			consolidatedTrade.targetProperty(),
			consolidatedTrade.stopLossProperty(),
			consolidatedTrade.currentPriceProperty(),
			consolidatedTrade.targetCautionProperty(),
			consolidatedTrade.stopLossCautionProperty(),
			consolidatedTrade.uPnlProperty()
	});
	
	private final ReadOnlyDoubleWrapper sumUPnl;
	private final ReadOnlyDoubleWrapper sumPnl;
	private final ReadOnlyDoubleWrapper totalAssetVal;

	// Read in tableView and populate HashMap
	public Portfolio(ObservableList<Trade> observableListOfTrades, ObservableList<ConsolidatedTrade> observableListOfConsolidatedTrades){
		this.observableListOfConsolidatedTrades = observableListOfConsolidatedTrades;
		
		portfolio = new HashMap<String, ArrayList<Trade>>();
		tempConsolidatedTradeHashMap = new HashMap<String, ConsolidatedTrade>();
		
		// put trades from transaction log into hashmap
		for(Trade trade : observableListOfTrades){
			if(!portfolio.containsKey(trade.getStockTicker())){
				ArrayList<Trade> tradeHist = new ArrayList<Trade>();
				tradeHist.add(trade);
				portfolio.put(trade.getStockTicker(), tradeHist);
			} else{
				portfolio.get(trade.getStockTicker()).add(trade);
			}
		}
		
		// put existing Consolidated trades from existing Portfolio into hashMap
		if(observableListOfConsolidatedTrades != null){
			for(ConsolidatedTrade ct : observableListOfConsolidatedTrades){
					tempConsolidatedTradeHashMap.put(ct.getStockTicker(), ct);
			}
		}
		observableListOfConsolidatedTrades.clear();
		sortTrade();
		//displayDataStructure();
		generateConsolidatedTrade();
		displayDataStructure();
		
		// sum of Unrealised Pnl
		sumUPnl = new ReadOnlyDoubleWrapper();
		//DoubleBinding sumUPnlBinding = Bindings.createDoubleBinding(() -> 
		//	observableListOfConsolidatedTrades.stream().collect(Collectors.summingDouble(ConsolidatedTrade::getUPnl)));		
		
		DoubleBinding sumUPnlBinding = new DoubleBinding() {

		    {
		        bind(observableListOfConsolidatedTrades);
		        observableListOfConsolidatedTrades.forEach(consolidatedTrade -> bind(consolidatedTrade.uPnlProperty()));
		        observableListOfConsolidatedTrades.addListener((Change<? extends ConsolidatedTrade> change) -> {
		            while (change.next()) {
		                if (change.wasAdded()) {
		                    change.getAddedSubList().forEach(consolidatedTrade -> bind(consolidatedTrade.uPnlProperty()));
		                }
		                if (change.wasRemoved()) {
		                    change.getRemoved().forEach(consolidatedTrade -> bind(consolidatedTrade.uPnlProperty()));
		                }
		            }
		        });
		    }

		    @Override
		    public double computeValue() {
		        return observableListOfConsolidatedTrades.stream().collect(Collectors.summingDouble(ConsolidatedTrade::getUPnl));
		    }
		};
		
		sumUPnl.bind(sumUPnlBinding);
	
		// total value of Asset 
		totalAssetVal = new ReadOnlyDoubleWrapper();
		DoubleBinding totalAssetValBinding = new DoubleBinding() {

		    {
		        bind(observableListOfConsolidatedTrades);
		        observableListOfConsolidatedTrades.forEach(consolidatedTrade -> bind(consolidatedTrade.mktValueProperty()));
		        observableListOfConsolidatedTrades.addListener((Change<? extends ConsolidatedTrade> change) -> {
		            while (change.next()) {
		                if (change.wasAdded()) {
		                    change.getAddedSubList().forEach(consolidatedTrade -> bind(consolidatedTrade.mktValueProperty()));
		                }
		                if (change.wasRemoved()) {
		                    change.getRemoved().forEach(consolidatedTrade -> bind(consolidatedTrade.mktValueProperty()));
		                }
		            }
		        });
		    }

		    @Override
		    public double computeValue() {
		        return observableListOfConsolidatedTrades.stream().collect(Collectors.summingDouble(ConsolidatedTrade::getMktValue));
		    }
		};
		totalAssetVal.bind(totalAssetValBinding);
		
		// sum of realised Pnl
		sumPnl = new ReadOnlyDoubleWrapper();
		//DoubleBinding sumUPnlBinding = Bindings.createDoubleBinding(() -> 
		//	observableListOfConsolidatedTrades.stream().collect(Collectors.summingDouble(ConsolidatedTrade::getUPnl)));		
		
		DoubleBinding sumPnlBinding = new DoubleBinding() {

		    {
		        bind(observableListOfConsolidatedTrades);
		        observableListOfConsolidatedTrades.forEach(consolidatedTrade -> bind(consolidatedTrade.pnlProperty()));
		        observableListOfConsolidatedTrades.addListener((Change<? extends ConsolidatedTrade> change) -> {
		            while (change.next()) {
		                if (change.wasAdded()) {
		                    change.getAddedSubList().forEach(consolidatedTrade -> bind(consolidatedTrade.pnlProperty()));
		                }
		                if (change.wasRemoved()) {
		                    change.getRemoved().forEach(consolidatedTrade -> bind(consolidatedTrade.pnlProperty()));
		                }
		            }
		        });
		    }

		    @Override
		    public double computeValue() {
		        return observableListOfConsolidatedTrades.stream().collect(Collectors.summingDouble(ConsolidatedTrade::getPnl));
		    }
		};
		
		sumPnl.bind(sumPnlBinding);
	}
	
	public ReadOnlyDoubleProperty sumUPnlProperty() {
		return sumUPnl.getReadOnlyProperty();
	}
	 
	public double getSumUPnl() {
		return sumUPnlProperty().get();
	}
	
	public ReadOnlyDoubleProperty sumPnlProperty() {
		return sumPnl.getReadOnlyProperty();
	}
	 
	public double getSumPnl() {
		return sumPnlProperty().get();
	}
	
	public ReadOnlyDoubleProperty totalAssetValProperty(){
		return totalAssetVal.getReadOnlyProperty();
	}
	
	public double getTotalAssetVal(){
		return totalAssetValProperty().get();
	}
	 
	// display data structure
	public void displayDataStructure(){
		System.out.println("Display Data Structure:");
		for(Map.Entry<String,ArrayList<Trade>> entry : portfolio.entrySet()){
			System.out.println(entry.getKey() + " : " );
			for(Iterator<Trade> iterator = portfolio.get(entry.getKey()).iterator(); iterator.hasNext();){
				Trade tt = iterator.next();
				System.out.println(tt);
			}
			System.out.println("");
		}	
	}
	
	// Sort Arraylist for each key within the HashMap<String, ArrayList<Trade>>  
	public void sortTrade(){
		for(Map.Entry<String,ArrayList<Trade>> entry : portfolio.entrySet()){
			Collections.sort(entry.getValue());
		}	
	}
	
	// generate consolidated trade
	public void generateConsolidatedTrade(){
		for(Map.Entry<String,ArrayList<Trade>> entry : portfolio.entrySet()){
			// initialize 8 variables
			String stockTicker = entry.getKey();
			double avgPrice = 0;
			double mktVal = 0;
			double volumeHeld = 0;
			double volumeSold = 0;
			String position = "Open";
			ArrayList<Double> pnl_i = new ArrayList<Double>();
			
			for(Iterator<Trade> iterator = portfolio.get(entry.getKey()).iterator(); iterator.hasNext();){
				Trade tr = iterator.next();
				//System.out.println(tr);
				// if Buy
				if(tr.getBuySell().equals("Buy")){
					volumeHeld += tr.getVolume();
					mktVal += ((tr.getPrice() * tr.getVolume()) + tr.getTransactionFee() );
				} else if(tr.getBuySell().equals("Sell")){
					// if Sell, compute profit first
					pnl_i.add((tr.getPrice() - avgPrice) * tr.getVolume());
					volumeHeld -= tr.getVolume();
					volumeSold += tr.getVolume();
					mktVal -= ((tr.getPrice() * tr.getVolume()) - tr.getTransactionFee());
				}
				// update average Price
				if(volumeHeld!=0){
					avgPrice = mktVal/volumeHeld;
					//System.out.println("avg Price: " + avgPrice);
				}
			}
			
			// position
			if(volumeHeld == 0){
				position = "Closed";
			} else if(volumeHeld < 0){
				position = "Error";
			}
			// Create consolidated Trade
			if(tempConsolidatedTradeHashMap.containsKey(stockTicker)){
				ConsolidatedTrade tempCT = tempConsolidatedTradeHashMap.get(stockTicker);
				this.observableListOfConsolidatedTrades.add(new ConsolidatedTrade(stockTicker, avgPrice, volumeHeld, volumeSold, position, pnl_i,tempCT.getTarget(), tempCT.getStopLoss()));

			} else{
				this.observableListOfConsolidatedTrades.add(new ConsolidatedTrade(stockTicker, avgPrice, volumeHeld, volumeSold, position, pnl_i,0,0));
			}
		}
		//return consolidatedTrades;
	}
	
	// return list of consolidatedTrades 
	public ObservableList<ConsolidatedTrade> getConsolidatedTrades(){
		System.out.println("Observable List:");
		for(ConsolidatedTrade ct : this.observableListOfConsolidatedTrades){
			System.out.println(ct);
		}
		
		return this.observableListOfConsolidatedTrades;
	}
	
	
}
