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
import javafx.application.Platform;

import java.net.URL;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.time.LocalDate;
import javafx.scene.paint.Color;
import java.io.File;
import java.io.IOException;
import java.lang.*;
import javafx.scene.control.cell.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import java.util.function.Function;

import javafx.collections.transformation.FilteredList;
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

import com.gargoylesoftware.htmlunit.javascript.host.dom.Text;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class Controller implements Initializable{
	
	@FXML
	// History Log 
	public Stage primaryStage;
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
	public TableColumn<Trade,String> fxTransactionLogPortfolio;
	
	// Consolidated Trades
	public TabPane fxTabPaneUpper;


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
    public MoreInfoDisplayBox midb;

    
	// Label
    public Label fxStockNameLabel;
    public Label fxLastLabel;
    public Label labelLotVal;
    public Label fxLastUpdateLabel;
    public Label fxChgLabel;
    public Label fxChgPercentLabel;
    public Label labelSpreadVal;
    public Label labelPeRatioVal;
    public Label labelYieldVal;
    public Label labelDividendPayoutVal;
    public Label labelEpsVal;
    public Label labelMarketCapVal;
    public Label labelNavVal;
    public Label labelDpsVal;
    public Label labelShortSellTurnoverVal;
    public Label labelShortSellRatioVal;
    public Label labelIndustryVal;
    public Label fxLabel3;
    public Label fxLabel4;

    // Tab pane
    public TabPane fxTabPaneLower;
    // Titled pane for looking up and displaying stock info 
    public TitledPane fxStockLookUp;
    // Vertical Box - Input Stock Calculator
    public VBox fxStockCalculator;
    // Horizontal Box - Watch List panel for adding new stocks
	public HBox fxWatchListPanel;
	// MenuBar
	public MenuBar fxMenuBar;
	public ComboBox<String> portfolioComboBox;
	public GridPane gridPane;

    private Pattern partialInputPattern = Pattern.compile("[-]?[0-9]*(\\.[0-9]*)?");
	
	private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");
	
	// HashMap for storing and keep track of stock ticker and target / stop loss relationship
	HashMap<String,Double> ctTickerTargetMap = new HashMap<String,Double>();
	HashMap<String,Double> ctTickerStopLossMap = new HashMap<String,Double>();

	// Save
	BooleanProperty saved = new SimpleBooleanProperty();
	InvalidationListener savedListener = obs -> saved.set(false);

	
    /**
     * The data as an observable list of Trade.
     */
	private ObservableList<Trade> observableListOfTrades = FXCollections.observableArrayList(trade ->
		new Observable[]{
				trade.transactionDateProperty(),
				trade.stockTickerProperty(),
				trade.buySellProperty(),
				trade.volumeProperty(),
				//trade.stockNameProperty(),
				trade.priceProperty(),
				trade.currentPriceProperty(),
				trade.portfolioProperty()

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
			//consolidatedTrade.currentPriceProperty(),
			consolidatedTrade.targetCautionProperty(),
			consolidatedTrade.stopLossCautionProperty(),
			consolidatedTrade.uPnlProperty()

	});
	
    /**
     * The data as an observable list of WatchListStocks.
     */
	private ObservableList<WatchListStock> observableListOfWatchListStocks = FXCollections.observableArrayList(watchListStock -> 
		new Observable[]{
			//watchListStock.currentPriceProperty(),
			watchListStock.conditionProperty(),
			watchListStock.stockTickerProperty(),
			watchListStock.targetProperty(),
			watchListStock.alertProperty(),
			//watchListStock.currentPriceProperty()
	});
	
    /*
     * The data as an filtered list of Trade.
     */
	private FilteredList<Trade> filterListOfTrades = new FilteredList<>(observableListOfTrades, trade -> trade.stockTickerProperty().get().equals(""));
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
   public ObservableList<Trade> getObservableListOfTrades() {
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
	
	public void initializeFilteredTable(){
		Tab filteredTab = new Tab();
		filteredTab.setClosable(false);
		fxTabPaneLower.getTabs().add(filteredTab);
		filteredTab.setText("Filtered History");
		TableView<Trade> filterTable = new TableView();
		filteredTab.setContent(filterTable);
		filterTable.setItems(filterListOfTrades);
		filterTable.setEditable(true);

		filterListOfTrades.addListener(new ListChangeListener<Trade>(){
			  @Override
	            public void onChanged(ListChangeListener.Change change) {
				  filterTable.setItems(filterListOfTrades);
			  }
		});

		filterTable.setItems(filterListOfTrades);
		// enable multiple selection
		filterTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		TableColumn<Trade, LocalDate> filterTableTransactionDate = new TableColumn("Date");
		TableColumn<Trade, String> filterTableStockName = new TableColumn("Stock Name");
		TableColumn<Trade, String> filterTableStockTicker = new TableColumn("Ticker");
		TableColumn<Trade, String> filterTableBuySell = new TableColumn("Buy/Sell");
		TableColumn<Trade, Number> filterTablePrice = new TableColumn("Price");
		TableColumn<Trade, Number> filterTableVolume = new TableColumn("Volume");
		TableColumn<Trade, Number> filterTableTransactionFee = new TableColumn("Transaction Fee");
		TableColumn<Trade, Number> filterTableCurrentPrice = new TableColumn("Last");
		TableColumn<Trade, String> filterTableRemarks = new TableColumn("Remarks");
		TableColumn<Trade,String> filterTablePortfolio = new TableColumn("Portfolio"); 
		
		filterTable.getColumns().addAll(filterTableTransactionDate,filterTableStockName,filterTableStockTicker,filterTableBuySell,filterTablePrice,filterTableVolume,filterTableTransactionFee,filterTableCurrentPrice,filterTableRemarks,filterTablePortfolio);
		
		
		// define setCellValueFactory
		filterTableTransactionDate.setCellValueFactory(cellData -> cellData.getValue().transactionDateProperty());
		filterTableStockName.setCellValueFactory(cellData -> cellData.getValue().stockNameProperty());
		filterTableStockTicker.setCellValueFactory(cellData -> cellData.getValue().stockTickerProperty());
		filterTableBuySell.setCellValueFactory(cellData -> cellData.getValue().buySellProperty());
		filterTablePrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
		filterTableVolume.setCellValueFactory(cellData -> cellData.getValue().volumeProperty());
		filterTableTransactionFee.setCellValueFactory(cellData -> cellData.getValue().transactionFeeProperty());
		filterTableCurrentPrice.setCellValueFactory(cellData -> cellData.getValue().currentPriceProperty());
		filterTableRemarks.setCellValueFactory(cellData -> cellData.getValue().remarksProperty());
		filterTablePortfolio.setCellValueFactory(cellData -> cellData.getValue().portfolioProperty());

		// define setCellFactory
		filterTableTransactionDate.setCellFactory(col -> new DateEditingCell());
		filterTableStockTicker.setCellFactory(col -> new EditingStockTickerCell<Trade>(""));
		filterTablePrice.setCellFactory(col -> new EditingNumberCell<Trade>("price-cell"));

		filterTableVolume.setCellFactory(col -> new EditingNumberCell<Trade>(""));
		filterTableTransactionFee.setCellFactory(col -> new NonEditableNumberCell<Trade>());				
		filterTableRemarks.setCellFactory(TextFieldTableCell.forTableColumn());

		// initialise buySell choicebox
		ObservableList<String> buySellList = FXCollections.observableArrayList(new String("Buy"), new String("Sell"));
		filterTableBuySell.setCellFactory(ChoiceBoxTableCell.forTableColumn(buySellList));
		filterTableBuySell.setOnEditCommit(
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

		
		filterTableStockTicker.setOnEditCommit(
				new EventHandler<CellEditEvent<Trade, String>>() {
					@Override
					public void handle(CellEditEvent<Trade, String> t) {
						((Trade) t.getTableView().getItems().get(t.getTablePosition().getRow())).setStockTicker(t.getNewValue());
					}
				});
		setPortfolioComboBoxItems();
		filterTablePortfolio.setCellFactory(ComboBoxTableCell.forTableColumn(portfolioComboBox.getItems()));
	
		
	}
	
	// initialise fxTransactionLog 
	public void initializeFxTransactionLog(){	
		
		observableListOfTrades.addAll(
				new Trade("Buy", LocalDate.now().minusDays(100),"1113",13680,0,"My Portfolio")
				,new Trade("Buy", LocalDate.now().minusDays(100),"1",13680,0,"My Portfolio")
				,new Trade("Buy", LocalDate.now().minusDays(100),"293",10000,0,"My Portfolio")
				,new Trade("Buy", LocalDate.now().minusDays(100),"4",10000,0,"My Portfolio")
				,new Trade("Buy", LocalDate.now().minusDays(100),"1972",10000,0,"My Portfolio")
				,new Trade("Buy", LocalDate.now().minusDays(100),"5",23183,0,"My Portfolio")
				,new Trade("Buy", LocalDate.now().minusDays(100),"3",91310,0,"My Portfolio")
				,new Trade("Buy", LocalDate.now().minusDays(100),"941",20000,0,"My Portfolio")
				//,new Trade("Buy", LocalDate.now().minusDays(100),"533",0,0,"abc")

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

				setPortfolioComboBoxItems();
				fxTransactionLogPortfolio.setCellFactory(ComboBoxTableCell.forTableColumn(portfolioComboBox.getItems()));				
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
		fxTransactionLogPortfolio.setCellValueFactory(cellData -> cellData.getValue().portfolioProperty());
		//fxTransactionLogPortfolio.setCellValueFactory(new PropertyValueFactory<Trade, String>("portfolio"));

		
		// define setCellFactory
		fxTransactionLogTransactionDate.setCellFactory(col -> new DateEditingCell());
		fxTransactionLogStockTicker.setCellFactory(col -> new EditingStockTickerCell<Trade>(""));
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
		                //filterListOfTrades.setPredicate(trade -> trade.getStockTicker().equals(""));

					}
				});
		
		// initialise Transaction Date
		//fxTransactionLogTransactionDate.setCellValueFactory(new PropertyValueFactory<Trade,LocalDate>("transactionDate"));

		
		//initialise Stock Ticker
		//fxTransactionLogStockTicker.setCellValueFactory(new PropertyValueFactory<Trade,String>("stockTicker"));
		//fxTransactionLogStockTicker.setCellFactory(TextFieldTableCell.forTableColumn());		

		/*
		fxTransactionLogStockTicker.setOnEditCommit(
				new EventHandler<CellEditEvent<Trade, String>>() {
					@Override
					public void handle(CellEditEvent<Trade, String> t) {
						((Trade) t.getTableView().getItems().get(t.getTablePosition().getRow())).setStockTicker(t.getNewValue());
					}
				});
		*/
		
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

		setPortfolioComboBoxItems();
		//fxTransactionLogPortfolio.setCellFactory(ComboBoxTableCell.forTableColumn(portfolioComboBox.getItems()));
		
		fxTransactionLogPortfolio.setCellFactory(tablecol -> {
           ComboBoxTableCell<Trade,String> ct= new ComboBoxTableCell<Trade,String>();
           ct.getItems().addAll(portfolioComboBox.getItems());
           ct.setComboBoxEditable(true);
           return ct;
        });
        
		
		/*
    	fxTransactionLogPortfolio.setCellFactory(new Callback<TableColumn<Trade, String>, TableCell<Trade, String>>(){
            @Override
            public TableCell<Trade, String> call(TableColumn<Trade, String> param) {
                return new LiveComboBoxTableCell<>(testing);
            }
		});
		*/
		
		
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
		// Add listener to observable list to listen to ALL changes
		observableListOfConsolidatedTrades.addListener(new ListChangeListener<ConsolidatedTrade>() {
		    @Override
		    public void onChanged(ListChangeListener.Change change) {
				ctTickerTargetMap = initialPortfolio.getCtTickerTargetMap();
				ctTickerStopLossMap = initialPortfolio.getCtTickerStopLossMap();
	
		    }
		});
		
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

		
		/*****************************************************
		 *	Table row highlighting for newly added trade
		 *****************************************************/
		
		/* 
		 * 1. Define an ObjectProperty<Trade> to represent the recently added Trade. 
		 *  Most of the time this will be null but when a new Trade is added to the list, 
		 *  it will be set to that new Trade:
		 */
		//final ObjectProperty<ConsolidatedTrade> recentlyAddedTrade = new SimpleObjectProperty<>();
		/*
		 * 2. Register a ListListener with the table's items list. 
		 * When a new item is added to the list, update the recentlyAddedTrade. 
		 * Since you don't want the new Trade to be labeled as "new" indefinitely, 
		 * start a pause transition that will reset recentlyAddedTrade to null after 
		 * some delay (a second or two).:
		 */
		
		/*
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
		
		
		fxPortfolio.setRowFactory(tableView -> new AnimatedPortfolioTableRow<>(recentlyAddedTrade, ConsolidatedTrade::stockTickerProperty, ConsolidatedTrade::targetCautionProperty, ConsolidatedTrade::stopLossCautionProperty, ConsolidatedTrade::uPnlStateProperty, ConsolidatedTrade::pnlStateProperty, 
				//observableListOfTrades, 
				filterListOfTrades, fxTabPaneLower));
		*/
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
        initialPortfolio = new Portfolio(observableListOfTrades, observableListOfConsolidatedTrades
        		,ctTickerTargetMap, ctTickerStopLossMap
        		);
        //fxPortfolio.setEditable(true);
		/*
        SortedList<ConsolidatedTrade> sortedTrades = new SortedList<ConsolidatedTrade>(initialPortfolio.getConsolidatedTrades());
		sortedTrades.setComparator(new Comparator<ConsolidatedTrade>(){
			@Override
			public int compare(ConsolidatedTrade trade1, ConsolidatedTrade trade2){
				return trade1.compareTo(trade2);
			}
		});
		*/
		//fxPortfolio.setItems(sortedTrades);
        for(String portfolioName : initialPortfolio.getUniquePortfolioSet()){
        	new PortfolioTable(fxTabPaneUpper, initialPortfolio.getConsolidatedTrades(), portfolioName , filterListOfTrades, fxTabPaneLower);
        }
        
		ctTickerTargetMap = initialPortfolio.getCtTickerTargetMap();
		ctTickerStopLossMap = initialPortfolio.getCtTickerStopLossMap();
		/*
		for (Map.Entry<String, Double> entry : ctTickerTargetMap.entrySet())
		{
		    System.out.println("Target HashMap: " + entry.getKey() + "/" + entry.getValue());
		}
		for (Map.Entry<String, Double> entry : ctTickerStopLossMap.entrySet())
		{
		    System.out.println("StopLoss HashMap: " + entry.getKey() + "/" + entry.getValue());
		}
		*/
		/*
		System.out.println("***************");
		//initialPortfolio.displayDataStructure();
		System.out.println("Item still in Transaction Log: ");
		for(Trade ttt : fxTransactionLog.getItems()){
		//for(Trade ttt : observableListOfTrades){
			System.out.println( "Change: " + ttt);
		}
		*/

        removeEmptyTabs();
	}

	// initialise fxWatchList 
	public void initializeFxWatchList(){	
		/*
		observableListOfWatchListStocks.addAll(
				new WatchListStock("Last >= Target","1",123.0)
		);
		*/
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
	
	// initialize Grid Pane
	public void initializeGridPane(){
		gridPane = new GridPane();
		//gridPane.setGridLinesVisible(true);
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.setPadding(new Insets(5,5,5,5));

        fxStockLookUp.setContent(gridPane);
        ColumnConstraints col0 = new ColumnConstraints(20,45,70);
        RowConstraints row0 = new RowConstraints(12,17,22);
        RowConstraints row1 = new RowConstraints(18,24,28);
        RowConstraints row2 = new RowConstraints(14,19,24);
        RowConstraints row3 = new RowConstraints(7,12,17);
        RowConstraints row4 = new RowConstraints(11,11,13);

        gridPane.getColumnConstraints().add(0,col0);
        gridPane.getColumnConstraints().add(1,col0);
        gridPane.getColumnConstraints().add(2,col0);
        gridPane.getColumnConstraints().add(3,col0);
        gridPane.getRowConstraints().add(0,row0);
        gridPane.getRowConstraints().add(1,row1);
        gridPane.getRowConstraints().add(2,row2);
        gridPane.getRowConstraints().add(3,row3);
        gridPane.getRowConstraints().add(4,row4);
        gridPane.getRowConstraints().add(5,row4);
        gridPane.getRowConstraints().add(6,row4);
        gridPane.getRowConstraints().add(7,row4);
        gridPane.getRowConstraints().add(8,row4);
        gridPane.getRowConstraints().add(9,row4);
        gridPane.getRowConstraints().add(10,row4);
        gridPane.getRowConstraints().add(11,row4);
        gridPane.getRowConstraints().add(12,row4);
        gridPane.getRowConstraints().add(13,row4);
        gridPane.getRowConstraints().add(14,row4);
        gridPane.getRowConstraints().add(15,row4);
        gridPane.getRowConstraints().add(16,row4);
        gridPane.getRowConstraints().add(17,row4);
        gridPane.getRowConstraints().add(18,row4);

        
        // Stock Name
        fxStockNameLabel = new Label("Stock Name");
        fxStockNameLabel.setFont(Font.font("Arial",FontWeight.BOLD,17));
        gridPane.add(fxStockNameLabel, 0, 0,4,1);
        
        // Last
        fxLastLabel = new Label("Last");
        fxLastLabel.setContentDisplay(ContentDisplay.LEFT);
        fxLastLabel.setFont(Font.font("Arial",FontWeight.BOLD ,32));
        gridPane.add(fxLastLabel, 0, 1,4,1);
        GridPane.setHalignment(fxLastLabel, HPos.RIGHT);
        
        // Chg
        fxChgLabel = new Label("Chg /");
        fxChgLabel.setContentDisplay(ContentDisplay.LEFT);
        fxChgLabel.setFont(Font.font("Arial",FontWeight.BOLD ,14));
        gridPane.add(fxChgLabel, 1, 2,2,1);
        GridPane.setHalignment(fxChgLabel, HPos.LEFT);

        // Chg %
        fxChgPercentLabel = new Label("Chg %");
        fxChgPercentLabel.setContentDisplay(ContentDisplay.LEFT);
        fxChgPercentLabel.setFont(Font.font("Arial",FontWeight.BOLD ,14));
        gridPane.add(fxChgPercentLabel, 2, 2,2,1);
        GridPane.setHalignment(fxChgPercentLabel, HPos.RIGHT);
               
        // Last Update
        fxLastUpdateLabel = new Label("Last Update");
        fxLastUpdateLabel.setFont(Font.font("Arial", 12));
        gridPane.add(fxLastUpdateLabel, 0, 3,4,1);
        GridPane.setHalignment(fxLastUpdateLabel, HPos.RIGHT);

        // Lot       
        Label labelLotText = getTextLabel("Lot Size",HPos.LEFT);
        gridPane.add(labelLotText, 0, 4,2,1);
        labelLotVal = getTextLabel("",HPos.RIGHT);
        gridPane.add(labelLotVal, 2,4,2,1);

        // Spread       
        Label labelSpreadText = getTextLabel("Spread",HPos.LEFT);
        gridPane.add(labelSpreadText, 0, 5,2,1);
        labelSpreadVal = getTextLabel("", HPos.RIGHT);
        gridPane.add(labelSpreadVal, 2,5,2,1);

        // PE Ratio       
        Label labelPeRatioText = getTextLabel("P/E Ratio-TMM", HPos.LEFT);
        gridPane.add(labelPeRatioText, 0, 6,2,1);
        labelPeRatioVal = getTextLabel("", HPos.RIGHT);
        gridPane.add(labelPeRatioVal, 2,6,2,1);

        // Yield       
        Label labelYieldText = getTextLabel("Yield-TMM", HPos.LEFT);
        gridPane.add(labelYieldText, 0, 7,2,1);
        labelYieldVal = getTextLabel("", HPos.RIGHT);
        gridPane.add(labelYieldVal, 2,7,2,1);

        // Dividend Payout       
        Label labelDividendPayoutText = getTextLabel("Dividend Payout", HPos.LEFT);
        gridPane.add(labelDividendPayoutText, 0, 8,3,1);
        labelDividendPayoutVal = getTextLabel("", HPos.RIGHT);
        gridPane.add(labelDividendPayoutVal, 2,8,2,1);

        // EPS       
        Label labelEpsText = getTextLabel("Earnings Per Share",HPos.LEFT);
        gridPane.add(labelEpsText, 0, 9,3,1);
        labelEpsVal = getTextLabel("",HPos.RIGHT);
        gridPane.add(labelEpsVal, 2,9,2,1);
        
        // Market Cap       
        Label labelMarketCapText = getTextLabel("Market Cap", HPos.LEFT);
        gridPane.add(labelMarketCapText, 0, 10,2,1);
        labelMarketCapVal = getTextLabel("",HPos.RIGHT);
        gridPane.add(labelMarketCapVal, 2,10,2,1);
        
        // NAV       
        Label labelNavText = getTextLabel("P/B Ratio / NAV", HPos.LEFT);
        gridPane.add(labelNavText, 0, 11,2,1);
        labelNavVal = getTextLabel("",HPos.RIGHT);
        gridPane.add(labelNavVal, 2,11,2,1);
        
        // DPS   
        Label labelDpsText = getTextLabel("Dividend/share", HPos.LEFT);
        gridPane.add(labelDpsText, 0, 12,3,1);
        labelDpsVal = getTextLabel("", HPos.RIGHT);
        gridPane.add(labelDpsVal, 2,12,2,1);
        
        // Short Sell Turnover  
        Label labelShortSellTurnoverText = getTextLabel("Short Sell Turnover", HPos.LEFT);
        gridPane.add(labelShortSellTurnoverText, 0, 13,3,1);
        labelShortSellTurnoverVal = getTextLabel("", HPos.RIGHT);
        gridPane.add(labelShortSellTurnoverVal, 2,13,2,1);
  
        // Short Sell Ratio   
        Label labelShortSellRatioText = getTextLabel("Short Sell Ratio", HPos.LEFT);
        gridPane.add(labelShortSellRatioText, 0, 14,3,1);
        labelShortSellRatioVal = getTextLabel("", HPos.RIGHT);
        gridPane.add(labelShortSellRatioVal, 2,14,2,1);
  
        // Industry  
        Label labelIndustryText = getTextLabel("Industry", HPos.LEFT);
        gridPane.add(labelIndustryText, 0, 15,3,1);
        labelIndustryVal = getTextLabel("", HPos.RIGHT);
        labelIndustryVal.setFont(Font.font("Arial", 10));
        gridPane.add(labelIndustryVal, 2,15,2,1);
  
        
        Button moreInfo = new Button("More Info");
        gridPane.add(moreInfo, 0, 17,4,1);
        GridPane.setHalignment(moreInfo,HPos.CENTER);
        midb = new MoreInfoDisplayBox();
        moreInfo.setOnAction(e -> {
        	if(midb!=null){
        		midb.window.show();
        		midb.window.requestFocus();
        	} else{
        		midb.window.hide();       	
        	}
        });
        
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

		// Portfolio name:
		portfolioComboBox = new ComboBox();
		//portfolioComboBox.setItems();
		portfolioComboBox.setEditable(true);
		Label labelPortfolio = new Label ("Portfolio");
		portfolioComboBox.setPrefWidth(130);
		
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

			
        Image upImg = new Image(getClass().getResourceAsStream("up.png"), 30,30,false,false);
        Image downImg = new Image(getClass().getResourceAsStream("down.png"), 30,30,false,false);
        Image upImgChg = new Image(getClass().getResourceAsStream("up.png"), 14,14,false,false);
        Image downImgChg = new Image(getClass().getResourceAsStream("down.png"), 14,14,false,false);
        Image upImgChgPercent = new Image(getClass().getResourceAsStream("up.png"), 14,14,false,false);
        Image downImgChgPercent = new Image(getClass().getResourceAsStream("down.png"), 14,14,false,false);

        ImageView upImgView = new ImageView(upImg);
        ImageView downImgView = new ImageView(downImg);
        ImageView upImgViewChg = new ImageView(upImgChg);
        ImageView downImgViewChg = new ImageView(downImgChg);
        ImageView upImgViewChgPercent = new ImageView(upImgChgPercent);
        ImageView downImgViewChgPercent = new ImageView(downImgChgPercent);

        
        
		Timeline delayDisplayStockInfo = new Timeline(new KeyFrame(Duration.seconds(0.5), new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent actionEvent) {
				fxLastLabel.textProperty().unbind();
				labelLotVal.textProperty().unbind();

		        Locale locale  = new Locale("en", "UK");
		        // Create stock ticker look up object
            	lookUpTicker = new StockLookUp(tfStockTicker.getText());
            	// stock name
            	fxStockNameLabel.textProperty().bind(lookUpTicker.stockNameProperty());
            	// Last
            	fxLastLabel.textProperty().bind(Bindings.format("%,.3f", lookUpTicker.currentPriceProperty()));
            	fxLastLabel.textFillProperty().bind(
						Bindings.when(lookUpTicker.posNegForLastProperty().isEqualTo("pos")).then(Color.LIMEGREEN).otherwise(
								Bindings.when(lookUpTicker.posNegForLastProperty().isEqualTo("neg")).then(Color.RED).otherwise(Color.GREY)  )
						);
            	fxLastLabel.graphicProperty().bind(
            			Bindings.when(lookUpTicker.posNegForLastProperty().isEqualTo("pos")).then(upImgView).otherwise(
								Bindings.when(lookUpTicker.posNegForLastProperty().isEqualTo("neg")).then(downImgView).otherwise(new ImageView(new WritableImage(3,3)))  )
            			);
            	// Chg
            	fxChgLabel.textProperty().bind(Bindings.concat(lookUpTicker.chgProperty()).concat(" / "));
            	fxChgLabel.textFillProperty().bind(
						Bindings.when(lookUpTicker.posNegForChgProperty().isEqualTo("pos")).then(Color.LIMEGREEN).otherwise(
								Bindings.when(lookUpTicker.posNegForChgProperty().isEqualTo("neg")).then(Color.RED).otherwise(Color.GREY))
						);
            	fxChgLabel.graphicProperty().bind(
            			Bindings.when(lookUpTicker.posNegForChgProperty().isEqualTo("pos")).then(upImgViewChg).otherwise(
								Bindings.when(lookUpTicker.posNegForChgProperty().isEqualTo("neg")).then(downImgViewChg).otherwise(new ImageView(new WritableImage(3,3)))  )
            			);
            	// Chg %
            	fxChgPercentLabel.textProperty().bind(lookUpTicker.chgPercentProperty());
            	fxChgPercentLabel.textFillProperty().bind(
						Bindings.when(lookUpTicker.posNegForChgPercentProperty().isEqualTo("pos")).then(Color.LIMEGREEN).otherwise(
								Bindings.when(lookUpTicker.posNegForChgPercentProperty().isEqualTo("neg")).then(Color.RED).otherwise(Color.GREY)  )
						);
            	fxChgPercentLabel.graphicProperty().bind(
            			Bindings.when(lookUpTicker.posNegForChgPercentProperty().isEqualTo("pos")).then(upImgViewChgPercent).otherwise(
								Bindings.when(lookUpTicker.posNegForChgPercentProperty().isEqualTo("neg")).then(downImgViewChgPercent).otherwise(new ImageView(new WritableImage(3,3)))  )
            			);
        		// Last Update
				fxLastUpdateLabel.textProperty().bind(Bindings.concat("Last Update: ").concat(lookUpTicker.lastUpdateProperty()));
				System.out.println("LookUP: " + tfStockTicker.getText());		
				// Lot
				labelLotVal.textProperty().bind(Bindings.format(locale,"%,.0f",lookUpTicker.lotSizeProperty()));
				labelSpreadVal.textProperty().bind(lookUpTicker.spreadProperty());
				labelPeRatioVal.textProperty().bind(lookUpTicker.peRatioProperty());
				labelYieldVal.textProperty().bind(lookUpTicker.yieldProperty());
				labelDividendPayoutVal.textProperty().bind(lookUpTicker.dividendPayoutProperty());
				labelEpsVal.textProperty().bind(lookUpTicker.epsProperty());
				labelMarketCapVal.textProperty().bind(lookUpTicker.marketCapProperty());
				labelNavVal.textProperty().bind(lookUpTicker.navProperty());
				labelDpsVal.textProperty().bind(lookUpTicker.dpsProperty());
				labelShortSellTurnoverVal.textProperty().bind(lookUpTicker.shortSellTurnoverProperty());
				labelShortSellRatioVal.textProperty().bind(lookUpTicker.shortSellRatioProperty());
				labelIndustryVal.textProperty().bind(lookUpTicker.industryProperty());
				midb.labelBid_delayedVal.textProperty().bind(lookUpTicker.bid_delayedProperty());
				midb.labelAsk_delayedVal.textProperty().bind(lookUpTicker.ask_delayedProperty());
				midb.labelHighVal.textProperty().bind(lookUpTicker.highProperty());
				midb.labelLowVal.textProperty().bind(lookUpTicker.lowProperty());
				midb.labelOpenVal.textProperty().bind(lookUpTicker.openProperty());
				midb.labelPrev_closeVal.textProperty().bind(lookUpTicker.prev_closeProperty());
				midb.labelVolumeVal.textProperty().bind(lookUpTicker.volumeProperty());
				midb.labelTurnoverVal.textProperty().bind(lookUpTicker.turnoverProperty());
				midb.labelOneMonthRangeVal.textProperty().bind(lookUpTicker.oneMonthRangeProperty());
				midb.labelTwoMonthRangeVal.textProperty().bind(lookUpTicker.twoMonthRangeProperty());
				midb.labelThreeMonthRangeVal.textProperty().bind(lookUpTicker.threeMonthRangeProperty());
				midb.labelFiftyTwoWeekRangeVal.textProperty().bind(lookUpTicker.fiftyTwoWeekRangeProperty());
				midb.labelRateRatioVal.textProperty().bind(lookUpTicker.rateRatioProperty());
				midb.labelVolumeRatioVal.textProperty().bind(lookUpTicker.volumeRatioProperty());
				midb.labelSma10Val.textProperty().bind(lookUpTicker.sma10Property());
				midb.labelSma50Val.textProperty().bind(lookUpTicker.sma50Property());
				midb.labelSma100Val.textProperty().bind(lookUpTicker.sma100Property());
				midb.labelSma250Val.textProperty().bind(lookUpTicker.sma250Property());
				midb.labelRsi10Val.textProperty().bind(lookUpTicker.rsi10Property());
				midb.labelRsi14Val.textProperty().bind(lookUpTicker.rsi14Property());
				midb.labelRsi20Val.textProperty().bind(lookUpTicker.rsi20Property());
				midb.labelMacd8_17Val.textProperty().bind(lookUpTicker.macd8_17Property());
				midb.labelMacd12_25Val.textProperty().bind(lookUpTicker.macd12_25Property());

			}
        }));
			
		tfStockTicker.textProperty().addListener((observable, oldValue, newValue) ->{
			System.out.println("TextField Text Changed (newValue: " + newValue + ")");
			if(!newValue.equals("")){	
				//fxLabel2.textProperty().unbind();
				delayDisplayStockInfo.play();
			} else{
				System.out.println("TextField Text Changed (newValue: " + newValue + ") Nothing for real");
				delayDisplayStockInfo.stop();
				//fxLabel2.textProperty().unbind();
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
		vb1.getChildren().addAll(labelDatePicker,labelPortfolio,labelStockTicker, labelPrice, labelVolume);
		vb1.setSpacing(20);
		vb1.setAlignment(Pos.CENTER_RIGHT);
		// Vertical Box stores user's input
		VBox vb2 = new VBox();
		vb2.getChildren().addAll(datepicker,portfolioComboBox,tfStockTicker, tfPrice, tfVolume);
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
			if(isFieldEmpty(tfStockTicker) == true || isFieldEmpty(tfPrice) == true || isFieldEmpty(tfVolume) == true || !(portfolioComboBox.getValue()!=null)){
				AlertBox.display("Empty Fields", "Error: Please fill in all info. ");
			} else{
				//System.out.println(Double.parseDouble(tfPrice.getText()));
				boolean confirmedClose = false;
				confirmedClose = isTypeCorrect(tfStockTicker, tfPrice, tfVolume, confirmedClose);
				if(confirmedClose == true){
					int stockTicker = Integer.parseInt(tfStockTicker.getText());
					double price = Double.parseDouble(tfPrice.getText());
					double volume = Double.parseDouble(tfVolume.getText());
					Trade newTrade = new Trade("Buy", datepicker.getValue(), tfStockTicker.getText(), volume, price,portfolioComboBox.getValue());
					observableListOfTrades.add(newTrade);
					clearTextfield(datepicker,tfStockTicker,tfPrice,tfVolume,portfolioComboBox);
				}
			}
		});

		// What happens when the Sell button is clicked
		sellButton.setOnAction(e -> {
			if(isFieldEmpty(tfStockTicker) == true || isFieldEmpty(tfPrice) == true || isFieldEmpty(tfVolume) == true || !(portfolioComboBox.getValue()!=null)){
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
					Trade newTrade = new Trade("Sell", datepicker.getValue(), tfStockTicker.getText(), volume, price, portfolioComboBox.getValue());
					//System.out.println("new trade: " + newTrade);
					observableListOfTrades.add(newTrade);
					clearTextfield(datepicker,tfStockTicker,tfPrice,tfVolume,portfolioComboBox);

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
				WatchListStock newWLStock = new WatchListStock("Last <= Target", tfStockTicker.getText(),price);
				observableListOfWatchListStocks.add(newWLStock);
				clearTextfield(datepicker,tfStockTicker,tfPrice,tfVolume,portfolioComboBox);

			}
		});
		
		addToWLSellButton.setOnAction(e ->{
			if(isFieldEmpty(tfStockTicker) == true || isFieldEmpty(tfPrice) == true){
				AlertBox.display("Empty Fields", "Error: Please fill in all info. ");
			} else{
				int stockTicker = Integer.parseInt(tfStockTicker.getText());
				double price = Double.parseDouble(tfPrice.getText());				
				WatchListStock newWLStock = new WatchListStock("Last >= Target", tfStockTicker.getText(),price);
				observableListOfWatchListStocks.add(newWLStock);
				clearTextfield(datepicker,tfStockTicker,tfPrice,tfVolume,portfolioComboBox);
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
			clearTextfield(datepicker,tfStockTicker,tfPrice,tfVolume,portfolioComboBox);
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
   public void clearTextfield(DatePicker datepicker, TextField tfStockTicker, TextField tfPrice, TextField tfVolume, ComboBox portfolioComboBox){
		datepicker.setValue(LocalDate.now());
		tfStockTicker.clear();
		tfPrice.clear(); 
		tfVolume.clear();
		portfolioComboBox.requestFocus();
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
   
	
	public void handlingIO(Stage primaryStage) throws Exception{
		this.primaryStage = primaryStage;
		new FileHandling(fxMenuBar,observableListOfTrades,observableListOfConsolidatedTrades, observableListOfWatchListStocks, ctTickerTargetMap, ctTickerStopLossMap, saved, this.primaryStage);
	}
	
	public void setPortfolioComboBoxItems(){
    	portfolioComboBox.setItems(null);
    	ObservableSet<String> portfolioSet = FXCollections.observableSet();
    	ObservableList<String> portfolios = FXCollections.observableArrayList();
    	for(Trade tr: observableListOfTrades){
    		//System.out.println("combobox: " + fxTransactionLogPortfolio.getCellObservableValue(tr).getValue());
    		portfolioSet.add(fxTransactionLogPortfolio.getCellObservableValue(tr).getValue());
		}
    	portfolios.addAll(portfolioSet);
    	portfolioComboBox.setItems(portfolios);

	}
	
	public void removeEmptyTabs(){
		for(ListIterator<Tab> iterator = fxTabPaneUpper.getTabs().listIterator(); iterator.hasNext(); ){
			Tab tab = iterator.next();
			if(tab!=null){
				TableView<ConsolidatedTrade> tableView = (TableView<ConsolidatedTrade>) tab.getContent();
				if(tableView.getItems().size() <= 0){
					//fxTabPaneUpper.getTabs().remove(tab);
					iterator.remove();
				}
			}
		}
		
	}
   
	public Label getTextLabel(String text, HPos hpos){
		Label textLabel = new Label(text);
		textLabel.setFont(Font.font("Arial",13));
		if(!text.equals("")){
			textLabel.setTextFill(Color.DIMGRAY);
		}
        GridPane.setHalignment(textLabel, hpos);

		return textLabel;
	}
	
	@Override // This method is called by the FXMLLoader when initialization is complete
	public void initialize(URL fxmlFileLocation, ResourceBundle resources){
		observableListOfTrades.addListener(savedListener);
		observableListOfConsolidatedTrades.addListener(savedListener);
		observableListOfWatchListStocks.addListener(savedListener);
		//fxTransactionLog.getItems().addListener(savedListener);
		//fxPortfolio.getItems().addListener(savedListener);
		//fxWatchList.getItems().addListener(savedListener);
		initializeGridPane();
		initialiseStockCalculator();
		initializeFxTransactionLog();
		initializeFXPortfolio();
		initializeWatchListPanel();
		initializeFxWatchList();
		initializeFilteredTable();
        Locale locale  = new Locale("en", "UK");
        String pattern = "###,###.###;-###,###.###";
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        df.applyPattern(pattern);
        df.setMinimumFractionDigits(3);
        df.setMaximumFractionDigits(10);
		fxLabel3.textProperty().bind(Bindings.format(locale,"Asset: %,.3f",initialPortfolio.totalAssetValProperty()));
		fxLabel4.textProperty().bind(Bindings.format(locale,"uPnl/Pnl: %,.3f/%,.3f",initialPortfolio.sumUPnlProperty(),initialPortfolio.sumPnlProperty()));
		//new FileHandling(fxMenuBar, fxFileTree,observableListOfTrades,observableListOfWatchListStocks, ctTickerTargetMap, ctTickerStopLossMap, saved, this.primaryStage);
	}
	
	
}
