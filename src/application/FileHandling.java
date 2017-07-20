package application;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FileHandling {

	File openLastVisited = new File(System.getProperty("user.home"));
	File saveAsLastVisited = new File(System.getProperty("user.home"));
	
	MenuBar fxMenuBar;
	ObservableList<Trade> observableListOfTrades;
	ObservableList<ConsolidatedTrade> observableListOfConsolidatedTrades;
	ObservableList<WatchListStock> observableListOfWatchListStocks;
	HashMap<String, Double> ctTickerTargetMap;
	HashMap<String, Double> ctTickerStopLossMap;
	BooleanProperty saved;
	File file;
	Stage primaryStage;
	
	// Constructor
	public FileHandling(MenuBar fxMenuBar,ObservableList<Trade> observableListOfTrades, ObservableList<ConsolidatedTrade> observableListOfConsolidatedTrades, ObservableList<WatchListStock> observableListOfWatchListStocks, HashMap<String, Double> ctTickerTargetMap, HashMap<String, Double> ctTickerStopLossMap, BooleanProperty saved, Stage primaryStage) throws Exception{
		this.primaryStage = primaryStage;
		//System.out.println("file handling constructor: set stage" + this.primaryStage);
		this.fxMenuBar = fxMenuBar;
		this.observableListOfTrades = observableListOfTrades;
		this.observableListOfConsolidatedTrades = observableListOfConsolidatedTrades;
		this.observableListOfWatchListStocks = observableListOfWatchListStocks;
		this.ctTickerTargetMap = ctTickerTargetMap;
		this.ctTickerStopLossMap = ctTickerStopLossMap;
		this.saved = saved;
		defineMenuBar();
    }
	
	public void defineMenuBar() throws Exception{
		/*
		 * Menu bar -> Menu -> MenuItems
		 * 
		 */
		Menu menuFile = new Menu("File");
		Menu menuEdit = new Menu("Edit");
		Menu menuView = new Menu("View");
		fxMenuBar.getMenus().addAll(menuFile, menuEdit, menuView);
		MenuItem menuItemNew = new MenuItem("New");
		MenuItem menuItemOpen = new MenuItem("Open");
		MenuItem menuItemSave = new MenuItem("Save");
		MenuItem menuItemSaveAs = new MenuItem("Save As");
		MenuItem menuItemExit = new MenuItem("Exit");
		menuFile.getItems().addAll(menuItemNew, menuItemOpen, menuItemSave, menuItemSaveAs, menuItemExit);
		menuItemSaveAs.disableProperty().bind(saved);
		menuItemSave.disableProperty().bind(saved);
		PauseTransition delay = new PauseTransition(Duration.seconds(2));
		delay.setOnFinished(ev -> close());
		
		/*
		 * Open Menu Item
		 */
		menuItemOpen.setOnAction(new EventHandler<ActionEvent>(){
			
			FileChooser fileChooser = new FileChooser();
			@Override
			public void handle(final ActionEvent e){
				configureFileChooser("Open", fileChooser, openLastVisited);
				//List<File> fileList = fileChooser.showOpenMultipleDialog(null);
				file = fileChooser.showOpenDialog(null);
				if(file != null){
					System.out.println(file.getParent());
					openLastVisited = file.getParentFile();
					//populateTree(fi.getName(),fi.getParent() );							
					try {
						ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(file));
						System.out.println("Deserialized data: ");
						ArrayList<Trade> tradeArrListIn = (ArrayList<Trade>) objIn.readObject();
						Map<String,Double> ctTickerTargetMapIn = (Map<String, Double>) objIn.readObject();
						Map<String,Double> ctTickerStopLossMapIn = (Map<String, Double>) objIn.readObject();
						ArrayList<WatchListStock> wlStockArrListIn = (ArrayList<WatchListStock>) objIn.readObject();								
						objIn.close();
						for(Trade t: tradeArrListIn){
							System.out.println("reading in Trade: " + t);
						}
						System.out.println("reading in Target HashMap: " + ctTickerTargetMapIn);
						System.out.println("reading in StopLoss HashMap: " + ctTickerStopLossMapIn);
	
						for(WatchListStock wls: wlStockArrListIn){
							System.out.println("reading in WatchListStock: " + wls);
						}
					} catch (ClassNotFoundException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
							
				}
			}
		});
		
		/*
		 * Menu Item - New
		 */
		menuItemNew.setOnAction(new EventHandler<ActionEvent>(){	
			@Override
			public void handle(final ActionEvent e){

			}
		});		
		
		/*
		 * Save As Menu Item
		 */
		menuItemSaveAs.setOnAction(new EventHandler<ActionEvent>(){
			
			@Override
			public void handle(final ActionEvent e){
				FileChooser fileChooser = new FileChooser();
				configureFileChooser("Save As", fileChooser, saveAsLastVisited);
				file = fileChooser.showSaveDialog(null);
				saveObjects();
			}
		});
		
		/*
		 * Save As Menu Item
		 */
		menuItemSave.setOnAction(new EventHandler<ActionEvent>(){
			
			@Override
			public void handle(final ActionEvent e){
				if(file != null && file.exists()){
					saveObjects();
				} else{
					FileChooser fileChooser = new FileChooser();
					configureFileChooser("Save As", fileChooser, saveAsLastVisited);
					file = fileChooser.showSaveDialog(null);
					saveObjects();
					
				}
			}
		});
		
		
		menuItemExit.setOnAction(new EventHandler<ActionEvent>(){
			@Override 
			public void handle(ActionEvent e){
				//System.out.println("set stage" + primaryStage);
				boolean[] answer;
				boolean needToSave = false;
				boolean closeApplication = false;
				if(saved.get() == false){
					answer = ConfirmBox.displayWarning("Warning", "Do you want to save your stuff?");
					needToSave = answer[0];
					closeApplication = answer[1];
				}
				if(needToSave == true){
					menuItemSave.fire();
				}
				if(closeApplication== true){
					Platform.runLater(new Runnable() {
					    public void run() {
					        close();
					    }
					});
				}
			}
		});
		
		primaryStage.setOnCloseRequest(e -> {
			e.consume();
			menuItemExit.fire();
		});
		menuItemOpen.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.META_DOWN));
		menuItemSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN));
		menuItemNew.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.META_DOWN));
		menuItemExit.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.META_DOWN));

		
	}
	
	
	
	public void saveObjects(){
		if(file != null && file.exists()){
			try{
				System.out.println(file.toString());
				saveAsLastVisited = file.getParentFile();
		        // create a new file with an ObjectOutputStream
		         FileOutputStream fileOut = new FileOutputStream(file.toString());
		         ObjectOutputStream objOut = new ObjectOutputStream(fileOut);

		         // write Trade items in the file
		         ArrayList<Trade> tradeArrList = new ArrayList<Trade>();
		         tradeArrList.addAll(observableListOfTrades);
		         //System.out.println("Save as: " + tradeArrList.size());
		         objOut.writeObject(tradeArrList);				        	     
		         // write HashMap of Target and StopLoss in the file
		         objOut.writeObject(ctTickerTargetMap);
		         objOut.writeObject(ctTickerStopLossMap);
		         // 
		         ArrayList<WatchListStock> wlStockArrList = new ArrayList<WatchListStock>();
		         wlStockArrList.addAll(observableListOfWatchListStocks);
		         objOut.writeObject(wlStockArrList);				        	     
	        	 //objOut.writeObject(wlStockArrList);
	
		         // close the stream
		         objOut.close();
		         fileOut.close();
		         
		         // saved
		         saved.set(true);
		         System.out.println("Saved: " + saved.getValue());
				
			} catch(IOException ex){
				System.out.println(ex.getMessage());
			} 	
		} else{
			file = null;
			System.out.println("_________");

		}	
	}
	
    public void configureFileChooser(String title, FileChooser fileChooser, File file ){                           
    	fileChooser.setTitle(title);
    	fileChooser.setInitialDirectory(file);
    	fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All files", "*.*"),
               // new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"),
                new FileChooser.ExtensionFilter("TXT (*.txt)", "*.txt")
    	);
	}

	
    
    public void close(){
    	this.primaryStage.close();
    }
}
