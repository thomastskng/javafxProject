package application;

import java.io.File;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class FileHandling {

	File openLastVisited = new File(System.getProperty("user.home"));
	File saveAsLastVisited = new File(System.getProperty("user.home"));
	static int newPortfolio = 0;
	
	MenuBar fxMenuBar;
	TreeView<String> fxFileTree;
	// Constructor
	public FileHandling(MenuBar fxMenuBar,TreeView<String> fxFileTree){
		this.fxMenuBar = fxMenuBar;
		this.fxFileTree = fxFileTree;
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
					System.out.println(file.getName());
					saveAsLastVisited = file.getParentFile();
					
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
                new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"),
                new FileChooser.ExtensionFilter("TXT (*.txt)", "*.txt")
    	);
	}
    
    public void defineFileTree(){
    	TreeItem<String> root = new TreeItem<String>("Portfolio");
    	root.setExpanded(true);
    	fxFileTree.setRoot(root);
    	fxFileTree.setShowRoot(true);
    }

    public void populateTree(String fileName, String filePath){
    	addLeaf(fileName, (TreeItem<String>) fxFileTree.getRoot(), filePath);
    	
    }
    
    public void addLeaf(String leaf, TreeItem<String> parent, String filePath){
    	TreeItem<String> item = new TreeItem<>(leaf);
    	item.setExpanded(true);
    	parent.getChildren().add(item);
    }
    
    
}
