package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.*;
import javafx.scene.Parent;
import java.io.*;
import javafx.scene.control.TableCell;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


import java.util.prefs.Preferences;


public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
 
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
	        this.primaryStage = primaryStage;
			Parent root = FXMLLoader.load(getClass().getResource("Interface.fxml"));
			this.primaryStage.setTitle("Stock Tracker");
			this.primaryStage.setScene(new Scene(root));
			this.primaryStage.show();
			// Button to Create new Stock Object
			//createNewTrade = new Button();
			//createNewTrade.setText("Create New Trade");
			//createNewTrade.setOnAction(this);				// whenever the button is clicked, the code to handle this in this class, handle method in this class
			
			//StackPane layout = new StackPane();
			//layout.setStyle("-fx-background-color: #000080");
			//layout.getChildren().add(createNewTrade);
			//BorderPane root = new BorderPane();
			//Scene scene = new Scene(layout,1200,600);
			
			
			root.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//@Override
	//public void handle(ActionEvent event){
		// handle method when different methods are being clicked 
		//if(event.getSource()== createNewTrade){
			//System.out.println("asdjashdasjdasdasdasdasd !!!");
		//}
	//}
	
	/**
	 * Returns the trade file preference, i.e. the file that was last opened.
	 * The preference is read from the OS specific registry. If no such
	 * preference can be found, null is returned.
	 * 
	 * @return
	 */
	public File getTradeFilePath() {
	    Preferences prefs = Preferences.userNodeForPackage(Main.class);
	    String filePath = prefs.get("filePath", null);
	    if (filePath != null) {
	        return new File(filePath);
	    } else {
	        return null;
	    }
	}

	/**
	 * Sets the file path of the currently loaded file. The path is persisted in
	 * the OS specific registry.
	 * 
	 * @param file the file or null to remove the path
	 */
	public void setTradeFilePath(File file) {
	    Preferences prefs = Preferences.userNodeForPackage(Main.class);
	    if (file != null) {
	        prefs.put("filePath", file.getPath());

	        // Update the stage title.
	        primaryStage.setTitle("TradeApp - " + file.getName());
	    } else {
	        prefs.remove("filePath");

	        // Update the stage title.
	        primaryStage.setTitle("TradeApp");
	    }
	}

	/**
    * Loads trade data from the specified file. The current person data will
    * be replaced.
    * 
    * @param file
    */
	public void loadPersonDataFromFile(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(TradeListWrapper.class);
            Unmarshaller um = context.createUnmarshaller();

            // Reading XML from the file and unmarshalling: XML -> Java Objects
            TradeListWrapper wrapper = (TradeListWrapper) um.unmarshal(file);

            Controller.getObservableListOfTrades().clear();
            Controller.getObservableListOfTrades().addAll(wrapper.getTrades());

            // Save the file path to the registry.
            setTradeFilePath(file);

        } catch (Exception e) { // catches ANY exception
        	Alert alert = new Alert(AlertType.ERROR);
        	alert.setTitle("Error");
        	alert.setHeaderText("Could not load data");
        	alert.setContentText("Could not load data from file:\n" + file.getPath());
        	
        	alert.showAndWait();
        }
    }

    /**
     * Saves the current trade data to the specified file.
     * 
     * @param file
     */
    public void savePersonDataToFile(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(TradeListWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Wrapping our person data.
            TradeListWrapper wrapper = new TradeListWrapper();
            wrapper.setTrades(Controller.getObservableListOfTrades());

            // Marshalling and saving XML to the file (Java Objects -> XML)
            m.marshal(wrapper, file);

            // Save the file path to the registry.
            setTradeFilePath(file);
        } catch (Exception e) { // catches ANY exception
        	Alert alert = new Alert(AlertType.ERROR);
        	alert.setTitle("Error");
        	alert.setHeaderText("Could not save data");
        	alert.setContentText("Could not save data to file:\n" + file.getPath());
        	
        	alert.showAndWait();
        }
    }

	
	
	public static void main(String[] args) {
		launch(args);
	}
}
