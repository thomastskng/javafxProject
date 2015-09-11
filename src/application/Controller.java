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
import java.time.LocalDate;
import java.io.IOException;
import java.lang.*;
import javafx.scene.control.cell.*;
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
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import java.util.function.Function;

import com.sun.javafx.css.converters.StringConverter;

import javafx.collections.transformation.SortedList;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.beans.property.SimpleObjectProperty;
import java.util.*;
import javafx.css.PseudoClass;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;


public class Controller implements Initializable{
	
	@FXML
	// Button
	public Button fxCreateNewTrade;
	public Button fxDeleteTrade;
	public Button fxEditTrade;
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
    public Portfolio initialPortfolio;

	
	
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
				trade.priceProperty()
		}
	);
	
    /**
     * The data as an observable list of Consolidated Trades.
     */
	private ObservableList<ConsolidatedTrade> observableListOfConsolidatedTrades = FXCollections.observableArrayList(consolidatedTrade -> new Observable[]{
			consolidatedTrade.avgPriceProperty(),
			consolidatedTrade.stockTickerProperty(),
			consolidatedTrade.positionProperty(),
			consolidatedTrade.targetProperty(),
			consolidatedTrade.stopLossProperty(),
			consolidatedTrade.currentPriceProperty(),
			consolidatedTrade.targetCautionProperty(),
			consolidatedTrade.stopLossCautionProperty()
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
                   return new EditingStockTickerCell();
                }
   };
  
   /**
    * Returns the data as an observable list of Persons. 
    * @return
    */
   public static ObservableList<Trade> getObservableListOfTrades() {
       return observableListOfTrades;
   } 
            
   // create new trade 
   public void CreateNewTrade(){
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Creating a New Trade");
		window.setMinWidth(300);
		window.setMinHeight(300);
		
		// Buy / Sell Choice Box
		ChoiceBox<BuySell> buySellBox = new ChoiceBox<>();
		buySellBox.getItems().addAll(BuySell.Buy, BuySell.Sell);
		buySellBox.setValue(BuySell.Buy);
		Label labelBuySell = new Label("Buy / Sell:");
		
		// Transaction Date: Calendar
		// http://code.makery.ch/blog/javafx-8-date-picker/
		DatePicker datepicker = new DatePicker();
		datepicker.setValue(LocalDate.now());
		datepicker.setOnAction(e -> {
			LocalDate date = datepicker.getValue();
			//System.out.println("Selected Date: " + date);
		});
		Label labelDatePicker = new Label("Transaction Date:");
		

		// capture user input: StockTicker, volume, price 
		// Stock Ticker
		Label labelStockTicker = new Label("Stock Ticker:");
		TextField tfStockTicker = new TextField();
		
		// Volume
		Label labelVolume = new Label("Volume:");
		TextField tfVolume = new TextField();

		// Price
		Label labelPrice = new Label("Price:");
		TextField tfPrice = new TextField();
		
		// Vertical Box stores text that prompt user:
		VBox vb1 = new VBox();
		vb1.getChildren().addAll(labelDatePicker,labelBuySell, labelStockTicker, labelPrice, labelVolume);
		vb1.setSpacing(20);
		vb1.setAlignment(Pos.CENTER_RIGHT);
		// Vertical Box stores user's input
		VBox vb2 = new VBox();
		vb2.getChildren().addAll(datepicker,buySellBox,tfStockTicker, tfPrice, tfVolume);
		vb2.setSpacing(10);
		
		// horizontal box that combines vb1 and vb2 
		HBox hb1 = new HBox();
		hb1.getChildren().addAll(vb1,vb2);
		hb1.setSpacing(5);
		hb1.setAlignment(Pos.CENTER);
		
		// create 2 buttons
		Button okayButton = new Button("OK");
		Button cancelButton = new Button("Cancel");
		
		// What happens when the OK button is clicked
		okayButton.setOnAction(e -> {
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
					Trade newTrade = new Trade(buySellBox.getValue(), datepicker.getValue(), stockTicker, volume, price);
					//System.out.println("new trade: " + newTrade);
					observableListOfTrades.add(newTrade);
					//fxTransactionLog.getItems().add(newTrade);
					//fxTransactionLog.getItems().add(observableListOfTrades.get(observableListOfTrades.size()-1));
					window.close();
				}
			}
		});

		// bind enter key to okaybutton
		window.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
	        if (ev.getCode() == KeyCode.ENTER)  
	           okayButton.fire();
	           ev.consume(); 
	    });
		

		// When Cancel Button is clicked
		cancelButton.setOnAction(e ->{
			window.close();
		});
		
		// bind esc key to cancel button
		window.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
		    @Override
		    public void handle(KeyEvent evt) {
		        if (evt.getCode().equals(KeyCode.ESCAPE)) {
		        	cancelButton.fire();
		        	evt.consume();
		        }
		    }
		});
		
		
		
		HBox bottomLayout = new HBox(10);
		bottomLayout.getChildren().addAll(okayButton, cancelButton);
		bottomLayout.setAlignment(Pos.BOTTOM_CENTER);
		
		
		VBox layout = new VBox(50);
		layout.getChildren().addAll(hb1, bottomLayout);
		layout.setAlignment(Pos.CENTER);
		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();
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
			int volume = Integer.parseInt(tfVolume.getText());
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
		//fxTransactionLog.setItems(observableListOfTrades);
		
		fxCreateNewTrade.setOnAction(e -> {
			CreateNewTrade();
			//System.out.println(results);
		});
		
		fxDeleteTrade.setOnAction(e ->{
			deleteTrade();
		});
		
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
	
	@Override // This method is called by the FXMLLoader when initialization is complete
	public void initialize(URL fxmlFileLocation, ResourceBundle resources){
		initializeFxTransactionLog();
		initializeFXPortfolio();
	}
	
	

	
}
