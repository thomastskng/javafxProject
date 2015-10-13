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
	
    public Label labelBid_delayedVal;
    public Label labelAsk_delayedVal;
    public Label labelHighVal;
    public Label labelLowVal;
    public Label labelOpenVal;
    public Label labelPrev_closeVal;
    public Label labelVolumeVal;
    public Label labelTurnoverVal;
    public Label labelOneMonthRangeVal;
    public Label labelTwoMonthRangeVal;
    public Label labelThreeMonthRangeVal;
    public Label labelFiftyTwoWeekRangeVal;
    public Label labelRateRatioVal;
    public Label labelVolumeRatioVal;
    public Label labelSma10Val;
    public Label labelSma20Val;
    public Label labelSma50Val;
    public Label labelSma100Val;
    public Label labelSma250Val;
    public Label labelRsi10Val;
    public Label labelRsi14Val;
    public Label labelRsi20Val;
    public Label labelMacd8_17Val;
    public Label labelMacd12_25Val;
	
	
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
		window.hide();
	}
	
	public GridPane createGridPane(){
		GridPane gridPane = new GridPane();
		gridPane.setStyle("-fx-background-color: #3D3D3D");
		//gridPane.setGridLinesVisible(true);
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
        // R1C0 - SMA
        Label labelSma10Text = getTextLabel("SMA 10", HPos.LEFT);
        gridPane_r1c0.add(labelSma10Text, 0, 0,1,1);        
        labelSma10Val = getTextLabel("", HPos.RIGHT);
        labelSma10Val.setFont(Font.font("Arial",FontWeight.BLACK,13));
        gridPane_r1c0.add(labelSma10Val, 1, 0,1,1);
        
        Label labelSma20Text = getTextLabel("SMA 20", HPos.LEFT);
        gridPane_r1c0.add(labelSma20Text, 0, 1,1,1);
        labelSma20Val = getTextLabel("", HPos.RIGHT);
        labelSma20Val.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,14));
        gridPane_r1c0.add(labelSma20Val, 1, 1,1,1);
        
        Label labelSma50Text = getTextLabel("SMA 50", HPos.LEFT);
        gridPane_r1c0.add(labelSma50Text, 0, 2,1,1);
        labelSma50Val = getTextLabel("", HPos.RIGHT);
        labelSma50Val.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,14));
        gridPane_r1c0.add(labelSma50Val, 1, 2,1,1);
        
        Label labelSma100Text = getTextLabel("SMA 100",HPos.LEFT);
        gridPane_r1c0.add(labelSma100Text, 0, 3,1,1);
        labelSma100Val = getTextLabel("", HPos.RIGHT);
        labelSma100Val.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,14));
        gridPane_r1c0.add(labelSma100Val, 1, 3,1,1);
        
        Label labelSma250Text = getTextLabel("SMA 250", HPos.LEFT);
        gridPane_r1c0.add(labelSma250Text, 0, 4,1,1);
        labelSma250Val = getTextLabel("", HPos.RIGHT);
        labelSma250Val.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,14));
        gridPane_r1c0.add(labelSma250Val, 1, 4,1,1);
 

        // R1C0 - RSI , MACD
        Label labelRsi10Text = getTextLabel("RSI 10", HPos.LEFT);
        gridPane_r1c0.add(labelRsi10Text, 2, 0,1,1);
        labelRsi10Val = getTextLabel("", HPos.RIGHT);
        labelRsi10Val.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,14));
        gridPane_r1c0.add(labelRsi10Val, 3, 0,1,1);
        
        Label labelRsi14Text = getTextLabel("RSI 14", HPos.LEFT);
        gridPane_r1c0.add(labelRsi14Text, 2, 1,1,1);
        labelRsi14Val = getTextLabel("", HPos.RIGHT);
        labelRsi14Val.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,14));
        gridPane_r1c0.add(labelRsi14Val, 3, 1,1,1);
 
        Label labelRsi20Text = getTextLabel("RSI 20",HPos.LEFT);
        gridPane_r1c0.add(labelRsi20Text, 2, 2,1,1);
        labelRsi20Val = getTextLabel("", HPos.RIGHT);
        labelRsi20Val.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,14));
        gridPane_r1c0.add(labelRsi20Val, 3, 2,1,1);
 
        Label labelMacd817Text = getTextLabel("MACD(8/17 Day)", HPos.LEFT);
        gridPane_r1c0.add(labelMacd817Text, 2, 3,2,1);
        labelMacd8_17Val = getTextLabel("", HPos.RIGHT);
        labelMacd8_17Val.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,14));
        gridPane_r1c0.add(labelMacd8_17Val, 3, 3,1,1);
 
        Label labelMacd1225Text = getTextLabel("MACD(12/25 Day)", HPos.LEFT);
        gridPane_r1c0.add(labelMacd1225Text, 2, 4,2,1);
        labelMacd12_25Val = getTextLabel("", HPos.RIGHT);
        labelMacd12_25Val.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,14));
        gridPane_r1c0.add(labelMacd12_25Val, 3, 4,1,1);
 

        // R0C0
        Label label1MRangeText = getTextLabel("1 Month Range", HPos.LEFT);
        gridPane_r0c0.add(label1MRangeText, 0, 0,1,1);
        labelOneMonthRangeVal = getTextLabel("", HPos.CENTER);
        labelOneMonthRangeVal.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,14));
        gridPane_r0c0.add(labelOneMonthRangeVal, 0, 1,1,1);
 
        
        Label label3MRangeText = getTextLabel("3 Month Range", HPos.LEFT);
        gridPane_r0c0.add(label3MRangeText, 0, 2,1,1);
        labelThreeMonthRangeVal = getTextLabel("", HPos.CENTER);
        labelThreeMonthRangeVal.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,14));
        gridPane_r0c0.add(labelThreeMonthRangeVal, 0, 3,1,1);
        
        Label labelRateRatioText = getTextLabel("Rate Ratio", HPos.LEFT);
        gridPane_r0c0.add(labelRateRatioText, 0, 4,1,1);
        labelRateRatioVal = getTextLabel("", HPos.CENTER);
        labelRateRatioVal.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,14));
        gridPane_r0c0.add(labelRateRatioVal, 0, 5,1,1);

        Label label2MRangeText = getTextLabel("2 Month Range", HPos.LEFT);
        gridPane_r0c0.add(label2MRangeText, 1, 0,1,1);
        labelTwoMonthRangeVal = getTextLabel("", HPos.CENTER);
        labelTwoMonthRangeVal.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,14));
        gridPane_r0c0.add(labelTwoMonthRangeVal, 1, 1,1,1);

        
        Label label52WRangeText = getTextLabel("52 Week Range", HPos.LEFT);
        gridPane_r0c0.add(label52WRangeText, 1, 2,1,1);
        labelFiftyTwoWeekRangeVal = getTextLabel("", HPos.CENTER);
        labelFiftyTwoWeekRangeVal.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,14));
        gridPane_r0c0.add(labelFiftyTwoWeekRangeVal, 1, 3,1,1);

        
        Label labelVolumeRatioText = getTextLabel("Rate Ratio", HPos.LEFT);
        gridPane_r0c0.add(labelVolumeRatioText, 1, 4,1,1);
        labelVolumeRatioVal = getTextLabel("", HPos.CENTER);
        labelVolumeRatioVal.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,14));
        gridPane_r0c0.add(labelVolumeRatioVal, 1, 5,1,1);

        
        // R0C1
        Label labelBidDelayedText = getTextLabel("Bid(Delayed)", HPos.LEFT);
        gridPane_r0c1.add(labelBidDelayedText, 0, 0,1,1);
        labelBid_delayedVal = getTextLabel("", HPos.CENTER);
        labelBid_delayedVal.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,23));
        gridPane_r0c1.add(labelBid_delayedVal, 0, 1,1,2);

        Label labelHighText = getTextLabel("High", HPos.LEFT);
        gridPane_r0c1.add(labelHighText, 1, 0,1,1);
        labelHighVal = getTextLabel("", HPos.CENTER);
        labelHighVal.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,23));
        gridPane_r0c1.add(labelHighVal, 1, 1,1,2);
        
        Label labelOpenText = getTextLabel("Open", HPos.LEFT);
        gridPane_r0c1.add(labelOpenText, 2, 0,1,1);
        labelOpenVal = getTextLabel("", HPos.CENTER);
        labelOpenVal.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,23));
        gridPane_r0c1.add(labelOpenVal, 2, 1,1,2);
        
        Label labelVolumeText = getTextLabel("Volume", HPos.LEFT);
        gridPane_r0c1.add(labelVolumeText, 3, 0,1,1);
        labelVolumeVal = getTextLabel("", HPos.CENTER);
        labelVolumeVal.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,23));
        gridPane_r0c1.add(labelVolumeVal, 3, 1,1,2);
        
        Label labelAskDelayedText = getTextLabel("Ask(Delayed)", HPos.LEFT);
        gridPane_r0c1.add(labelAskDelayedText, 0, 3,1,1);
        labelAsk_delayedVal = getTextLabel("", HPos.CENTER);
        labelAsk_delayedVal.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,23));
        gridPane_r0c1.add(labelAsk_delayedVal, 0, 4,1,2);
        
        Label labelLowText = getTextLabel("Low", HPos.LEFT);
        gridPane_r0c1.add(labelLowText, 1, 3,1,1);
        labelLowVal = getTextLabel("", HPos.CENTER);
        labelLowVal.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,23));
        gridPane_r0c1.add(labelLowVal, 1, 4,1,2);
        
        Label labelPrevCloseText = getTextLabel("Prev Close",HPos.LEFT);
        gridPane_r0c1.add(labelPrevCloseText, 2, 3,1,1);
        labelPrev_closeVal = getTextLabel("", HPos.CENTER);
        labelPrev_closeVal.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,23));
        gridPane_r0c1.add(labelPrev_closeVal, 2, 4,1,2);
        
        Label labelTurnoverText = getTextLabel("Turnover", HPos.LEFT);
        gridPane_r0c1.add(labelTurnoverText, 3, 3,1,1);
        labelTurnoverVal = getTextLabel("", HPos.CENTER);
        labelTurnoverVal.setFont(Font.font("Arial",FontWeight.BLACK,23));
        gridPane_r0c1.add(labelTurnoverVal, 3, 4,1,2);

        gridPane.add(gridPane_r1c0, 0, 1,1,1);
        gridPane.add(gridPane_r0c0, 0, 0,1,1);
        gridPane.add(gridPane_r0c1, 1, 0,1,1);

        //gridPane.setHalignment(gridPane_r1c0, HPos.CENTER);
        return gridPane;
 
	}
	
	public GridPane individualGridPane(int num_col, int num_row,int min_col, int pref_col, int max_col, int min_row, int pref_row, int max_row ){
        // gridPane_r1c0
        GridPane gridPane = new GridPane();
        //gridPane.setGridLinesVisible(true);
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
	
	public Label getTextLabel(String text, HPos hpos){
		final String cssDefault = "-fx-border-color: blue;\n"
                + "-fx-border-insets: 5;\n"
                + "-fx-border-width: 3;\n"
                + "-fx-border-style: dashed;\n";
		Label textLabel = new Label(text);
		//textLabel.setStyle("-fx-background-color: #3D3D3D;");
		if(!text.equals("")){
			textLabel.setTextFill(Color.WHITESMOKE);
			textLabel.setFont(Font.font("Arial",FontWeight.BOLD,13));
		} else{
			textLabel.setTextFill(Color.ROYALBLUE);
		}
        GridPane.setHalignment(textLabel, hpos);

		return textLabel;
	}
}
