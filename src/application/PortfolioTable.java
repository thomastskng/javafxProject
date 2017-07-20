package application;

import java.util.List;

import javafx.animation.PauseTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
	TabPane fxTabPaneLower;
	private FilteredList<ConsolidatedTrade> filteredListofConsolidatedTrades;
	private FilteredList<Trade> filterListOfTrades;
	private TableView<ConsolidatedTrade> portfolioTableView;
	
	public PortfolioTable(TabPane fxTabPaneUpper, ObservableList<ConsolidatedTrade> observableListOfConsolidatedTrades,String portfolioName, FilteredList<Trade> filterListOfTrades, TabPane fxTabPaneLower){
		this.fxTabPaneUpper = fxTabPaneUpper;
		this.filterListOfTrades = filterListOfTrades;
		this.fxTabPaneLower = fxTabPaneLower;
		
		int tabIndex = tabExist(portfolioName);
		
		if(tabIndex == -1){					
			Tab portfolioTab = new Tab();
			portfolioTableView = new TableView<ConsolidatedTrade>();
			portfolioTableView.getStyleClass().add("portfolio");
			/*
			portfolioTableView.setStyle("-fx-background-color: grey;"+
										"-fx-control-inner-background:  #3D3D3D;" + 
										"-fx-accent:  derive(-fx-control-inner-background, -40%);" + 
										"-fx-cell-hover-color:  derive(-fx-control-inner-background, -20%);" + 
										"-fx-text-fill:  #FFFFFF;" + 
										"-fx-font-weight:  bold;" + 
										"-fx-border-color:  skyblue;" + 
										"-fx-table-cell-border-color:  transparent;" + 
										"-fx-table-header-border-color: yellow;" + 
										"-fx-table-header-color: yellow;" + 
										"-fx-table-column-header-background: yellow;");
			*/
			
			portfolioTab.setContent(portfolioTableView);
			this.fxTabPaneUpper.getTabs().add(portfolioTab);
			
			portfolioTab.setClosable(false);
			portfolioTab.setText(portfolioName);
			portfolioTab.setId(portfolioName);
			portfolioTableView.setEditable(true);
					
			// enable multiple selection
			portfolioTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			
			
			TableColumn <ConsolidatedTrade, String> fxPortfolioTicker = new TableColumn <ConsolidatedTrade, String>("Ticker");
			TableColumn <ConsolidatedTrade, Number> fxPortfolioAvgCost = new TableColumn <ConsolidatedTrade, Number> ("Avg cost");
			TableColumn <ConsolidatedTrade, Number> fxPortfolioVolumeHeld = new TableColumn <ConsolidatedTrade, Number> ("Qty Held");
			TableColumn <ConsolidatedTrade, Number> fxPortfolioVolumeSold = new TableColumn <ConsolidatedTrade, Number> ("Qty Sold");
			TableColumn <ConsolidatedTrade, Number> fxPortfolioTarget = new TableColumn <ConsolidatedTrade, Number> ("Target");
			TableColumn <ConsolidatedTrade, Number> fxPortfolioStopLoss = new TableColumn <ConsolidatedTrade, Number> ("Stop Loss");
			TableColumn <ConsolidatedTrade, Number> fxPortfolioCurrentPrice = new TableColumn <ConsolidatedTrade, Number> ("Last");
			TableColumn <ConsolidatedTrade, Number> fxPortfolioUPnL = new TableColumn <ConsolidatedTrade, Number> ("UPnL");
			TableColumn <ConsolidatedTrade, Number> fxPortfolioPnL = new TableColumn <ConsolidatedTrade, Number> ("PnL");
			TableColumn <ConsolidatedTrade, String> fxPortfolioPosition = new TableColumn <ConsolidatedTrade, String>("Position");
			TableColumn <ConsolidatedTrade, String> fxPortfolioPnLHistory = new TableColumn <ConsolidatedTrade, String>("PnL History");
			TableColumn <ConsolidatedTrade, String> fxPortfolioStockName = new TableColumn <ConsolidatedTrade, String>("Name");
			TableColumn<ConsolidatedTrade,String> fxPortfolioPortfolioName = new TableColumn<ConsolidatedTrade,String>("Portfolio"); 

			fxPortfolioTicker.prefWidthProperty().bind(portfolioTableView.widthProperty().divide(13));
			fxPortfolioAvgCost.prefWidthProperty().bind(portfolioTableView.widthProperty().divide(8));
			fxPortfolioVolumeHeld.prefWidthProperty().bind(portfolioTableView.widthProperty().divide(10));
			fxPortfolioVolumeSold.prefWidthProperty().bind(portfolioTableView.widthProperty().divide(10));
			fxPortfolioTarget.prefWidthProperty().bind(portfolioTableView.widthProperty().divide(10));
			fxPortfolioStopLoss.prefWidthProperty().bind(portfolioTableView.widthProperty().divide(10));
			fxPortfolioCurrentPrice.prefWidthProperty().bind(portfolioTableView.widthProperty().divide(13));
			fxPortfolioUPnL.prefWidthProperty().bind(portfolioTableView.widthProperty().divide(9));
			fxPortfolioPnL.prefWidthProperty().bind(portfolioTableView.widthProperty().divide(9));
			fxPortfolioPosition.prefWidthProperty().bind(portfolioTableView.widthProperty().divide(13));
			fxPortfolioPnLHistory.prefWidthProperty().bind(portfolioTableView.widthProperty().divide(13));
			fxPortfolioStockName.prefWidthProperty().bind(portfolioTableView.widthProperty().divide(5));
			fxPortfolioPortfolioName.prefWidthProperty().bind(portfolioTableView.widthProperty().divide(7));

			portfolioTableView.getColumns().addAll(fxPortfolioStockName,fxPortfolioTicker,fxPortfolioAvgCost,fxPortfolioCurrentPrice,fxPortfolioUPnL,fxPortfolioPnL,fxPortfolioTarget,fxPortfolioStopLoss,fxPortfolioPosition,fxPortfolioVolumeHeld,fxPortfolioVolumeSold,fxPortfolioPnLHistory,fxPortfolioPortfolioName);

			for(TableColumn tc:portfolioTableView.getColumns()){
				tc.getStyleClass().add("portfolioCol");
			}
			
			// define setCellValueFactory
			fxPortfolioTicker.setCellValueFactory(cellData -> cellData.getValue().stockTickerProperty());
			fxPortfolioAvgCost.setCellValueFactory(cellData -> cellData.getValue().avgPriceProperty());
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
			fxPortfolioPortfolioName.setCellValueFactory(cellData -> cellData.getValue().portfolioProperty());

			// define setCellFactory
			fxPortfolioAvgCost.setCellFactory(col -> new NonEditableNumberCell<ConsolidatedTrade>());
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
	
			portfolioTableView.setRowFactory(tableView -> new AnimatedPortfolioTableRow<>(recentlyAddedTrade, ConsolidatedTrade::stockTickerProperty, ConsolidatedTrade::targetCautionProperty, ConsolidatedTrade::stopLossCautionProperty, ConsolidatedTrade::uPnlStateProperty, ConsolidatedTrade::pnlStateProperty, filterListOfTrades, fxTabPaneLower));
		}
		else{
			portfolioTableView = (TableView<ConsolidatedTrade>) fxTabPaneUpper.getTabs().get(tabIndex).getContent();
		}
		// filter consolidated trades by portfolio
		
		filteredListofConsolidatedTrades = new FilteredList<>(observableListOfConsolidatedTrades, consoliatedTrade -> consoliatedTrade.getPortfolio().equals(portfolioName));
		filteredListofConsolidatedTrades.setPredicate(consoliatedTrade -> consoliatedTrade.getPortfolio().equals(portfolioName));
		portfolioTableView.setItems(filteredListofConsolidatedTrades);
	


	}	
	
	// check if tab exists and its index
	public int tabExist(String portfolioName){
			for(int i=0; i < fxTabPaneUpper.getTabs().size(); i++){
				if(fxTabPaneUpper.getTabs().get(i).getId() !=null && fxTabPaneUpper.getTabs().get(i).getId().equals(portfolioName)){
					return i;
				}
			}
		return -1;
	}
}
