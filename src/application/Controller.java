package application;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.time.LocalDate;
import java.io.IOException;
import java.lang.*;
import javafx.scene.control.cell.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.*;
import javafx.scene.control.MultipleSelectionModel.*;
import javafx.scene.control.SelectionModel.*;
import javafx.util.converter.*;
import javafx.scene.control.TableColumn.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.scene.input.MouseEvent;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import java.util.function.Function;
//import com.sun.javafx.css.converters.StringConverter;
import javafx.collections.transformation.SortedList;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.*;
import javafx.css.PseudoClass;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.scene.Cursor;
import javafx.util.*;
import javafx.util.StringConverter;
import java.text.ParseException;
import java.util.regex.Pattern;

public class Controller implements Initializable{
	
	@FXML

	// History Log 
	public TableView<Trade> fxTransactionLog;
	public TableColumn<Trade, LocalDate> fxTransactionLogTransactionDate;
	public TableColumn<Trade, String> fxTransactionLogStockTicker;
	public TableColumn<Trade, String> fxTransactionLogBuySell;
	public TableColumn<Trade, Number> fxTransactionLogPrice;
	public TableColumn<Trade, Number> fxTransactionLogVolume;
	public TableColumn<Trade, Number> fxTransactionLogTransactionFee;
	public TableColumn<Trade,Number> fxTransactionLogCurrentPrice;
	public TableColumn<Trade,String> fxTransactionLogRemarks;
	public TableColumn<Trade,String> fxTransactionLogStockName;
	
	// Consolidated Trades
	public TableView<ConsolidatedTrade> fxPortfolio;
	public TableColumn <ConsolidatedTrade, String> fxPortfolioTicker;
	public TableColumn <ConsolidatedTrade, Number> fxPortfolioAvgPrice;
	public TableColumn <ConsolidatedTrade, Number> fxPortfolioVolumeHeld;
	public TableColumn <ConsolidatedTrade, Number> fxPortfolioVolumeSold;
	public TableColumn <ConsolidatedTrade, Number> fxPortfolioTarget;
	public TableColumn <ConsolidatedTrade, Number> fxPortfolioStopLoss;
	public TableColumn <ConsolidatedTrade, Number> fxPortfolioCurrentPrice;
	public TableColumn <ConsolidatedTrade, Number> fxPortfolioUPnL;
	public TableColumn <ConsolidatedTrade, Number> fxPortfolioPnL;
	public TableColumn <ConsolidatedTrade, String> fxPortfolioPosition;
	public TableColumn <ConsolidatedTrade, String> fxPortfolioPnLHistory;
	public TableColumn <ConsolidatedTrade, String> fxPortfolioStockName;

	// WatchList
	
	public TableView<WatchListStock> fxWatchList;
	public TableColumn<WatchListStock,String> fxWatchListTicker;
	public TableColumn<WatchListStock,Number> fxWatchListCurrentPrice;
	public TableColumn<WatchListStock,Number> fxWatchListTarget;
	public TableColumn<WatchListStock,String> fxWatchListStockName;
	public TableColumn<WatchListStock,String> fxWatchListCondition;
	
    public Portfolio initialPortfolio;
    public StockLookUp lookUpTicker;
    
    // Button
	// Label
    public Label fxLabel1;
    public Label fxLabel2;
    public Label fxLabel3;
    public Label fxLabel4;

    // Titled pane for looking up and displaying stock info 
    public TitledPane fxStockLookUp;
    // Vertical Box - Input Stock Calculator
    public VBox fxStockCalculator;
    // Horizontal Box - Watch List panel for adding new stocks
	public HBox fxWatchListPanel;
	
    private Pattern partialInputPattern = Pattern.compile("[-]?[0-9]*(\\.[0-9]*)?");
	
	private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");
	
	
    /**
     * The data as an observable list of Trade.
     */
	private static ObservableList<Trade> observableListOfTrades = FXCollections.observableArrayList(trade ->
		new Observable[]{
				trade.transactionDateProperty(),
				trade.stockTickerProperty(),
				trade.buySellProperty(),
				trade.volumeProperty(),
				//trade.stockNameProperty(),
				trade.priceProperty()

		}
	);
	
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
	
    /**
     * The data as an observable list of WatchListStocks.
     */
	private ObservableList<WatchListStock> observableListOfWatchListStocks = FXCollections.observableArrayList(watchListStock -> 
		new Observable[]{
			watchListStock.currentPriceProperty(),
			watchListStock.conditionProperty(),
			watchListStock.stockTickerProperty(),
			watchListStock.targetProperty(),
			watchListStock.alertProperty(),
			watchListStock.currentPriceProperty()
	});
	
	
	// Callback for setCellFactory
	Callback<TableColumn<Trade,Number>, TableCell<Trade,Number>> NumberCellFactory =
            new Callback<TableColumn<Trade,Number>, TableCell<Trade,Number>>() {
                public TableCell call(TableColumn<Trade,Number> p) {
                   //return new EditingNumberCell("price-cell");
                	return new EditingDoubleCell("price-cell");
                }
   };
	
