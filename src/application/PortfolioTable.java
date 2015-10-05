package application;

import java.util.List;

import javafx.animation.PauseTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Duration;

public class PortfolioTable {

	TabPane fxTabPaneUpper;
	
	public PortfolioTable(TabPane fxTabPaneUpper, String portfolioName){
		this.fxTabPaneUpper = fxTabPaneUpper;
		Tab portfolioTab = new Tab();
		TableView<ConsolidatedTrade> portfolioTableView = new TableView<ConsolidatedTrade>();
		portfolioTab.setContent(portfolioTableView);
		this.fxTabPaneUpper.getTabs().add(portfolioTab);
		
		portfolioTab.setClosable(false);
		portfolioTab.setText(portfolioName);
		portfolioTableView.setEditable(true);
		
		// enable multiple selection
		portfolioTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		
		TableColumn <ConsolidatedTrade, String> fxPortfolioTicker = new TableColumn <ConsolidatedTrade, String>();
		TableColumn <ConsolidatedTrade, Number> fxPortfolioAvgPrice = new TableColumn <ConsolidatedTrade, Number> ();
		TableColumn <ConsolidatedTrade, Number> fxPortfolioVolumeHeld = new TableColumn <ConsolidatedTrade, Number> ();
		TableColumn <ConsolidatedTrade, Number> fxPortfolioVolumeSold = new TableColumn <ConsolidatedTrade, Number> ();
		TableColumn <ConsolidatedTrade, Number> fxPortfolioTarget = new TableColumn <ConsolidatedTrade, Number> ();
		TableColumn <ConsolidatedTrade, Number> fxPortfolioStopLoss = new TableColumn <ConsolidatedTrade, Number> ();
		TableColumn <ConsolidatedTrade, Number> fxPortfolioCurrentPrice = new TableColumn <ConsolidatedTrade, Number> ();
		TableColumn <ConsolidatedTrade, Number> fxPortfolioUPnL = new TableColumn <ConsolidatedTrade, Number> ();
		TableColumn <ConsolidatedTrade, Number> fxPortfolioPnL = new TableColumn <ConsolidatedTrade, Number> ();
		TableColumn <ConsolidatedTrade, String> fxPortfolioPosition = new TableColumn <ConsolidatedTrade, String>();
		TableColumn <ConsolidatedTrade, String> fxPortfolioPnLHistory = new TableColumn <ConsolidatedTrade, String>();
		TableColumn <ConsolidatedTrade, String> fxPortfolioStockName = new TableColumn <ConsolidatedTrade, String>();

		portfolioTableView.getColumns().addAll(fxPortfolioStockName,fxPortfolioTicker,fxPortfolioAvgPrice,fxPortfolioCurrentPrice,fxPortfolioUPnL,fxPortfolioPnL,fxPortfolioTarget,fxPortfolioStopLoss,fxPortfolioPosition,fxPortfolioVolumeHeld,fxPortfolioVolumeSold,fxPortfolioPnLHistory);
		
		// define setCellValueFactory
		fxPortfolioTicker.setCellValueFactory(cellData -> cellData.getValue().stockTickerProperty());
		fxPortfolioAvgPrice.setCellValueFactory(cellData -> cellData.getValue().avgPriceProperty());
		fxPortfolioVolumeHeld.setCellValueFactory(cellData -> cellData.getValue().volumeHeldProperty());
		fxPortfolioVolumeSold.setCellValueFactory(cellData -> cellData.getValue().volumeSoldProperty());
		fxPortfolioTarget.setCellValueFactory(cellData -> cellData.getValue().targetProperty());
		fxPortfolioStopLoss.setCellValueFactory(cellData -> cellData.getValue().stopLossProperty());
		fxPortfolioCurrentPrice.setCellValueFactory(cellData -> cellData.getValue().currentPriceProperty());
		fxPortfolioUPnL.setCellValueFactory(cellData -> cellData.getValue().uPnlProperty());
		fxPortfolioPnL.setCellValueFactory(cellData -> cellData.getValue().pnlProperty());
		fxPortfolioPosition.setCellValueFactory(cellData -> cellData.getValue().positionProperty());
		fxPortfolioPnLHistory.setCellValueFactory(new PropertyValueFactory<ConsolidatedTrade,String>("pnl_i"));
		fxPortfolioStockName.setCellValueFactory(cellData -> cellData.getValue().stockNameProperty());
		// define setCellFactory
		fxPortfolioAvgPrice.setCellFactory(col -> new NonEditableNumberCell<ConsolidatedTrade>());
		fxPortfolioVolumeHeld.setCellFactory(col -> new NonEditableNumberCell<ConsolidatedTrade>());
		fxPortfolioVolumeSold.setCellFactory(col -> new NonEditableNumberCell<ConsolidatedTrade>());
		fxPortfolioTarget.setCellFactory(col -> new EditingNumberCell<ConsolidatedTrade>("target-cell"));
		fxPortfolioStopLoss.setCellFactory(col -> new EditingNumberCell<ConsolidatedTrade>("stopLoss-cell"));
		//fxPortfolioCurrentPrice.setCellFactory(col -> new NonEditableNumberCell<ConsolidatedTrade>());
		fxPortfolioUPnL.setCellFactory(col -> new NonEditableNumberCell<ConsolidatedTrade>("uPnl-cell"));
		fxPortfolioPnL.setCellFactory(col -> new NonEditableNumberCell<ConsolidatedTrade>("pnl-cell"));
		fxPortfolioPnLHistory.setCellFactory(TextFieldTableCell.forTableColumn());
		//fxPortfolioPosition.setCellFactory(col -> new NonEditableNumberCell<ConsolidatedTrade>());
		fxPortfolioTicker.setCellFactory(col -> new NonEditableStockTickerCell<ConsolidatedTrade>());
		
		/*****************************************************
		 *	Table row highlighting for newly added trade
		 *****************************************************/
		
		/* 
		 * 1. Define an ObjectProperty<Trade> to represent the recently added Trade. 
		 *  Most of the time this will be null but when a new Trade is added to the list, 
		 *  it will be set to that new Trade:
		 */
		final ObjectProperty<ConsolidatedTrade> recentlyAddedTrade = new SimpleObjectProperty<>();
		/*
		 * 2. Register a ListListener with the table's items list. 
		 * When a new item is added to the list, update the recentlyAddedTrade. 
		 * Since you don't want the new Trade to be labeled as "new" indefinitely, 
		 * start a pause transition that will reset recentlyAddedTrade to null after 
		 * some delay (a second or two).:
		 */
		
		final Duration timeToGetOld = Duration.seconds(1.0);
		portfolioTableView.getItems().addListener((Change<? extends ConsolidatedTrade> change) ->{
			while(change.next()){
				if(change.wasAdded()){
					List<? extends ConsolidatedTrade> addedTrade = change.getAddedSubList();
					ConsolidatedTrade lastAddedTrade = addedTrade.get(addedTrade.size()-1);
					recentlyAddedTrade.set(lastAddedTrade);
					
					// set back to null after a short delay, unless changed since then:
		            PauseTransition agingTime = new PauseTransition(timeToGetOld);
		            agingTime.setOnFinished(event -> {
		                if (recentlyAddedTrade.get() == lastAddedTrade) {
		                    recentlyAddedTrade.set(null);
		                }
		            });
		            agingTime.play();
				}
			}
		});
		
		
		//portfolioTableView.setRowFactory(tableView -> new AnimatedPortfolioTableRow<>(recentlyAddedTrade, ConsolidatedTrade::stockTickerProperty, ConsolidatedTrade::targetCautionProperty, ConsolidatedTrade::stopLossCautionProperty, ConsolidatedTrade::uPnlStateProperty, ConsolidatedTrade::pnlStateProperty, observableListOfTrades, filterListOfTrades, fxTabPaneUpper));

	}
}
