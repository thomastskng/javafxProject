package application;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.*;
import javafx.scene.control.TableCell;

public class NonEditableNumberCell<T> extends TableCell<T,Number>{

		public NonEditableNumberCell(String...styleClasses){
	        getStyleClass().addAll(styleClasses);
		}
	
        @Override 
        public void updateItem(Number item, boolean empty) {
        	 Locale locale = new Locale("en", "UK");
             String pattern = "###,###.###;-###,###.###";
             DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(locale);
             df.applyPattern(pattern);
             df.setNegativePrefix("(" + df.getPositivePrefix());
             df.setNegativeSuffix(")");
             df.setMinimumFractionDigits(2);
             df.setMaximumFractionDigits(10);

             super.updateItem(item, empty);
            if (empty) {
                setText(null);
            } else {
                setText(df.format(item.doubleValue()));
            }
        }

}