   Callback<TableColumn<Trade,String>, TableCell<Trade,String>> stockTickerCellFactory =
            new Callback<TableColumn<Trade,String>, TableCell<Trade,String>>() {
                public TableCell call(TableColumn<Trade,String> p) {
                   return new EditingStockTickerCell("");
                }
   };
  
   /**
    * Returns the data as an observable list of Persons. 
    * @return
    */
   public static ObservableList<Trade> getObservableListOfTrades() {
       return observableListOfTrades;
   } 
   
            

	
	public static boolean isFieldEmpty(TextField textField){
		if(textField.getText() != null && ! textField.getText().trim().isEmpty()){
			return false;
		} else{
			return true;
		}
	}
	
	public static boolean isTypeCorrect(TextField tfStockTicker, TextField tfPrice, TextField tfVolume, boolean confirmedClose){
		try{
			int stockTicker = Integer.parseInt(tfStockTicker.getText());
			double price = Double.parseDouble(tfPrice.getText());
			double volume = Double.parseDouble(tfVolume.getText());
			confirmedClose = true;
			return confirmedClose;
		} catch(NumberFormatException e){
			AlertBox.display("Invalid Input", "Error: Invalid Input. Please enter again. ");
			return confirmedClose;
		}
	}

	public void deleteTrade(){
		ObservableList<Trade> tradeSelected;
		tradeSelected = fxTransactionLog.getSelectionModel().getSelectedItems();
		for(Trade ttt : tradeSelected){
			ttt.stopMonitoring();
		}
		observableListOfTrades.removeAll(tradeSelected);
	}
	
	public void deleteWLStock(){
		ObservableList<WatchListStock> wlStockSelected;
		wlStockSelected = fxWatchList.getSelectionModel().getSelectedItems();
		for(WatchListStock wlStock : wlStockSelected){
			wlStock.stopMonitoring();
		}
		observableListOfWatchListStocks.removeAll(wlStockSelected);
	}
	
