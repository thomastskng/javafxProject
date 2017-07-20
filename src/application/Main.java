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
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("Interface.fxml"));
	        Parent root = loader.load();
			Controller controller = loader.<Controller>getController();
			controller.handlingIO(this.primaryStage);
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
	

	
	public static void main(String[] args) {
		launch(args);
	}
}
