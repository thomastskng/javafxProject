package application;

import java.io.File;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class FileHandling {

	File openLastVisited = new File(System.getProperty("user.home"));
	File saveAsLastVisited = new File(System.getProperty("user.home"));

	
	public FileHandling(MenuBar fxMenuBar){
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
		
		System.out.println("----------" + openLastVisited);
		System.out.println("----------" + saveAsLastVisited);
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
	
						}					
					}
				}
			}
		});
		
		menuItemSaveAs.setOnAction(new EventHandler<ActionEvent>(){
			
			FileChooser fileChooser = new FileChooser();
			@Override
			public void handle(final ActionEvent e){
				configureFileChooser("Save As", fileChooser, saveAsLastVisited);
				File file = fileChooser.showSaveDialog(null);
				if(file != null){
					System.out.println(file.getParent());
					saveAsLastVisited = file.getParentFile();	
				} else{
					System.out.println("_________");

				}					
			}
		});
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
    
    
}
