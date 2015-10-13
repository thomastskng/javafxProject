package application;

import javafx.beans.property.ReadOnlyStringProperty;

public class Testing {
	public static void main(String[] args){
		String[] var = { "bid_delayed", "ask_delayed", "high", "low", "open", "prev_close", "volume", "turnover", "oneMonthRange", "twoMonthRange", "threeMonthRange", "fiftyTwoWeekRange", "rateRatio", "volumeRatio", "sma10", "sma20", "sma50", "sma100", "sma250", "rsi10", "rsi14", "rsi20", "macd8_17", "macd12_25"};
		
		for(int i=0; i<var.length; i++){
			/*
			System.out.println("");
			String vv = var[i];
			String vvv = Character.toUpperCase(vv.charAt(0)) + vv.substring(1);
			System.out.println("public String get" + vvv + "(){");
			System.out.println("\treturn this." + var[i]+";");
			System.out.println("}");
			*/
			String vv = var[i];
			String vvv = Character.toUpperCase(vv.charAt(0)) + vv.substring(1);	
			/*
			System.out.println("public ReadOnlyStringProperty " + var[i] + "Property(){");
			System.out.println("\treturn this." + var[i] + ".getReadOnlyProperty();");
			System.out.println("}");
			System.out.println("");
			System.out.println("public String get" + vvv + "(){");
			System.out.println("\treturn this." + var[i] + "Property().get();");
			System.out.println("}");
			*/
			//System.out.println("get"+vvv+"()");
			//midb.labelBid_delayedVal.textProperty().bind(lookUpTicker.bid_delayedProperty());

			System.out.println("midb.label"+vvv+"Val.textProperty().bind(lookUpTicker."+vv+"Property());");
			//System.out.println("//" + var[i]);
			//System.out.println("makeStringBinding(this." + vv + ", StockScrapedInfo::get" + vvv + ");");

			//System.out.println("private final ReadOnlyStringWrapper " + var[i] + ";");
			//System.out.println("this." + var[i] + "= new ReadOnlyStringWrapper(\"\")" + ";");
		}
	
	}
}
/*
public ReadOnlyStringProperty dpsProperty(){
	return this.dps.getReadOnlyProperty();
}
			makeStringBinding(this.spread, StockScrapedInfo::getSpread);

public String getDps(){
	return this.dpsProperty().get();
}
*/