	// initialise fxTransactionLog 
	public void initializeFxTransactionLog(){	
		observableListOfTrades.addAll(
				new Trade(BuySell.Buy, LocalDate.now().plusDays(3),1,50,5)
				//,new Trade(BuySell.Sell, LocalDate.now().plusDays(1), 1,25,3),
				//new Trade(BuySell.Sell, LocalDate.now().plusDays(4), 1,50,3),
				//new Trade(BuySell.Sell, LocalDate.now().plusDays(2),1,100,3),
				//new Trade(BuySell.Buy, LocalDate.now(),1,100,2),
				//new Trade(BuySell.Sell, LocalDate.now(),1,10000,9999),
				//new Trade(BuySell.Buy, LocalDate.now(),1,100,2)
		);
		
		
		// Add listener to observable list to listen to ALL changes
		observableListOfTrades.addListener(new ListChangeListener<Trade>() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                System.out.println("Detected a change! ");
				//for(Trade ttt : fxTransactionLog.getItems()){
				for(Trade ttt : observableListOfTrades){
					System.out.println( "Change: " + ttt);
				}
				SortedList<Trade> sortedTrades = new SortedList<Trade>(observableListOfTrades);
				sortedTrades.setComparator(new Comparator<Trade>(){
					@Override
					public int compare(Trade trade1, Trade trade2){
						return trade1.compareTo(trade2);
					}
				});
				fxTransactionLog.setItems(sortedTrades);

				refreshPortfolio();
				//initialPortfolio.displayDataStructure();
				//initialPortfolio.getConsolidatedTrades();

            }
        });
	
		fxTransactionLog.setEditable(true);
		SortedList<Trade> sortedTrades = new SortedList<Trade>(observableListOfTrades);
		sortedTrades.setComparator(new Comparator<Trade>(){
			@Override
			public int compare(Trade trade1, Trade trade2){
				return trade1.compareTo(trade2);
			}
		});
		
		//sortedTrades.comparatorProperty().bind(fxTransactionLog.comparatorProperty());
		fxTransactionLog.setItems(sortedTrades);
				
		// enable multiple selection
		fxTransactionLog.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		// define setCellValueFactory
		fxTransactionLogTransactionDate.setCellValueFactory(cellData -> cellData.getValue().transactionDateProperty());
		fxTransactionLogStockTicker.setCellValueFactory(cellData -> cellData.getValue().stockTickerProperty());
		fxTransactionLogBuySell.setCellValueFactory(cellData -> cellData.getValue().buySellProperty());
		fxTransactionLogPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
		fxTransactionLogVolume.setCellValueFactory(cellData -> cellData.getValue().volumeProperty());
		fxTransactionLogTransactionFee.setCellValueFactory(cellData -> cellData.getValue().transactionFeeProperty());
		fxTransactionLogRemarks.setCellValueFactory(cellData -> cellData.getValue().remarksProperty());
		fxTransactionLogCurrentPrice.setCellValueFactory(cellData -> cellData.getValue().currentPriceProperty());
		fxTransactionLogStockName.setCellValueFactory(cellData -> cellData.getValue().stockNameProperty());
		
		// define setCellFactory
		fxTransactionLogTransactionDate.setCellFactory(col -> new DateEditingCell());
		fxTransactionLogStockTicker.setCellFactory(stockTickerCellFactory);
	    fxTransactionLogPrice.setCellFactory(col -> new EditingNumberCell<Trade>("price-cell"));
	    fxTransactionLogVolume.setCellFactory(col -> new EditingNumberCell<Trade>(""));
		fxTransactionLogTransactionFee.setCellFactory(col -> new NonEditableNumberCell<Trade>());				
		fxTransactionLogRemarks.setCellFactory(TextFieldTableCell.forTableColumn());

		// initialise buySell choicebox
		ObservableList<String> buySellList = FXCollections.observableArrayList(new String("Buy"), new String("Sell"));
		fxTransactionLogBuySell.setCellFactory(ChoiceBoxTableCell.forTableColumn(buySellList));
		fxTransactionLogBuySell.setOnEditCommit(
				new EventHandler<CellEditEvent<Trade, String>>() {
					@Override
					public void handle(CellEditEvent<Trade, String> t) {
						((Trade) t.getTableView().getItems().get(t.getTablePosition().getRow())).setBuySell(t.getNewValue());
					}
				});
		
		// initialise Transaction Date
		//fxTransactionLogTransactionDate.setCellValueFactory(new PropertyValueFactory<Trade,LocalDate>("transactionDate"));

		
		//initialise Stock Ticker
		//fxTransactionLogStockTicker.setCellValueFactory(new PropertyValueFactory<Trade,String>("stockTicker"));
		//fxTransactionLogStockTicker.setCellFactory(TextFieldTableCell.forTableColumn());		

		
		fxTransactionLogStockTicker.setOnEditCommit(
				new EventHandler<CellEditEvent<Trade, String>>() {
					@Override
					public void handle(CellEditEvent<Trade, String> t) {
						((Trade) t.getTableView().getItems().get(t.getTablePosition().getRow())).setStockTicker(t.getNewValue());
					}
				});
		
		
		// initialise price
		/*
		//fxTransactionLogPrice.setCellValueFactory(new PropertyValueFactory<Trade,Double>("price"));
		//fxTransactionLogPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
		//fxTransactionLogPrice.setCellFactory(TextFieldTableCell.<Trade,Double>forTableColumn(new DoubleStringConverter()));
		
		Callback<TableColumn<Trade,Double>, TableCell<Trade,Double>> PriceCellFactory =
	             new Callback<TableColumn<Trade,Double>, TableCell<Trade,Double>>() {
	                 public TableCell call(TableColumn<Trade,Double> p) {
	                    //return new EditingNumberCell();
	                	 return new EditingDoubleCell("price-cell");
	                 }
	    };
	    fxTransactionLogPrice.setCellFactory(PriceCellFactory);
	    		fxTransactionLogPrice.setOnEditCommit(
				new EventHandler<CellEditEvent<Trade, Double>>() {
					@Override
					public void handle(CellEditEvent<Trade, Double> t) {
						((Trade) t.getTableView().getItems().get(t.getTablePosition().getRow())).setPrice(t.getNewValue());
					}
				});
	    */
		
		// initialise price
		// API: DoubleProperty implements ObservableValue<Number>, not ObservableValue<Double>
		//fxTransactionLogPrice.setCellValueFactory(new PropertyValueFactory<Trade,Number>("price"));
	             

		// initialise volume
		//fxTransactionLogVolume.setCellValueFactory(new PropertyValueFactory<Trade,Double>("volume"));

		
		// initialise transaction fee
		
		// Drag and Drop TableRow
		/*
		fxTransactionLog.setRowFactory(tv -> {
			TableRow<Trade> row = new TableRow<>();
			
			row.setOnDragDetected(event -> {
				if(! row.isEmpty()){
					Integer index = row.getIndex();
					Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
	                db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
				}
			});
			
			row.setOnDragOver(event -> {
	                Dragboard db = event.getDragboard();
	                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
	                    if (row.getIndex() != ((Integer)db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
	                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
	                        event.consume();
	                    }
	                }
			});

			row.setOnDragDropped(event -> {
	                Dragboard db = event.getDragboard();
	                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
	                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
	                    Trade draggedTrade = fxTransactionLog.getItems().remove(draggedIndex);

	                    int dropIndex ; 

	                    if (row.isEmpty()) {
	                        dropIndex = fxTransactionLog.getItems().size() ;
	                    } else {
	                        dropIndex = row.getIndex();
	                    }

	                    fxTransactionLog.getItems().add(dropIndex, draggedTrade);
	                    event.setDropCompleted(true);
	                    fxTransactionLog.getSelectionModel().select(dropIndex);
	                    event.consume();
	                }
	            });
	            return row ;	
		});
		*/
		
		/*****************************************************
		 *	Table row highlighting for newly added trade
		 *****************************************************/
		
		/* 
		 * 1. Define an ObjectProperty<Trade> to represent the recently added Trade. 
		 *  Most of the time this will be null but when a new Trade is added to the list, 
		 *  it will be set to that new Trade:
		 */
		final ObjectProperty<Trade> recentlyAddedTrade = new SimpleObjectProperty<>();
		/*
		 * 2. Register a ListListener with the table's items list. 
		 * When a new item is added to the list, update the recentlyAddedTrade. 
		 * Since you don't want the new Trade to be labeled as "new" indefinitely, 
		 * start a pause transition that will reset recentlyAddedTrade to null after 
		 * some delay (a second or two).:
		 */
		
		final Duration timeToGetOld = Duration.seconds(1.0);
		fxTransactionLog.getItems().addListener((Change<? extends Trade> change) ->{
			while(change.next()){
				if(change.wasAdded()){
					List<? extends Trade> addedTrade = change.getAddedSubList();
					Trade lastAddedTrade = addedTrade.get(addedTrade.size()-1);
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
		
		/*Create a row factory for the table. This row factory returns a custom TableRow. 
		 * This custom TableRow creates a BooleanBinding which is set to true 
		 * if this row represents a recently-added row. 
		 * (This will be true if the row's item is not null and 
		 * is equal to the recentlyAddedPerson defined above.)
		 * 
		 * To actually implement the highlighting, I would just use a CSS PseudoClass. 
		 * The implementation of the highlighting then becomes trivial; 
		 * just set the pseudoclass state to the value in the 
		 * BooleanBinding defined in the TableRow implementation.
		 * */
		
		final PseudoClass newTradePseudoClass = PseudoClass.getPseudoClass("new");
		/*
		fxTransactionLog.setRowFactory(tableView -> new TableRow<Trade>(){
			// Bindings API uses weak listeners, so this needs to be a field to
	        // make sure it stays in scope as long as the TableRow is in scope.
	        private final BooleanBinding itemIsNewTrade = Bindings.isNotNull(itemProperty()).and(Bindings.equal(itemProperty(), recentlyAddedTrade));

	        {
	            // anonymous constructor:
	            itemIsNewTrade.addListener((obs, wasNew, isNew) -> pseudoClassStateChanged(newTradePseudoClass, isNew));
	        }	
		});
		*/
		
		fxTransactionLog.setRowFactory(tableView -> new AnimatedTransactionLogTableRow<>(recentlyAddedTrade
							,Trade::cautionProperty
							));

	}
	
	// initialise Portfolio
	public void initializeFXPortfolio(){
		refreshPortfolio();
		/*
        SortedList<ConsolidatedTrade> sortedTrades = new SortedList<ConsolidatedTrade>(initialPortfolio.getConsolidatedTrades());
		sortedTrades.setComparator(new Comparator<ConsolidatedTrade>(){
			@Override
			public int compare(ConsolidatedTrade trade1, ConsolidatedTrade trade2){
				return trade1.compareTo(trade2);
			}
		});
		
		fxPortfolio.setItems(sortedTrades);
		*/
		
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
		fxPortfolio.getItems().addListener((Change<? extends ConsolidatedTrade> change) ->{
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
		
		
		fxPortfolio.setRowFactory(tableView -> new AnimatedPortfolioTableRow<>(recentlyAddedTrade, ConsolidatedTrade::targetCautionProperty, ConsolidatedTrade::stopLossCautionProperty, ConsolidatedTrade::uPnlStateProperty, ConsolidatedTrade::pnlStateProperty));

	}
	
	// refresh Portfolio whenever there is any change in the Transaction Log
	public void refreshPortfolio(){
		// Stop monitoring currentPriceProperty() when there is any change 
		if(initialPortfolio != null){
			for(ConsolidatedTrade ct : initialPortfolio.getConsolidatedTrades()){
				ct.stopMonitoring();
			}
		}
		
		// Create a new portfolio whenever there is any change, which will also start monitoring currentPriceProperty().
        initialPortfolio = new Portfolio(observableListOfTrades, observableListOfConsolidatedTrades);
        fxPortfolio.setEditable(true);
		
        SortedList<ConsolidatedTrade> sortedTrades = new SortedList<ConsolidatedTrade>(initialPortfolio.getConsolidatedTrades());
		sortedTrades.setComparator(new Comparator<ConsolidatedTrade>(){
			@Override
			public int compare(ConsolidatedTrade trade1, ConsolidatedTrade trade2){
				return trade1.compareTo(trade2);
			}
		});
		
		System.out.println("***************");
		//initialPortfolio.displayDataStructure();

		fxPortfolio.setItems(sortedTrades);
		System.out.println("Item still in Transaction Log: ");
		for(Trade ttt : fxTransactionLog.getItems()){
		//for(Trade ttt : observableListOfTrades){
			System.out.println( "Change: " + ttt);
		}		
	}

	// initialise fxWatchList 
	public void initializeFxWatchList(){	
		observableListOfWatchListStocks.addAll(
				new WatchListStock("Last >= Target",1,23.0)
		);
		
		fxWatchList.setItems(observableListOfWatchListStocks);
	
		fxWatchList.setEditable(true);

				
		// enable multiple selection
		fxWatchList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		// define setCellValueFactory
		fxWatchListTicker.setCellValueFactory(cellData -> cellData.getValue().stockTickerProperty());
		fxWatchListCurrentPrice.setCellValueFactory(cellData -> cellData.getValue().currentPriceProperty());
		fxWatchListTarget.setCellValueFactory(cellData -> cellData.getValue().targetProperty());
		fxWatchListStockName.setCellValueFactory(cellData -> cellData.getValue().stockNameProperty());
		fxWatchListCondition.setCellValueFactory(cellData -> cellData.getValue().conditionProperty());
		Label fxWatchListConditionLabel = new Label("Condition");
		fxWatchListConditionLabel.setTooltip(new Tooltip("Alert when Condition is met"));
		fxWatchListCondition.setGraphic(fxWatchListConditionLabel);
		fxWatchList.setTableMenuButtonVisible(true);
		
		// define setCellFactory
		fxWatchListTicker.setCellFactory(col -> new EditingStockTickerCell<WatchListStock>("wlTicker-cell"));
		fxWatchListTarget.setCellFactory(col -> new EditingNumberCell<WatchListStock>(""));
		ObservableList<String> conditionList = FXCollections.observableArrayList(new String("Last >= Target"), new String("Last <= Target"));

		fxWatchListCondition.setCellFactory(ChoiceBoxTableCell.forTableColumn(conditionList));
		fxWatchListCondition.setOnEditCommit(
			new EventHandler<CellEditEvent<WatchListStock, String>>() {
				@Override
				public void handle(CellEditEvent<WatchListStock, String> t) {
					((WatchListStock) t.getTableView().getItems().get(t.getTablePosition().getRow())).setCondition(t.getNewValue());
				}
			});
	
		fxWatchListTicker.setOnEditCommit(
			new EventHandler<CellEditEvent<WatchListStock, String>>() {
				@Override
				public void handle(CellEditEvent<WatchListStock, String> t) {
					((WatchListStock) t.getTableView().getItems().get(t.getTablePosition().getRow())).setStockTicker(t.getNewValue());
				}
			});
		
		
		// Drag and Drop TableRow
		/*
		fxTransactionLog.setRowFactory(tv -> {
			TableRow<Trade> row = new TableRow<>();
			
			row.setOnDragDetected(event -> {
				if(! row.isEmpty()){
					Integer index = row.getIndex();
					Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
	                db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
				}
			});
			
			row.setOnDragOver(event -> {
	                Dragboard db = event.getDragboard();
	                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
	                    if (row.getIndex() != ((Integer)db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
	                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
	                        event.consume();
	                    }
	                }
			});

			row.setOnDragDropped(event -> {
	                Dragboard db = event.getDragboard();
	                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
	                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
	                    Trade draggedTrade = fxTransactionLog.getItems().remove(draggedIndex);

	                    int dropIndex ; 

	                    if (row.isEmpty()) {
	                        dropIndex = fxTransactionLog.getItems().size() ;
	                    } else {
	                        dropIndex = row.getIndex();
	                    }

	                    fxTransactionLog.getItems().add(dropIndex, draggedTrade);
	                    event.setDropCompleted(true);
	                    fxTransactionLog.getSelectionModel().select(dropIndex);
	                    event.consume();
	                }
	            });
	            return row ;	
		});
		*/
		
		/*****************************************************
		 *	Table row highlighting for newly added trade
		 *****************************************************/
		
		/* 
		 * 1. Define an ObjectProperty<Trade> to represent the recently added Trade. 
		 *  Most of the time this will be null but when a new Trade is added to the list, 
		 *  it will be set to that new Trade:
		 */
		final ObjectProperty<WatchListStock> recentlyAddedWLStock = new SimpleObjectProperty<>();
		/*
		 * 2. Register a ListListener with the table's items list. 
		 * When a new item is added to the list, update the recentlyAddedTrade. 
		 * Since you don't want the new Trade to be labeled as "new" indefinitely, 
		 * start a pause transition that will reset recentlyAddedTrade to null after 
		 * some delay (a second or two).:
		 */
		
		final Duration timeToGetOld = Duration.seconds(1.0);
		fxWatchList.getItems().addListener((Change<? extends WatchListStock> change) ->{
			while(change.next()){
				if(change.wasAdded()){
					List<? extends WatchListStock> addedWLStock = change.getAddedSubList();
					WatchListStock lastAddedWLStock = addedWLStock.get(addedWLStock.size()-1);
					recentlyAddedWLStock.set(lastAddedWLStock);
					
					// set back to null after a short delay, unless changed since then:
		            PauseTransition agingTime = new PauseTransition(timeToGetOld);
		            agingTime.setOnFinished(event -> {
		                if (recentlyAddedWLStock.get() == lastAddedWLStock) {
		                	recentlyAddedWLStock.set(null);
		                }
		            });
		            agingTime.play();
				}
			}
		});
		
		/*Create a row factory for the table. This row factory returns a custom TableRow. 
		 * This custom TableRow creates a BooleanBinding which is set to true 
		 * if this row represents a recently-added row. 
		 * (This will be true if the row's item is not null and 
		 * is equal to the recentlyAddedPerson defined above.)
		 * 
		 * To actually implement the highlighting, I would just use a CSS PseudoClass. 
		 * The implementation of the highlighting then becomes trivial; 
		 * just set the pseudoclass state to the value in the 
		 * BooleanBinding defined in the TableRow implementation.
		 * */
		
		final PseudoClass newWLStockPseudoClass = PseudoClass.getPseudoClass("new");
		/*
		fxTransactionLog.setRowFactory(tableView -> new TableRow<Trade>(){
			// Bindings API uses weak listeners, so this needs to be a field to
	        // make sure it stays in scope as long as the TableRow is in scope.
	        private final BooleanBinding itemIsNewTrade = Bindings.isNotNull(itemProperty()).and(Bindings.equal(itemProperty(), recentlyAddedTrade));

	        {
	            // anonymous constructor:
	            itemIsNewTrade.addListener((obs, wasNew, isNew) -> pseudoClassStateChanged(newTradePseudoClass, isNew));
	        }	
		});
		*/
		
		fxWatchList.setRowFactory(tableView -> new AnimatedWatchListTableRow<WatchListStock>(recentlyAddedWLStock ,WatchListStock::alertProperty,WatchListStock::conditionProperty));
		
	}
	

	
	// initialise Stock Calculator
	public void initialiseStockCalculator(){
		// Transaction Date: Calendar
		// http://code.makery.ch/blog/javafx-8-date-picker/
		DatePicker datepicker = new DatePicker();
		datepicker.setValue(LocalDate.now());
		datepicker.setOnAction(e -> {
		LocalDate date = datepicker.getValue();
			//System.out.println("Selected Date: " + date);
		});
		Label labelDatePicker = new Label("Date:");
		datepicker.setPrefWidth(130);

		// capture user input: StockTicker, volume, price 
		// Stock Ticker
		Label labelStockTicker = new Label("Ticker:");
		TextField tfStockTicker = new TextField();
		tfStockTicker.setPrefWidth(100);
		tfStockTicker.setPromptText("e.g. 1,328");
			
		NumberFormat nf = NumberFormat.getIntegerInstance();        

		// add filter to allow for typing only integer
		tfStockTicker.setTextFormatter( new TextFormatter<>( c -> {
			if (c.getControlNewText().isEmpty()) {
				return c;
	        }
			ParsePosition parsePosition = new ParsePosition( 0 );
            Object object = nf.parse( c.getControlNewText(), parsePosition );

            if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() || c.getControlNewText().length() > 4){
                return null;
            }
            else
            {
                return c;
            }
        }));

			
		Timeline delayDisplayStockInfo = new Timeline(new KeyFrame(Duration.seconds(1.5), new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent actionEvent) {
            	lookUpTicker = new StockLookUp(Integer.parseInt(tfStockTicker.getText()));
            	fxLabel1.textProperty().bind(lookUpTicker.stockNameProperty());
				fxLabel2.textProperty().bind(Bindings.format("%.3f", lookUpTicker.currentPriceProperty()));		
            	System.out.println("LookUP: " + tfStockTicker.getText());
            }
        }));
			
		tfStockTicker.textProperty().addListener((observable, oldValue, newValue) ->{
			System.out.println("TextField Text Changed (newValue: " + newValue + ")");
			if(!newValue.equals("")){	
				fxLabel2.textProperty().unbind();
				delayDisplayStockInfo.play();
			} else{
				System.out.println("TextField Text Changed (newValue: " + newValue + ") Nothing for real");
				delayDisplayStockInfo.stop();
				fxLabel2.textProperty().unbind();
			}
		});
		
		// Volume
		Label labelVolume = new Label("Volume:");
		TextField tfVolume = new TextField();
		tfVolume.setPrefWidth(100);
        TextFormatter<Double> volumeTextFormatter = new TextFormatter<>( c -> {
            if (partialInputPattern.matcher(c.getControlNewText()).matches()) {
                return c ;
            } else {
                return null ;
            }
        }) ;

        // add filter to allow for typing only double
        tfVolume.setTextFormatter( volumeTextFormatter);
        tfVolume.setPromptText("100");

		// Price
		Label labelPrice = new Label("Price:");
		TextField tfPrice = new TextField();
		tfPrice.setPrefWidth(100);
        TextFormatter<Double> priceTextFormatter = new TextFormatter<>( c -> {
            if (partialInputPattern.matcher(c.getControlNewText()).matches()) {
                return c ;
            } else {
                return null ;
            }
        }) ;
		tfPrice.setTextFormatter( priceTextFormatter);
		tfPrice.setPromptText("WatchList Target");
		
		
		// Vertical Box stores text that prompt user:
		VBox vb1 = new VBox();
		vb1.getChildren().addAll(labelDatePicker,labelStockTicker, labelPrice, labelVolume);
		vb1.setSpacing(20);
		vb1.setAlignment(Pos.CENTER_RIGHT);
		// Vertical Box stores user's input
		VBox vb2 = new VBox();
		vb2.getChildren().addAll(datepicker,tfStockTicker, tfPrice, tfVolume);
		vb2.setSpacing(10);
		
		// horizontal box that combines vb1 and vb2 
		HBox hb1 = new HBox();
		hb1.getChildren().addAll(vb1,vb2);
		hb1.setSpacing(5);
		hb1.setAlignment(Pos.CENTER);
		
		// create buttons
		//final ImageView imageView = new ImageView(new Image("file:eye.jpg",1,1,false, false));
		final ImageView imageViewB = new ImageView(new Image(getClass().getResourceAsStream("eye2.png"), 17,17,false,false));
		final ImageView imageViewS = new ImageView(new Image(getClass().getResourceAsStream("eye.jpg"), 17,17,false,false));
		

		Button buyButton = new Button("Buy");
		buyButton.setTooltip(new Tooltip("Hover over me!!!!!"));
		Button sellButton = new Button("Sell");
		Button cancelButton = new Button("Cancel");
		Button deleteTradeButton = new Button("Delete Trade");
		Button addToWLBuyButton = new Button("", imageViewB);
		addToWLBuyButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		addToWLBuyButton.setTooltip(new Tooltip("Add to WatchList and ALERT when Last price >= Target price"));
		Button addToWLSellButton = new Button("",imageViewS);
		addToWLSellButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		addToWLSellButton.setTooltip(new Tooltip("Add to WatchList and ALERT when Last price <= Target price"));

		
		// What happens when the BUY button is clicked
		buyButton.setOnAction(e -> {
			if(isFieldEmpty(tfStockTicker) == true || isFieldEmpty(tfPrice) == true || isFieldEmpty(tfVolume) == true){
				AlertBox.display("Empty Fields", "Error: Please fill in all info. ");
			} else{
				//System.out.println(Double.parseDouble(tfPrice.getText()));
				boolean confirmedClose = false;
				confirmedClose = isTypeCorrect(tfStockTicker, tfPrice, tfVolume, confirmedClose);
				if(confirmedClose == true){
					int stockTicker = Integer.parseInt(tfStockTicker.getText());
					double price = Double.parseDouble(tfPrice.getText());
					double volume = Double.parseDouble(tfVolume.getText());

					/*System.out.println("Transaction date:" + datepicker.getValue());
					System.out.println("Buy / Sell: " + buySellBox.getValue());
					System.out.println("Stock Ticker: " + stockTicker);
					System.out.println("Price: " + price);
					System.out.println("Volume: " + volume);
					*/
					Trade newTrade = new Trade(BuySell.Buy, datepicker.getValue(), stockTicker, volume, price);
					//System.out.println("new trade: " + newTrade);
					observableListOfTrades.add(newTrade);
					clearTextfield(datepicker,tfStockTicker,tfPrice,tfVolume);

					//fxTransactionLog.getItems().add(newTrade);
					//fxTransactionLog.getItems().add(observableListOfTrades.get(observableListOfTrades.size()-1));
				}
			}
		});

		// What happens when the Sell button is clicked
		sellButton.setOnAction(e -> {
			if(isFieldEmpty(tfStockTicker) == true || isFieldEmpty(tfPrice) == true || isFieldEmpty(tfVolume) == true){
				AlertBox.display("Empty Fields", "Error: Please fill in all info. ");
			} else{
				//System.out.println(Double.parseDouble(tfPrice.getText()));
				boolean confirmedClose = false;
				confirmedClose = isTypeCorrect(tfStockTicker, tfPrice, tfVolume, confirmedClose);
				if(confirmedClose == true){
					int stockTicker = Integer.parseInt(tfStockTicker.getText());
					double price = Double.parseDouble(tfPrice.getText());
					double volume = Double.parseDouble(tfVolume.getText());

					/*System.out.println("Transaction date:" + datepicker.getValue());
					System.out.println("Buy / Sell: " + buySellBox.getValue());
					System.out.println("Stock Ticker: " + stockTicker);
					System.out.println("Price: " + price);
					System.out.println("Volume: " + volume);
					*/
					Trade newTrade = new Trade(BuySell.Sell, datepicker.getValue(), stockTicker, volume, price);
					//System.out.println("new trade: " + newTrade);
					observableListOfTrades.add(newTrade);
					clearTextfield(datepicker,tfStockTicker,tfPrice,tfVolume);

					//fxTransactionLog.getItems().add(newTrade);
					//fxTransactionLog.getItems().add(observableListOfTrades.get(observableListOfTrades.size()-1));
				}
			}
		});
		
		addToWLBuyButton.setOnAction(e ->{
			if(isFieldEmpty(tfStockTicker) == true || isFieldEmpty(tfPrice) == true){
				AlertBox.display("Empty Fields", "Error: Please fill in all info. ");
			} else{
				int stockTicker = Integer.parseInt(tfStockTicker.getText());
				double price = Double.parseDouble(tfPrice.getText());
				WatchListStock newWLStock = new WatchListStock("Last <= Target", stockTicker,price);
				observableListOfWatchListStocks.add(newWLStock);
				clearTextfield(datepicker,tfStockTicker,tfPrice,tfVolume);

			}
		});
		
		addToWLSellButton.setOnAction(e ->{
			if(isFieldEmpty(tfStockTicker) == true || isFieldEmpty(tfPrice) == true){
				AlertBox.display("Empty Fields", "Error: Please fill in all info. ");
			} else{
				int stockTicker = Integer.parseInt(tfStockTicker.getText());
				double price = Double.parseDouble(tfPrice.getText());				
				WatchListStock newWLStock = new WatchListStock("Last >= Target", stockTicker,price);
				observableListOfWatchListStocks.add(newWLStock);
				clearTextfield(datepicker,tfStockTicker,tfPrice,tfVolume);
			}
		});
		
		// bind enter key to buybutton
		fxStockCalculator.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
	        if (ev.getCode() == KeyCode.ENTER)  
	           buyButton.fire();
	           ev.consume(); 
	    });
		

		// When Cancel Button is clicked
		cancelButton.setOnAction(e ->{
			clearTextfield(datepicker,tfStockTicker,tfPrice,tfVolume);
		});
		
		// When Delete Trade button is clicked
		deleteTradeButton.setOnAction(e ->{
			deleteTrade();
		});
		// bind esc key to cancel button
		fxStockCalculator.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
		    @Override
		    public void handle(KeyEvent evt) {
		        if (evt.getCode().equals(KeyCode.ESCAPE)) {
		        	cancelButton.fire();
		        	evt.consume();
		        }
		    }
		});
		
		
		
		HBox buyLayout = new HBox(0);
		buyLayout.getChildren().addAll(buyButton,addToWLBuyButton);
				//, sellButton
		HBox sellLayout = new HBox(0);
		sellLayout.getChildren().addAll(sellButton,addToWLSellButton);
		sellLayout.setAlignment(Pos.CENTER);
		
		HBox hb2 = new HBox(10);
		hb2.getChildren().addAll(buyLayout,sellLayout);
		hb2.setAlignment(Pos.CENTER);
		HBox hb3 = new HBox(10);
		hb3.getChildren().addAll(cancelButton, deleteTradeButton);
		hb3.setAlignment(Pos.CENTER);
		
		
		fxStockCalculator.getChildren().addAll(hb1, hb2,hb3);
		fxStockCalculator.setAlignment(Pos.CENTER);
	}
	  
   // clear all texfield in fxStockCalculator
   public void clearTextfield(DatePicker datepicker, TextField tfStockTicker, TextField tfPrice, TextField tfVolume){
		datepicker.setValue(LocalDate.now());
		tfStockTicker.clear();
		tfPrice.clear(); 
		tfVolume.clear();
		tfStockTicker.requestFocus();
	}
	
	public void initializeWatchListPanel(){
		/*
		TextField tfTicker = new TextField();
		tfTicker.setPromptText("Ticker");
		tfTicker.setPrefWidth(50);
		
		NumberFormat nf = NumberFormat.getIntegerInstance();        
        // add filter to allow for typing only integer
		tfTicker.setTextFormatter( new TextFormatter<>( c ->
        {
        	if (c.getControlNewText().isEmpty()) {
        	    return c;
        	}
            ParsePosition parsePosition = new ParsePosition( 0 );
            Object object = nf.parse( c.getControlNewText(), parsePosition );

            if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() || c.getControlNewText().length() > 4)
            {
                return null;
            }
            else
            {
                return c;
            }
        } ) );
		
		TextField tfTarget = new TextField();
		tfTarget.setPromptText("Target price");
		tfTarget.setPrefWidth(50);
        TextFormatter<Double> targetFormatter = new TextFormatter<>( c -> {
            if (partialInputPattern.matcher(c.getControlNewText()).matches()) {
                return c ;
            } else {
                return null ;
            }
        }) ;

        // add filter to allow for typing only double
        tfTarget.setTextFormatter( targetFormatter);
		*/
		
		Button removeFromWatchList = new Button("Remove from WatchList");
		
		removeFromWatchList.setOnAction(e ->{
			deleteWLStock();
		});
		
		fxWatchListPanel.getChildren().addAll(removeFromWatchList);
		fxWatchListPanel.setSpacing(20);
		fxWatchListPanel.setAlignment(Pos.CENTER);
	}
   
   
	@Override // This method is called by the FXMLLoader when initialization is complete
	public void initialize(URL fxmlFileLocation, ResourceBundle resources){
		initializeFxTransactionLog();
		initializeFXPortfolio();
		initialiseStockCalculator();
		initializeWatchListPanel();
		initializeFxWatchList();
        Locale locale  = new Locale("en", "UK");
        String pattern = "###,###.###;-###,###.###";
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        df.applyPattern(pattern);
        df.setMinimumFractionDigits(3);
        df.setMaximumFractionDigits(10);
		fxLabel3.textProperty().bind(Bindings.format(locale,"Asset: %,.3f",initialPortfolio.totalAssetValProperty()));
		fxLabel4.textProperty().bind(Bindings.format(locale,"uPnl/Pnl: %,.3f/%,.3f",initialPortfolio.sumUPnlProperty(),initialPortfolio.sumPnlProperty()));

		
	}
	
	
}
