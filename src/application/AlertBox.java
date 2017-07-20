package application;
import javafx.stage.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.*;
import javafx.geometry.*;

public class AlertBox {

	public static void display(String title, String message){
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setMinWidth(300);
		Label label = new Label();
		label.setText(message);
		Button cancelButton = new Button("OK");
		cancelButton.setOnAction(e -> window.close());
		
		// bind enter key to cancelButton
		window.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
	        if (ev.getCode() == KeyCode.ENTER || ev.getCode() == KeyCode.ESCAPE)  
	           cancelButton.fire();
	           ev.consume(); 
	    });
		VBox layout = new VBox(20);
		layout.setPadding(new Insets(20,5,20,5));
		layout.getChildren().addAll(label,cancelButton);
		layout.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();
	}
}
