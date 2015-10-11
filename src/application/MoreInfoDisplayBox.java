package application;
import javafx.stage.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.*;
import javafx.geometry.*;

public class MoreInfoDisplayBox {

	public Stage window;
	//public GridPane gridPane;
	public MoreInfoDisplayBox(){
		window = new Stage();
		window.initModality(Modality.NONE);
		window.setTitle("More Info");
		window.setMinWidth(450);
		window.setMinHeight(450);
		
		Group root = new Group();
		Scene scene = new Scene(root, 850,400,Color.WHITE);

        root.getChildren().add(createGridPane());
		window.setScene(scene);
		window.show();
	}
	
	public GridPane createGridPane(){
		GridPane gridPane = new GridPane();
		gridPane.setGridLinesVisible(true);
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.setPadding(new Insets(5));
		ColumnConstraints col0 = new ColumnConstraints(400,400,400);
		RowConstraints row0 = new RowConstraints(200,200,200);
        gridPane.getColumnConstraints().add(0,col0);
        gridPane.getColumnConstraints().add(1,col0);
        gridPane.getRowConstraints().add(0,row0);
        gridPane.getRowConstraints().add(1,row0);

        
		// Add gridPanes to gridPane
        GridPane gridPane_r0c0 = individualGridPane(2,6,190,190,190,15,20,25);
        GridPane gridPane_r1c0 = individualGridPane(4,5,93,93,93,15,20,25);
        GridPane gridPane_r0c1 = individualGridPane(4,6,93,93,93,20,25,30);

        //GridPane gridPane_r1c0 = individualGridPane(4,2,93,93,93,15,20,25);

        gridPane.add(gridPane_r1c0, 0, 1,1,1);
        gridPane.add(gridPane_r0c0, 0, 0,1,1);
        gridPane.add(gridPane_r0c1, 1, 0,1,1);

        //gridPane.setHalignment(gridPane_r1c0, HPos.CENTER);
        return gridPane;
 
	}
	
	public GridPane individualGridPane(int num_col, int num_row,int min_col, int pref_col, int max_col, int min_row, int pref_row, int max_row ){
        // gridPane_r1c0
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(10));
		ColumnConstraints gridPane_col = new ColumnConstraints(min_col,pref_col,max_col);
		RowConstraints gridPane_row = new RowConstraints(min_row,pref_row,max_row);
		for(int i = 0; i < num_col; i++){
			gridPane.getColumnConstraints().add(i,gridPane_col);
		}
		for(int j = 0; j < num_row; j++){
			gridPane.getRowConstraints().add(j,gridPane_row);
		}


		return gridPane;
	}
}
