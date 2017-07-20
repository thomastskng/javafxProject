package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmBox {
	// answer[0] determines the need to Save
	// answer[1] determines whether to close the application or not
	private static boolean[] answer = new boolean[]{false,false};
	private static Stage window;
	
	public static boolean[] displayWarning(String title, String message){
		window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setMinWidth(300);
		Label label = new Label();
		label.setText(message);
		Button yesButton = new Button("Yes");
		Button noButton = new Button("No");
		
		// needToSave = true, close Application = true and close this confirmbox
		yesButton.setOnAction(ey ->{
			answer[0] = true;
			answer[1] = true;
			window.close();
		});
		
		// needToSave = false, close Application = true and close this confirmbox
		noButton.setOnAction(en -> {
			answer[0] = false;
			answer[1] = true;
			window.close();
		});
		
		
		// needToSave = false, close Application = false and close this confirmbox
		window.setOnCloseRequest(e -> {
			answer[0] = false;
			answer[1] = false;
			closeConfirmBox();
		});
		
		// key binding 		
		window.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
	        if ( e.getCode() == KeyCode.N){
	        	noButton.fire();
	        	e.consume(); 

	        }
	    });
		
		// bind enter key to yesButton		
		window.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
	        if (ev.getCode() == KeyCode.ENTER ){  
	        	yesButton.fire();
	           ev.consume(); 
	        }
	    });
		
		
		window.addEventFilter(KeyEvent.KEY_PRESSED, ev ->{
			if(ev.getCode()==KeyCode.ESCAPE){
				ev.consume();
				answer[0] = false;
				answer[1] = false;
				closeConfirmBox();
			}
		});
		
		VBox layout = new VBox(20);
		layout.setPadding(new Insets(20,5,20,5));
		HBox bottomLayout = new HBox(50);
		bottomLayout.setPadding(new Insets(20,5,20,5));
		bottomLayout.getChildren().addAll(yesButton,noButton);
		bottomLayout.setAlignment(Pos.CENTER);
		layout.getChildren().addAll(label,bottomLayout);
		layout.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();
		return answer;
	}
	
	public static void closeConfirmBox(){
		window.close(); 
	}
}
