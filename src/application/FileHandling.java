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

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class FileHandling {

	File openLastVisited = new File(System.getProperty("user.home"));
	File saveAsLastVisited = new File(System.getProperty("user.home"));
	static int newPortfolio = 0;
	
	MenuBar fxMenuBar;
	TreeView<Path> fxFileTree;
	ObservableList<Trade> observableListOfTrades;
	ObservableList<WatchListStock> observableListOfWatchListStocks;
	HashMap<String, Double> ctTickerTargetMap;
	HashMap<String, Double> ctTickerStopLossMap;
	// Constructor
	public FileHandling(MenuBar fxMenuBar,TreeView<Path> fxFileTree, ObservableList<Trade> observableListOfTrades, ObservableList<WatchListStock> observableListOfWatchListStocks, HashMap<String, Double> ctTickerTargetMap, HashMap<String, Double> ctTickerStopLossMap){
		this.fxMenuBar = fxMenuBar;
		this.fxFileTree = fxFileTree;
		this.observableListOfTrades = observableListOfTrades;
		this.observableListOfWatchListStocks = observableListOfWatchListStocks;
		this.ctTickerTargetMap = ctTickerTargetMap;
		this.ctTickerStopLossMap = ctTickerStopLossMap;
		defineFileTree();
		defineMenuBar();
    }
	
	public void defineMenuBar(){
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
		

		/*
		 * Open Menu Item
		 */
		menuItemOpen.setOnAction(new EventHandler<ActionEvent>(){
			
			FileChooser fileChooser = new FileChooser();
			@Override
			public void handle(final ActionEvent e){
				configureFileChooser("Open", fileChooser, openLastVisited);
				List<File> fileList = fileChooser.showOpenMultipleDialog(null);
				if(fileList != null){
					for(File fi : fileList){
						if(fi != null){
							System.out.println(fi.getParent());
							openLastVisited = fi.getParentFile();
							populateTree(fi.getName(),fi.getParent() );							
							try {
								ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(fi));
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
				}
			}
		});
		
		/*
		 * Menu Item - New
		 */
		menuItemNew.setOnAction(new EventHandler<ActionEvent>(){	
			@Override
			public void handle(final ActionEvent e){
				String str = newPortfolioNumber();
				populateTree("New"+str,"");

			}
		});		
		
		/*
		 * Save As Menu Item
		 */
		menuItemSaveAs.setOnAction(new EventHandler<ActionEvent>(){
			
			FileChooser fileChooser = new FileChooser();
			@Override
			public void handle(final ActionEvent e){
				configureFileChooser("Save As", fileChooser, saveAsLastVisited);
				File file = fileChooser.showSaveDialog(null);
				if(file != null){
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
						
					} catch(IOException ex){
						System.out.println(ex.getMessage());
					}
					
				} else{
					System.out.println("_________");

				}					
			}
		});
	}
	
	public String newPortfolioNumber(){
		newPortfolio++;
		if(newPortfolio == 1){
			return "";
		} else{
			return "(" + (newPortfolio-1) + ")";
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
    
    public void defineFileTree(){
    	TreeItem<String> root = new TreeItem<String>("Portfolio");
    	fxFileTree.setShowRoot(true);
    	root.setExpanded(true);
    	//fxFileTree.setRoot(root);
    }

    public void populateTree(String fileName, String filePath){
    	//addLeaf(fileName, (TreeCell<Path>) fxFileTree.getRoot(), filePath);
    	
    }
    
    public void addLeaf(String leaf, TreeItem<String> parent, String filePath){
    	TreeItem<String> item = new TreeItem<>(leaf);
    	//Tooltip.install(item, filePath);
    	//item.setExpanded(true);
    	parent.getChildren().add(item);
    }
    
    
}
